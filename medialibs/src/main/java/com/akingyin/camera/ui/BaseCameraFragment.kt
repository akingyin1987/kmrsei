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
import com.akingyin.base.SimpleFragment
import com.akingyin.base.ext.*
import com.akingyin.base.mvvm.SingleLiveEvent
import com.akingyin.camera.CameraData
import com.akingyin.camera.CameraManager
import com.akingyin.camera.CameraParameBuild
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


    var camerarLiveData: SingleLiveEvent<CameraData> = SingleLiveEvent()

    @CameraManager.CameraNetGrid
    private var netGrid: Int by Delegates.observable(CameraManager.CameraNetGrid.CAMERA_NET_GRID_NONE) { property, oldValue, newValue ->
        bindView.buttonGrid.setImageResource(when (newValue) {
            CameraManager.CameraNetGrid.CAMERA_NET_GRID_OPEN -> {
                bindView.groupGridLines.visiable()
                R.drawable.ic_grid_on
            }
            else -> {
                bindView.groupGridLines.gone()
                R.drawable.ic_grid_off
            }
        })

    }

    @CameraManager.CameraFlashModel
    private var flashMode:Int by Delegates.observable(CameraManager.CameraFlashModel.CAMERA_FLASH_NONE){
        _, _, newValue ->
        bindView.buttonFlash.setImageResource(when(newValue){
            CameraManager.CameraFlashModel.CAMERA_FLASH_ON->{
                R.drawable.ic_flash_on
            }
            CameraManager.CameraFlashModel.CAMERA_FLASH_OFF->{
                R.drawable.ic_flash_off
            }
            else ->{
                R.drawable.ic_flash_auto
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

    }

    lateinit var cameraManager: CameraManager

    var cameraRotation = 0
    override fun initView() {

        cameraManager = CameraManager(mContext, { _, uiRotation ->
            cameraRotation = uiRotation + 90
            if (cameraRotation == 180) {
                cameraRotation = 0
            }
            if (cameraRotation == 360) {
                cameraRotation = 180
            }
            if (cameraManager.cameraUiAngle != cameraRotation) {
                cameraManager.cameraUiAngle = cameraRotation
                CameraManager.startCameraViewRoteAnimator(uiRotation.toFloat(), bindView.buttonFlash, bindView.buttonGrid, bindView.buttonHdr, bindView.textCountDown)
            }

        }, {
            showSucces("运动对焦成功！")
        })

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
            camerarLiveData.value = CameraData().apply {
                mediaType = MediaConfig.TYPE_IMAGE
                localPath = bindView.viewFinder.cameraParameBuild.localPath
            }
        }
        bindView.buttonGrid.click {
            toggleGrid()
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
        withPermissionsCheck(Manifest.permission.CAMERA) {
            bindView.viewFinder.bindSurfaceView(cameraManager, CameraParameBuild())
        }


    }

    private fun closeFlashAndSelect(@CameraManager.CameraFlashModel flash: Int) = bindView.layoutFlashOptions.circularClose(bindView.buttonFlash){
        flashMode = flash
        cameraManager.camera?.let {
            cameraManager.setCameraFlashModel(it,flashMode){
                result, error ->
                if(!result){
                    showError("出错了,${error}")
                }
            }
        }

    }

    private fun toggleGrid() {
        bindView.buttonGrid.toggleButton(
                netGrid==CameraManager.CameraNetGrid.CAMERA_NET_GRID_CLOSE,
                180f,
                R.drawable.ic_grid_off,
               R.drawable.ic_grid_on
        ) { flag ->
            netGrid = if(flag) CameraManager.CameraNetGrid.CAMERA_NET_GRID_OPEN else CameraManager.CameraNetGrid.CAMERA_NET_GRID_CLOSE
            bindView.groupGridLines.visibility = if (flag) View.VISIBLE else View.GONE
        }
    }




    override fun lazyLoad() {

    }


    override fun onDestroy() {
        super.onDestroy()
        cameraManager.closeDriver()
    }

    companion object {
        const val KEY_CAMERA_FLASH = "pre-flash-camera"
        const val KEY_CAMERA_GRID = "pre-grid-camera"
        const val KEY_CAMERA_SHUTTER_SOUND = "pre-shutter-sound-camera"

        fun newInstance(cameraParameBuild: CameraParameBuild): BaseCameraFragment {
            return BaseCameraFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("data", cameraParameBuild)
                }
            }
        }
    }



}