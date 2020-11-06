package com.akingyin.fitter.adapter

import android.view.View
import android.widget.FrameLayout


interface MenuAdapter {
    /**
     * 设置筛选条目个数
     */
    fun getMenuCount(): Int

    /**
     * 设置每个筛选器默认Title
     */
    fun getMenuTitle(position: Int): String

    /**
     * 设置每个筛选条目距离底部距离
     */
    fun getBottomMargin(position: Int): Int

    /**
     * 设置每个筛选条目的View
     */
    fun getView(position: Int, parentContainer: FrameLayout): View
}