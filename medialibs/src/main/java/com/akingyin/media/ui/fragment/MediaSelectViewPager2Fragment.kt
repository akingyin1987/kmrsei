/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
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
import com.akingyin.base.SimpleFragment
import com.akingyin.media.R
import com.akingyin.media.adapter.MediaViewpager2Adapter
import com.akingyin.media.databinding.FragmentMedialViewpager2Binding
import com.akingyin.media.model.MediaDataListModel

/**
 * @ Description:
 * @author king
 * @ Date 2021/4/16 15:33
 * @version V1.0
 */
class MediaSelectViewPager2Fragment : SimpleFragment() {

    lateinit var bindView:FragmentMedialViewpager2Binding
    lateinit var  mediaViewpager2Adapter: MediaViewpager2Adapter
    override fun injection() {

    }

    override fun getLayoutId()= R.layout.fragment_medial_viewpager2


    override fun useViewBind()=true

    override fun initViewBind(inflater: LayoutInflater, container: ViewGroup?): View {
        bindView = FragmentMedialViewpager2Binding.inflate(inflater,container,false)
        return bindView.root
    }

    override fun initEventAndData() {

    }

    override fun initView() {
        mediaViewpager2Adapter = MediaViewpager2Adapter(fragmentManager = childFragmentManager)
        bindView.fragmentViewpager.adapter = mediaViewpager2Adapter
        mediaViewpager2Adapter.showChecked = true


    }

    override fun lazyLoad() {

    }

    companion object{
        fun  newInstance(imageTextList: MediaDataListModel, titleName:String=""):MediaSelectViewPager2Fragment{
            return MediaSelectViewPager2Fragment().apply {
                arguments= Bundle().apply {
                    putParcelable("data",imageTextList)
                    putString("titleName",titleName)
                }
            }
        }
    }
}