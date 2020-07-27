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
import android.graphics.*
import android.hardware.Camera
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.akingyin.media.R
import kotlin.math.abs


/**
 * @ Description:
 * @author king
 * @ Date 2020/7/16 17:20
 * @version V1.0
 */

@Suppress("DEPRECATION")
class FouceView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) :
        View(context, attrs, defStyleAttr, defStyleRes) {

    ////焦点附近设置矩形区域作为对焦区域
    private var touchFocusRect: Rect? = null

    private val mPaint: Paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        color = Color.WHITE
        strokeWidth = 4F
        style = Paint.Style.STROKE
    }


    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            println("正常画")
            drawTouchFocusRect(it)
        }
        super.onDraw(canvas)


    }
    /**
     * 确保所选区域在合理范围内,不会超过边界值
     */
    fun clamp(touchCoordinateInCameraReper: Int, focusAreaSize: Int): Int {
        return if (abs(touchCoordinateInCameraReper) + focusAreaSize > 1000) {
            if (touchCoordinateInCameraReper > 0) {
                1000 - focusAreaSize
            } else {
                focusAreaSize - 1000
            }
        } else {
            touchCoordinateInCameraReper - focusAreaSize / 2
        }
    }

    var  screenPoint:Point?= null

    fun calculateTapArea(x: Float, y: Float, coefficient: Float):Rect? {
        val FOCUS_AREA_SIZE = 300
       return screenPoint?.let {
            val areaSize = FOCUS_AREA_SIZE * (coefficient.toInt())
            val left =clamp((y/it.y*2000-1000).toInt(),areaSize)
            val top = clamp(((it.x-x)/it.x*2000-1000).toInt(),areaSize)
            return@let Rect(left,top,left+areaSize,top+areaSize)
        }?:null


    }
    //对焦并绘制对焦矩形框
    fun setTouchFoucusRect(camera: Camera, autoFocusCallback: Camera.AutoFocusCallback, x: Float, y: Float) {
        //以焦点为中心，宽度为300的矩形框

        //以焦点为中心，宽度为300的矩形框
        touchFocusRect = Rect((x - 150).toInt(), (y - 150).toInt(), (x + 150).toInt(), (y + 150).toInt())
        //对焦区域
        val targetFocusRect =calculateTapArea(x,y,1F)?: Rect().apply {
            touchFocusRect?.let {
                left = it.left * 2000 / width - 1000
                top = it.top * 2000 / height - 1000
                right = it.right * 2000 / width - 1000
                bottom = it.bottom * 2000 / height - 1000
            }
        }
        doTouchFocus(camera, autoFocusCallback, targetFocusRect)
        postInvalidate()//刷新界面，
    }

    private fun doTouchFocus(camera: Camera, autoFocusCallback: Camera.AutoFocusCallback, tfocusRect: Rect) {
        try {
            val focusList = arrayListOf<Camera.Area>()
            val focus = Camera.Area(tfocusRect, 1000) //相机参数：对焦区域
            focusList.add(focus)
            val par = camera.parameters.apply {
                focusAreas = focusList
                meteringAreas = focusList
            }
            camera.parameters = par
            camera.autoFocus(autoFocusCallback)

        } catch (e: Exception) {
            e.printStackTrace()
            autoFocusCallback.onAutoFocus(false, camera)
        }

    }



    //对焦完成后，清除对焦矩形框
    fun disDrawTouchFocusRect(result: Boolean = true) {
        mPaint.color = if (result) Color.GREEN else Color.RED
        println("改变画笔颜色")
        postInvalidate()

        AnimationUtils.loadAnimation(context, R.anim.camera_fouce_rotate).run {
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    touchFocusRect = null
                    mPaint.color = Color.WHITE
                    println("清除画笔颜色")
                    postInvalidate()
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            startAnimation(this)

        }


    }

    private fun drawTouchFocusRect(canvas: Canvas) {
        touchFocusRect?.run {
            canvas.run {
                //左下角
                drawRect((left - 2).toFloat(), bottom.toFloat(), (left + 20).toFloat(), (bottom + 2).toFloat(), mPaint)
                drawRect((left - 2).toFloat(), (bottom - 20).toFloat(), left.toFloat(), bottom.toFloat(), mPaint)

                //左上角
                drawRect((left - 2).toFloat(), (top - 2).toFloat(), (left + 20).toFloat(), top.toFloat(), mPaint)
                drawRect((left - 2).toFloat(), top.toFloat(), (left + 2).toFloat(), (top + 20).toFloat(), mPaint)

                //右上角
                drawRect((right - 20).toFloat(), (top - 2).toFloat(), (right + 2).toFloat(), top.toFloat(), mPaint)
                drawRect(right.toFloat(), top.toFloat(), (right + 2).toFloat(), (top + 20).toFloat(), mPaint)

                //右下角
                drawRect((right - 20).toFloat(), bottom.toFloat(), (right + 2).toFloat(), (bottom + 2).toFloat(), mPaint)
                drawRect(right.toFloat(), (bottom - 20).toFloat(), (right + 2).toFloat(), bottom.toFloat(), mPaint)
            }
        }




    }


}