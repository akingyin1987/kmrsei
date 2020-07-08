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
import com.akingyin.media.R
import com.akingyin.media.adapter.MediaViewpager2Adapter
import com.akingyin.media.model.ImageTextModel
import com.akingyin.media.model.ImageTextTypeList
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_media_type_viewpager2.*


/**
 * 多媒体 浏览及下载选择
 * @ Description:
 * @author king
 * @ Date 2020/7/8 15:43
 * @version V1.0
 */
class MediaTypeViewpagerActivity : SimpleActivity(){

    override fun initInjection() {

    }

    override fun getLayoutId() = R.layout.activity_media_type_viewpager2

    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }
    var data: ImageTextTypeList? =null
    lateinit var  mediaViewpager2Adapter: MediaViewpager2Adapter

    override fun initView() {
         data = intent.getSerializableExtra("data") as ImageTextTypeList
         data?.items?.let {
             val listData = mutableListOf<ImageTextModel>()
             it.forEach {
                 typeModel->
                typeModel.items?.let {
                    list ->
                    tablayout.addTab(tablayout.newTab().setText(typeModel.text))
                     listData.addAll(list)
                }

             }

             mediaViewpager2Adapter = MediaViewpager2Adapter()
             viewpager.adapter = mediaViewpager2Adapter
             mediaViewpager2Adapter.setDiffCallback(object : DiffUtil.ItemCallback<ImageTextModel>(){
                 override fun areItemsTheSame(oldItem: ImageTextModel, newItem: ImageTextModel): Boolean {
                     return oldItem.objectId == newItem.objectId
                 }

                 override fun areContentsTheSame(oldItem: ImageTextModel, newItem: ImageTextModel): Boolean {
                     return oldItem.toString() == newItem.toString()
                 }
             })
             mediaViewpager2Adapter.setDiffNewData(listData)
             viewpager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){

                 override fun onPageSelected(position: Int) {
                     super.onPageSelected(position)

                     tablayout.selectTab(tablayout.getTabAt(getTablayPostionByViewpagerPos(position)))
                 }
             })
             tablayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
                 override fun onTabReselected(tab: TabLayout.Tab?) {

                 }

                 override fun onTabUnselected(tab: TabLayout.Tab?) {

                 }

                 override fun onTabSelected(tab: TabLayout.Tab?) {
                     tab?.let {
                         itab->
                         viewpager.currentItem = getViewpagerPostionByTabPos(itab.position)
                     }
                 }
             })
         }?:finish()
    }

    override fun startRequest() {

    }

    fun  getViewpagerPostionByTabPos(position: Int):Int{
        return data?.items?.let {
            var  len = 0
            it.forEachIndexed { index, imageTextTypeModel ->
                len += (imageTextTypeModel.items?.size ?: 0)
                if(position == index){
                    return@let len
                }
            }
            0
        }?:0
    }

    fun   getTablayPostionByViewpagerPos(position: Int):Int{
      return  data?.items?.let {
          var  len = 0
          it.forEachIndexed { index, imageTextTypeModel ->
              len += (imageTextTypeModel.items?.size ?: 0)
              if(position<= len-1){
                  return@let index
              }
          }
          0
        }?:0
    }

    fun  switchFragment(position: Int){
        viewpager.currentItem = position
    }


}