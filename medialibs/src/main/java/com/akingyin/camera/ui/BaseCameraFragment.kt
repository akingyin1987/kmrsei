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
import android.hardware.Camera
import android.hardware.Camera.CAMERA_ERROR_SERVER_DIED
import android.hardware.Camera.CAMERA_ERROR_UNKNOWN
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
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
import permissions.dispatcher.ktx.withPermissionsCheck
import kotlin.properties.Delegates

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/21 15:10
 * @version V1.0
 */

@Suppress("DEPRECATION", "ClickableViewAccessibility")
class BaseCameraFragment : SimpleFragment() {

    lateinit var bindView: FragmentCameraBinding
    lateinit var sharedPreferencesName: String
    lateinit var cameraSensorController: CameraSensorController
    var cameraParameBuild :CameraParameBuild  by Delegates.notNull()

    var cameraLiveData: SingleLiveEvent<CameraData> = SingleLiveEvent()

    //点击设置改变
    var cameraSetting : MutableLiveData<Boolean> = MutableLiveData()

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
        PreferencesUtil.put(sharedPreferencesName, KEY_CAMERA_FLASH, newValue)
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
    private var shutterSound: Int by Delegates.observable(CameraManager.CameraShutterSound.CAMERA_SHUTTER_SOUND_NONE) { property, oldValue, newValue ->
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
    }

    lateinit var cameraManager: CameraManager

    var cameraRotation = 0
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
                println("uiRotation=>$uiRotation")
                if(cameraManager.cameraAngle != cameraRotation){
                    cameraManager.cameraAngle = cameraRotation
                    CameraManager.startCameraViewRoteAnimator((uiRotation+90).toFloat(),bindView.buttonShutter,bindView.buttonSetting,bindView.buttonFlash,
                    bindView.buttonGrid,bindView.btnConfig,bindView.btnCancel,bindView.textCountDown)
                }
            }
        }
        cameraManager = CameraManager(mContext) {
            showSucces("运动对焦成功！")
        }

        bindView.fabTakePicture.captureLisenter = object : CaptureButton.onClickTakePicturesListener() {
            override fun takePictures() {
                bindView.fabTakePicture.isEnabled = false
                bindView.viewFinder.takePhoto { result, error ->
                    if (result) {
                        CameraManager.startTypeCaptureAnimator(bindView.fabTakePicture, bindView.btnConfig, bindView.btnCancel)

                    } else {
                        showError(error)
                    }
                }
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
            cameraSetting.value = cameraSetting.value?.let {
                !it
            }?:true
        }
        withPermissionsCheck(Manifest.permission.CAMERA) {
            bindView.viewFinder.bindSurfaceView(cameraManager, CameraParameBuild())
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


    override fun lazyLoad() {

    }


    override fun onDestroy() {
        super.onDestroy()
        cameraManager.closeDriver()
    }

    companion object {
        const val KEY_CAMERA_FLASH = "key_camera_flash"
        const val KEY_CAMERA_GRID = "key_camera_netgrid"
        const val KEY_CAMERA_SHUTTER_SOUND = "key_camera_shutter_sound"

        /** camera 相机分辨率 */
        const val KEY_CAMERA_RESOLUTION = "camera_resolution"

        const val KEY_CAMERA_PHTOT_HORIZONTAL = "key_camera_phtot_horizontal"

        const val KEY_CAMERA_LOCATION = "key_camera_location"

        const val KEY_CAMERA_AUTO_FOCUS = "key_camera_auto_focus"

        const val KEY_CAMERA_FOCUS_TAKEPHOTO = "key_camera_focus_takephoto"

        const val KEY_CAMERA_AUTO_TAKEPHOTO_DELAYTIME = "key_camera_auto_takephoto_delaytime"

        const val KEY_CAMERA_AUTOSAVE_TAKEPHOTO = "key_camera_autosave_takephoto"

        const val KEY_CAMERA_AUTO_SAVE_DELAYTIME = "key_camera_auto_save_delaytime"

        /** camerax 相机分辨率 */
        const val KEY_CAMERAX_RESOLUTION = "camerax_resolution"
        fun newInstance(cameraParameBuild: CameraParameBuild, sharedPreferencesName: String = "app_setting"): BaseCameraFragment {
            return BaseCameraFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("data", cameraParameBuild)
                    putString("sharedPreferencesName", sharedPreferencesName)
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
}