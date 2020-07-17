/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.camera

import android.content.Context
import android.graphics.Point
import android.hardware.Camera
import android.util.AttributeSet
import android.view.*

import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout

import com.akingyin.media.R

/**
 * 旧相机预览
 * @ Description:
 * @author king
 * @ Date 2020/7/16 15:01
 * @version V1.0
 */
@Suppress("ClickableViewAccessibility")
class CameraPreview @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) :
        RelativeLayout(context, attrs, defStyleAttr, defStyleRes),SurfaceHolder.Callback {

    var  camera_surface : SurfaceView
    var  camera_fouce:FouceView
    var  camera_img : ImageView

    lateinit var  cameraManager:CameraManager

    var  errorCallback : Camera.ErrorCallback?=null


    init {
       View.inflate(context,R.layout.camera_view,this).run {
           camera_fouce = findViewById(R.id.camera_fouce)
           camera_surface = findViewById(R.id.camera_surface)
           camera_img = findViewById(R.id.camera_img)
       }

       camera_surface.setOnTouchListener { _, event ->
           if(event.action == MotionEvent.ACTION_DOWN){

           }

           return@setOnTouchListener false
       }
    }

    lateinit  var  cameraParameBuild:CameraParameBuild
    fun   bindSurfaceView(cameraManager: CameraManager,cameraParameBuild: CameraParameBuild){
        this.cameraManager = cameraManager
        this.cameraParameBuild = cameraParameBuild
        camera_surface.holder.addCallback(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
       cameraManager.stopPreview()
       cameraManager.closeDriver()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
       cameraManager.openCamera(holder,errorCallback)
        cameraManager.camera?.let {
            cameraManager.setCameraParametersValues(it,cameraParameBuild){
                result, error ->

            }
        }

    }
}