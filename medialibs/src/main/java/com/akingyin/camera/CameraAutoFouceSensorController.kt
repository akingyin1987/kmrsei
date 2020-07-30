/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.camera

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs
import kotlin.math.sqrt


/**
 * 通过加速器 控制相机自动对焦
 * @ Description:
 * @author king
 * @ Date 2020/7/16 14:37
 * @version V1.0
 */
class CameraAutoFouceSensorController (var content:Context, var callBack:()->Unit) : SensorEventListener {

    private var mSensorManager: SensorManager? = null
    private var mSensor: Sensor? = null

    init {
        mSensorManager = content.getSystemService(Activity.SENSOR_SERVICE) as SensorManager
        if(null != mSensorManager){
            mSensor = mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }
    }


    val STATUS_NONE = 0
    val STATUS_STATIC = 1
    val STATUS_MOVE = 2
    private var mX = 0
    private  var mY:Int = 0
    private  var mZ:Int = 0
    private var STATUE = STATUS_NONE
    var canFocus = false
    var canFocusIn = false
    var isFocusing = false
    private val moveIs = 1.4
    private var lastStaticStamp: Long = 0
    val DELAY_DURATION = 500
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {
        println("run->onSensorChanged")
        if (event.sensor == null) {
            return
        }

        if (isFocusing) {
            restParams()
            return
        }

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0].toInt()
            val y = event.values[1].toInt()
            val z = event.values[2].toInt()
            val stamp = System.currentTimeMillis()

            if (STATUE != STATUS_NONE) {
                val px = abs(mX - x)
                val py = abs(mY - y)
                val pz = abs(mZ - z)
                val value = sqrt(px * px + py * py + (pz * pz).toDouble())
                if (value > moveIs) {
                    STATUE = STATUS_MOVE
                } else {
                    if (STATUE == STATUS_MOVE) {
                        lastStaticStamp = stamp
                        canFocusIn = true
                    }
                    if (canFocusIn) {
                        if (stamp - lastStaticStamp > DELAY_DURATION) {
                            //移动后静止一段时间，可以发生对焦行为
                            if (!isFocusing) {
                                canFocusIn = false
                                //                                onCameraFocus();
                                println("开始自动对焦---->>>>")
                                callBack.invoke()
                            }
                        }
                    }
                    STATUE = STATUS_STATIC
                }
            } else {
                lastStaticStamp = stamp
                STATUE = STATUS_STATIC
            }
            mX = x
            mY = y
            mZ = z
        }
    }

    /**
     * 注册
     */
    fun onRegisterSensor() :Boolean {
        restParams()
        canFocus = true
        return mSensorManager?.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL)?:false
    }


    /**
     *取消注册
     */
    fun unRegisterSensor()  {
        mSensorManager?.unregisterListener(this, mSensor)
        canFocus = false
    }


    private fun restParams() {
        STATUE = STATUS_NONE
        canFocusIn = false
        mX = 0
        mY = 0
        mZ = 0
    }

    /**
     * 对焦是否被锁定
     * @return
     */
    fun isFocusLocked(): Boolean {
        return canFocus && isFocusing
    }

    /**
     * 锁定对焦
     */
    fun lockFocus() {
        isFocusing = true
    }

    /**
     * 解锁对焦
     */
    fun unlockFocus() {
        isFocusing = false
    }

    fun restFocus() {
        isFocusing = false
    }

}