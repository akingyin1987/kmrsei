/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.akingyin.base.SimpleActivity
import com.akingyin.media.R
import com.akingyin.media.adapter.MediaViewpager2Adapter
import com.akingyin.media.model.MediaDataListModel
import com.akingyin.media.model.MediaDataModel
import com.shuyu.gsyvideoplayer.GSYVideoManager
import kotlinx.android.synthetic.main.activity_media_viewpager2_info.*


/**
 * @ Description:
 * @author king
 * @ Date 2020/7/2 17:56
 * @version V1.0
 */
open class MediaViewPager2Activity : SimpleActivity() {

    var data: MediaDataListModel = MediaDataListModel()

    override fun initInjection() {

    }

    override fun getLayoutId() = R.layout.activity_media_viewpager2_info

    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    lateinit var mediaViewpager2Adapter: MediaViewpager2Adapter
    override fun initView() {
        data = intent.getParcelableExtra("data")?:MediaDataListModel()
        data.items?.let {
            mediaViewpager2Adapter = MediaViewpager2Adapter()
            onBindAdapter()
            mediaViewpager2Adapter.downloadLiveEvent.observe(this,  { postion ->
                downloadItemFile(mediaViewpager2Adapter.getItem(postion))
            })
            mediaViewpager2Adapter.liveEvent.observe(this,  { postion ->
                onCheckedItem(mediaViewpager2Adapter.getItem(postion))
            })

            mediaViewpager2Adapter.setDiffCallback(object : DiffUtil.ItemCallback<MediaDataModel>() {
                override fun areItemsTheSame(oldItem: MediaDataModel, newItem: MediaDataModel): Boolean {
                    return oldItem.objectId == newItem.objectId
                }

                override fun areContentsTheSame(oldItem: MediaDataModel, newItem: MediaDataModel): Boolean {
                    return oldItem.toString() == newItem.toString()
                }
            })
            mediaViewpager2Adapter.setDiffNewData(it.toMutableList())
        } ?: finish()
    }

    override fun startRequest() {

    }


    /**
     * 绑定适配器
     */
    open fun onBindAdapter() {
        viewpager.adapter = mediaViewpager2Adapter
    }

    /**
     * 下载某项文件
     */
    open fun downloadItemFile(imageTextModel: MediaDataModel) {

    }

    /**
     * 选中某项文件
     */
    open fun onCheckedItem(imageTextModel: MediaDataModel) {

    }

    companion object {
        fun startMediaViewPager(context: Context, imageTextList: MediaDataListModel) {
            context.startActivity(Intent(context, MediaViewPager2Activity::class.java).apply {
                putExtra("data", imageTextList)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        GSYVideoManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        GSYVideoManager.onPause()
    }

    override fun onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
    }
}