/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.camera

import android.content.Context
import android.graphics.ImageFormat
import android.graphics.Point
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.WindowManager
import androidx.annotation.IntDef
import com.akingyin.base.ext.no
import com.akingyin.base.ext.yes
import com.akingyin.base.utils.FileUtils
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.util.*
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
class CameraManager  (content:Context) {

    private  val  TAG = "camera-manager"

    /** 拍照时相机旋转角度 */
    var  cameraAngle = 90

    var requestedCameraId = -1

    @CameraShutterSound
    var  cameraShutterSound = CameraShutterSound.CAMERA_SHUTTER_SOUND_NONE

     var camera: Camera? = null

    /** 是否在预览中 */
    private var previewing = false
    //当前手机分辨率
    var theScreenResolution = Point()

    //相机设置的分辨率
    var  cameraBestResolution: Point = Point()
    var BEST_SCALE = 16.0 / 9.0
    //自动定义分辨率
    var  customResolution: Point = Point()



    // 打开相机后默认使用的分辨率()
    var  defaultPreviewSize = Point()
    init {
        val windowManager: WindowManager = content.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val theScreenResolution = Point()
        windowManager.defaultDisplay.getRealSize(theScreenResolution)
        BEST_SCALE = max(theScreenResolution.x,theScreenResolution.y).toDouble()/ min(theScreenResolution.x,theScreenResolution.y).toDouble()

    }


