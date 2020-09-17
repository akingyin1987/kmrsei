/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.camerax

import android.annotation.SuppressLint

import android.content.Context
import android.content.Intent

import android.media.MediaActionSound
import android.media.MediaScannerConnection
import android.net.Uri
import android.util.DisplayMetrics
import android.view.View
import android.webkit.MimeTypeMap
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.lifecycle.LifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.akingyin.base.ext.no
import com.akingyin.base.ext.yes
import com.akingyin.media.camera.CameraAutoFouceSensorController
import com.akingyin.media.camera.CameraData
import com.akingyin.media.camera.CameraManager
import com.akingyin.media.camera.CameraParameBuild
import com.akingyin.media.camerax.callback.MotionFocusCall
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
import java.nio.ByteBuffer
import java.util.ArrayDeque
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


typealias LumaListener = (luma: Double) -> Unit
/**
 * @ Description:
 * @author king
 * @ Date 2020/9/15 15:47
 * @version V1.0
 */
class CameraxManager (var context: Context,var previewView: PreviewView){

    private val TAG = "camerax-manager"
    /** 拍照时相机旋转角度 */
    var cameraAngle = 90

    /** UI 显示旋转角度 */
    var cameraUiAngle = 0
    /** 是否在预览中 */
     private var previewing = false

    var motionFocusCall:MotionFocusCall?= null

     var cameraParameBuild: CameraParameBuild= CameraParameBuild()
    /** 运动对焦监听器 */
     private var cameraAutoFouceSensorController: CameraAutoFouceSensorController = CameraAutoFouceSensorController(context){
          if(!previewing){
              autoStartFuoce{  result,error->
                  motionFocusCall?.focusCall(result,error)
              }
          }
    }
    /** Blocking camera operations are performed using this executor */
    private  var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private  var cameraMainExecutor: Executor = ContextCompat.getMainExecutor(context)
    private  var mediaActionSound = MediaActionSound()
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null



