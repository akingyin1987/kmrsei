/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.adapter

import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import cn.jzvd.JzvdStd
import com.akingyin.media.audio.AudioPlayView
import com.akingyin.base.ext.click
import com.akingyin.base.ext.gone
import com.akingyin.base.ext.visiable
import com.akingyin.base.mvvm.SingleLiveEvent
import com.akingyin.base.utils.FileUtils
import com.akingyin.media.R
import com.akingyin.media.engine.ImageEngine
import com.akingyin.media.engine.LocationEngine
import com.akingyin.media.glide.GlideEngine
import com.akingyin.media.glide.GlideImageView
import com.akingyin.media.glide.OnProgressListener
import com.akingyin.media.glide.progress.CircleProgressView
import com.akingyin.media.model.MediaDataModel
import com.akingyin.media.ui.fragment.MedialFileInfoFragmentDialog
import com.akingyin.media.widget.CheckView
import com.akingyin.util.VideoCacheProxy

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
class MediaViewpager2Adapter(var imageEngine: ImageEngine= GlideEngine.getGlideEngineInstance(),
                             var locationEngine: LocationEngine?=null,
                             var fragmentManager: FragmentManager?=null) : BaseQuickAdapter<MediaDataModel,BaseViewHolder>(R.layout.item_media_viewpager) {

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

    override fun convert(holder: BaseViewHolder, item: MediaDataModel) {
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
           if(FileUtils.isFileExist(item.localPath)){
               getView<ImageButton>(R.id.info_button).apply {
                   visiable()
                   click {
                       fragmentManager?.let {
                           MedialFileInfoFragmentDialog.newInstance(item.localPath,locationEngine = locationEngine,imageEngine = imageEngine).show(it,"media-info")
                       }

                   }
               }
           }else{
               getView<ImageButton>(R.id.info_button).gone()
           }

           setText(R.id.tv_page, MessageFormat.format("{0}/{1}",bindingAdapterPosition+1,getDefItemCount()))
           val downloadView :ImageView = getView(R.id.iv_download)
           downloadView.click {
               downloadLiveEvent.value = bindingAdapterPosition
           }
           setText(R.id.tv_tips,item.title)
           val textView : TextView = getView(R.id.tv_text)
           textView.gone()
           val glideImageView: GlideImageView = getView(R.id.iv_image)
           glideImageView.gone()
           val circleProgressView = getView<CircleProgressView>(R.id.progressView)
           circleProgressView.gone()
           val sampleCoverVideo : JzvdStd =  getView(R.id.video_player)
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
              MediaDataModel.TEXT ->{
                  downloadView.gone()
                  textView.visiable()
                  textView.text = item.text
              }

               MediaDataModel.IMAGE ->{
                   glideImageView.let {
                       it.visiable()
                       if(FileUtils.isFileExist(item.localPath)){
                           imageEngine.loadImage(it.context,item.localPath,it)
                       }else{
                           if(item.haveNetServer){
                               circleProgressView.visiable()
                               circleProgressView.progress = 0
                               circleProgressView.max = 100
                               it.load(item.serverPath,R.drawable.ic_img_loading_error,object :OnProgressListener{
                                   override fun onProgress(isComplete: Boolean, percentage: Int, bytesRead: Long, totalBytes: Long) {

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

               MediaDataModel.VIDEO ->{
                   sampleCoverVideo.run {
                       visiable()
                       if(FileUtils.isFileExist(item.localPath)){
                           setUp("file://${item.localPath}",FileUtils.getFileName(item.localPath))
                           imageEngine.loadImage(context,"file://${item.localPath}",posterImageView,null,null)
                       }else{

                           if(item.haveNetServer){
                               setUp(VideoCacheProxy.getProxy(context).getProxyUrl(item.serverPath),FileUtils.getFileName(item.serverPath))
                               imageEngine.loadImage(context,item.serverPath,posterImageView,null,null)
                           }else{
                              posterImageView.setImageResource(R.drawable.ic_img_loading_error)
                           }
                       }
                   }
               }

               MediaDataModel.AUDIO ->{
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