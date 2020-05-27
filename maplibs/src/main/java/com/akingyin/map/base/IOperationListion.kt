/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.map.base

import android.view.View
import android.widget.TextView
import com.akingyin.map.IMarker


/**
 * @ Description:
 * @author king
 * @ Date 2020/5/25 17:55
 * @version V1.0
 */
interface IOperationListion<T : IMarker> {

    /**
     * 初始化操作名称
     * @param left
     * @param center
     * @param right
     */
    fun initView(left: TextView?, center: TextView?, right: TextView?, postion: Int,
                 iMarkerModel: T?, vararg views: View?)

    /**
     * 点击操作
     * @param postion  当前位置
     * @param iMarkerModel  当前对象
     */
    fun onOperation(postion: Int, iMarkerModel: T)

    /**
     * 路径规划
     * @param postion
     * @param iMarkerModel
     */
    fun onPathPlan(postion: Int, iMarkerModel: T)

    /**
     * 图文信息
     * @param postion
     * @param iMarkerModel
     */
    fun onTuWen(postion: Int, iMarkerModel: T)

    /**
     * 点击详情图片
     * @param postion
     * @param iMarkerModel
     */
    fun onObjectImg(postion: Int, iMarkerModel: T, view: View?)

    /**
     * 其它操作
     * @param postion
     * @param iMarkerModel
     * @param view
     */
    fun onOtherOperation(postion: Int, iMarkerModel: T, view: View?)
}