    // 初始花相机及预览
    @Synchronized
    @Throws(IOException::class)
    fun openCamera(holder: SurfaceHolder,errorCallback:Camera.ErrorCallback?) {
        var theCamera = camera
        if (theCamera == null) {
            // 获取手机后置的摄像头
            theCamera = open(requestedCameraId)
            if (theCamera == null) {
                throw IOException()
            }
            try {
                // 设置摄像头的角度，一般来说90度
                theCamera.setDisplayOrientation(90)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            camera = theCamera
            camera?.let {
                val  parameters = it.parameters
                parameters.previewSize.run {
                    defaultPreviewSize.x = width
                    defaultPreviewSize.y = height
                }
                cameraBestResolution = findBestPreviewSizeValue(parameters,theScreenResolution)?:defaultPreviewSize

            }
        }
        // 设置摄像头预览view
        theCamera.setErrorCallback(errorCallback)
        theCamera.setPreviewDisplay(holder)
    }


    /**
     * 设置相机参数
     */
    fun  setCameraParametersValues(camera:Camera,cameraParameBuild: CameraParameBuild,callBack:(result:Boolean,error:String?)->Unit){
           try {
               cameraShutterSound = cameraParameBuild.shutterSound
               if(cameraShutterSound != CameraShutterSound.CAMERA_SHUTTER_SOUND_NONE){
                   camera.enableShutterSound(cameraShutterSound == CameraShutterSound.CAMERA_SHUTTER_SOUND_ON)
               }

               camera.parameters = camera.parameters.apply {
                   pictureFormat = ImageFormat.JPEG
                   val  point = cameraParameBuild.cameraResolution?:cameraBestResolution
                   setPreviewSize(point.x, point.y)
                   setPictureSize(point.x, point.y)
                   jpegQuality = 100
                   when(cameraParameBuild.flashModel){
                       CameraFlashModel.CAMERA_FLASH_NONE ->{}
                       CameraFlashModel.CAMERA_FLASH_AUTO ->{ flashMode = Camera.Parameters.FLASH_MODE_AUTO }
                       CameraFlashModel.CAMERA_FLASH_OFF ->{ flashMode = Camera.Parameters.FLASH_MODE_OFF}
                       CameraFlashModel.CAMERA_FLASH_ON ->{ flashMode = Camera.Parameters.FLASH_MODE_TORCH}
                   }
               }

               callBack(true,null)
           }catch (e : Exception){
               e.printStackTrace()
               callBack(false,e.message)
           }

    }




    /**
     * 设置闪光灯模式
     */
    fun  setCameraFlashModel(camera:Camera,@CameraFlashModel flashModel: Int,callBack: (result: Boolean, error: String?) -> Unit){
        try {
            camera.parameters = camera.parameters.apply {
                when(flashModel){
                    CameraFlashModel.CAMERA_FLASH_NONE ->{}
                    CameraFlashModel.CAMERA_FLASH_AUTO ->{ flashMode = Camera.Parameters.FLASH_MODE_AUTO }
                    CameraFlashModel.CAMERA_FLASH_OFF ->{ flashMode = Camera.Parameters.FLASH_MODE_OFF}
                    CameraFlashModel.CAMERA_FLASH_ON ->{ flashMode = Camera.Parameters.FLASH_MODE_TORCH}
                }
            }
            callBack(true,null)
        }catch (e : Exception){
            e.printStackTrace()
            callBack(false,e.message)
        }
    }

    private   var  focusing = false


    /**
     * 这是自动对焦
     */
    fun  autoStartFoce(callBack: (result: Boolean, error: String?) -> Unit){
        if(focusing){
            return
        }
        focusing = true
        try {
            GlobalScope.launch(Main) {
                withContext(IO){
                    Thread.sleep(1000)
                    camera?.let {
                        it.cancelAutoFocus()
                        it.autoFocus { success, _ ->
                            focusing = false
                            callBack(success,null)
                        }
                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
            focusing = false
            try {
               camera?.cancelAutoFocus()
            }catch (e:Exception){
                e.printStackTrace()
            }
            callBack(false,e.message)
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
    }

    /**
     * 拍照
      */
    fun  takePictrue(cameraParameBuild: CameraParameBuild,callBack: (result: Boolean, error: String?) -> Unit){
        if(cameraShutterSound != CameraShutterSound.CAMERA_SHUTTER_SOUND_NONE){
            camera?.enableShutterSound(cameraShutterSound == CameraShutterSound.CAMERA_SHUTTER_SOUND_ON)
        }
        camera?.takePicture(if(cameraParameBuild.shutterSound == CameraShutterSound.CAMERA_SHUTTER_SOUND_ON){
            Camera.ShutterCallback {
                println("ShutterCallback")
            }
        }else{null},null,Camera.PictureCallback { data, _ ->
            GlobalScope.launch(Main) {
                try {
                    withContext(IO){
                        CameraBitmapUtil.dataToBaseBitmap(data,FileUtils.getFolderName(cameraParameBuild.localPath),
                                "base_"+FileUtils.getFileName(cameraParameBuild.localPath),90)
                    }?.let {
                        withContext(IO){
                            CameraBitmapUtil.zipImageTo960x540(it,cameraParameBuild.cameraAngle,fileDir = FileUtils.getFolderName(cameraParameBuild.localPath),fileName = FileUtils.getFileName(cameraParameBuild.localPath))
                        }.yes {
                            callBack(true,null)
                        }.no {
                            callBack(false,"图片转换失败")
                        }
                    }?:callBack(true,"图片转换失败")
                }catch (e : Exception){
                    e.printStackTrace()
                    callBack(false,e.message)
                }

            }
        })

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

    fun open( cameraId: Int): Camera? {

        //手机上camera的数量 =0 则当前手机无摄像头
        var cameraId = cameraId
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
            Timber.tag(TAG).i( "Opening camera #$cameraId")
            Camera.open(cameraId)
        } else {
            if (explicitRequest) {
                Timber.tag(TAG).w("Requested camera does not exist: $cameraId")
                null
            } else {
                Timber.tag(TAG).i( "No camera facing back; returning camera #0")
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
                Timber.tag(TAG).i( "Found preview size exactly matching screen size: $exactPoint")
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
                if (abs(BEST_SCALE - scale) <= 0.01 && supportedPreviewSize.width.coerceAtLeast(supportedPreviewSize.height) > 960) {
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
        Timber.tag(TAG).i( "No suitable preview sizes, using default: $defaultSize")
        return defaultSize
    }


    @IntDef(value = [CameraFlashModel.CAMERA_FLASH_AUTO, CameraFlashModel.CAMERA_FLASH_OFF,
        CameraFlashModel.CAMERA_FLASH_ON,CameraFlashModel.CAMERA_FLASH_NONE])
    @Retention(AnnotationRetention.SOURCE)
    annotation class CameraFlashModel{
        companion object{
            /** 自动 */
            const val  CAMERA_FLASH_AUTO = 0
            /** 开 */
            const val  CAMERA_FLASH_ON = 1

            /**关 */
            const val  CAMERA_FLASH_OFF = 2

            /** 不使用*/
            const val CAMERA_FLASH_NONE = -1
        }
    }


    @IntDef(value = [CameraShutterSound.CAMERA_SHUTTER_SOUND_ON,CameraShutterSound.CAMERA_SHUTTER_SOUND_OFF,CameraShutterSound.CAMERA_SHUTTER_SOUND_NONE])
    @Retention(AnnotationRetention.SOURCE)
    annotation class CameraShutterSound{
        companion object{
            /** 打开快门声音 */
            const val CAMERA_SHUTTER_SOUND_ON = 0
            const val CAMERA_SHUTTER_SOUND_OFF = 1
            const val CAMERA_SHUTTER_SOUND_NONE = -1
        }
    }

    @IntDef(value = [CameraNetGrid.CAMERA_NET_GRID_CLOSE,CameraNetGrid.CAMERA_NET_GRID_OPEN,CameraNetGrid.CAMERA_NET_GRID_NONE])
    @Retention(AnnotationRetention.SOURCE)
    annotation class CameraNetGrid{
        companion object{
            /** 相机网格线 */
            const val CAMERA_NET_GRID_CLOSE = 0
            const val CAMERA_NET_GRID_OPEN = 1
            const val CAMERA_NET_GRID_NONE = -1
        }
    }



    companion object{
        //480 * 320
        const val MIN_PREVIEW_PIXELS = 540 * 540 // normal screen
        const val MAX_EXPOSURE_COMPENSATION = 1.5f
        const val MIN_EXPOSURE_COMPENSATION = 0.0f
        const val MAX_ASPECT_DISTORTION = 0.15
    }
}