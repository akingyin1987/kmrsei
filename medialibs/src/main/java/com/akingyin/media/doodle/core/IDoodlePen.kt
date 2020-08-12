/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.doodle.core

import android.graphics.Canvas

/**
 * 画笔
 * @ Description:
 * @author king
 * @ Date 2020/8/11 11:32
 * @version V1.0
 */
interface IDoodlePen {



    /**
     * 深度拷贝
     * @return
     */
    fun copy(): IDoodlePen

    /**
     * 绘制画笔辅助工具，由IDoodle绘制，不属于IDoodleItem的内容
     * 比如可以用于仿制功能时 定位器的绘制
     *
     * @param canvas
     * @param doodle
     */
    fun drawHelpers(canvas: Canvas, doodle: IDoodle)


    /**
     * 获取涂鸦View
     */
    fun  getIdoodleView():IDoodle


    /**
     * 获取画布
     */
    fun  getIdoodleCanvas():Canvas



}