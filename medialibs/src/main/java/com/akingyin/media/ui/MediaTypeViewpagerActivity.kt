/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.ui

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import com.akingyin.base.SimpleActivity
import com.akingyin.base.ext.click
import com.akingyin.media.MediaViewAndSelector
import com.akingyin.media.R
import com.akingyin.media.adapter.MediaViewpager2Adapter
import com.akingyin.media.databinding.ActivityMediaTypeViewpager2Binding
import com.akingyin.media.model.*



/**
 * 多媒体 浏览及下载选择
 * @ Description:
 * @author king
 * @ Date 2020/7/8 15:43
 * @version V1.0
 */
class MediaTypeViewpagerActivity : SimpleActivity() {

    override fun initInjection() {

    }

    override fun getLayoutId() = R.layout.activity_media_type_viewpager2

    override fun useViewBind()=true

    lateinit var viewBinding:ActivityMediaTypeViewpager2Binding

    override fun initViewBind() {
        super.initViewBind()
        viewBinding = ActivityMediaTypeViewpager2Binding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }

    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    var data: MediaIncludeMediaDataModel = MediaIncludeMediaDataModel()
    lateinit var mediaViewpager2Adapter: MediaViewpager2Adapter

    override fun initView() {
        MediaViewAndSelector.BuildConfig().build(activity = this)
        data = intent.getParcelableExtra("data")?:MediaIncludeMediaDataModel()
        data.items?.let {
            val listData = mutableListOf<MediaDataModel>()
            it.forEach { typeModel ->
                viewBinding.tablayout.addTab(viewBinding.tablayout.newTab().setText(typeModel.text))
                typeModel.items?.let {datas->
                    listData.addAll(datas)
                }


            }
            for (index in 0..viewBinding.tablayout.tabCount) {
                viewBinding.tablayout.getTabAt(index)?.view?.click {
                    val postion = getViewpagerPostionByTabPos(index)
                    if (postion != viewBinding.viewpager.currentItem) {
                        viewBinding.viewpager.currentItem = postion
                    }
                }
            }

            mediaViewpager2Adapter = MediaViewpager2Adapter()
            mediaViewpager2Adapter.showChecked = false
            viewBinding.viewpager.adapter = mediaViewpager2Adapter
            mediaViewpager2Adapter.setDiffCallback(object : DiffUtil.ItemCallback<MediaDataModel>() {
                override fun areItemsTheSame(oldItem: MediaDataModel, newItem: MediaDataModel): Boolean {
                    return oldItem.objectId == newItem.objectId
                }

                override fun areContentsTheSame(oldItem: MediaDataModel, newItem: MediaDataModel): Boolean {
                    return oldItem.toString() == newItem.toString()
                }
            })
            mediaViewpager2Adapter.setDiffNewData(listData)
            viewBinding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val index = getTablayPostionByViewpagerPos(position)
                    println("viewpager = $index")
                    if (index != viewBinding.tablayout.selectedTabPosition) {
                        viewBinding.tablayout.selectTab(viewBinding.tablayout.getTabAt(index))
                    }

                }
            })


        } ?: finish()
    }

    override fun startRequest() {

    }

    private fun getViewpagerPostionByTabPos(position: Int): Int {
        return data.items?.let {
            var len = 0
            it.forEachIndexed { index, imageTextTypeModel ->
                len += (imageTextTypeModel.items?.size ?: 0)
                if (position == index) {
                    println("size->>$len")
                    return@let len-(imageTextTypeModel.items?.size ?: 0)
                }
            }
            0
        } ?: 0
    }



    fun getTablayPostionByViewpagerPos(position: Int): Int {
        return data.items?.let {
            var len = 0
            it.forEachIndexed { index, imageTextTypeModel ->
                len += (imageTextTypeModel.items?.size ?: 0)
                if (position <= len - 1) {
                    println("index->>$len")
                    return@let index
                }
            }
            0
        } ?: 0
    }

    fun switchFragment(position: Int) {
        viewBinding.viewpager.currentItem = position
    }


}