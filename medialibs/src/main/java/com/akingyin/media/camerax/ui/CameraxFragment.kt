/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.camerax.ui


import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.akingyin.base.SimpleFragment
import com.akingyin.base.ext.*
import com.akingyin.base.mvvm.SingleLiveEvent
import com.akingyin.base.utils.PreferencesUtil
import com.akingyin.base.utils.RandomUtil
import com.akingyin.media.MediaConfig
import com.akingyin.media.R
import com.akingyin.media.camera.*
import com.akingyin.media.camera.ui.CameraSettingActivity
import com.akingyin.media.camerax.CameraxManager
import com.akingyin.media.camerax.CameraxSurfaceView
import com.akingyin.media.camerax.GalleryDataVo
import com.akingyin.media.camerax.callback.MotionFocusCall
import com.akingyin.media.databinding.FragmentCameraxBinding
import com.akingyin.media.engine.LocationEngine
import com.akingyin.media.engine.LocationManagerEngine
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import permissions.dispatcher.ktx.LocationPermission
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructLocationPermissionRequest
import java.io.File
import kotlin.properties.Delegates


/**
 * camerax
 * @ Description:
 * @author king
 * @ Date 2020/9/14 16:34
 * @version V1.0
 */
open class CameraxFragment : SimpleFragment() {
    lateinit var bindView: FragmentCameraxBinding
    lateinit var cameraxManager: CameraxManager
    lateinit var sharedPreferencesName: String
    lateinit var locationPermissionsRequester: PermissionsRequester
    private val locationEngine : LocationEngine? by lazy {
        locationEngineManager?.createEngine()
    }



    private var cameraParameBuild :CameraParameBuild  by Delegates.notNull()
    private var bindCameraInit = false
    lateinit var  displayManager:DisplayManager
    private var displayId = -1
    lateinit var cameraSensorController: CameraSensorController
    var cameraLiveData: SingleLiveEvent<CameraData> = SingleLiveEvent()
    /** AndroidX navigation arguments */
    private val args: CameraxFragmentArgs by navArgs()
    override fun injection() {

    }

    override fun getLayoutId() = R.layout.fragment_camerax

