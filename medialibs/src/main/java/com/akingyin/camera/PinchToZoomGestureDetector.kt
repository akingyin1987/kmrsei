/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.camera

import android.content.Context
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener


/**
 * @ Description:
 * @author king
 * @ Date 2020/7/30 13:49
 * @version V1.0
 */
class PinchToZoomGestureDetector(context: Context,myScaleGestureDetector: MyScaleGestureDetector,var listion:OnCamerZoomListion) : ScaleGestureDetector(context,myScaleGestureDetector), OnScaleGestureListener{

    init {
        myScaleGestureDetector.listener = this
    }

    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector?) {

    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        var scale = detector.scaleFactor

        // Speeding up the zoom by 2X.

        // Speeding up the zoom by 2X.
        scale = if (scale > 1f) {
            1.0f + (scale - 1.0f) * 2
        } else {
            1.0f - (1.0f - scale) * 2
        }

        var newRatio: Float = listion.getZoomRatio() * scale
        newRatio = rangeLimit(newRatio, listion.getMaxZoomRatio(), listion.getMinZoomRatio())
        listion.setZoomRatio(newRatio)
        return true

    }
    private fun rangeLimit(`val`: Float, max: Float, min: Float): Float {
        return `val`.coerceAtLeast(min).coerceAtMost(max)
    }
    interface  OnCamerZoomListion{
        fun   getZoomRatio():Float

        fun  getMaxZoomRatio():Float

        fun  getMinZoomRatio():Float

        fun  setZoomRatio(zoom:Float)

        fun  isZoomSupported():Boolean

        fun  isPinchToZoomEnabled():Boolean
    }
}