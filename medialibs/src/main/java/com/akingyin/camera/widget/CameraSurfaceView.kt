/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.camera.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceView

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/27 17:06
 * @version V1.0
 */
class CameraSurfaceView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr) {

    var  onSurfaceViewListion:OnSurfaceViewListion?=null
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            if(it.pointerCount == 1 && event.action ==  MotionEvent.ACTION_DOWN){
                 onSurfaceViewListion?.onFouceClick(event.x,event.y)
                return true
            }
        }
        return super.onTouchEvent(event)
    }


    interface  OnSurfaceViewListion{
        fun   onFouceClick(x:Float,y:Float)

    }
}