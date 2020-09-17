/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.camerax.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import androidx.viewpager2.adapter.FragmentStateAdapter
import com.akingyin.base.SimpleFragment
import com.akingyin.media.R
import java.io.File

/**
 * @ Description:
 * @author king
 * @ Date 2020/9/17 10:55
 * @version V1.0
 */
class GalleryFragment internal constructor(): SimpleFragment(){
    private lateinit var mediaList: MutableList<File>
    override fun injection() {

    }

    override fun getLayoutId()= R.layout.fragment_gallery

    override fun initEventAndData() {

    }

    override fun initView() {

    }

    override fun lazyLoad() {

    }

    /** Adapter class used to present a fragment containing one photo or video as a page */
    inner class MediaPagerAdapter(fm: FragmentManager) : FragmentStateAdapter(fm, lifecycle) {
        override fun getItemCount() = mediaList.size

        override fun createFragment(position: Int): Fragment {
           return PhotoFragment.create(mediaList[position])
        }
    }
}