/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.camera

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.Point
import android.hardware.Camera
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import android.view.WindowManager
import androidx.annotation.IntDef
import androidx.annotation.StringDef
import androidx.exifinterface.media.ExifInterface
import autodispose2.autoDispose
import com.akingyin.base.ext.*
import com.akingyin.base.rx.RxUtil
import com.akingyin.base.utils.CalculationUtil
import com.akingyin.base.utils.FileUtils
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


/**
 * camera1 管理器
 * @ Description:
 * @author king
 * @ Date 2020/7/16 16:02
 * @version V1.0
 */

@Suppress("DEPRECATION")
class CameraManager(content: Context, autoFouceCall: () -> Unit) {

    private val TAG = "camera-manager"

    /** 拍照时相机旋转角度 */
    var cameraAngle = 90

    var windowRotation = 90
    /** UI 显示旋转角度 */
    var cameraUiAngle = 0

    var requestedCameraId = -1

    @CameraShutterSound
    var cameraShutterSound = CameraShutterSound.CAMERA_SHUTTER_SOUND_NONE

    var camera: Camera? = null

    /** 是否在预览中 */
    private var previewing = false

    //当前手机分辨率
    var theScreenResolution = Point()

    //相机设置的分辨率
    var cameraBestResolution: Point = Point()
    /** 自定义 相机分辨率*/
    var cameraCustomResolution:Point = Point()

    /** 相机当前的最小zoom */
    var  cameraMinZoom = 0
    /**
     * 获取相机可支持的最大值
     */
    var  cameraMaxZoom :Int= 99
    var  cameraCurrentZoom = 1F
    private var SCALE = SCALE_16_9


    // 打开相机后默认使用的分辨率()
    var defaultPreviewSize = Point()

    var cameraAutoFouceSensorController: CameraAutoFouceSensorController

    init {
        val windowManager: WindowManager = content.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getRealSize(theScreenResolution)
        windowRotation = try {
            CameraDisplayOrientation(windowManager.defaultDisplay.rotation)

        }catch (e : Exception){
            e.printStackTrace()
            90
        }

        SCALE = max(theScreenResolution.x, theScreenResolution.y).toDouble() / min(theScreenResolution.x, theScreenResolution.y).toDouble()
        SCALE = arrayListOf<Double>().apply {
            add(abs(SCALE - SCALE_16_9))
            add(abs(SCALE - SCALE_4_3))
            add(abs(SCALE - SCALE_1_1))
        }.asSequence().sortedBy {
            it
        }.first()
        cameraAutoFouceSensorController = CameraAutoFouceSensorController(content) {
            autoStartFuoce { result, msg ->
                println("运动对焦$result,$msg")
                if (result) {
                    autoFouceCall.invoke()
                }
            }
        }

    }


