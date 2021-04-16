/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import com.akingyin.base.SimpleFragment
import com.akingyin.media.R
import com.akingyin.media.adapter.MediaViewpager2Adapter
import com.akingyin.media.databinding.FragmentMedialViewpager2Binding
import com.akingyin.media.model.MediaDataListModel
import com.akingyin.media.model.MediaDataModel
import com.akingyin.media.ui.MediaTypeViewpagerActivity

import timber.log.Timber

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/8 16:00
 * @version V1.0
 */
class MediaViewPager2Fragment :SimpleFragment(){

    override fun getLayoutId() = R.layout.fragment_medial_viewpager2

    lateinit var  mediaViewpager2Adapter: MediaViewpager2Adapter
    override fun injection() {

    }

    override fun useViewBind()=true
    lateinit var viewBinding:FragmentMedialViewpager2Binding
    override fun initViewBind(inflater: LayoutInflater, container: ViewGroup?): View {
        viewBinding = FragmentMedialViewpager2Binding.inflate(inflater,container,false)
        return viewBinding.root
    }

    /** 父viewpager2 */
    lateinit var  mainViewPager:ViewPager2
    var imageTextList:MediaDataListModel?= null
    var titleName=""
    override fun initEventAndData() {
         imageTextList = arguments?.getParcelable("data")?:MediaDataListModel()
         titleName = arguments?.getString("titleName","")?:""
    }

    override fun initView() {
        mainViewPager = (activity as MediaTypeViewpagerActivity).viewBinding.viewpager
        mediaViewpager2Adapter = MediaViewpager2Adapter(fragmentManager = childFragmentManager)
        mediaViewpager2Adapter.setDiffCallback(object :DiffUtil.ItemCallback<MediaDataModel>(){
            override fun areItemsTheSame(oldItem: MediaDataModel, newItem: MediaDataModel): Boolean {
                return oldItem.objectId == newItem.objectId
            }

            override fun areContentsTheSame(oldItem: MediaDataModel, newItem: MediaDataModel): Boolean {
                return oldItem.toString() == newItem.toString()
            }
        })
        viewBinding.fragmentViewpager.adapter = mediaViewpager2Adapter
        viewBinding.fragmentViewpager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            private   var currentPosition = 0
            private  var oldPositon = 0

            //页面滚动的位置信息回调： position 当前滚动到哪个页面，positionOffset 位置偏移百分比, positionOffsetPixels 当前所在页面偏移量
            //此回调会触发完onPageScrollStateChanged 的 state 值为1时后面才触发回调
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                currentPosition = position
            }
            // 滚动状态改变回调，state的值分别有0，1，2 ;
            // 0为ViewPager所有事件(1,2)已结束触发
            // 1为在viewPager里按下并滑动触发多次
            // 2是手指抬起触发
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if(state == 0){
                    if(currentPosition == oldPositon){
                        when (currentPosition) {
                            0 -> {
                               Timber.d("到达最左一个继续往左滑....")
                                //若还有上一个bottom fragment页面则切换
                                mainViewPager.currentItem.takeIf { it > 0 }
                                        ?.also { (activity as MediaTypeViewpagerActivity).switchFragment(it - 1) }
                            }

                            mediaViewpager2Adapter.itemCount - 1 -> {
                                Timber.d("到达最右一个继续往右滑....")
                                //若还有下一个bottom fragment页面则切换
                                mainViewPager.currentItem.takeIf { it < mainViewPager.adapter!!.itemCount - 1 }
                                        ?.also { (activity as MediaTypeViewpagerActivity).switchFragment(it + 1) }
                            }
                        }

                    }
                    oldPositon = currentPosition
                }
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                println("$titleName,$position")
            }
        })
    }

    override fun lazyLoad() {
        println("加载数据->${titleName}")
       imageTextList?.items?.let {
           mediaViewpager2Adapter.setDiffNewData(it.toMutableList())
       }
    }

    companion object{
        fun  newInstance(imageTextList: MediaDataListModel, titleName:String=""):MediaViewPager2Fragment{
            return MediaViewPager2Fragment().apply {
                arguments= Bundle().apply {
                    putParcelable("data",imageTextList)
                    putString("titleName",titleName)
                }
            }
        }
    }
}