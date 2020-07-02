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
import com.akingyin.media.model.ImageTextList
import com.akingyin.media.model.ImageTextModel
import kotlinx.android.synthetic.main.activity_media_viewpager2_info.*
import kotlin.properties.Delegates

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/2 17:56
 * @version V1.0
 */
class MediaViewPager2Activity : SimpleActivity(){

     var  data:ImageTextList by Delegates.observable(initialValue = ImageTextList(),onChange = {

        property, oldValue, newValue ->

     })

    override fun initInjection() {

    }

    override fun getLayoutId()= R.layout.activity_media_viewpager2_info

    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    lateinit var  mediaViewpager2Adapter: MediaViewpager2Adapter
    override fun initView() {
        data = intent.getSerializableExtra("data") as ImageTextList
        data.items?.let {
            mediaViewpager2Adapter = MediaViewpager2Adapter()
            viewpager.adapter = mediaViewpager2Adapter
            mediaViewpager2Adapter.setDiffCallback(object :DiffUtil.ItemCallback<ImageTextModel>(){
                override fun areItemsTheSame(oldItem: ImageTextModel, newItem: ImageTextModel): Boolean {
                   return  oldItem.objectId == newItem.objectId
                }

                override fun areContentsTheSame(oldItem: ImageTextModel, newItem: ImageTextModel): Boolean {
                    return  oldItem.toString() == newItem.toString()
                }
            })
        }?:finish()
    }

    override fun startRequest() {

    }

    companion object{
        fun  startMediaViewPager(context: Context,imageTextList: ImageTextList){
            context.startActivity(Intent(context,MediaViewPager2Activity::class.java).apply {
                putExtra("data",imageTextList)
            })
        }
    }
}