    // 初始花相机及预览
    @Synchronized
    @Throws(IOException::class)
    fun openCamera(holder: SurfaceHolder, errorCallback: Camera.ErrorCallback?) {
        var theCamera = camera
        if (theCamera == null) {
            // 获取手机后置的摄像头
            theCamera = open(requestedCameraId)
            if (theCamera == null) {
                throw IOException()
            }
            try {
                // 设置摄像头的角度，一般来说90度
                theCamera.setDisplayOrientation(windowRotation)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            camera = theCamera

            camera?.let {
                val parameters = it.parameters
                parameters.previewSize.run {
                    defaultPreviewSize.x = width
                    defaultPreviewSize.y = height
                }
                cameraMaxZoom = parameters.maxZoom
                cameraCurrentZoom = parameters.zoom.toFloat()
                cameraBestResolution = findBestPreviewSizeValue(parameters, theScreenResolution)
                    ?: defaultPreviewSize

            }
        }
        // 设置摄像头预览view
        theCamera.setErrorCallback(errorCallback)
        theCamera.setPreviewDisplay(holder)
    }

    private var cameraParameBuild: CameraParameBuild? = null

    /**
     * 设置相机参数
     */
    fun setCameraParametersValues(camera: Camera, cameraParameBuild: CameraParameBuild, callBack: (result: Boolean, error: String?) -> Unit) {
        try {
            println("设置相机参数->$cameraParameBuild")
            cameraShutterSound = cameraParameBuild.shutterSound
            if (cameraShutterSound != CameraShutterSound.CAMERA_SHUTTER_SOUND_NONE) {
                camera.enableShutterSound(cameraShutterSound == CameraShutterSound.CAMERA_SHUTTER_SOUND_ON)
            }
            if(cameraParameBuild.supportMoveFocus){
                cameraAutoFouceSensorController.unRegisterSensor()
            }else{
                cameraAutoFouceSensorController.onRegisterSensor()
            }

            camera.parameters = camera.parameters.apply {
                pictureFormat = ImageFormat.JPEG
                var point = cameraParameBuild.cameraResolution ?: cameraBestResolution
                if(point.x == 0 || point.y == 0){
                    point = cameraBestResolution
                }
                setPreviewSize(point.x, point.y)
                setPictureSize(point.x, point.y)
                jpegQuality = 100
                when (cameraParameBuild.flashModel) {
                    CameraFlashModel.CAMERA_FLASH_NONE -> {
                    }
                    CameraFlashModel.CAMERA_FLASH_AUTO -> {
                        flashMode = Camera.Parameters.FLASH_MODE_AUTO
                    }
                    CameraFlashModel.CAMERA_FLASH_OFF -> {
                        flashMode = Camera.Parameters.FLASH_MODE_OFF
                    }
                    CameraFlashModel.CAMERA_FLASH_ON -> {
                        flashMode = Camera.Parameters.FLASH_MODE_TORCH
                    }
                }
            }

            callBack(true, null)
        } catch (e: Exception) {
            e.printStackTrace()
            callBack(false, e.message)
        }

    }


    /**
     * 设置闪光灯模式
     */
    fun setCameraFlashModel(camera: Camera, @CameraFlashModel flashModel: Int, callBack: (result: Boolean, error: String?) -> Unit) {
        try {
            camera.parameters = camera.parameters.apply {
                when (flashModel) {
                    CameraFlashModel.CAMERA_FLASH_NONE -> {
                    }
                    CameraFlashModel.CAMERA_FLASH_AUTO -> {
                        flashMode = Camera.Parameters.FLASH_MODE_AUTO
                    }
                    CameraFlashModel.CAMERA_FLASH_OFF -> {
                        flashMode = Camera.Parameters.FLASH_MODE_OFF
                    }
                    CameraFlashModel.CAMERA_FLASH_ON -> {
                        flashMode = Camera.Parameters.FLASH_MODE_TORCH
                    }
                }
            }
            cameraParameBuild?.flashModel = flashModel
            callBack(true, null)
        } catch (e: Exception) {
            e.printStackTrace()
            callBack(false, e.message)
        }
    }



    private var focusing = false


    /**
     * 这是自动对焦
     */
     fun autoStartFuoce(callBack: (result: Boolean, error: String?) -> Unit) {
        println("autoStartFuoce=$focusing,$previewing")
        if (focusing || previewing) {
            return
        }
        focusing = true
        try {
            GlobalScope.launch(Main) {
                delay(1000)
                withContext(IO) {

                    camera?.let {
                        it.cancelAutoFocus()
                        it.autoFocus { success, _ ->
                            focusing = false
                            callBack(success, null)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            focusing = false
            try {
                camera?.cancelAutoFocus()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            callBack(false, e.message)
        }

    }





    fun  getZoomRatio() = cameraCurrentZoom

    /**
     * 设置相机的放大倍率
     */
    fun   setCameraZoom(zoom:Float){

        camera?.parameters?.let {
            parameters ->
            if(parameters.isZoomSupported){
                if(zoom<= parameters.maxZoom ){
                    camera?.parameters = parameters.also {
                        it.zoom = zoom.toInt()
                    }
                    cameraCurrentZoom = zoom
                }
            }
        }
    }


    /**
     * 开启预览
     */
    @Synchronized
    fun startPreview() {

        val theCamera = camera
        if (theCamera != null && !previewing) {
            theCamera.startPreview()
            previewing = true
        }
        cameraParameBuild?.let {
            if(it.supportMoveFocus){
                cameraAutoFouceSensorController.onRegisterSensor()
            }else{
                cameraAutoFouceSensorController.unRegisterSensor()
            }
        }

    }


    /**
     * 关闭预览
     * Tells the camera to stop drawing preview frames.
     */
    @Synchronized
    fun stopPreview() {

        println("pr=$previewing")
        if (camera != null && previewing) {
            camera?.stopPreview()
            previewing = false
        }
        cameraAutoFouceSensorController.unRegisterSensor()
    }


    /** 是否正在拍照 */
    private  var   takePhotoing = false
    /**
     * 拍照
     */
    @SuppressLint("RestrictedApi")
    @Synchronized
    fun takePictrue(cameraParameBuild: CameraParameBuild, callBack: (result: Boolean, error: String?) -> Unit) {
        try {
            println("当前角度=$cameraAngle")
            if(takePhotoing){
                callBack(false,"正在拍照中")
                return
            }
            takePhotoing = true
            if (cameraShutterSound != CameraShutterSound.CAMERA_SHUTTER_SOUND_NONE) {
                camera?.enableShutterSound(cameraShutterSound == CameraShutterSound.CAMERA_SHUTTER_SOUND_ON)
            }
            camera?.takePicture(if (cameraParameBuild.shutterSound == CameraShutterSound.CAMERA_SHUTTER_SOUND_ON) {
                Camera.ShutterCallback {
                    println("ShutterCallback")
                }
            } else {
                null
            }, null, Camera.PictureCallback { data, _ ->
                GlobalScope.launch(Main) {
                    try {
                        withIO {
                            CameraBitmapUtil.dataToBaseBitmap(data, FileUtils.getFolderName(cameraParameBuild.localPath),
                                    "base_" + FileUtils.getFileName(cameraParameBuild.localPath), 90)
                        }?.let {
                            withIO {
                                println("当前width=${it.width},hight=${it.height}")
                                CameraBitmapUtil.zipImageTo960x540(it, cameraParameBuild.cameraAngle,landscape = cameraParameBuild.horizontalPicture, fileDir = FileUtils.getFolderName(cameraParameBuild.localPath), fileName = FileUtils.getFileName(cameraParameBuild.localPath))

                            }.yes {
                                try {
                                    val exifInterface = ExifInterface(cameraParameBuild.localPath)
                                    exifInterface.dateTime = appServerTime
                                    if(cameraParameBuild.lat>0 && cameraParameBuild.lng>0){
                                        exifInterface.setLatLong(cameraParameBuild.lat,cameraParameBuild.lng)
                                        exifInterface.setAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD,cameraParameBuild.locType)
                                    }

                                    exifInterface.saveAttributes()
                                }catch (e : Exception){
                                    e.printStackTrace()
                                }


                                callBack(true, null)
                            }.no {
                                callBack(false, "图片转换失败")
                            }
                        } ?: callBack(true, "图片转换失败")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        println("发现异常1")
                        callBack(false, e.message)
                        takePhotoing = false
                    }

                }
            })
        }catch (e : Exception){
            e.printStackTrace()
            println("发现异常2")
            callBack(false,"出错了,${e.message}")
        }finally {
            takePhotoing = false
        }


    }

    /**
     * 释放相机
     */
    @Synchronized
    fun closeDriver() {
        if (camera != null) {
            camera?.release()
            camera = null

            println("关闭自定义相机")
        }
    }

    fun open(cameraOpenId: Int): Camera? {

        //手机上camera的数量 =0 则当前手机无摄像头
        var cameraId = cameraOpenId
        val numCameras = Camera.getNumberOfCameras()
        println("numCameras=$numCameras")
        if (numCameras == 0) {
            Timber.tag(TAG).w("No cameras!")
            return null
        }
        val explicitRequest = cameraId >= 0
        if (!explicitRequest) {
            // Select a camera if no explicit camera requested
            var index = 0
            //遍历当前所有摄像头信息
            while (index < numCameras) {
                val cameraInfo = Camera.CameraInfo()
                Camera.getCameraInfo(index, cameraInfo)
                //判断是否是后置摄像头
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    break
                }
                index++
            }
            cameraId = index
        }
        val camera: Camera?
        camera = if (cameraId < numCameras) {
            Timber.tag(TAG).i("Opening camera #$cameraId")

            Camera.open(cameraId)

        } else {
            if (explicitRequest) {
                Timber.tag(TAG).w("Requested camera does not exist: $cameraId")
                null
            } else {
                windowRotation = 90
                Timber.tag(TAG).i("No camera facing back; returning camera #0")
                Camera.open()

            }
        }
        return camera
    }


    /**
     * 找到最合适的PreviewSize，如果有当前技能的Size 进行计算
     * 1.Size >= 480 * 320
     * 2.屏幕的Size 与当前 Size 宽与高比值之差《=0.15
     * @param parameters
     * @param screenResolution 当前屏幕Size
     * @return
     */
    private fun findBestPreviewSizeValue(parameters: Camera.Parameters, screenResolution: Point): Point? {

        //找到所有支持的Size
        val rawSupportedSizes = parameters.supportedPreviewSizes
        if (rawSupportedSizes == null) {
            Timber.tag(TAG).w("Device returned no supported preview sizes; using default")
            val defaultSize = parameters.previewSize
                ?: throw IllegalStateException("Parameters contained no preview size!")
            return Point(defaultSize.width, defaultSize.height)
        }

        // Sort by size, descending
        val supportedPreviewSizes: MutableList<Camera.Size> = ArrayList(rawSupportedSizes)

        //自定义排序规则
        Collections.sort(supportedPreviewSizes, object : Comparator<Camera.Size> {
            override fun compare(a: Camera.Size, b: Camera.Size): Int {
                val aPixels = a.height * a.width
                val bPixels = b.height * b.width
                if (bPixels < aPixels) {
                    return -1
                }
                return if (bPixels > aPixels) {
                    1
                } else 0
            }
        })

        val screenAspectRatio = max(screenResolution.x, screenResolution.y).toDouble() / min(screenResolution.x, screenResolution.y).toDouble()

        // Remove sizes that are unsuitable
        val it = supportedPreviewSizes.iterator()
        while (it.hasNext()) {
            val supportedPreviewSize = it.next()
            val realWidth = supportedPreviewSize.width
            val realHeight = supportedPreviewSize.height
            if (realWidth * realHeight < MIN_PREVIEW_PIXELS) {
                it.remove()
                continue
            }
            val isCandidatePortrait = realWidth < realHeight
            val maybeFlippedWidth = if (isCandidatePortrait) realHeight else realWidth
            val maybeFlippedHeight = if (isCandidatePortrait) realWidth else realHeight
            val aspectRatio = maybeFlippedWidth.toDouble() / maybeFlippedHeight.toDouble()
            val distortion = abs(aspectRatio - screenAspectRatio)
            if (distortion > MAX_ASPECT_DISTORTION) {
                Timber.tag(TAG).v("maybeFlippedWidth=$maybeFlippedWidth,maybeFlippedHeight=$maybeFlippedHeight  remove")
                it.remove()
                continue
            }
            if (maybeFlippedWidth == screenResolution.x && maybeFlippedHeight == screenResolution.y) {
                val exactPoint = Point(realWidth, realHeight)
                Timber.tag(TAG).i("Found preview size exactly matching screen size: $exactPoint")
                return exactPoint
            }
        }

        // If no exact match, use largest preview size. This was not a great idea on older devices because
        // of the additional computation needed. We're likely to get here on newer Android 4+ devices, where
        // the CPU is much more powerful.
        //排除完上述条件后，获取第一个Size
        if (supportedPreviewSizes.isNotEmpty()) {
            var largestPreview = supportedPreviewSizes[0]
            for (supportedPreviewSize in supportedPreviewSizes) {
                val scale = supportedPreviewSize.width.coerceAtLeast(supportedPreviewSize.height) / (supportedPreviewSize.width.coerceAtMost(supportedPreviewSize.height) * 1.0)
                if (abs(SCALE - scale) <= 0.01 && supportedPreviewSize.width.coerceAtLeast(supportedPreviewSize.height) > 960) {
                    largestPreview = supportedPreviewSize
                    break
                }
            }
            val largestSize = Point(largestPreview.width, largestPreview.height)
            Timber.tag(TAG).i("Using largest suitable preview size: $largestSize")
            return largestSize
        }

        // If there is nothing at all suitable, return current preview size
        val defaultPreview = parameters.previewSize
            ?: throw IllegalStateException("Parameters contained no preview size!")
        val defaultSize = Point(defaultPreview.width, defaultPreview.height)
        Timber.tag(TAG).i("No suitable preview sizes, using default: $defaultSize")
        return defaultSize
    }


    private var disposable: Disposable? = null

    /**
     * 延迟多少秒后自动拍照
     */
    fun autoTakePhoto(delayTime: Int = 0, cameraParameBuild: CameraParameBuild, callBack: (result: Boolean, error: String?) -> Unit) {
        disposable?.dispose()
        disposable = Observable.timer(delayTime.toLong(), TimeUnit.SECONDS).compose(RxUtil.IO_Main()).autoDispose(Completable.complete())
                .subscribe({
                    takePictrue(cameraParameBuild, callBack)
                }, {
                    callBack(false, "出错了,${it.message}")
                })
    }


    /**
     * 获取最佳cameraView
     */
    fun findBestViewSize(screenPoint: Point, cameraPoint: Point): Point? {

        //屏幕的比例
        val screenScale = max(screenPoint.x, screenPoint.y) / min(screenPoint.x, screenPoint.y).toFloat()

        //相机预览的比例
        val scale = max(cameraPoint.x, cameraPoint.y) / min(cameraPoint.x, cameraPoint.y).toFloat()
        var width = min(screenPoint.x, screenPoint.y)
        val height = max(screenPoint.x, screenPoint.y)

        //如何比比例操作某个范围，则需要处理当前预览界面（默认是全屏）
        //正常只需要修改高度
        if (abs(scale - screenScale) > 0.01) {
            val beseheight = (width * scale).toInt()
            if (beseheight > height) {
                //如果高度超出范围
                width = (height / scale).toInt()
                return Point(width, 0)
            }
            return Point(0, beseheight)
        }
        return null
    }


    @IntDef(value = [CameraFlashModel.CAMERA_FLASH_AUTO, CameraFlashModel.CAMERA_FLASH_OFF,
        CameraFlashModel.CAMERA_FLASH_ON, CameraFlashModel.CAMERA_FLASH_NONE])
    @Retention(AnnotationRetention.SOURCE)
    annotation class CameraFlashModel {
        companion object {
            /** 自动 */
            const val CAMERA_FLASH_AUTO = 0

            /** 开 */
            const val CAMERA_FLASH_ON = 1

            /**关 */
            const val CAMERA_FLASH_OFF = 2

            /** 不使用*/
            const val CAMERA_FLASH_NONE = -1
        }
    }


    @IntDef(value = [CameraShutterSound.CAMERA_SHUTTER_SOUND_ON, CameraShutterSound.CAMERA_SHUTTER_SOUND_OFF, CameraShutterSound.CAMERA_SHUTTER_SOUND_NONE])
    @Retention(AnnotationRetention.SOURCE)
    annotation class CameraShutterSound {
        companion object {
            /** 打开快门声音 */
            const val CAMERA_SHUTTER_SOUND_ON = 0
            const val CAMERA_SHUTTER_SOUND_OFF = 1
            const val CAMERA_SHUTTER_SOUND_NONE = -1
        }
    }

    @IntDef(value = [CameraNetGrid.CAMERA_NET_GRID_CLOSE, CameraNetGrid.CAMERA_NET_GRID_OPEN, CameraNetGrid.CAMERA_NET_GRID_NONE])
    @Retention(AnnotationRetention.SOURCE)
    annotation class CameraNetGrid {
        companion object {
            /** 相机网格线 */
            const val CAMERA_NET_GRID_CLOSE = 0
            const val CAMERA_NET_GRID_OPEN = 1
            const val CAMERA_NET_GRID_NONE = -1
        }
    }

    @StringDef(value = [LocalType.CORR_TYPE_BD09,LocalType.CORR_TYPE_GCJ02,LocalType.CORR_TYPE_WGS84])
    @Retention(AnnotationRetention.SOURCE)
    annotation class LocalType{
        companion object{
            //坐标类型
            const val  CORR_TYPE_BD09="bd09ll"
            const val CORR_TYPE_GCJ02 ="gcj02"
            const val CORR_TYPE_WGS84 ="wgs84"
        }
    }


    companion object {
        //480 * 320
        const val MIN_PREVIEW_PIXELS = 540 * 540 // normal screen

        const val MAX_ASPECT_DISTORTION = 0.15

        const val SCALE_16_9 = 16.0 / 9.0

        const val SCALE_4_3 = 4.0 / 3.0

        const val SCALE_1_1 = 1.0

        /**
         * 点击开始拍照动画
         */
        @JvmStatic
        fun startTypeCaptureAnimator(captureButton: View, configBtn: View, cancelBtn: View,rotationView: View) {
            captureButton.visibility = View.INVISIBLE
            configBtn.visiable()
            cancelBtn.visiable()
            rotationView.visiable()
            println("with=${configBtn.width},${cancelBtn.width}")
            AnimatorSet().apply {
                playTogether(ObjectAnimator.ofFloat(cancelBtn, "translationX", cancelBtn.width / 4F, 0F),
                        ObjectAnimator.ofFloat(configBtn, "translationX", configBtn.width / -4f, 0F))
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        cancelBtn.isClickable = true
                        configBtn.isClickable = true
                    }
                })
                duration = 200
            }.start()

        }

        @JvmStatic
        fun startCameraViewRoteAnimator(rotation: Float, vararg views: View) {
            AnimatorSet().apply {
                playTogether(views.map { view ->
                    ObjectAnimator.ofFloat(view, "rotation", rotation)
                })
                duration = 200
            }.start()
        }

        @JvmStatic
        fun recoveryCaptureAnimator(captureButton: View, configBtn: View, cancelBtn: View,rotationView: View) {
            captureButton.visiable()
            captureButton.isEnabled = true
            configBtn.gone()
            rotationView.gone()
            cancelBtn.gone()
        }

        @JvmStatic
        fun getCameraResolutionScale(point: Point): String {

            val result = CalculationUtil.maxCommonDivisor2(point.x, point.y)
            return "[" + max(point.x, point.y) / result + ":" + min(point.x, point.y) / result + "]"
        }




        /**
         * 获取支持的分辨率
         * @param parameters
         *
         * @return
         */
        fun findSuportCameraSizeValue(parameters: Camera.Parameters): ArrayList<Point>? {
            val list: ArrayList<Point> = ArrayList()
            //找到所有支持的Size
            val rawSupportedSizes = parameters.supportedPreviewSizes

            if (rawSupportedSizes == null) {

                val defaultSize = parameters.previewSize ?: return null
                val point = Point(defaultSize.width, defaultSize.height)
                list.add(point)
                return list
            }
            // Sort by size, descending
            val supportedPreviewSizes: MutableList<Camera.Size> = ArrayList(rawSupportedSizes)

            //自定义排序规则
            Collections.sort(supportedPreviewSizes, object : Comparator<Camera.Size> {
                override fun compare(a: Camera.Size, b: Camera.Size): Int {
                    val aPixels = a.height * a.width
                    val bPixels = b.height * b.width
                    if (bPixels < aPixels) {
                        return -1
                    }
                    return if (bPixels > aPixels) {
                        1
                    } else 0
                }
            })
            val it = supportedPreviewSizes.iterator()
            while (it.hasNext()) {
                val supportedPreviewSize = it.next()
                val realWidth = supportedPreviewSize.width
                val realHeight = supportedPreviewSize.height
                if (realWidth * realHeight < MIN_PREVIEW_PIXELS) {
                    it.remove()
                    continue
                }
                list.add(Point(supportedPreviewSize.width, supportedPreviewSize.height))
            }
            if (list.size > 1) {
                removeDuplicate(list)
            }
            return list
        }

        fun <T> removeDuplicate(list: ArrayList<T>): List<T> {
            for (i in 0 until list.size - 1) {
                for (j in list.size - 1 downTo i + 1) {
                    if (list[j] == list[i]) {
                        list.removeAt(j)
                    }
                }
            }
            return list
        }
    }


    @Throws(Exception::class)
    fun CameraDisplayOrientation(rotation: Int): Int {
        val info = Camera.CameraInfo()
        val cameraId: Int = CameraId()
        if (cameraId == -1) {
            throw java.lang.Exception("没有摄像头！")
        }
        Camera.getCameraInfo(cameraId, info)
        var degrees = 0

        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
            else -> {
            }
        }

        return if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            (info.orientation + degrees) % 360

        } else { // back-facing
            (info.orientation - degrees + 360) % 360
        }
    }


    //获取到当前后置摄像头的ID
    private fun CameraId(): Int {
        val numCameras = Camera.getNumberOfCameras()
        if (numCameras == 0) {

            return -1
        }
        var index = 0
        while (index < numCameras) {
            val cameraInfo = Camera.CameraInfo()
            Camera.getCameraInfo(index, cameraInfo)
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                break
            }
            index++
        }
        return index
    }
}