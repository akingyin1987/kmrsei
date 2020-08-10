/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.camera

import android.graphics.Point
import kotlin.math.max
import kotlin.math.min


/**
 * @ Description:
 * @author king
 * @ Date 2020/7/29 16:16
 * @version V1.0
 */
data class CameraSize (var width:Int,var hight:Int,var defaultSize :Boolean= false,var checked:Boolean = false){
    override fun toString(): String {
        return CameraManager.getCameraResolutionScale(Point(width, hight)) + max(width, hight) + "x" + min(width, hight) + (if (defaultSize) "[默认]" else "") + if (checked) "[当前]" else ""
    }
}