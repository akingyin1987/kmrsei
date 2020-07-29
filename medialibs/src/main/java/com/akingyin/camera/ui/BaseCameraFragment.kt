/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.camera.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Point
import android.hardware.Camera
import android.hardware.Camera.CAMERA_ERROR_SERVER_DIED
import android.hardware.Camera.CAMERA_ERROR_UNKNOWN
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.akingyin.base.SimpleFragment
import com.akingyin.base.ext.*
import com.akingyin.base.mvvm.SingleLiveEvent
import com.akingyin.base.utils.PreferencesUtil
import com.akingyin.camera.CameraData
import com.akingyin.camera.CameraManager
import com.akingyin.camera.CameraParameBuild
import com.akingyin.camera.CameraSensorController
import com.akingyin.camera.widget.CaptureButton
import com.akingyin.media.MediaConfig
import com.akingyin.media.R
import com.akingyin.media.databinding.FragmentCameraBinding
import com.akingyin.media.engine.LocationEngine
import com.akingyin.media.engine.LocationManagerEngine
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import permissions.dispatcher.ktx.withPermissionsCheck
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
    private  var locationEngine :LocationEngine?=null
    var cameraParameBuild :CameraParameBuild  by Delegates.notNull()

    var cameraLiveData: SingleLiveEvent<CameraData> = SingleLiveEvent()



    @CameraManager.CameraNetGrid
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

    @CameraManager.CameraFlashModel
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

    @CameraManager.CameraShutterSound
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

    override fun injection() {

    }

    override fun useViewBind() = true


    override fun initViewBind(inflater: LayoutInflater, container: ViewGroup?): View? {
        bindView = FragmentCameraBinding.inflate(inflater, container, false)
        return bindView.root
    }

    override fun getLayoutId() = R.layout.fragment_camera

    override fun initEventAndData() {
        sharedPreferencesName = arguments?.getString("sharedPreferencesName", "app_setting")
            ?: "app_setting"
        cameraParameBuild = arguments?.let {
            it.getSerializable("data") as CameraParameBuild
        }?: CameraParameBuild()
        initCameraParame(cameraParameBuild)
    }

    open    fun   initCameraParame(cameraParame: CameraParameBuild = cameraParameBuild, changeCameraParme:Boolean = false){
        cameraParame.apply {
            this.flashModel = PreferencesUtil.get(sharedPreferencesName, KEY_CAMERA_FLASH, "0").toInt()
            this.shutterSound = if(PreferencesUtil.get(sharedPreferencesName, KEY_CAMERA_SHUTTER_SOUND,true)) CameraManager.CameraShutterSound.CAMERA_SHUTTER_SOUND_ON else CameraManager.CameraShutterSound.CAMERA_SHUTTER_SOUND_OFF
            this.horizontalPicture = PreferencesUtil.get(sharedPreferencesName, KEY_CAMERA_PHTOT_HORIZONTAL,false)
            this.netGrid = if(PreferencesUtil.get(sharedPreferencesName, KEY_CAMERA_GRID,false)) CameraManager.CameraNetGrid.CAMERA_NET_GRID_CLOSE else CameraManager.CameraNetGrid.CAMERA_NET_GRID_OPEN
            this.cameraResolution ?:Point().apply {
                x = PreferencesUtil.get(sharedPreferencesName, KEY_CAMERA_RESOLUTION_X,0)
                y = PreferencesUtil.get(sharedPreferencesName, KEY_CAMERA_RESOLUTION_Y,0)
            }
            this.autoSavePhotoDelayTime = PreferencesUtil.get(sharedPreferencesName, KEY_CAMERA_AUTO_SAVE_DELAYTIME,0)
            this.focesedAutoPhotoDelayTime = PreferencesUtil.get(sharedPreferencesName, KEY_CAMERA_AUTO_TAKEPHOTO_DELAYTIME,0)
            this.supportAutoSavePhoto = PreferencesUtil.get(sharedPreferencesName, KEY_CAMERA_AUTOSAVE_TAKEPHOTO,false)
            this.supportFocesedAutoPhoto = PreferencesUtil.get(sharedPreferencesName, KEY_CAMERA_FOCUS_TAKEPHOTO,false)
            this.supportManualFocus = PreferencesUtil.get(sharedPreferencesName,KEY_CAMERA_MANUAL_AUTO_FOCUS,false)
            this.supportLocation = PreferencesUtil.get(sharedPreferencesName, KEY_CAMERA_LOCATION,false)
        }
        netGrid = cameraParame.netGrid
        flashMode = cameraParame.flashModel
        shutterSound = cameraParame.shutterSound
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
        cameraSensorController = CameraSensorController(mContext)
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
                    CameraManager.startCameraViewRoteAnimator((uiRotation+90).toFloat(),bindView.buttonShutter,bindView.buttonSetting,bindView.buttonFlash,
                            bindView.buttonGrid,bindView.btnConfig,bindView.btnCancel,bindView.textCountDown)
                }
            }
        }
        cameraManager = CameraManager(mContext) {
            showSucces("运动对焦成功！")
            if(cameraParameBuild.supportFocesedAutoPhoto){
                captureImage()
            }
        }

        bindView.fabTakePicture.captureLisenter = object : CaptureButton.onClickTakePicturesListener() {
            override fun takePictures() {
                bindView.fabTakePicture.isEnabled = false
                captureImage()
            }
        }
        bindView.btnCancel.click {
            CameraManager.recoveryCaptureAnimator(bindView.fabTakePicture, bindView.btnConfig, bindView.btnCancel)
            bindView.fabTakePicture.resetRecordAnim()
            bindView.fabTakePicture.isEnabled = true
            bindView.viewFinder.onStartCameraView()
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
            cameraLiveData.value = CameraData().apply {
                mediaType = MediaConfig.TYPE_IMAGE
                localPath = bindView.viewFinder.cameraParameBuild.localPath
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
        bindView.buttonSetting.click {
           startCameraSettingActivity()
        }
        bindView.textCountDown.click {
            countDownJob?.cancel()
            showSucces("倒计时已取消")
        }
        withPermissionsCheck(Manifest.permission.CAMERA,locationEngine?.let {
            Manifest.permission.ACCESS_FINE_LOCATION
        }?:Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            bindView.viewFinder.bindSurfaceView(cameraManager, cameraParameBuild)
        }


    }

    /** 拍照*/
    private  fun  captureImage(){
        bindView.viewFinder.takePhoto { result, error ->
            if (result) {
                CameraManager.startTypeCaptureAnimator(bindView.fabTakePicture, bindView.btnConfig, bindView.btnCancel)
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
        activity?.startActivityFromFragment(this, Intent(mContext,CameraSettingActivity::class.java).apply {
            cameraManager.camera?.let {
                putExtra("cameraSizes",CameraManager.findSuportCameraSizeValue(it.parameters))
            }
            putExtra("cameraOld",true)
        }, CAMERA_SETTING_REQUEST_CODE)
    }

    private  fun  toggleShutter(){
        bindView.buttonShutter.toggleButton(shutterSound == CameraManager.CameraShutterSound.CAMERA_SHUTTER_SOUND_ON,
        180F,
        R.drawable.ic_action_volume_off,
        R.drawable.ic_action_volume_on){
            flag ->
            shutterSound = if (flag) CameraManager.CameraShutterSound.CAMERA_SHUTTER_SOUND_OFF else CameraManager.CameraShutterSound.CAMERA_SHUTTER_SOUND_ON

        }
    }

    private fun toggleGrid() {
        bindView.buttonGrid.toggleButton(
                netGrid == CameraManager.CameraNetGrid.CAMERA_NET_GRID_OPEN,
                180f,
                R.drawable.ic_grid_off,
                R.drawable.ic_grid_on
        ) { flag ->
            netGrid = if (flag) CameraManager.CameraNetGrid.CAMERA_NET_GRID_CLOSE else CameraManager.CameraNetGrid.CAMERA_NET_GRID_OPEN

        }
    }


    private   var  countDownJob:Job?= null
    /**
     * 开始倒计时
     */
    fun  countDownStart(count:Int,tip:String,call:()->Unit){
        bindView.textCountDownTip.text = tip
       countDownJob = lifecycleScope.launch(Main){
            for (i in count downTo 1){
                bindView.textCountDown.text = i.toString()
                delay(1000)
            }
        }
        bindView.textCountDown.text=""
        bindView.textCountDownTip.text = ""
        call.invoke()
    }

    override fun lazyLoad() {

    }


    override fun onDestroy() {
        super.onDestroy()
        cameraManager.closeDriver()
    }

    companion object {
        const val CAMERA_SETTING_REQUEST_CODE = 1001
        const val KEY_CAMERA_FLASH = "key_camera_flash"
        const val KEY_CAMERA_GRID = "key_camera_netgrid"
        const val KEY_CAMERA_SHUTTER_SOUND = "key_camera_shutter_sound"

        /** camera 相机分辨率 */
        const val KEY_CAMERA_RESOLUTION_X = "camera_resolution_x"
        const val KEY_CAMERA_RESOLUTION_Y = "camera_resolution_y"
        const val KEY_CAMERA_PHTOT_HORIZONTAL = "key_camera_phtot_horizontal"

        const val KEY_CAMERA_LOCATION = "key_camera_location"

        const val KEY_CAMERA_MANUAL_AUTO_FOCUS = "key_camera_manual_auto_focus"

        const val KEY_CAMERA_FOCUS_TAKEPHOTO = "key_camera_focus_takephoto"

        const val KEY_CAMERA_AUTO_TAKEPHOTO_DELAYTIME = "key_camera_auto_takephoto_delaytime"

        const val KEY_CAMERA_AUTOSAVE_TAKEPHOTO = "key_camera_autosave_takephoto"

        const val KEY_CAMERA_AUTO_SAVE_DELAYTIME = "key_camera_auto_save_delaytime"

        /** camerax 相机分辨率 */
        const val KEY_CAMERAX_RESOLUTION_X = "camerax_resolution_x"
        const val KEY_CAMERAX_RESOLUTION_Y = "camerax_resolution_y"
        fun newInstance(cameraParameBuild: CameraParameBuild, sharedPreferencesName: String = "app_setting",locationManagerEngine: LocationManagerEngine?=null): BaseCameraFragment {
            return BaseCameraFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("data", cameraParameBuild)
                    putString("sharedPreferencesName", sharedPreferencesName)
                    locationEngine = locationManagerEngine?.createEngine()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cameraSensorController.onResume()
    }

    override fun onPause() {
        super.onPause()
        cameraSensorController.onPause()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CAMERA_SETTING_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                initCameraParame(changeCameraParme = true)
            }
        }
    }
}