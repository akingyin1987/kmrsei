/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.camera.ui

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Point
import android.hardware.Camera
import android.hardware.Camera.CAMERA_ERROR_SERVER_DIED
import android.hardware.Camera.CAMERA_ERROR_UNKNOWN
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.akingyin.base.SimpleFragment
import com.akingyin.base.ext.*
import com.akingyin.base.mvvm.SingleLiveEvent
import com.akingyin.base.utils.PreferencesUtil
import com.akingyin.media.camera.*
import com.akingyin.media.camera.widget.CaptureButton
import com.akingyin.media.MediaConfig
import com.akingyin.media.R
import com.akingyin.media.camera.CameraManager.Companion.KEYDOWN_VOLUME_KEY_ACTION
import com.akingyin.media.camera.CameraManager.Companion.KEY_CAMERA_FLASH
import com.akingyin.media.camera.CameraManager.Companion.KEY_CAMERA_GRID
import com.akingyin.media.camera.CameraManager.Companion.KEY_CAMERA_SHUTTER_SOUND
import com.akingyin.media.databinding.FragmentCameraBinding
import com.akingyin.media.engine.LocationEngine
import com.akingyin.media.engine.LocationManagerEngine
import com.google.android.material.snackbar.Snackbar

import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.CancellationException
import kotlin.properties.Delegates

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/21 15:10
 * @version V1.0
 */

@Suppress("DEPRECATION", "ClickableViewAccessibility")
open class BaseCameraFragment : SimpleFragment() {

    lateinit var bindView: FragmentCameraBinding
    lateinit var sharedPreferencesName: String
    lateinit var cameraSensorController: CameraSensorController
      var locationEngine :LocationEngine?=null
    var cameraParameBuild :CameraParameBuild  by Delegates.notNull()

    var cameraLiveData: SingleLiveEvent<CameraData> = SingleLiveEvent()

    private  var  volumeKeyControlBroadcast :BroadcastReceiver = VolumeKeyControlBroadcast()

    inner  class  VolumeKeyControlBroadcast :BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
           intent?.let {
               if(it.action == KEYDOWN_VOLUME_KEY_ACTION){

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


    private var netGrid: Int by Delegates.observable(CameraManager.CameraNetGrid.CAMERA_NET_GRID_NONE) { _, _, newValue ->

        bindView.buttonGrid.setImageResource(when (newValue) {
            CameraManager.CameraNetGrid.CAMERA_NET_GRID_OPEN -> {
                bindView.groupGridLines.visiable()
                PreferencesUtil.put(sharedPreferencesName, KEY_CAMERA_GRID, true)
                R.drawable.ic_grid_on

            }
            else -> {
                PreferencesUtil.put(sharedPreferencesName, KEY_CAMERA_GRID, false)
                bindView.groupGridLines.gone()
                R.drawable.ic_grid_off
            }
        })

    }


    private var flashMode: Int by Delegates.observable(CameraManager.CameraFlashModel.CAMERA_FLASH_NONE) { _, _, newValue ->
        PreferencesUtil.put(sharedPreferencesName, KEY_CAMERA_FLASH, newValue.toString())
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
                PreferencesUtil.put(sharedPreferencesName, KEY_CAMERA_SHUTTER_SOUND, true)
                R.drawable.ic_action_volume_on
            }
            else ->{
                PreferencesUtil.put(sharedPreferencesName, KEY_CAMERA_SHUTTER_SOUND, false)
                R.drawable.ic_action_volume_off
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        cameraManager = CameraManager(context) {
            showSucces("运动对焦成功！")
            println("运动对焦成功")
            focusSucessCaptureImage()
        }


    }

    override fun injection() {

    }

    override fun useViewBind() = true


    override fun initViewBind(inflater: LayoutInflater, container: ViewGroup?): View? {
        bindView = FragmentCameraBinding.inflate(inflater, container, false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                zoomBarJob?.cancel()
               countDownJob?.cancel(cause = CancellationException("返回取消"))
                cameraManager.closeDriver()
               requireActivity().finish()
            }
        })
        return bindView.root
    }

