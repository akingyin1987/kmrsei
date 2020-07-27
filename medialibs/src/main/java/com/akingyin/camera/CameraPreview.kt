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
import android.hardware.Camera
import android.net.Uri
import android.util.AttributeSet
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import com.akingyin.base.dialog.ToastUtil
import com.akingyin.base.ext.gone
import com.akingyin.base.ext.visiable
import com.akingyin.base.utils.FileUtils
import com.akingyin.camera.widget.CameraSurfaceView
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

    var  camera_surface : CameraSurfaceView
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


    }

    lateinit  var  cameraParameBuild:CameraParameBuild
    fun   bindSurfaceView(cameraManager: CameraManager,cameraParameBuild: CameraParameBuild){
        this.cameraManager = cameraManager
        this.cameraParameBuild = cameraParameBuild
        camera_fouce.screenPoint = cameraManager.theScreenResolution
        camera_surface.holder.addCallback(this)
        camera_surface.onSurfaceViewListion = object :CameraSurfaceView.OnSurfaceViewListion{
            override fun onFouceClick(x: Float, y: Float) {
               if(cameraParameBuild.supportManualFocus){
                   cameraManager.camera?.let {
                       camera_fouce.setTouchFoucusRect(it, Camera.AutoFocusCallback {
                           success, _ ->
                           camera_fouce.disDrawTouchFocusRect(success)
                           if(success){
                               //手动对焦成功
                               authTakePhoto()
                           }
                       },x,y)
                   }
               }
            }
        }

    }

    /**
     * 自动拍照
     */
    fun   authTakePhoto(){
        if(cameraParameBuild.supportFocesedAutoPhoto){
            cameraManager.autoTakePhoto(cameraParameBuild.focesedAutoPhotoDelayTime,cameraParameBuild){
                result, error ->
               if(!result){
                  ToastUtil.showError(context,"出错了,$error")
               }
            }
        }

    }


    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        cameraParameBuild.cameraResolution?.let {
             cameraManager.findBestViewSize(cameraManager.theScreenResolution,it)?.let {
                 best ->
                camera_surface.layoutParams.apply {
                     if (best.x == 0 && layoutParams.height != best.y) {
                         this.height = best.y

                     } else if (best.y == 0 && layoutParams.width != best.x) {
                         this.width = best.x
                     }
                 }
             }
        }
        cameraManager.startPreview()

    }

    fun   takePhoto(cameraParame: CameraParameBuild = cameraParameBuild,callBack: (result: Boolean, error: String?) -> Unit){
        cameraManager.takePictrue(cameraParame){
            result, error ->
            cameraManager.stopPreview()
            camera_img.visiable()
            camera_img.setImageURI(Uri.parse(cameraParame.localPath))
            callBack.invoke(result,error)
        }
    }

    fun   onStartCameraView(){
        camera_img.setImageURI(null)
        camera_img.gone()
        cameraManager.startPreview()
        FileUtils.deleteFile(cameraParameBuild.localPath)

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
                 if(!result){
                     ToastUtil.showError(context,"设置参数出错了,$error")
                 }
            }
        }
        println("camera-${cameraManager.theScreenResolution},${cameraManager.cameraBestResolution}")

    }
}