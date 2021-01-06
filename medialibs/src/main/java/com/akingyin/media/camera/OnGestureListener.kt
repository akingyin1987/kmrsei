/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.camera

import android.content.Context
import android.graphics.PointF
import android.view.MotionEvent
import android.view.View
import kotlin.math.pow
import kotlin.math.sqrt


/**
 * 手势监听
 * @ Description:
 * @author king
 * @ Date 2020/7/30 12:20
 * @version V1.0
 */
abstract class OnGestureListener constructor(var context: Context): View.OnTouchListener{
    private var count = 0 //点击次数

    private var firstClick: Long = 0 //第一次点击时间

    private var secondClick: Long = 0 //第二次点击时间

    /**
     * 两次点击时间间隔，单位毫秒
     */
    private val totalTime = 500

    //记录第一根手指xy
    var first = PointF(0F, 0F)

    //第二根手指
    var sencond = PointF(0F, 0F)

    //两指间距离
    var distance = 0f

    //一个手势过程中总的偏移量(distance差值)
    var total = 0f

    abstract fun onStepFingerChange(total: Float, offset: Float)

    abstract fun onStepEnd()

    abstract fun onDoubleClick()

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_UP) {
            distance = 0f
            total = 0f
        }
        /***
         *  监听手势
         */
        /***
         * 监听手势
         */
        if (event.pointerCount > 1 && event.actionMasked == MotionEvent.ACTION_MOVE) {
            first.x = event.getX(0)
            first.y = event.getY(0)
            sencond.x = event.getX(1)
            sencond.y = event.getY(1)
            if (distance != 0f) {
                val offset = (getDistance(first, sencond) - distance).toInt()
                total += offset.toFloat()
                onStepFingerChange(total, offset.toFloat())
            }
            distance = getDistance(first, sencond)
            return true
        }

        //一根手指离开重置
        if (event.actionMasked == MotionEvent.ACTION_POINTER_UP) {
            distance = 0f
            onStepEnd()
        }

        /**
         * 监听双击
         */
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            count++
            when (count) {
                1 -> {
                    firstClick = System.currentTimeMillis()
                }
                2 -> {
                    secondClick = System.currentTimeMillis()
                    if (secondClick - firstClick < totalTime) {
                        onDoubleClick()
                    }
                    count = 0
                    firstClick = 0
                }
                else -> {
                    firstClick = secondClick
                    count = 1
                }
            }
            secondClick = 0
        }
        return true
    }

    //获取两指间距离
    open fun getDistance(a: PointF, b: PointF): Float {
        val py = ((b.x - a.x.toDouble()).pow(2.0) + (b.y - a.y.toDouble()).pow(2.0)).toInt()
        return sqrt(py.toDouble()).toFloat()
    }
}