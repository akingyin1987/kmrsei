/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.ui

import androidx.viewpager2.widget.ViewPager2
import com.akingyin.base.config.AppFileConfig
import com.akingyin.base.ext.click
import com.akingyin.base.ext.messageFormat
import com.akingyin.base.utils.FileUtils
import com.akingyin.media.DownloadFileUtil
import com.akingyin.media.R
import com.akingyin.media.databinding.ActivityMediaSelectDownloadViewpager2Binding
import com.akingyin.media.model.MediaDataModel
import java.io.File



/**
 * 可选择及下载文件类 多媒体浏览
 * @ Description:
 * @author king
 * @ Date 2020/7/7 17:42
 * @version V1.0
 */
class MediaSelectDownloadViewPager2Activity : MediaViewPager2Activity() {

    override fun getLayoutId()= R.layout.activity_media_select_download_viewpager2

    override fun useViewBind()=true

    lateinit var bindView:ActivityMediaSelectDownloadViewpager2Binding

    override fun initViewBind() {

        bindView = ActivityMediaSelectDownloadViewpager2Binding.inflate(layoutInflater)
        setContentView(bindView.root)
    }

    override fun onBindAdapter() {
        mediaViewpager2Adapter.showChecked = true
        mediaViewpager2Adapter.supportDownload = true
        bindView.viewpager.adapter = mediaViewpager2Adapter
    }

    override fun initView() {
        super.initView()
        bindView.buttonBack.click {
            onBackPressed()
        }

        bindView.buttonApply.click {

           onBackPressed()
        }
    }

    override fun onCheckedItem(imageTextModel: MediaDataModel) {
        bindView.buttonApply.text="使用({0})".messageFormat(mediaViewpager2Adapter.getCheckedNum())
    }

    override fun initMediaData(data: List<MediaDataModel>): MutableList<MediaDataModel> {

        return data.map {
            it.checked = true
            it
        }.toMutableList()
    }

    override fun initMediaDataAfter() {
        bindView.buttonApply.text="使用({0})".messageFormat(mediaViewpager2Adapter.getCheckedNum())

    }

    override fun getViewPageView(): ViewPager2 {
        return bindView.viewpager
    }

    override fun downloadItemFile(imageTextModel: MediaDataModel) {
        showLoading()

        val  filePath = AppFileConfig.APP_FILE_ROOT+ File.separator+FileUtils.getFileName(imageTextModel.serverPath)

        if(FileUtils.isFileExist(filePath)){
            imageTextModel.localPath = filePath
            hideLoadDialog()
            showSucces("当前文件已存在：$filePath")
            mediaViewpager2Adapter.notifyDataSetChanged()
            return
        }
        DownloadFileUtil.coordinateDownloadFile(imageTextModel.serverPath,AppFileConfig.APP_FILE_ROOT,FileUtils.getFileName(imageTextModel.serverPath)){
            result,str->
            hideLoadDialog()
            if(result){
                imageTextModel.localPath = filePath
                mediaViewpager2Adapter.notifyDataSetChanged()
                showSucces("下载成功,$str")
            }else{
                showError("下载失败,$str")
            }
        }
    }


}