    /**
     * 绑定相机参数
     */
    fun  bindCameraUseCases(lifecycleOwner:LifecycleOwner, callBack:()->Unit){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()


            // Get screen metrics used to setup camera for full screen resolution
            val metrics = DisplayMetrics().also { previewView.display.getRealMetrics(it) }
            Timber.d(TAG, "Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")

            val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
            Timber.d(TAG, "Preview aspect ratio: $screenAspectRatio")

            val rotation = previewView.display.rotation

            // CameraProvider
            val cameraProvider = cameraProvider
                ?: throw IllegalStateException("Camera initialization failed.")

            // CameraSelector
            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            // Preview
            preview = Preview.Builder()
                    // We request aspect ratio but no resolution
                    .setTargetAspectRatio(screenAspectRatio)
                    // Set initial target rotation
                    .setTargetRotation(rotation)
                    .build()


            // ImageCapture
            imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    // We request aspect ratio but no resolution to match preview config, but letting
                    // CameraX optimize for whatever specific resolution best fits our use cases
                    .setTargetAspectRatio(screenAspectRatio)
                    // Set initial target rotation, we will have to call this again if rotation changes
                    // during the lifecycle of this use case
                    .setTargetRotation(rotation)
                    .build()

            // ImageAnalysis
            imageAnalyzer = ImageAnalysis.Builder()
                    // We request aspect ratio but no resolution
                    .setTargetAspectRatio(screenAspectRatio)
                    // Set initial target rotation, we will have to call this again if rotation changes
                    // during the lifecycle of this use case
                    .setTargetRotation(rotation)
                    .build()
                    // The analyzer can then be assigned to the instance
                    .also {
                        it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                            // Values returned from our analyzer are passed to the attached listener
                            // We log image analysis results here - you should do something useful
                            // instead!
                            Timber.d(TAG, "Average luminosity: $luma")
                        })
                    }

            // Must unbind the use-cases before rebinding them
            cameraProvider.unbindAll()

            try {
                // A variable number of use-cases can be passed here -
                // camera provides access to CameraControl & CameraInfo
                camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, imageCapture, imageAnalyzer)

                // Attach the viewfinder's surface provider to preview use case

                preview?.setSurfaceProvider(previewView.createSurfaceProvider())
                callBack()
            } catch (exc: Exception) {
               exc.printStackTrace()
            }
        },cameraMainExecutor)
    }


    /**
     * 设置当前相机参数角度
     */
    @SuppressLint("RestrictedApi")
    fun  setCameraUseCasesRotation(view:View){
        preview?.targetRotation = view.display.rotation
        imageCapture?.targetRotation = view.display.rotation
        imageAnalyzer?.targetRotation = view.display.rotation
    }

    /**
     * 配制相机的个性化参数
     */
    fun  setCameraParame(cameraParameBuild: CameraParameBuild){
        this.cameraParameBuild = cameraParameBuild

    }

    /**
     * 开始预览
     */
    fun   startCameraPreview(){
        previewing = true
        cameraParameBuild.let {
            //设置运动对焦
            if(it.supportMoveFocus){
                cameraAutoFouceSensorController.onRegisterSensor()
            }else{
                cameraAutoFouceSensorController.unRegisterSensor()
            }
            setCameraFlashMode(it.flashModel)

        }
    }

    fun  stopCameraPreview(){
        previewing = false
        cameraAutoFouceSensorController.unRegisterSensor()
        camera?.cameraControl?.cancelFocusAndMetering()
    }

    /**
     * 拍照
     */
    fun takePicture(callBack: (result:Result<String>) -> Unit){
        // Create output file to hold the image
        val photoFile = File(cameraParameBuild.localPath)
        // Setup image capture metadata
        val metadata = ImageCapture.Metadata().apply {
            // Mirror image when using the front camera
            isReversedHorizontal = true
        }
        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
                .setMetadata(metadata)
                .build()
        if(cameraParameBuild.shutterSound == CameraManager.CameraShutterSound.CAMERA_SHUTTER_SOUND_ON){
            mediaActionSound.play(MediaActionSound.SHUTTER_CLICK)
        }
        imageCapture?.takePicture(outputOptions,cameraExecutor,object :ImageCapture.OnImageSavedCallback{
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                // so if you only target API level 24+ you can remove this statement
                // If the folder selected is an external media directory, this is
                // unnecessary but otherwise other apps will not be able to access our
                // images unless we scan them using [MediaScannerConnection]
                val mimeType = MimeTypeMap.getSingleton()
                        .getMimeTypeFromExtension(savedUri.toFile().extension)
                MediaScannerConnection.scanFile(
                        context,
                        arrayOf(savedUri.toFile().absolutePath),
                        arrayOf(mimeType)
                ) { _, uri ->
                    Timber.tag(TAG).d( "Image capture scanned into media store: $uri")
                }
                cameraMainExecutor.execute {
                    callBack.invoke(Result.success(savedUri.toFile().absolutePath))
                }

            }

            override fun onError(exception: ImageCaptureException) {
                cameraMainExecutor.execute {
                    callBack.invoke(Result.failure(CameraException(code = exception.imageCaptureError,errorMsg = "数据出错了,代码=${exception.imageCaptureError},${exception.message}",exception)))
                }

            }
        })?:cameraMainExecutor.execute {
            callBack.invoke(Result.failure(CameraException(errorMsg = "数据出错了")))
        }
    }


    /**
     * 设置相机闪光灯模式
     */
    fun   setCameraFlashMode( @CameraManager.CameraFlashModel flashModel:Int){
        when(flashModel){
            CameraManager.CameraFlashModel.CAMERA_FLASH_AUTO ->{
                imageCapture?.flashMode = ImageCapture.FLASH_MODE_AUTO
                camera?.cameraControl?.enableTorch(false)
            }
            CameraManager.CameraFlashModel.CAMERA_FLASH_ON->{
                camera?.cameraControl?.enableTorch(true)
                imageCapture?.flashMode = ImageCapture.FLASH_MODE_OFF
            }
            CameraManager.CameraFlashModel.CAMERA_FLASH_OFF->{
                imageCapture?.flashMode = ImageCapture.FLASH_MODE_OFF
                camera?.cameraControl?.enableTorch(false)
            }
        }

    }

    fun  getCameraMaxZoom()=camera?.cameraInfo?.zoomState?.value?.maxZoomRatio?:100F

    fun  getCameraMinZoom() = camera?.cameraInfo?.zoomState?.value?.minZoomRatio?:0F

    fun  getZoomRatio() = camera?.cameraInfo?.zoomState?.value?.zoomRatio?:0F

    /**
     * 设置相机的缩放比例
     */
    fun  setCameraZoom(zoomRatio:Float) = camera?.cameraControl?.setZoomRatio(zoomRatio)


    /**
     * 设置对焦
     */
    fun  setCameraFocus(x:Float, y:Float,callBack: (result : Boolean) -> Unit){
        if(focusing){
            callBack(false)
            return
        }
        focusing = true
        val point = previewView.meteringPointFactory.createPoint(x,y)
        val action = FocusMeteringAction.Builder(point,FocusMeteringAction.FLAG_AF)
                .setAutoCancelDuration(3,TimeUnit.SECONDS).build()
       camera?.cameraControl?.startFocusAndMetering(action)?.let {   future ->
         future.addListener({
            future.get().isFocusSuccessful.yes {

              cameraMainExecutor.execute {
                  focusing = false
                  callBack.invoke(true)
              }
            }.no {
                cameraMainExecutor.execute {
                    focusing = false
                    callBack.invoke(false)
                }
            }
         },cameraExecutor)

       }?:callBack(false).apply {
           focusing = false
       }

    }


    private  var  focusing = false

    /**
     * 这是自动对焦
     */
    fun autoStartFuoce(callBack: (result: Boolean, error: String?) -> Unit) {
        if(focusing){
            callBack(false,"正在对焦中")
            return
        }
        try {
            GlobalScope.launch(Dispatchers.Main) {
                delay(1000)
                withContext(Dispatchers.IO) {
                    setCameraFocus(previewView.width/2.toFloat(),previewView.height/2.toFloat()){
                        callBack(it,"")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            focusing = false

            callBack(false, e.message)
        }

    }

    fun unBindCamera(){
        // Shut down our background executor
        mediaActionSound.release()
        cameraExecutor.shutdown()
    }

    /**
     * Our custom image analysis class.
     *
     * <p>All we need to do is override the function `analyze` with our desired operations. Here,
     * we compute the average luminosity of the image by looking at the Y plane of the YUV frame.
     */
    private class LuminosityAnalyzer(listener: LumaListener? = null) : ImageAnalysis.Analyzer {
        private val frameRateWindow = 8
        private val frameTimestamps = ArrayDeque<Long>(5)
        private val listeners = ArrayList<LumaListener>().apply { listener?.let { add(it) } }
        private var lastAnalyzedTimestamp = 0L
        var framesPerSecond: Double = -1.0
            private set

        /**
         * Used to add listeners that will be called with each luma computed
         */
        fun onFrameAnalyzed(listener: LumaListener) = listeners.add(listener)

        /**
         * Helper extension function used to extract a byte array from an image plane buffer
         */
        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        /**
         * Analyzes an image to produce a result.
         *
         * <p>The caller is responsible for ensuring this analysis method can be executed quickly
         * enough to prevent stalls in the image acquisition pipeline. Otherwise, newly available
         * images will not be acquired and analyzed.
         *
         * <p>The image passed to this method becomes invalid after this method returns. The caller
         * should not store external references to this image, as these references will become
         * invalid.
         *
         * @param image image being analyzed VERY IMPORTANT: Analyzer method implementation must
         * call image.close() on received images when finished using them. Otherwise, new images
         * may not be received or the camera may stall, depending on back pressure setting.
         *
         */
        override fun analyze(image: ImageProxy) {
            // If there are no listeners attached, we don't need to perform analysis
            if (listeners.isEmpty()) {
                image.close()
                return
            }

            // Keep track of frames analyzed
            val currentTime = System.currentTimeMillis()
            frameTimestamps.push(currentTime)

            // Compute the FPS using a moving average
            while (frameTimestamps.size >= frameRateWindow) frameTimestamps.removeLast()
            val timestampFirst = frameTimestamps.peekFirst() ?: currentTime
            val timestampLast = frameTimestamps.peekLast() ?: currentTime
            framesPerSecond = 1.0 / ((timestampFirst - timestampLast) /
                    frameTimestamps.size.coerceAtLeast(1).toDouble()) * 1000.0

            // Analysis could take an arbitrarily long amount of time
            // Since we are running in a different thread, it won't stall other use cases

            lastAnalyzedTimestamp = frameTimestamps.first

            // Since format in ImageAnalysis is YUV, image.planes[0] contains the luminance plane
            val buffer = image.planes[0].buffer

            // Extract image data from callback object
            val data = buffer.toByteArray()

            // Convert the data into an array of pixel values ranging 0-255
            val pixels = data.map { it.toInt() and 0xFF }

            // Compute average luminance for the image
            val luma = pixels.average()

            // Call all listeners with new value
            listeners.forEach { it(luma) }

            image.close()
        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }


    companion object{
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
        /** 新增照片*/
        const val KEY_CAMERA_PHOTO_ADD_ACTION="android.camera.photo.add.action"
        /**删除照片 */
        const val KEY_CAMERA_PHOTO_DELECT_ACTION="android.camera.photo.delect.action"
        /**拍照完成 */
        const val KEY_CAMERA_PHOTO_COMPLETE_ACTION="android.camera.photo.complete.action"
        /** 拍照取消 */
        const val KEY_CAMERA_PHOTO_CANCEL_ACTION="android.camera.photo.cancel.action"

        fun  sendAddTakePhoto(filePath:String,context: Context){
            LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(KEY_CAMERA_PHOTO_ADD_ACTION).apply {
                putExtra("filePath",filePath)
            })
        }
        fun  sendDelectTakePhoto(filePath:String,context: Context){
            LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(KEY_CAMERA_PHOTO_DELECT_ACTION).apply {
                putExtra("filePath",filePath)
            })
        }

        fun  sendTakePhotoComplete(cameraData: CameraData,context: Context){
            LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(KEY_CAMERA_PHOTO_COMPLETE_ACTION).apply {
               if(cameraData.supportMultiplePhoto){
                   putExtra("filePaths",cameraData.cameraPhotoDatas.keys.toTypedArray())
               }else{
                   putExtra("filePath",cameraData.localPath)
               }
                putExtra("cameraData",cameraData)
            })
        }

        fun  sendTakePhtotCancel(context: Context){
            LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(KEY_CAMERA_PHOTO_CANCEL_ACTION))
        }
    }
}