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
import com.akingyin.base.ext.click
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


    private var netGrid: Int by Delegates.observable(CameraManager.CameraNetGrid.CAMERA_NET_GRID_NONE) { property, oldValue, newValue ->
        bindView.buttonGrid.setImageResource(when(newValue){
            CameraManager.CameraNetGrid.CAMERA_NET_GRID_OPEN ->{
                R.drawable.ic_grid_on
            }
            else ->{
                R.drawable.ic_grid_off
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
            netGrid = if (netGrid == CameraManager.CameraNetGrid.CAMERA_NET_GRID_OPEN) {
                CameraManager.CameraNetGrid.CAMERA_NET_GRID_CLOSE
            }else {
                CameraManager.CameraNetGrid.CAMERA_NET_GRID_OPEN
            }
        }
        withPermissionsCheck(Manifest.permission.CAMERA) {
            bindView.viewFinder.bindSurfaceView(cameraManager, CameraParameBuild())
        }


    }


    override fun lazyLoad() {

    }


    override fun onDestroy() {
        super.onDestroy()
        cameraManager.closeDriver()
    }

    companion object {
        fun newInstance(cameraParameBuild: CameraParameBuild): BaseCameraFragment {
            return BaseCameraFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("data", cameraParameBuild)
                }
            }
        }
    }


}