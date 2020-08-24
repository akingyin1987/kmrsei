/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.doodle.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape


/**
 * @ Description:
 * @author king
 * @ Date 2020/8/19 15:29
 * @version V1.0
 */
class MyShapeDrawable(shape: Shape) : ShapeDrawable(shape) {
    //Paint.ANTI_ALIAS_FLAG代表这个画笔的图形是光滑的
     var mStrokePaint: Paint = Paint()


    override fun onDraw(shape: Shape?, canvas: Canvas?, paint: Paint?) {
        //绘制填充效果的图形
        shape?.draw(canvas, mStrokePaint)
    }
}