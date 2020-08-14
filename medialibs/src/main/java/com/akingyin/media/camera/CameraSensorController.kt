/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.camera
import android.app.Activity
import android.content.Context
import android.view.OrientationEventListener
import kotlin.math.abs

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/20 12:18
 * @version V1.0
 */
class CameraSensorController constructor(context:Context,rotation:Int = 90) {



    private var mLastOrientation = 0

    private var rotation = 90
    var mOrientationChangeListener: OrientationChangeListener? = null



    var orientationEventListener: OrientationEventListener

    init {
       this.rotation = rotation
        orientationEventListener = object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) {
                    return
                }
                var diff = abs(orientation - mLastOrientation)
                if (diff > 180) {
                    diff = 360 - diff
                }
                if (diff > 60) {
                    var orientation2 = (orientation + 45) / 90 * 90
                    orientation2 %= 360
                    if (orientation2 != mLastOrientation) {
                        mLastOrientation = orientation2
                        if (null != mOrientationChangeListener) {
                            val relativeOrientation = (mLastOrientation + rotation) % 360
                            val uiRotation = (360 - relativeOrientation) % 360
                            mOrientationChangeListener?.onChange(relativeOrientation, uiRotation)
                        }
                    }
                }
            }
        }
    }





    fun onResume() {
        orientationEventListener.enable()
    }

    fun onPause() {
        orientationEventListener.disable()
    }


    interface OrientationChangeListener {
        fun onChange(relativeRotation: Int, uiRotation: Int)
    }
}