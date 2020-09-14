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
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.impl.ImageOutputConfig
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.akingyin.base.SimpleFragment
import com.akingyin.base.ext.gone
import com.akingyin.base.ext.visiable
import com.akingyin.base.utils.PreferencesUtil
import com.akingyin.media.R
import com.akingyin.media.camera.CameraManager
import com.akingyin.media.camera.CameraParameBuild
import com.akingyin.media.camera.ui.BaseCameraFragment
import com.akingyin.media.databinding.FragmentCameraxBinding
import com.akingyin.media.engine.LocationEngine
import com.akingyin.media.engine.LocationManagerEngine
import com.google.android.material.snackbar.Snackbar
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
    lateinit var sharedPreferencesName: String
    var locationEngine : LocationEngine?=null
    var cameraParameBuild :CameraParameBuild  by Delegates.notNull()
    var bindCameraInit = false

    private var displayId = -1
    private lateinit var displayManager: DisplayManager
    private lateinit var preview: Preview
    private lateinit var imageCapture: ImageCapture
    private lateinit var imageAnalyzer: ImageAnalysis

    override fun injection() {

    }

    override fun getLayoutId() = R.layout.fragment_camerax

    override fun useViewBind() = true
    override fun initViewBind(inflater: LayoutInflater, container: ViewGroup?): View? {
        bindView = FragmentCameraxBinding.inflate(inflater, container, false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {

                requireActivity().finish()
            }
        })
        return bindView.root
    }
    override fun initEventAndData() {
        sharedPreferencesName = arguments?.getString("sharedPreferencesName", "app_setting")
            ?: "app_setting"
        cameraParameBuild = arguments?.getParcelable("data") ?: CameraParameBuild()

    }
    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit
        @SuppressLint("RestrictedApi")
        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
            if (displayId == this@CameraxFragment.displayId) {
                preview.targetRotation = view.display.rotation
                imageCapture.targetRotation = view.display.rotation
                imageAnalyzer.targetRotation = view.display.rotation
            }
        } ?: Unit
    }

    override fun initView() {
        displayManager = requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
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


    open  fun  keyVolumeDown(){

    }

    open fun  keyVolumeUp(){

    }

    open  fun  captureImage(){

    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(volumeKeyControlBroadcast, IntentFilter().apply {
            addAction(BaseCameraFragment.KEYDOWN_VOLUME_KEY_ACTION)
        })
        if(allPermissionsGranted()){
            onPermissionGranted()
        }else{
            bindView.viewFinder.unBindSurfaceView()
            bindCameraInit = false
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
                //通过的权限
                val grantedList = it.filterValues {
                    value->
                    value
                }.mapNotNull {
                    value->
                    value.key
                }
                //是否所有权限都通过
                val allGranted = grantedList.size == it.size
                val list = (it - grantedList).map { it.key }
                //未通过的权限
                val deniedList = list.filter {
                    ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), it)
                }
                //拒绝并且点了“不再询问”权限
               val alwaysDeniedList = list - deniedList
                if(allGranted){
                    onPermissionGranted()
                }else{
                    mView.let { v ->
                        Snackbar.make(v, "拍照没有这些权限将无法运行！$alwaysDeniedList", Snackbar.LENGTH_INDEFINITE)
                                .setAction("退出") { ActivityCompat.finishAffinity(requireActivity()) }
                                .show()
                    }
                }
            }.launch(cameraPermissions)

        }
    }

    // Check for the permissions
    private fun allPermissionsGranted() = cameraPermissions.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }


    private fun onPermissionGranted() {
        if(!bindCameraInit){

        }
        bindCameraInit = true

    }


    companion object{
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