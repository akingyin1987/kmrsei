/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.camera

import android.view.ScaleGestureDetector


/**
 * @ Description:
 * @author king
 * @ Date 2020/7/30 13:46
 * @version V1.0
 */
class MyScaleGestureDetector :ScaleGestureDetector.SimpleOnScaleGestureListener(){
    var listener: ScaleGestureDetector.OnScaleGestureListener?=null
    override fun onScale(detector: ScaleGestureDetector?): Boolean {
        return listener?.onScale(detector)?:super.onScale(detector)
    }
}