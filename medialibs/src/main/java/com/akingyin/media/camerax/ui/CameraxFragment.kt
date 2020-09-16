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
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.akingyin.base.SimpleFragment
import com.akingyin.base.ext.click
import com.akingyin.base.ext.gone
import com.akingyin.base.ext.visiable
import com.akingyin.base.utils.FileUtils
import com.akingyin.base.utils.PreferencesUtil
import com.akingyin.base.utils.StringUtils
import com.akingyin.media.R
import com.akingyin.media.camera.CameraManager
import com.akingyin.media.camera.CameraParameBuild
import com.akingyin.media.camera.MyScaleGestureDetector
import com.akingyin.media.camera.PinchToZoomGestureDetector
import com.akingyin.media.camera.ui.BaseCameraFragment
import com.akingyin.media.camera.ui.CameraSettingActivity
import com.akingyin.media.camera.widget.CaptureButton
import com.akingyin.media.camerax.CameraxManager
import com.akingyin.media.databinding.FragmentCameraxBinding
import com.akingyin.media.engine.LocationEngine
import com.akingyin.media.engine.LocationManagerEngine
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    var locationEngine : LocationEngine?=null
    private var cameraParameBuild :CameraParameBuild  by Delegates.notNull()
    private var bindCameraInit = false
    lateinit var  displayManager:DisplayManager
    private var displayId = -1
    /** AndroidX navigation arguments */
    private val args: CameraxFragmentArgs by navArgs()
    override fun injection() {

    }

    override fun getLayoutId() = R.layout.fragment_camerax

    override fun useViewBind() = true
    override fun initViewBind(inflater: LayoutInflater, container: ViewGroup?): View? {
        bindView = FragmentCameraxBinding.inflate(inflater, container, false)
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,object : OnBackPressedCallback(true){
//            override fun handleOnBackPressed() {
//
//                requireActivity().finish()
//            }
//        })
        return bindView.root
    }
    override fun initEventAndData() {

        sharedPreferencesName = arguments?.getString("sharedPreferencesName", "app_setting")
            ?: "app_setting"
        cameraParameBuild = arguments?.getParcelable("data") ?: CameraParameBuild()
        cameraParameBuild.localPath = args.fileDir+ File.separator+args.fileName
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
        cameraxManager = CameraxManager(requireContext(),bindView.viewFinder.camera_surface)
        displayManager = requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        displayManager.registerDisplayListener(displayListener, null)
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
            updateCameraUi()
            cameraxManager.bindCameraUseCases(this){

            }
        }

    }


    private  fun  updateCameraUi(){

        bindView.tvLocation.click {

        }
       bindView.fabTakePicture.captureLisenter = object : CaptureButton.onClickTakePicturesListener() {
           override fun takePictures() {
               bindView.fabTakePicture.isEnabled = false
               captureImage()
           }
       }
    }

    override fun lazyLoad() {

    }




    private var netGrid: Int by Delegates.observable(CameraManager.CameraNetGrid.CAMERA_NET_GRID_NONE) { _, _, newValue ->

        bindView.buttonGrid.setImageResource(when (newValue) {
            CameraManager.CameraNetGrid.CAMERA_NET_GRID_OPEN -> {
                bindView.groupGridLines.visiable()
                PreferencesUtil.put(sharedPreferencesName, BaseCameraFragment.KEY_CAMERA_GRID, true)
                R.drawable.ic_grid_on

            }
            else -> {
                PreferencesUtil.put(sharedPreferencesName, BaseCameraFragment.KEY_CAMERA_GRID, false)
                bindView.groupGridLines.gone()
                R.drawable.ic_grid_off
            }
        })

    }


    private var flashMode: Int by Delegates.observable(CameraManager.CameraFlashModel.CAMERA_FLASH_NONE) { _, _, newValue ->
        PreferencesUtil.put(sharedPreferencesName, BaseCameraFragment.KEY_CAMERA_FLASH, newValue.toString())
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
                PreferencesUtil.put(sharedPreferencesName, BaseCameraFragment.KEY_CAMERA_SHUTTER_SOUND, true)
                R.drawable.ic_action_volume_on
            }
            else ->{
                PreferencesUtil.put(sharedPreferencesName, BaseCameraFragment.KEY_CAMERA_SHUTTER_SOUND, false)
                R.drawable.ic_action_volume_off
            }
        })
    }



    private  var  volumeKeyControlBroadcast : BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if(it.action == BaseCameraFragment.KEYDOWN_VOLUME_KEY_ACTION){

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

    open    fun   initCameraParame(cameraParame: CameraParameBuild = cameraParameBuild, changeCameraParme:Boolean = false){
        cameraParame.apply {
            this.flashModel = PreferencesUtil.get(sharedPreferencesName, BaseCameraFragment.KEY_CAMERA_FLASH, "0").toInt()
            this.shutterSound = if(PreferencesUtil.get(sharedPreferencesName, BaseCameraFragment.KEY_CAMERA_SHUTTER_SOUND,true)) CameraManager.CameraShutterSound.CAMERA_SHUTTER_SOUND_ON else CameraManager.CameraShutterSound.CAMERA_SHUTTER_SOUND_OFF
            this.horizontalPicture = PreferencesUtil.get(sharedPreferencesName, BaseCameraFragment.KEY_CAMERA_PHTOT_HORIZONTAL,false)
            this.netGrid = if(PreferencesUtil.get(sharedPreferencesName, BaseCameraFragment.KEY_CAMERA_GRID,false)) CameraManager.CameraNetGrid.CAMERA_NET_GRID_OPEN else CameraManager.CameraNetGrid.CAMERA_NET_GRID_CLOSE
            this.cameraResolution = Point().apply {
                x = PreferencesUtil.get(sharedPreferencesName, BaseCameraFragment.KEY_CAMERAX_RESOLUTION_X,0)
                y = PreferencesUtil.get(sharedPreferencesName, BaseCameraFragment.KEY_CAMERAX_RESOLUTION_Y,0)
            }

            this.supportMoveFocus = PreferencesUtil.get(sharedPreferencesName, BaseCameraFragment.KEY_CAMERA_MOVE_AUTO_FOCUS,false)
            this.autoSavePhotoDelayTime = PreferencesUtil.get(sharedPreferencesName, BaseCameraFragment.KEY_CAMERA_AUTO_SAVE_DELAYTIME,"0").toInt()
            this.focesedAutoPhotoDelayTime = PreferencesUtil.get(sharedPreferencesName, BaseCameraFragment.KEY_CAMERA_AUTO_TAKEPHOTO_DELAYTIME,"0").toInt()
            this.supportAutoSavePhoto = PreferencesUtil.get(sharedPreferencesName, BaseCameraFragment.KEY_CAMERA_AUTOSAVE_TAKEPHOTO,false)
            this.supportFocesedAutoPhoto = PreferencesUtil.get(sharedPreferencesName, BaseCameraFragment.KEY_CAMERA_FOCUS_TAKEPHOTO,false)
            this.supportManualFocus = PreferencesUtil.get(sharedPreferencesName, BaseCameraFragment.KEY_CAMERA_MANUAL_AUTO_FOCUS,false)
            this.supportLocation = PreferencesUtil.get(sharedPreferencesName, BaseCameraFragment.KEY_CAMERA_LOCATION,false)
            this.volumeKeyControl = PreferencesUtil.get(sharedPreferencesName, BaseCameraFragment.KEY_CAMERA_VOLUME_KEY_CONTROL,"0").toInt()
        }
        println("相机参数：$cameraParame")
        netGrid = cameraParame.netGrid
        flashMode = cameraParame.flashModel
        shutterSound = cameraParame.shutterSound
        if(cameraParame.supportLocation){
            bindView.tvLocation.visiable()
        }else{
            bindView.tvLocation.gone()
        }

    }
    open  fun    startCameraSettingActivity(){
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

            if( it.resultCode == Activity.RESULT_OK){
                initCameraParame(changeCameraParme = true)
            }
        }.launch(Intent(requireContext(), CameraSettingActivity::class.java).apply {

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
        cameraxManager.takePicture {
           if(it.isFailure){

               showError(it.exceptionOrNull()?.message)
           }else if(it.isSuccess){
               Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(CameraxFragmentDirections.actionCameraToPhoto(it.getOrDefault("")))
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

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(volumeKeyControlBroadcast, IntentFilter().apply {
            addAction(BaseCameraFragment.KEYDOWN_VOLUME_KEY_ACTION)
        })
        if(allPermissionsGranted()){
            onPermissionGranted()
        }else{
            bindCameraInit = false
            Navigation.findNavController(requireActivity(),R.id.fragment_container).navigate(CameraxFragmentDirections.actionCameraToPermissions().apply {
                arguments.apply {
                    putString("fileName",FileUtils.getFileName(cameraParameBuild.localPath))
                    putString("fileDir",FileUtils.getFolderName(cameraParameBuild.localPath))
                }
            })
//            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
//                //通过的权限
//                val grantedList = it.filterValues {
//                    value->
//                    value
//                }.mapNotNull {
//                    value->
//                    value.key
//                }
//                //是否所有权限都通过
//                val allGranted = grantedList.size == it.size
//                val list = (it - grantedList).map { it.key }
//                //未通过的权限
//                val deniedList = list.filter {
//                    ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), it)
//                }
//                //拒绝并且点了“不再询问”权限
//               val alwaysDeniedList = list - deniedList
//                if(allGranted){
//                    onPermissionGranted()
//                }else{
//                    mView.let { v ->
//                        Snackbar.make(v, "拍照没有这些权限将无法运行！$alwaysDeniedList", Snackbar.LENGTH_INDEFINITE)
//                                .setAction("退出") { ActivityCompat.finishAffinity(requireActivity()) }
//                                .show()
//                    }
//                }
//            }.launch(cameraPermissions)

        }
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



    override fun onDestroyView() {
        super.onDestroyView()
        // Shut down our background executor
        cameraxManager.unBindCamera()

        // Unregister the broadcast receivers and listeners
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(volumeKeyControlBroadcast)
        displayManager.unregisterDisplayListener(displayListener)
    }

    companion object{
        const val ANIMATION_FAST_MILLIS = 50L
        const val ANIMATION_SLOW_MILLIS = 100L

        fun newInstance(cameraParameBuild: CameraParameBuild, sharedPreferencesName: String = "app_setting", locationManagerEngine: LocationManagerEngine?=null): CameraxFragment {
            return CameraxFragment().apply {
                locationEngine = locationManagerEngine?.createEngine()
                arguments = Bundle().apply {
                    putParcelable("data", cameraParameBuild)
                    putString("sharedPreferencesName", sharedPreferencesName)

                }
            }
        }
    }
}