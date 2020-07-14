/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.adapter

import android.widget.ImageView
import android.widget.TextView
import com.akingyin.audio.AudioPlayView
import com.akingyin.base.ext.click
import com.akingyin.base.ext.gone
import com.akingyin.base.ext.visiable
import com.akingyin.base.mvvm.SingleLiveEvent
import com.akingyin.base.utils.FileUtils
import com.akingyin.media.ImageLoadUtil
import com.akingyin.media.R
import com.akingyin.media.glide.GlideImageView
import com.akingyin.media.glide.OnProgressListener
import com.akingyin.media.glide.progress.CircleProgressView
import com.akingyin.media.model.ImageTextModel
import com.akingyin.media.widget.CheckView
import com.akingyin.media.widget.SampleCoverVideo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

import java.text.MessageFormat


/**
 * 基于viewpager2 图文 适配器
 * @ Description:
 * @author king
 * @ Date 2020/7/2 16:29
 * @version V1.0
 */
class MediaViewpager2Adapter : BaseQuickAdapter<ImageTextModel,BaseViewHolder>(R.layout.item_media_viewpager) {

    /** 选中或被选中消息 */
    var  liveEvent : SingleLiveEvent<Int> = SingleLiveEvent()

    /** 下载文件 */
    var  downloadLiveEvent:SingleLiveEvent<Int> = SingleLiveEvent()

    /** 是否显示选择 */
    var   showChecked = false

    var  supportDownload = false

    fun   getCheckedNum():Int{
        var  num  = 0
        data.forEach {
            if(it.checked){
                num++
            }
        }
        return  num
    }

    override fun convert(holder: BaseViewHolder, item: ImageTextModel) {
       with(holder){

           getView<CheckView>(R.id.check_view).run {
               if(showChecked){
                  click {
                      checkView ->
                      checkView.setCountable(false)
                      checkView.setChecked(!item.checked)
                      item.checked = !item.checked
                      liveEvent.value = bindingAdapterPosition
                  }
               }else{
                   gone()
               }
           }
           setText(R.id.tv_page, MessageFormat.format("{0}/{1}",bindingAdapterPosition+1,getDefItemCount()))
           val downloadView :ImageView = getView(R.id.iv_download)
           downloadView.click {
               downloadLiveEvent.value = bindingAdapterPosition
           }
           val textView : TextView = getView(R.id.tv_text)
           textView.gone()
           val glideImageView: GlideImageView = getView(R.id.iv_image)
           glideImageView.gone()
           val circleProgressView = getView<CircleProgressView>(R.id.progressView)
           circleProgressView.gone()
           val sampleCoverVideo :SampleCoverVideo =  getView(R.id.video_player)
           sampleCoverVideo.gone()
           val audioPlayView : AudioPlayView = getView(R.id.audio_player)
           audioPlayView.gone()
           if(supportDownload && item.serverPath.isNotEmpty() ){
               if(item.localPath.isEmpty() || !FileUtils.isFileExist(item.localPath)){
                   downloadView.visiable()
               }else{
                   downloadView.gone()
               }
           }else{
               downloadView.gone()
           }
           when(item.multimediaType){
              ImageTextModel.TEXT ->{
                  downloadView.gone()
                  textView.visiable()
                  textView.text = item.text
              }

               ImageTextModel.IMAGE ->{
                   glideImageView.let {
                       it.visiable()
                       if(FileUtils.isFileExist(item.localPath)){
                           ImageLoadUtil.loadImageLocalFile(item.localPath,context,it)
                       }else{
                           if(item.haveNetServer){
                               circleProgressView.visiable()
                               circleProgressView.progress = 0
                               circleProgressView.max = 100

                               it.load(item.serverPath,R.drawable.big_img_error,object :OnProgressListener{
                                   override fun onProgress(isComplete: Boolean, percentage: Int, bytesRead: Long, totalBytes: Long) {
                                       println("isComplete=${isComplete} percentage=${percentage}")
                                       if(isComplete){
                                           circleProgressView.gone()
                                       }
                                       circleProgressView.progress = percentage
                                   }
                               })
                           }else{
                               it.setImageResource(R.drawable.big_img_error)
                           }
                       }
                   }

               }

               ImageTextModel.VIDEO ->{
                   sampleCoverVideo.run {
                       visiable()
                       if(FileUtils.isFileExist(item.localPath)){
                           loadCoverImage("file://${item.localPath}",R.drawable.big_img_error)
                           setUpLazy("file://${item.localPath}",true,null,null,FileUtils.getFileName(item.localPath))
                           playPosition = bindingAdapterPosition
                           isAutoFullWithSize = true
                       }else{
                           println("serverpath=${item.serverPath}")
                           if(item.haveNetServer){
                               loadCoverImage(item.serverPath,R.drawable.big_img_error)
                               setUpLazy(item.serverPath,true,null,null,FileUtils.getFileName(item.serverPath))
                               playPosition = bindingAdapterPosition
                               isAutoFullWithSize = true
                           }else{
                               loadCoverImage("file://${item.localPath}",R.drawable.big_img_error)
                               setUpLazy("file://${item.localPath}",true,null,null,FileUtils.getFileName(item.localPath))
                           }
                       }
                   }
               }

               ImageTextModel.AUDIO ->{
                   audioPlayView.let {
                       it.visiable()
                       if(FileUtils.isFileExist(item.localPath)){
                           it.url = "file://${item.localPath}"
                       }else{
                            if(item.haveNetServer){
                                it.url = item.serverPath
                            }else{
                                it.setPlayError("当前播放文件异常！")
                            }
                       }
                   }
               }
               else -> {

               }
           }

       }
    }
}