    override fun useViewBind() = true
    override fun initViewBind(inflater: LayoutInflater, container: ViewGroup?): View? {
        println("cameraXFragemnt")
        bindView = FragmentCameraxBinding.inflate(inflater, container, false)
        locationIsEnable.observe(viewLifecycleOwner,{
            bindView.tvLocation.visibility = if(it){View.VISIBLE} else{View.GONE}
        })
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,object : OnBackPressedCallback(true){
//            override fun handleOnBackPressed() {
//
//                requireActivity().finish()
//            }
//        })
        return bindView.root
    }


    override fun initEventAndData() {
        cameraxManager = CameraxManager(requireContext(),bindView.viewFinder.camera_surface)
        sharedPreferencesName = args.sharedPreferencesName
        cameraParameBuild =  CameraParameBuild()
        cameraParameBuild.localPath = if(args.fileName.isNullOrEmpty()){""}else{args.fileDir+File.separator+args.fileName}
        cameraParameBuild.saveFileDir = args.fileDir
        println("传递的参数")
        initCameraParame(cameraParameBuild)
    }
    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit
        @SuppressLint("RestrictedApi")
        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
            if (displayId == this@CameraxFragment.displayId) {
               cameraxManager.setCameraUseCasesRotation(view)
            }
        } ?: Unit
    }


    override fun initView() {
        println("注册广播")
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(volumeKeyControlBroadcast, IntentFilter().apply {
            addAction(CameraManager.KEYDOWN_VOLUME_KEY_ACTION)
        })
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(cameraInfoBroadcastReceiver,IntentFilter().apply {

            addAction(CameraxManager.KEY_PUSH_TAKE_PHOTO_PATH)
            addAction(CameraxManager.KEY_PUSH_TAKE_PHOTO_ALL_PATH)
        })

        cameraSensorController = CameraSensorController(requireContext(), requireContext().display?.rotation?:0)
        cameraSensorController.mOrientationChangeListener = object : CameraSensorController.OrientationChangeListener {
            override fun onChange(relativeRotation: Int, uiRotation: Int) {
                var cameraRotation = uiRotation + 90
                if (cameraRotation == 180) {
                    cameraRotation = 0
                }
                if (cameraRotation == 360) {
                    cameraRotation = 180
                }

                if(cameraxManager.cameraAngle != cameraRotation){
                    cameraxManager.cameraAngle = cameraRotation
                }
                if(cameraxManager.cameraUiAngle != cameraRotation){
                    cameraxManager.cameraUiAngle = cameraRotation
                    CameraManager.startCameraViewRoteAnimator((uiRotation).toFloat(),bindView.buttonShutter,bindView.buttonSetting,bindView.buttonFlash,
                            bindView.buttonGrid,bindView.textCountDown)
                }
            }
        }


        cameraxManager.motionFocusCall = object :MotionFocusCall{
            override fun focusCall(result: Boolean, error: String?) {
                if(result){
                    focusSucessCaptureImage()
                }

            }
        }
        displayManager = requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        displayManager.registerDisplayListener(displayListener, null)
        bindView.viewFinder.camera_surface.onSurfaceViewListion = object :CameraxSurfaceView.OnSurfaceViewListion{
            override fun onFouceClick(x: Float, y: Float) {
                bindView.viewFinder.camera_fouce.setTouchFoucusRect(cameraxManager,x,y)
            }
        }
        bindView.viewFinder.camera_surface.pinchToZoomGestureDetector = PinchToZoomGestureDetector(requireContext(), MyScaleGestureDetector(),object :PinchToZoomGestureDetector.OnCamerZoomListion{
           override fun getZoomRatio() = cameraxManager.getZoomRatio()
           override fun getMaxZoomRatio() = cameraxManager.getCameraMaxZoom()

           override fun getMinZoomRatio() = cameraxManager.getCameraMinZoom()

           override fun setZoomRatio(zoom: Float) {
             cameraxManager.setCameraZoom(zoom)
           }

           override fun isZoomSupported() = true

           override fun isPinchToZoomEnabled()= true
       })
        bindView.viewFinder.camera_surface.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View?) {
                displayManager.registerDisplayListener(displayListener,null)
            }

            override fun onViewDetachedFromWindow(v: View?) {
                displayManager.unregisterDisplayListener(displayListener)
            }
        })
        bindView.viewFinder.camera_surface.post {
            // Keep track of the display in which this view is attached
            displayId =  bindView.viewFinder.camera_surface.display.displayId

            cameraxManager.setCameraParame(cameraParameBuild)
            cameraxManager.bindCameraUseCases(this){
                bindView.rulerView.valueFrom = cameraxManager.getCameraMinZoom()
                if(cameraxManager.getCameraMaxZoom() > 0){
                    bindView.rulerView.valueTo = cameraxManager.getCameraMaxZoom()
                }

                bindView.rulerView.value = cameraxManager.getZoomRatio()
            }
            updateCameraUi()
            cameraxManager.startCameraPreview()
        }
        bindView.buttonSetting.click {
            startCameraSettingActivity()
        }

    }



    var thumbnail :ImageButton ?= null
    var cancelButton :ImageButton?=null
    private  fun  updateCameraUi(){
       bindView.root.findViewById<ConstraintLayout>(R.id.camera_ui_container)?.let {
           bindView.root.removeView(it)
       }
        // Inflate a new view containing all UI for controlling the camera
       val  controls = View.inflate(requireContext(), R.layout.camera_ui_container,  bindView.root)
        controls.findViewById<ImageButton>(R.id.camera_capture_button).click {
            captureImage()
        }
        thumbnail = controls.findViewById(R.id.photo_view_button)
        CameraxManager.getTakePhotoPath(requireContext())
        controls.findViewById<ImageButton>(R.id.photo_view_button).click {
           CameraxManager.getTakePhotoPath(requireContext(),true)
        }
        controls?.findViewById<ImageButton>(R.id.camera_switch_button)?.click {
            CameraxManager.sendTakePhtotCancel(requireContext())
        }
        cancelButton = controls?.findViewById(R.id.camera_switch_button)
        if(cameraParameBuild.supportMultiplePhoto){
            cancelButton?.visiable()
            thumbnail?.visiable()
        }else{
            cancelButton?.gone()
            thumbnail?.gone()
        }

        bindView.tvLocation.click {
            locationPermissionsRequester.launch()
        }
        bindView.rulerView.addOnChangeListener { _, value, fromUser ->
            if(fromUser){
                showCameraZoomBar(value)
            }
        }
        bindView.buttonGrid.click {
            toggleGrid()
        }
        bindView.buttonShutter.click {
            toggleShutter()
        }
        bindView.buttonFlash.click {
            bindView.layoutFlashOptions.circularReveal(it)
        }
        bindView.buttonFlashAuto.click {
            closeFlashAndSelect(CameraManager.CameraFlashModel.CAMERA_FLASH_AUTO)
        }
        bindView.buttonFlashOn.click {
            closeFlashAndSelect(CameraManager.CameraFlashModel.CAMERA_FLASH_ON)
        }
        bindView.buttonFlashOff.click {
            closeFlashAndSelect(CameraManager.CameraFlashModel.CAMERA_FLASH_OFF)
        }
        lifecycleScope.launchWhenResumed {
            if(bindView.tvLocation.isVisible){
                getLocationInfo()
            }
        }
    }
    private fun closeFlashAndSelect(@CameraManager.CameraFlashModel flash: Int) = bindView.layoutFlashOptions.circularClose(bindView.buttonFlash) {
        flashMode = flash
        cameraxManager.setCameraFlashMode(flash)

    }


    private  fun  toggleShutter(){
        bindView.buttonShutter.toggleButton(shutterSound == CameraManager.CameraShutterSound.CAMERA_SHUTTER_SOUND_ON,
                180F,
                R.drawable.ic_action_volume_off,
                R.drawable.ic_action_volume_on){
            flag ->
            shutterSound = if (flag) CameraManager.CameraShutterSound.CAMERA_SHUTTER_SOUND_ON else CameraManager.CameraShutterSound.CAMERA_SHUTTER_SOUND_OFF

        }
    }

    private fun toggleGrid() {
        bindView.buttonGrid.toggleButton(
                netGrid == CameraManager.CameraNetGrid.CAMERA_NET_GRID_OPEN,
                180f,
                R.drawable.ic_grid_off,
                R.drawable.ic_grid_on
        ) { flag ->
            netGrid = if (flag) CameraManager.CameraNetGrid.CAMERA_NET_GRID_OPEN else CameraManager.CameraNetGrid.CAMERA_NET_GRID_CLOSE

        }
    }
    private  fun   getLocationInfo(){
        locationEngine?.let {
            it.getNewLocation(CameraManager.LocalType.CORR_TYPE_BD09,cameraParameBuild.lat,cameraParameBuild.lng){
                lat, lng, addr ->
                cameraParameBuild.locType = CameraManager.LocalType.CORR_TYPE_BD09
                cameraParameBuild.lat = lat
                cameraParameBuild.lng = lng
                bindView.tvLocation.text = addr
            }
        }?:showError("暂不支持定位信息")
    }
    override fun lazyLoad() {

    }

    /**
     * 对焦成功，自动拍照
     */
    private  fun   focusSucessCaptureImage(){
        if(cameraParameBuild.supportFocesedAutoPhoto){
            countDownStop()
            if(cameraParameBuild.focesedAutoPhotoDelayTime>0 ){

                countDownStart(cameraParameBuild.focesedAutoPhotoDelayTime){
                    captureImage()
                }
            }else{
                captureImage()
            }
        }

    }

    private   var  countDownJob: Job?= null
    /**
     * 开始倒计时
     */
    private fun  countDownStart(count:Int,call:()->Unit){
        bindView.textCountDownTip.text = "自动拍照"
        countDownJob = lifecycleScope.launch(Dispatchers.Main){
            for (i in count downTo 1){
                bindView.textCountDown.text = i.toString()
                delay(1000)
            }
            bindView.textCountDown.text=""
            bindView.textCountDownTip.text = ""
            call.invoke()
        }

    }

    /**
     *取消倒计时
     */
    private fun countDownStop(){
        bindView.textCountDown.text=""
        bindView.textCountDownTip.text = ""
        countDownJob?.cancel()
    }

    private var netGrid: Int by Delegates.observable(CameraManager.CameraNetGrid.CAMERA_NET_GRID_NONE) { _, _, newValue ->

        bindView.buttonGrid.setImageResource(when (newValue) {
            CameraManager.CameraNetGrid.CAMERA_NET_GRID_OPEN -> {
                bindView.groupGridLines.visiable()
                PreferencesUtil.put(sharedPreferencesName, CameraManager.KEY_CAMERA_GRID, true)
                R.drawable.ic_grid_on

            }
            else -> {
                PreferencesUtil.put(sharedPreferencesName, CameraManager.KEY_CAMERA_GRID, false)
                bindView.groupGridLines.gone()
                R.drawable.ic_grid_off
            }
        })

    }


    private var flashMode: Int by Delegates.observable(CameraManager.CameraFlashModel.CAMERA_FLASH_NONE) { _, _, newValue ->
        PreferencesUtil.put(sharedPreferencesName, CameraManager.KEY_CAMERA_FLASH, newValue.toString())
        bindView.buttonFlash.setImageResource(when (newValue) {
            CameraManager.CameraFlashModel.CAMERA_FLASH_ON -> {
                R.drawable.ic_flash_on
            }
            CameraManager.CameraFlashModel.CAMERA_FLASH_OFF -> {
                R.drawable.ic_flash_off
            }
            else -> {
                R.drawable.ic_flash_auto
            }
        })
    }


    private var shutterSound: Int by Delegates.observable(CameraManager.CameraShutterSound.CAMERA_SHUTTER_SOUND_NONE) { _, _, newValue ->
        bindView.buttonShutter.setImageResource(when (newValue) {
            CameraManager.CameraShutterSound.CAMERA_SHUTTER_SOUND_ON -> {
                PreferencesUtil.put(sharedPreferencesName, CameraManager.KEY_CAMERA_SHUTTER_SOUND, true)
                R.drawable.ic_action_volume_on
            }
            else ->{
                PreferencesUtil.put(sharedPreferencesName, CameraManager.KEY_CAMERA_SHUTTER_SOUND, false)
                R.drawable.ic_action_volume_off
            }
        })
    }



    private  var  volumeKeyControlBroadcast : BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if(it.action == CameraManager.KEYDOWN_VOLUME_KEY_ACTION){

                    when(it.getIntExtra("keyCode",0)){
                        KeyEvent.KEYCODE_VOLUME_DOWN ->{
                            keyVolumeDown()
                        }
                        KeyEvent.KEYCODE_VOLUME_UP ->{
                            keyVolumeUp()
                        }
                        KeyEvent.KEYCODE_CAMERA ->{
                            captureImage()
                        }
                    }
                }
            }
        }
    }


    private  var  cameraInfoBroadcastReceiver : BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            intent?.let {
                println("fragment 收到相机广播信息--->${it.action}")
               when(it.action){

                   CameraxManager.KEY_PUSH_TAKE_PHOTO_PATH->{
                       println("data->${it.extras}")
                        it.getStringExtra("localPath")?.let {path->
                            setGalleryThumbnail(File(path).toUri())
                        }?:setGalleryThumbnail(null)
                   }
                   CameraxManager.KEY_PUSH_TAKE_PHOTO_ALL_PATH->{
                      it.getStringArrayExtra("paths")?.let {
                          paths->
                          if(paths.isEmpty()){
                              showError("没有可查看的照片")
                          }else{
                              findNavController().navigate(CameraxFragmentDirections.actionCameraToGallery(GalleryDataVo().apply {
                                  data =paths.toList()
                              }))
                          }

                      }
                   }
                   else -> {}
               }
            }
        }
    }
    open    fun   initCameraParame(cameraParame: CameraParameBuild = cameraParameBuild, changeCameraParme:Boolean = false){
       CameraManager.readCameraParame(cameraParame,sharedPreferencesName)

        netGrid = cameraParame.netGrid
        flashMode = cameraParame.flashModel
        shutterSound = cameraParame.shutterSound
        if(cameraParame.supportLocation){
            bindView.tvLocation.visiable()
        }else{
            bindView.tvLocation.gone()
        }
        if(cameraParame.supportMultiplePhoto){
            cancelButton?.visiable()
            thumbnail?.visiable()
        }else{
            cancelButton?.gone()
            thumbnail?.gone()
        }
        cameraxManager.setCameraParame(cameraParame)

    }
    private lateinit var startActivitylaunch: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         startActivitylaunch =  registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if( it.resultCode == Activity.RESULT_OK){
                initCameraParame(changeCameraParme = true)

            }
        }
        locationPermissionsRequester = constructLocationPermissionRequest(LocationPermission.FINE,LocationPermission.COARSE){
            getLocationInfo()
        }
    }

    open  fun    startCameraSettingActivity(){
       startActivitylaunch.launch(Intent(requireContext(), CameraSettingActivity::class.java).apply {

            putExtra("sharedPreferencesName",sharedPreferencesName)
            putExtra("cameraOld",false)
            putExtra("cameraX",true)
        })

    }
    open  fun  keyVolumeDown(){

    }

    open fun  keyVolumeUp(){

    }

    open  fun  captureImage(){
        cameraParameBuild.cameraAngle =cameraxManager.cameraAngle
        cameraxManager.setCameraParame(cameraParameBuild)
        if(cameraParameBuild.supportMultiplePhoto){
            cameraParameBuild.localPath = cameraParameBuild.saveFileDir+File.separator+RandomUtil.randomUUID+".jpg"
        }
        cameraxManager.takePicture {
           if(it.isFailure){
               showError(it.exceptionOrNull()?.message)
           }else if(it.isSuccess){
               if(cameraParameBuild.supportAutoSavePhoto){
                   if(cameraParameBuild.autoSavePhotoDelayTime==0){
                       setGalleryThumbnail(Uri.parse(it.getOrDefault("")))
                       cameraLiveData.value = CameraData().apply {
                           if(cameraParameBuild.supportMultiplePhoto){
                               supportMultiplePhoto = true
                               cameraPhotoDatas.add(it.getOrDefault(""))
                           }else{
                               supportMultiplePhoto = false
                               mediaType = MediaConfig.TYPE_IMAGE
                               localPath = cameraParameBuild.localPath
                           }
                       }
                       return@takePicture
                   }
               }
               findNavController().navigate(CameraxFragmentDirections.actionCameraToPhoto(it.getOrDefault(""),sharedPreferencesName))
           }

        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
           mView.postDelayed({
               mView.foreground = ColorDrawable(Color.WHITE)
               mView.postDelayed({
                   mView.foreground = null
               },ANIMATION_FAST_MILLIS)
           },ANIMATION_SLOW_MILLIS)
        }
    }
    private fun setGalleryThumbnail(uri: Uri?) {
        if(cameraParameBuild.supportMultiplePhoto){
            thumbnail?.post {
                thumbnail?.let {
                    it.setPadding(resources.getDimension(R.dimen.stroke_small).toInt())
                    Glide.with(it)
                            .load(uri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(it)
                }

            }
        }

    }
    override fun onPause() {
        super.onPause()
        cameraSensorController.onPause()

    }

    override fun onResume() {
        super.onResume()

        if(allPermissionsGranted()){
            onPermissionGranted()
        }else{
            bindCameraInit = false
            findNavController().navigate(CameraxFragmentDirections.actionCameraToPermissions(args.fileDir,args.fileName,args.sharedPreferencesName))
        }

        cameraSensorController.onResume()
    }

    // Check for the permissions
    private fun allPermissionsGranted() = cameraPermissions.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }


    private fun onPermissionGranted() {
        if(!bindCameraInit){
           bindView.viewFinder.camera_surface.let {   vf ->
             vf.post {
                 displayId = vf.display.displayId
             }
           }
        }
        bindCameraInit = true

    }
    private   var  zoomBarJob:Job?=null
    private  fun  showCameraZoomBar(zoom:Float){
        zoomBarJob?.cancel()
        zoomBarJob = lifecycleScope.launch(Dispatchers.Main) {
            cameraxManager.setCameraZoom(zoom)
            bindView.rulerView.visiable()
            bindView.rulerView.value = zoom
            delay(5000)
            bindView.rulerView.gone()
        }
    }

    override fun close() {
        locationEngine?.cancelLocation()
        super.close()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Shut down our background executor
        println("onDestroyView->>>cameraFragment")
        cameraxManager.unBindCamera()
        cameraxManager.stopCameraPreview()
        // Unregister the broadcast receivers and listeners
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(volumeKeyControlBroadcast)
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(cameraInfoBroadcastReceiver)
        displayManager.unregisterDisplayListener(displayListener)
    }

    companion object{
        const val ANIMATION_FAST_MILLIS = 50L
        const val ANIMATION_SLOW_MILLIS = 100L
        private var  locationEngineManager:LocationManagerEngine?=null
        private var  locationIsEnable:MutableLiveData<Boolean> = MutableLiveData(false)
        fun setCameraXLocationEngine(locationManagerEngine: LocationManagerEngine?){
            locationEngineManager = locationManagerEngine
            locationIsEnable.postValue(locationManagerEngine != null)
        }


    }
}