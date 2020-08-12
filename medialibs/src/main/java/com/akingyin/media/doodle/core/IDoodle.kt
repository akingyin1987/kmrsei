/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.doodle.core

import android.view.MotionEvent
import androidx.annotation.IntDef



/**
 * @ Description:
 * @author king
 * @ Date 2020/8/10 16:07
 * @version V1.0
 */
interface IDoodle {


    /**
     *
     * 获取当前涂鸦坐标系中的单位大小，该单位参考dp，独立于图片
     *
     * @return
     */
   fun getUnitSize():Float

    /**
     * 设置图片旋转值
     *
     * @param degree
     */
   fun  setDoodleRotation(degree:Int)

    /**
     * 获取图片旋转值
     *
     * @return
     */
   fun  getDoodleRotation():Int

    /**
     * 设置图片缩放倍数
     *
     * @param scale
     * @param pivotX
     * @param pivotY
     */
    fun  setDoodleScale(scale:Float,pivotX:Float,pivotY:Float)


    /**
     * 获取图片缩放倍数
     */
    fun getDoodleScale(): Float



    abstract fun flipCurrentSticker(@Flip direction:Int)

    @IntDef(flag = true, value = [Flip.FLIP_HORIZONTALLY, Flip.FLIP_VERTICALLY])
    @Retention(AnnotationRetention.SOURCE)
    annotation class Flip{
        companion object{
            const val FLIP_HORIZONTALLY = 1
            const val FLIP_VERTICALLY = 1 shl 1
        }
    }

     fun  zoomAndRotateCurrentSticker(event:MotionEvent)

    /**
     * 移除当前操作的图形
     */
    fun  removeCurrentDoodeShape()
}