package com.zlcdgroup.mrsei.ui

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.os.Environment
import android.os.HandlerThread
import android.util.DisplayMetrics
import android.util.Log
import android.view.OrientationEventListener
import android.view.TextureView
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageButton
import androidx.camera.core.*
import com.akingyin.base.SimpleActivity
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.utils.AutoFitPreviewBuilder
import com.zlcdgroup.mrsei.utils.SensorHelp
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

/** Helper type alias used for analysis use case callbacks */
typealias LumaListener = (luma: Double) -> Unit
/**
 * @ Description:
 * @author king
 * @ Date 2019/7/16 17:56
 * @version V1.0
 */
class CameraXActivity  : SimpleActivity(), SensorEventListener {
    private  val TAG = "CameraXBasic"

    private lateinit var  imageButton : ImageButton
    private lateinit var mSensorManager :SensorManager
    private lateinit var  viewFind:TextureView
    private  lateinit var  displayManager :DisplayManager

    private   var mSensor :Sensor by Delegates.notNull()

    private  var mSensorHelp: SensorHelp by Delegates.notNull()

    private  lateinit var  preview :Preview
    private  lateinit var  imageCapture :ImageCapture
    private var imageAnalyzer: ImageAnalysis? = null

    private  var  orientationEventListener : OrientationEventListener by Delegates.notNull()
    private var displayId = -1
    override fun initInjection() {
    }

    override fun getLayoutId() = R.layout.activity_test_camera

    private   var  displayListener = object :DisplayManager.DisplayListener{
        override fun onDisplayChanged(displayId: Int) {
            if(displayId == this@CameraXActivity.displayId){
                preview.setTargetRotation(viewFind.display.rotation)
                imageCapture.setTargetRotation(viewFind.display.rotation)
                imageAnalyzer?.setTargetRotation(viewFind.display.rotation)
            }
        }

        override fun onDisplayAdded(displayId: Int) {
        }

        override fun onDisplayRemoved(displayId: Int) {
        }
    }

    var   lastdegree :Float = 0F
    override fun initializationData(savedInstanceState: Bundle?) {
        mSensorManager =getSystemService(Activity.SENSOR_SERVICE) as SensorManager
        displayManager = getSystemService(Activity.DISPLAY_SERVICE) as DisplayManager
        displayManager.registerDisplayListener(displayListener,null)
        //加速度
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mSensorHelp = SensorHelp(this)
        imageButton = findViewById(R.id.camera_capture_button)
        viewFind = findViewById(R.id.viewfinder)

        viewFind.post {
            displayId = viewFind.display.displayId
            updateCameraUi()
            bindCameraUseCases()

        }
        mSensorHelp.setOrientationChangeListener(object  :SensorHelp.OrientationChangeListener{
            override fun onChange(value: Float) {
                println("value="+value)
                if(lastdegree != value){
                    lastdegree = value
                    setViewRotation(imageButton,lastdegree)
                }
            }
        })
        orientationEventListener = object :OrientationEventListener(this){
            override fun onOrientationChanged(orientation: Int) {

            }
        }
    }

    override fun onSaveInstanceData(outState: Bundle?) {
    }

    override fun initView() {
    }

    override fun startRequest() {
    }


    private   fun  bindCameraUseCases(){
        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also { viewFind.display.getRealMetrics(it) }

        //val screenAspectRatio = Rational(metrics.widthPixels, metrics.heightPixels)
        val screenAspectRatio = AspectRatio.RATIO_16_9
        Log.d(TAG, "Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")
        // Set up the view finder use case to display camera preview

        //图像预览
        val viewFinderConfig = PreviewConfig.Builder().apply {
            setLensFacing(CameraX.LensFacing.BACK)
            // We request aspect ratio but no resolution to let CameraX optimize our use cases
            setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            setTargetRotation(viewFind.display.rotation)


        }.build()
        // Use the auto-fit preview builder to automatically handle size and orientation changes
        preview = AutoFitPreviewBuilder.build(viewFinderConfig, viewFind)


        //图像拍摄
        // Set up the capture use case to allow users to take photos
        val imageCaptureConfig = ImageCaptureConfig.Builder().apply {
            setLensFacing(CameraX.LensFacing.BACK)
            setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
            // We request aspect ratio but no resolution to match preview config but letting
            // CameraX optimize for whatever specific resolution best fits requested capture mode
            setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            setTargetRotation(viewFind.display.rotation)
        }.build()
        imageCapture = ImageCapture(imageCaptureConfig)

        // Setup image analysis pipeline that computes average pixel luminance in real time
        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            setLensFacing(CameraX.LensFacing.BACK)
            // Use a worker thread for image analysis to prevent preview glitches
            val analyzerThread = HandlerThread("LuminosityAnalysis").apply { start() }
            //setCallbackHandler(Handler(analyzerThread.looper))
            // In our analysis, we care more about the latest image than analyzing *every* image
            setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            setTargetRotation(viewFind.display.rotation)
        }.build()
        imageAnalyzer = ImageAnalysis(analyzerConfig)
        //图像分析
//        imageAnalyzer = ImageAnalysis(analyzerConfig).apply {
//            analyzer = LuminosityAnalyzer { luma ->
//                // Values returned from our analyzer are passed to the attached listener
//                // We log image analysis results here -- you should do something useful instead!
//                val fps = (analyzer as LuminosityAnalyzer).framesPerSecond
//                Log.d(TAG, "Average luminosity: $luma. " +
//                        "Frames per second: ${"%.01f".format(fps)}")
//            }
//        }


