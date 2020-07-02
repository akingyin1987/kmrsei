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
import com.akingyin.base.ext.messageFormat
import com.akingyin.base.ext.visiable
import com.akingyin.base.utils.FileUtils
import com.akingyin.media.ImageLoadUtil
import com.akingyin.media.R
import com.akingyin.media.model.ImageTextModel
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.shuyu.gsyvideoplayer.video.GSYADVideoPlayer


/**
 * 基于viewpager2 图文 适配器
 * @ Description:
 * @author king
 * @ Date 2020/7/2 16:29
 * @version V1.0
 */
class MediaViewpager2Adapter : BaseQuickAdapter<ImageTextModel,BaseViewHolder>(R.layout.item_media_viewpager) {

    override fun convert(holder: BaseViewHolder, item: ImageTextModel) {
       with(holder){
           setText(R.id.tv_page,"{0}/{1}".messageFormat(bindingAdapterPosition+1,getDefItemCount()))
           when(item.multimediaType){
              ImageTextModel.TEXT ->{
                  getView<ImageView>(R.id.iv_download).gone()
                  getView<TextView>(R.id.tv_text).text = item.text
              }

               ImageTextModel.IMAGE ->{
                   if(item.downloadPath.isEmpty()){
                       getView<ImageView>(R.id.iv_download).gone()
                   }else{
                       getView<ImageView>(R.id.iv_download).visiable()
                   }
                   getView<ImageView>(R.id.iv_image).let {
                       it.visiable()
                       if(FileUtils.isFileExist(item.localPath)){
                           ImageLoadUtil.loadImageLocalFile(item.localPath,context,it)
                       }else{
                           if(item.haveNetServer){
                               ImageLoadUtil.loadImageServerFile(item.serverPath,context,it)
                           }else{
                               it.setImageResource(R.drawable.big_img_error)
                           }
                       }
                   }

               }

               ImageTextModel.VIDEO ->{
                   if(item.downloadPath.isEmpty()){
                       getView<ImageView>(R.id.iv_download).gone()
                   }else{
                       getView<ImageView>(R.id.iv_download).visiable()
                   }
                   getView<GSYADVideoPlayer>(R.id.video_player).run {
                       visiable()
                       if(FileUtils.isFileExist(item.localPath)){
                           setUpLazy("file://${item.localPath}",true,null,null,FileUtils.getFileName(item.localPath))
                           playPosition = bindingAdapterPosition
                           isAutoFullWithSize = true
                       }else{
                           if(item.haveNetServer){
                               setUpLazy(item.serverPath,true,null,null,FileUtils.getFileName(item.localPath))
                               playPosition = bindingAdapterPosition
                               isAutoFullWithSize = true
                           }else{
                               setUpLazy("file://${item.localPath}",true,null,null,FileUtils.getFileName(item.localPath))
                           }
                       }
                   }
               }

               ImageTextModel.AUDIO ->{
                   if(item.downloadPath.isEmpty()){
                       getView<ImageView>(R.id.iv_download).gone()
                   }else{
                       getView<ImageView>(R.id.iv_download).visiable()
                   }
                   getView<AudioPlayView>(R.id.audio_player).let {
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