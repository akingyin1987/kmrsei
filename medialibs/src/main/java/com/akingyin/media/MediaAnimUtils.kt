/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.RotateAnimation
import android.widget.ImageView

/**
 *
 * 动画相关
 * @ Description:
 * @author king
 * @ Date 2020/7/14 17:09
 * @version V1.0
 */
object MediaAnimUtils {

    private const val DURATION = 450

    fun zoom(view: View, isZoomAnim: Boolean) {
        if (isZoomAnim) {
            val set = AnimatorSet()
            set.playTogether(
                    ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.12f),
                    ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.12f)
            )
            set.duration = DURATION.toLong()
            set.start()
        }
    }

    fun disZoom(view: View, isZoomAnim: Boolean) {
        if (isZoomAnim) {
            val set = AnimatorSet()
            set.playTogether(
                    ObjectAnimator.ofFloat(view, "scaleX", 1.12f, 1f),
                    ObjectAnimator.ofFloat(view, "scaleY", 1.12f, 1f)
            )
            set.duration = DURATION.toLong()
            set.start()
        }
    }

    /**
     * 箭头旋转动画
     *
     * @param arrow
     * @param flag
     */
    fun rotateArrow(arrow: ImageView, flag: Boolean) {
        val pivotX = arrow.width / 2f
        val pivotY = arrow.height / 2f
        // flag为true则向上
        val fromDegrees = if (flag) 180f else 180f
        val toDegrees = if (flag) 360f else 360f
        //旋转动画效果   参数值 旋转的开始角度  旋转的结束角度  pivotX x轴伸缩值
        val animation = RotateAnimation(fromDegrees, toDegrees,
                pivotX, pivotY)
        //该方法用于设置动画的持续时间，以毫秒为单位
        animation.duration = 350
        //启动动画
        arrow.startAnimation(animation)
    }
}