        // Apply declared configs to CameraX using the same lifecycle owner
        CameraX.bindToLifecycle(this,preview,imageCapture,imageAnalyzer)

    }


    private   fun   updateCameraUi(){

        findViewById<View>(R.id.camera_capture_button).setOnClickListener {
            // Create output file to hold the image
            val outputDirectory = Environment.getExternalStorageDirectory().absolutePath+ File.separator+"test"
            val photoFile = createFile(File(outputDirectory), FILENAME, PHOTO_EXTENSION)
            // Setup image capture metadata

            val metadata =  ImageCapture.Metadata().apply {
                // Mirror image when using the front camera
                isReversedHorizontal = false
            }

            val  executor = Executors.newSingleThreadExecutor()

            imageCapture.takePicture(photoFile,metadata,executor,onImageCapturedListener)
        }
    }


    val  onImageCapturedListener = object :ImageCapture.OnImageSavedListener{
        override fun onImageSaved(file: File) {
        }

        override fun onError(imageCaptureError: ImageCapture.ImageCaptureError, message: String, cause: Throwable?) {
        }
    }

    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL)
        mSensorHelp.onRegister()
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
        mSensorHelp.onUnregister()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {

        //手机移动一段时间后静止，然后静止一段时间后进行对焦
        // 读取加速度传感器数值，values数组0,1,2分别对应x,y,z轴的加速度
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

        }

    }


    private   var  view_rotate_animation = false
    private fun setViewRotation(view: View, ui_rotation: Float) {
        if (!view_rotate_animation) {
            view.rotation = ui_rotation

        }
        var rotate_by = ui_rotation - view.rotation
        if (rotate_by > 181.0f)
            rotate_by -= 360.0f
        else if (rotate_by < -181.0f)
            rotate_by += 360.0f
        // view.animate() modifies the view's rotation attribute, so it ends up equivalent to view.setRotation()
        // we use rotationBy() instead of rotation(), so we get the minimal rotation for clockwise vs anti-clockwise
        view.animate().rotationBy(rotate_by).setDuration(100).setInterpolator(AccelerateDecelerateInterpolator()).start()
    }

    override fun onDestroy() {
        displayManager.unregisterDisplayListener(displayListener)
        CameraX.unbindAll()
        super.onDestroy()
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
         * @param image image being analyzed VERY IMPORTANT: do not close the image, it will be
         * automatically closed after this method returns
         * @return the image analysis result
         */
        override fun analyze(image: ImageProxy, rotationDegrees: Int) {
            // If there are no listeners attached, we don't need to perform analysis
            if (listeners.isEmpty()) return

            // Keep track of frames analyzed
            frameTimestamps.push(System.currentTimeMillis())

            // Compute the FPS using a moving average
            while (frameTimestamps.size >= frameRateWindow) frameTimestamps.removeLast()
            framesPerSecond = 1.0 / ((frameTimestamps.peekFirst() -
                    frameTimestamps.peekLast())  / frameTimestamps.size.toDouble()) * 1000.0

            // Calculate the average luma no more often than every second
            if (frameTimestamps.first - lastAnalyzedTimestamp >= TimeUnit.SECONDS.toMillis(1)) {
                // Since format in ImageAnalysis is YUV, image.planes[0] contains the Y
                // (luminance) plane
                val buffer = image.planes[0].buffer

                // Extract image data from callback object
                val data = buffer.toByteArray()

                // Convert the data into an array of pixel values
                val pixels = data.map { it.toInt() and 0xFF }

                // Compute average luminance for the image
                val luma = pixels.average()

                // Call all listeners with new value
                listeners.forEach { it(luma) }

                lastAnalyzedTimestamp = frameTimestamps.first
            }
        }
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"

        /** Helper function used to create a timestamped file */
        private fun createFile(baseFolder: File, format: String, extension: String) =
                File(baseFolder, SimpleDateFormat(format, Locale.US)
                        .format(System.currentTimeMillis()) + extension)
    }

}