/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.camerax.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.akingyin.base.SimpleFragment
import com.akingyin.base.ext.gone
import com.akingyin.base.ext.visiable
import com.akingyin.media.R
import com.akingyin.media.camera.CameraData
import com.akingyin.media.camera.CameraManager
import com.akingyin.media.camera.CameraParameBuild
import com.akingyin.media.camerax.CameraxManager
import com.akingyin.media.databinding.FragmentConfigPhotoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 拍照后确认
 * @ Description:
 * @author king
 * @ Date 2020/9/17 11:13
 * @version V1.0
 */
class CameraxConfigPhotoFragment internal constructor(): SimpleFragment() {

    lateinit var  bindView:FragmentConfigPhotoBinding


    override fun injection() {

    }

    override fun useViewBind() = true

    override fun getLayoutId() = R.layout.fragment_config_photo
    override fun initViewBind(inflater: LayoutInflater, container: ViewGroup?): View? {
        bindView = FragmentConfigPhotoBinding.inflate(inflater,container,false)
        return bindView.root
    }


    override fun initEventAndData() {

    }

     var  cameraParameBuild: CameraParameBuild = CameraParameBuild()
    override fun initView() {
         if(cameraParameBuild.supportMultiplePhoto){
             bindView.btnCustom.visiable()
         }else{
             bindView.btnCustom.gone()
         }
         if(cameraParameBuild.supportAutoSavePhoto){
            countDownStart(cameraParameBuild.autoSavePhotoDelayTime,"拍照保存"){
                saveTakePhoto()
            }
         }
    }

    private  fun  saveTakePhoto(){
        if(cameraParameBuild.supportMultiplePhoto){
            CameraxManager.sendAddTakePhoto(cameraParameBuild.localPath,requireContext())

        }else{
            CameraxManager.sendTakePhotoComplete(CameraData().apply {
                localPath = cameraParameBuild.localPath
                originalPath = cameraParameBuild.localPath
            },requireContext())
        }
    }

    private   var  countDownJob: Job?= null
    /**
     * 开始倒计时
     */
    private fun  countDownStart(count:Int,tip:String,call:()->Unit){
        bindView.textCountDownTip.text = tip
        countDownJob = lifecycleScope.launch(Dispatchers.Main){
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
    override fun lazyLoad() {

    }
}