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
import android.view.OrientationEventListener
import java.lang.ref.WeakReference
import kotlin.math.abs

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/20 12:18
 * @version V1.0
 */
class CameraSensorController constructor(context: Context) {

    private var mContextWeakReference: WeakReference<Context> = WeakReference(context)

    private var mLastOrientation = 0

    private var rotation = 90
    var mOrientationChangeListener: OrientationChangeListener? = null



    var orientationEventListener: OrientationEventListener

    init {


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
                            val relative_orientation = (mLastOrientation + rotation) % 360
                            val ui_rotation = (360 - relative_orientation) % 360
                            mOrientationChangeListener?.onChange(relative_orientation, ui_rotation)
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