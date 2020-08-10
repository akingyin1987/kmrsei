/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.camera.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.ViewConfiguration
import com.akingyin.media.camera.PinchToZoomGestureDetector

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/27 17:06
 * @version V1.0
 */
@SuppressLint("ClickableViewAccessibility")
class CameraSurfaceView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr) {
    var  pinchToZoomGestureDetector:PinchToZoomGestureDetector?=null
    var  onSurfaceViewListion:OnSurfaceViewListion?= null



    // For accessibility event
    private var mUpEvent: MotionEvent? = null
    // For tap-to-focus
    private var mDownEventTimestamp: Long = 0
    private fun delta(): Long {
        return System.currentTimeMillis() - mDownEventTimestamp
    }

    private fun isZoomSupported(): Boolean {
        return pinchToZoomGestureDetector?.listion?.isZoomSupported()?:false
    }
    private fun isPinchToZoomEnabled():Boolean{
        return pinchToZoomGestureDetector?.listion?.isPinchToZoomEnabled()?:false
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {

        if(isPinchToZoomEnabled()){
            pinchToZoomGestureDetector?.onTouchEvent(event)
        }
        if (event.pointerCount == 2 && isPinchToZoomEnabled() && isZoomSupported()) {
            return true
        }
        // Camera focus
        when (event.action) {
            MotionEvent.ACTION_DOWN -> mDownEventTimestamp = System.currentTimeMillis()
            MotionEvent.ACTION_UP -> if (delta() < ViewConfiguration.getLongPressTimeout()) {
                mUpEvent = event
                onSurfaceViewListion?.onFouceClick(event.x,event.y)
                performClick()
            }
            else ->                 // Unhandled event.
                return false
        }

        return true
    }

    override fun performContextClick(): Boolean {
        mUpEvent = null
        return super.performContextClick()
    }

    interface  OnSurfaceViewListion{
        fun   onFouceClick(x:Float,y:Float)

    }
}