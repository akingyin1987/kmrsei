/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.camerax.ui



import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.akingyin.base.SimpleFragment
import com.akingyin.base.config.BaseConfig
import com.akingyin.base.ext.*

import com.akingyin.media.R
import com.akingyin.media.camera.CameraBitmapUtil
import com.akingyin.media.camera.CameraData
import com.akingyin.media.camera.CameraManager
import com.akingyin.media.camera.CameraParameBuild
import com.akingyin.media.camerax.CameraxManager
import com.akingyin.media.databinding.FragmentConfigPhotoBinding
import com.akingyin.media.glide.GlideEngine
import com.akingyin.media.ui.fragment.MedialFileInfoFragmentDialog
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

/**
 * 拍照后确认
 * @ Description:
 * @author king
 * @ Date 2020/9/17 11:13
 * @version V1.0
 */

class CameraxConfigPhotoFragment internal constructor(): SimpleFragment() {

    lateinit var  bindView:FragmentConfigPhotoBinding
    private val args: CameraxConfigPhotoFragmentArgs by navArgs()

    override fun injection() {

    }

    override fun useViewBind() = true
    override fun onUseActivityBackCallBack() = true

    override fun getLayoutId() = R.layout.fragment_config_photo
    override fun initViewBind(inflater: LayoutInflater, container: ViewGroup?): View? {
        bindView = FragmentConfigPhotoBinding.inflate(inflater,container,false)
        return bindView.root
    }


    override fun initEventAndData() {
        cameraParameBuild.apply {
            localPath = args.filePath
        }
        CameraManager.readCameraParame(cameraParameBuild,args.sharedPreferencesName)
    }

     var  cameraParameBuild: CameraParameBuild = CameraParameBuild()
    override fun initView() {

         if(cameraParameBuild.supportMultiplePhoto){
             bindView.btnCustom.visiable()

         }else{
             bindView.btnCustom.gone()
         }
         if(cameraParameBuild.supportAutoSavePhoto){
            countDownStart(cameraParameBuild.autoSavePhotoDelayTime){
                saveTakePhoto()
            }
         }
         Glide.with(bindView.cameraPhoto).apply {
             load(cameraParameBuild.localPath).fitCenter()

                 .into(bindView.cameraPhoto)
         }

        bindView.btnCancel.click {
            //放弃当前拍照

            findNavController().navigateUp()
//            if(cameraParameBuild.supportMultiplePhoto){
//               findNavController().navigateUp()
//            }else{
//                CameraxManager.sendTakePhtotCancel(requireContext())
//            }
        }
        bindView.backButton.click {
            findNavController().navigateUp()
        }
        bindView.btnConfig.click {
            CameraxManager.sendAddTakePhoto(args.filePath,requireContext(),true)

        }

        bindView.shareButton.click {
            try {
                val mediaFile = File(args.filePath)
                val intent = Intent().apply {
                    // Infer media type from file extension
                    val mediaType = MimeTypeMap.getSingleton()
                            .getMimeTypeFromExtension(mediaFile.extension)
                    // Get URI from our FileProvider implementation
                    val uri = FileProvider.getUriForFile(
                            requireContext(), BaseConfig.getAuthority() + ".provider", mediaFile)
                    // Set the appropriate intent extra, type, action and flags
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = mediaType
                    action = Intent.ACTION_SEND
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                startActivity(Intent.createChooser(intent, getString(R.string.share_hint)))
            }catch (e:Exception){
                e.printStackTrace()
                showError("分享出错了")
            }
        }


        bindView.btnCustom.click {
            CameraxManager.sendAddTakePhoto(args.filePath,requireContext(),false)
            findNavController().navigateUp()
        }
        bindView.infoButton.click {
            MedialFileInfoFragmentDialog.newInstance(args.filePath).show(childFragmentManager,"medialInfo")
        }
        bindView.textCountDown.click {
            countDownJob?.cancel()
            showSucces("倒计时已取消")
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
    }
    private fun  rotateTakePhotoBitmap(degree:Int){
        File(cameraParameBuild.localPath).exists().yes {
            lifecycleScope.launch(Dispatchers.Main){
                withIO {
                    CameraBitmapUtil.rotateBitmap(degree,cameraParameBuild.localPath, appServerTime)
                }.yes {
                    showSucces("图片旋转成功")

                    bindView.cameraPhoto.setImageURI(null)
                    GlideEngine.getGlideEngineInstance().clearCacheByImageView(bindView.cameraPhoto)
                    GlideEngine.getGlideEngineInstance().loadImage(requireContext(),cameraParameBuild.localPath,bindView.cameraPhoto)

                }.no {
                    showError("图片旋转失败")
                }
            }
        }.no {
            showError("文件不存在，无法旋转")
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().navigateUp()
//        if(cameraParameBuild.supportMultiplePhoto){
//            MaterialDialogUtil.showConfigDialog(requireContext(),message = "确定要放弃当前拍的所有照片？",positive = "放弃",negative = "再看看"){
//                if(it){
//                    CameraxManager.sendTakePhtotCancel(requireContext())
//                }
//            }
//        }else{
//            CameraxManager.sendTakePhtotCancel(requireContext())
//        }
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
    private fun  countDownStart(count:Int,call:()->Unit){
        bindView.textCountDownTip.text = "拍照保存"
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

    override fun onDestroyView() {
        super.onDestroyView()
        countDownStop()
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