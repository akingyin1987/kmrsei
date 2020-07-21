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
import android.view.animation.RotateAnimation

import com.akingyin.base.SimpleFragment
import com.akingyin.base.ext.click
import com.akingyin.camera.CameraManager
import com.akingyin.camera.CameraParameBuild
import com.akingyin.camera.CameraSensorController
import com.akingyin.camera.widget.CaptureButton
import com.akingyin.media.R
import com.akingyin.media.databinding.FragmentCameraBinding
import permissions.dispatcher.ktx.withPermissionsCheck

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/21 15:10
 * @version V1.0
 */

@Suppress("DEPRECATION","ClickableViewAccessibility")
class BaseCameraFragment : SimpleFragment() {

    lateinit var fragmentCameraBinding: FragmentCameraBinding

    override fun injection() {

    }

    override fun useViewBind() = true


    override fun initViewBind(inflater: LayoutInflater, container: ViewGroup?): View? {
        fragmentCameraBinding = FragmentCameraBinding.inflate(inflater, container, false)
        return fragmentCameraBinding.root
    }

    override fun getLayoutId() = R.layout.fragment_camera

    override fun initEventAndData() {

    }

    lateinit var cameraManager: CameraManager

    override fun initView() {

        cameraManager = CameraManager(mContext,{
            relativeRotation, uiRotation ->
        },{

        })
        fragmentCameraBinding.fabTakePicture.captureLisenter = object : CaptureButton.onClickTakePicturesListener(){
            override fun takePictures() {
               CameraManager.startTypeCaptureAnimator(fragmentCameraBinding.fabTakePicture, fragmentCameraBinding.btnConfig, fragmentCameraBinding.btnCancel)
            }
        }
        fragmentCameraBinding.btnCancel.click {
            CameraManager.recoveryCaptureAnimator(fragmentCameraBinding.fabTakePicture, fragmentCameraBinding.btnConfig, fragmentCameraBinding.btnCancel)
        }
        fragmentCameraBinding.viewFinder.errorCallback = Camera.ErrorCallback { error, _ ->
            when (error) {
                CAMERA_ERROR_SERVER_DIED -> {
                    showError("相机服务异常，请退出或重启手机,错误代码${error}")
                }
                CAMERA_ERROR_UNKNOWN -> {
                    showError("相机出错了,错误代码${error}")
                }
            }
        }
        withPermissionsCheck(Manifest.permission.CAMERA) {
            fragmentCameraBinding.viewFinder.bindSurfaceView(cameraManager, CameraParameBuild())
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