    override fun getLayoutId() = R.layout.fragment_camera

    override fun initEventAndData() {
        sharedPreferencesName = arguments?.getString("sharedPreferencesName", "app_setting")
            ?: "app_setting"
        cameraParameBuild = arguments?.getParcelable("data") ?: CameraParameBuild()
        initCameraParame(cameraParameBuild)

    }

    open    fun   initCameraParame(cameraParame: CameraParameBuild = cameraParameBuild, changeCameraParme:Boolean = false){
        CameraManager.readCameraParame(cameraParame,sharedPreferencesName)
        cameraParame.cameraResolution?.let {
            if(it.x>0 && it.y>0){
                cameraManager.cameraCustomResolution = Point(it.x,it.y)
            }
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
        if(changeCameraParme){
            cameraManager.camera?.let {
                cameraManager.setCameraParametersValues(it,cameraParame){
                    result, error ->
                    if(!result){
                        showError(error)
                    }
                }
            }

        }
    }

    lateinit var cameraManager: CameraManager


    override fun initView() {

        cameraSensorController = CameraSensorController(requireContext(), requireActivity().windowManager.defaultDisplay.rotation)
        cameraSensorController.mOrientationChangeListener = object : CameraSensorController.OrientationChangeListener {
            override fun onChange(relativeRotation: Int, uiRotation: Int) {
                var cameraRotation = uiRotation + 90
                if (cameraRotation == 180) {
                    cameraRotation = 0
                }
                if (cameraRotation == 360) {
                    cameraRotation = 180
                }
                println("uiRotation=>$uiRotation,$cameraRotation,${cameraManager.cameraAngle}")
                if(cameraManager.cameraAngle != cameraRotation){
                    cameraManager.cameraAngle = cameraRotation

                }
                if(cameraManager.cameraUiAngle != cameraRotation){
                    cameraManager.cameraUiAngle = cameraRotation
                    CameraManager.startCameraViewRoteAnimator((uiRotation).toFloat(),bindView.buttonShutter,bindView.buttonSetting,bindView.buttonFlash,
                            bindView.buttonGrid,bindView.btnConfig,bindView.btnCancel,bindView.textCountDown)
                }
            }
        }


        bindView.rulerView.valueFrom = cameraManager.cameraMinZoom.toFloat()
        bindView.rulerView.valueTo = cameraManager.cameraMaxZoom.toFloat()
        bindView.rulerView.value = cameraManager.cameraCurrentZoom
        bindView.tvLocation.click {
            getLocationInfo()
        }
        bindView.fabTakePicture.captureLisenter = object : CaptureButton.onClickTakePicturesListener() {
            override fun takePictures() {
                bindView.fabTakePicture.isEnabled = false
                captureImage()
            }
        }
        bindView.btnCancel.click {
            countDownJob?.cancel()
            CameraManager.recoveryCaptureAnimator(bindView.fabTakePicture, bindView.btnConfig, bindView.btnCancel,bindView.rlTurn)
            bindView.fabTakePicture.resetRecordAnim()
            bindView.fabTakePicture.isEnabled = true
            bindView.viewFinder.onStartCameraView()
        }
        bindView.ivTurnleft.click {
            rotateTakePhotoBitmap(270)
        }
        bindView.ivTurnright.click {
            rotateTakePhotoBitmap(90)
        }
        bindView.ivTurncenter.click {
            rotateTakePhotoBitmap(180)
        }
        bindView.viewFinder.errorCallback = Camera.ErrorCallback { error, _ ->
            when (error) {
                CAMERA_ERROR_SERVER_DIED -> {
                    showError("相机服务异常，请退出或重启手机,错误代码${error}")
                }
                CAMERA_ERROR_UNKNOWN -> {
                    showError("相机出错了,错误代码${error}")
                }
            }
        }
        bindView.btnConfig.click {
            println("放送消息")
            countDownJob?.cancel()
            cameraLiveData.postValue(CameraData().apply {
                mediaType = MediaConfig.TYPE_IMAGE
                localPath = bindView.viewFinder.cameraParameBuild.localPath
            })
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
        bindView.buttonSetting.click {
           startCameraSettingActivity()
        }
        bindView.textCountDown.click {
            countDownJob?.cancel()
            showSucces("倒计时已取消")
        }
        bindView.rulerView.addOnChangeListener { _, value, fromUser ->
            if(fromUser){
                cameraManager.setCameraZoom(value)
            }
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

    /**
     * 对焦成功，自动拍照
     */
    private  fun   focusSucessCaptureImage(){
        if(cameraParameBuild.supportFocesedAutoPhoto){
            countDownStop()
            if(cameraParameBuild.focesedAutoPhotoDelayTime>0 ){

                countDownStart(cameraParameBuild.focesedAutoPhotoDelayTime,"自动拍照"){
                    captureImage()
                }
            }else{
                captureImage()
            }
        }

    }

    /** 拍照*/
    private  fun  captureImage(){
        println("cameraAngle=${cameraManager.cameraAngle}")
        cameraParameBuild.cameraAngle =cameraManager.cameraAngle
        bindView.viewFinder.takePhoto { result, error ->
            if (result) {

                CameraManager.startTypeCaptureAnimator(bindView.fabTakePicture, bindView.btnConfig, bindView.btnCancel,bindView.rlTurn)
                if(cameraParameBuild.supportAutoSavePhoto){
                    if(cameraParameBuild.autoSavePhotoDelayTime>0){
                        countDownStart(cameraParameBuild.autoSavePhotoDelayTime,"拍照自动保存倒计时"){
                            cameraLiveData.value = CameraData().apply {
                                mediaType = MediaConfig.TYPE_IMAGE
                                localPath = cameraParameBuild.localPath
                            }
                        }
                    }else{
                        cameraLiveData.value = CameraData().apply {
                            mediaType = MediaConfig.TYPE_IMAGE
                            localPath = cameraParameBuild.localPath
                        }

                    }
                }
            } else {
                countDownJob?.cancel()
                CameraManager.recoveryCaptureAnimator(bindView.fabTakePicture, bindView.btnConfig, bindView.btnCancel,bindView.rlTurn)
                bindView.fabTakePicture.resetRecordAnim()
                bindView.fabTakePicture.isEnabled = true
                bindView.viewFinder.onStartCameraView()
                showError(error)
            }
        }
    }

    private fun closeFlashAndSelect(@CameraManager.CameraFlashModel flash: Int) = bindView.layoutFlashOptions.circularClose(bindView.buttonFlash) {
        flashMode = flash
        cameraManager.camera?.let {
            cameraManager.setCameraFlashModel(it, flashMode) { result, error ->
                if (!result) {
                    showError("出错了,${error}")
                }
            }
        }

    }

    open  fun    startCameraSettingActivity(){
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

            if( it.resultCode == Activity.RESULT_OK){
                initCameraParame(changeCameraParme = true)
            }
        }.launch(Intent(requireContext(),CameraSettingActivity::class.java).apply {
            cameraManager.camera?.let {
                putExtra("cameraSizes",CameraManager.findSuportCameraSizeValue(it.parameters))
            }
            putExtra("sharedPreferencesName",sharedPreferencesName)
            putExtra("cameraOld",true)
            putExtra("cameraX",false)
        })

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


    private   var  countDownJob:Job?= null
    /**
     * 开始倒计时
     */
    private fun  countDownStart(count:Int,tip:String,call:()->Unit){
        bindView.textCountDownTip.text = tip
       countDownJob = lifecycleScope.launch(Main){
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

    private fun  rotateTakePhotoBitmap(degree:Int){
        File(cameraParameBuild.localPath).exists().yes {
            lifecycleScope.launch(Main){
                withIO {
                    CameraBitmapUtil.rotateBitmap(degree,cameraParameBuild.localPath, appServerTime)
                }.yes {
                    showSucces("图片旋转成功")
                    bindView.viewFinder.camera_img.setImageURI(null)
                    bindView.viewFinder.camera_img.setImageURI(Uri.parse(cameraParameBuild.localPath))
                }.no {
                    showError("图片旋转失败")
                }
            }
        }.no {
            showError("文件不存在，无法旋转")
        }
    }

    /** 音量键*/
    private  fun  keyVolumeDown(){
        if(cameraParameBuild.volumeKeyControl ==1){
            captureImage()
        }else if(cameraParameBuild.volumeKeyControl == 2){
            val len = cameraManager.cameraCurrentZoom-(cameraManager.cameraMaxZoom - cameraManager.cameraMinZoom)/10
            showCameraZoomBar(if(len<0) 0F else len)
        }
    }

    private  fun  keyVolumeUp(){
        if(cameraParameBuild.volumeKeyControl ==1){
            captureImage()
        }else if(cameraParameBuild.volumeKeyControl == 2){
            val len = cameraManager.cameraCurrentZoom+(cameraManager.cameraMaxZoom - cameraManager.cameraMinZoom)/10
            showCameraZoomBar(if(len>cameraManager.cameraMaxZoom) cameraManager.cameraMaxZoom.toFloat() else len)
        }
    }

    private   var  zoomBarJob:Job?=null
    private  fun  showCameraZoomBar(zoom:Float){
        zoomBarJob?.cancel()
        zoomBarJob = lifecycleScope.launch(Main) {
            cameraManager.setCameraZoom(zoom)
            bindView.rulerView.visiable()
            bindView.rulerView.value = zoom
            delay(5000)
            bindView.rulerView.gone()
        }
    }

    override fun lazyLoad() {

    }


    override fun onDestroy() {
        super.onDestroy()
        locationEngine = null
        cameraManager.closeDriver()
    }

    companion object {
         const val REQUEST_CODE_PERMISSIONS = 10


        fun newInstance(cameraParameBuild: CameraParameBuild, sharedPreferencesName: String = "app_setting",locationManagerEngine: LocationManagerEngine?=null): BaseCameraFragment {
            return BaseCameraFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("data", cameraParameBuild)
                    putString("sharedPreferencesName", sharedPreferencesName)
                    locationEngine = locationManagerEngine?.createEngine()
                }
            }
        }
    }

    private  var bindCameraInit = false
    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(volumeKeyControlBroadcast, IntentFilter().apply {
            addAction(KEYDOWN_VOLUME_KEY_ACTION)
        })
        if(allPermissionsGranted()){
            onPermissionGranted()
        }else{
            bindView.viewFinder.unBindSurfaceView()
            bindCameraInit = false
            ActivityCompat.requestPermissions(requireActivity(), cameraPermissions, REQUEST_CODE_PERMISSIONS)
        }
        cameraSensorController.onResume()
    }

    private fun onPermissionGranted() {
        if(!bindCameraInit){
            bindView.viewFinder.bindSurfaceView(cameraManager, cameraParameBuild,{
                result, error ->
                if(result){
                    focusSucessCaptureImage()
                }else{
                    showError(error)
                }
            },{
                zoom ->
                showCameraZoomBar(zoom)
            })
        }
        bindCameraInit = true

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                onPermissionGranted()
            }else{
                mView.let { v ->
                    Snackbar.make(v, "拍照没有这些权限将无法运行！", Snackbar.LENGTH_INDEFINITE)
                            .setAction("退出") { ActivityCompat.finishAffinity(requireActivity()) }
                            .show()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(volumeKeyControlBroadcast)
        cameraSensorController.onPause()
    }
    // Check for the permissions
    private fun allPermissionsGranted() = cameraPermissions.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }




}