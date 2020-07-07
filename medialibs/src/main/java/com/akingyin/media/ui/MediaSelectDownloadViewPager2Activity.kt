/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.ui

import com.akingyin.base.config.AppFileConfig
import com.akingyin.base.ext.messageFormat
import com.akingyin.base.utils.FileUtils
import com.akingyin.media.DownloadFileUtil
import com.akingyin.media.R
import com.akingyin.media.model.ImageTextModel
import kotlinx.android.synthetic.main.activity_media_select_download_viewpager2.*

/**
 * 可选择及下载文件类 多媒体浏览
 * @ Description:
 * @author king
 * @ Date 2020/7/7 17:42
 * @version V1.0
 */
class MediaSelectDownloadViewPager2Activity : MediaViewPager2Activity() {

    override fun getLayoutId()= R.layout.activity_media_select_download_viewpager2

    override fun onBindAdapter() {
        mediaViewpager2Adapter.showChecked = true
        viewpager.adapter = mediaViewpager2Adapter
    }

    override fun onCheckedItem(imageTextModel: ImageTextModel) {
        button_apply.text="使用({0})".messageFormat(mediaViewpager2Adapter.getCheckedNum())
    }

    override fun downloadItemFile(imageTextModel: ImageTextModel) {
        showLoading()
        DownloadFileUtil.coordinateDownloadFile(imageTextModel.serverPath,AppFileConfig.APP_FILE_ROOT,FileUtils.getFileName(imageTextModel.serverPath)){
            result,str->
            hideLoadDialog()
            if(result){
                showSucces("下载成功,$str")
            }else{
                showError("下载失败,$str")
            }
        }
    }
}