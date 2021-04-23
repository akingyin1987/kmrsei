/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import cn.jzvd.Jzvd
import com.akingyin.base.SimpleActivity
import com.akingyin.media.R
import com.akingyin.media.adapter.MediaViewpager2Adapter
import com.akingyin.media.databinding.ActivityMediaViewpager2InfoBinding
import com.akingyin.media.engine.LocationEngine
import com.akingyin.media.model.MediaDataListModel
import com.akingyin.media.model.MediaDataModel



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

    override fun useViewBind()=true

    lateinit var viewBinding:ActivityMediaViewpager2InfoBinding

    override fun initViewBind() {
        super.initViewBind()
        viewBinding = ActivityMediaViewpager2InfoBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }

    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    lateinit var mediaViewpager2Adapter: MediaViewpager2Adapter
    override fun initView() {
        data = intent.getParcelableExtra("data")?:MediaDataListModel()
        data.items?.let {
            mediaViewpager2Adapter = MediaViewpager2Adapter(fragmentManager = supportFragmentManager,locationEngine = getLocationEngine())

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
            mediaViewpager2Adapter.setDiffNewData(initMediaData(it))

            val post = intent.getIntExtra("postion",0)
            if(post>0){
                getViewPageView().currentItem = post
            }
            getViewPageView().registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    Jzvd.releaseAllVideos()
                }
            })

            initMediaDataAfter()
        } ?: finish()
    }

    override fun startRequest() {

    }

    open  fun  initMediaDataAfter(){

    }

    open  fun  getViewPageView():ViewPager2{
        return viewBinding.viewpager
    }

    open  fun  initMediaData(data:List<MediaDataModel>):MutableList<MediaDataModel>{
        return data.toMutableList()
    }

    /**
     * 绑定适配器
     */
    open fun onBindAdapter() {
        viewBinding.viewpager.adapter = mediaViewpager2Adapter

    }

    open fun getLocationEngine():LocationEngine?{
        return null
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
        fun startMediaViewPager(context: Context, imageTextList: MediaDataListModel,postion:Int = 0) {
            context.startActivity(Intent(context, MediaViewPager2Activity::class.java).apply {
                putExtra("data", imageTextList)
                putExtra("postion",postion)
            })
        }
    }



    override fun onPause() {
        super.onPause()
        Jzvd.releaseAllVideos()
    }

    override fun onBackPressed() {
        if (Jzvd.backPress()) {
            return
        }

        setResult(Activity.RESULT_OK,Intent().apply {
            putParcelableArrayListExtra("result",mediaViewpager2Adapter.data.filter {
                it.checked
            }.toMutableList()  as ArrayList<out Parcelable >)
        })
        super.onBackPressed()
    }


}