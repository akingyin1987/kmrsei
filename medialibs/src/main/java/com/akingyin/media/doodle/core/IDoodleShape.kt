/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.doodle.core

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt

import com.akingyin.media.doodle.Pt
import java.util.*
import kotlin.math.atan2


/**
 * @ Description:
 * @author king
 * @ Date 2020/8/11 11:40
 * @version V1.0
 */
abstract class IDoodleShape : Sticker() {

    /** 边框4个按钮坐标 */
    private val bitmapPoints = FloatArray(8)
    private val bounds = FloatArray(8)




    /** 画笔起始位置 */
    var startPt: Pt = Pt(0, 0)

    /** 设置起始位置*/
   open fun setStartLocation(x: Float, y: Float) {
        startPt.x = x.toInt()
        startPt.y = y.toInt()
    }

    /**
     * 设置移动过程的坐标信息
     */
    open fun  setMoveLocation(x: Float, y: Float){
        setEndLocation(x,y)
    }

    /** 设置终点位置 */
   open fun setEndLocation(x: Float, y: Float) {
        endPt.x = x.toInt()
        endPt.y = y.toInt()
        centerPt.x = (startPt.x + endPt.x) / 2
        centerPt.y = (startPt.y + endPt.y) / 2

    }

    /** 画笔终点位置 */
    var endPt: Pt = Pt(0, 0)


    /** 中心位置 */
    var centerPt = Pt(0, 0)


    var mPath = Path() // 画笔的路径

    //画笔
    var mPaint: Paint = Paint()

    /** 是否被选中 */
    var isSelected = false

    val ITEM_CAN_ROTATE_BOUND = 35
    val ITEM_PADDING = 6// 绘制item矩形区域时增加的padding

    var mRect = Rect()
    private val mRectTemp = Rect()
    private val mSrcRect = Rect()
    private val mDstRect = Rect()


    /**
     * 绘制图形辅助工具，由IDoodle绘制，不属于IDoodleItem的内容
     *
     * @param canvas
     * @param doodle
     */
    abstract fun drawHelpers(canvas: Canvas, doodle: IDoodle)


    /**
     * 计算
     */
    open fun calculation() {

    }

    /** 画操作框 */
    open fun doDrawAtShapeFrame(canvas: Canvas, paint: Paint) {
        val count = canvas.save()
        mRectTemp.set(mRect)
        mRectTemp.left -= ITEM_PADDING
        mRectTemp.top -= ITEM_PADDING
        mRectTemp.right += ITEM_PADDING
        mRectTemp.bottom += ITEM_PADDING
        paint.reset()
        paint.run {
            shader = null
            color = 0x00888888
            style = Paint.Style.FILL
            strokeWidth = 1F
        }
        canvas.drawRect(mRectTemp, paint)
        getStickerPoints()
        val x1 = bitmapPoints[0]
        val y1 = bitmapPoints[1]
        val x2 = bitmapPoints[2]
        val y2 = bitmapPoints[3]
        val x3 = bitmapPoints[4]
        val y3 = bitmapPoints[5]
        val x4 = bitmapPoints[6]
        val y4 = bitmapPoints[7]
        canvas.drawLine(x1, y1, x2, y2, paint)
        canvas.drawLine(x1, y1, x3, y3, paint)
        canvas.drawLine(x2, y2, x4, y4, paint)
        canvas.drawLine(x4, y4, x3, y3, paint)
        val rotation = calculateRotation(x4, y4, x3, y3)
        canvas.restoreToCount(count)
    }

    /**
     * 移动当前形状坐标
     */
    open fun onMoveShape(moveX: Float, moveY: Float) {
        startPt.postOffset(moveX, moveY)
        endPt.postOffset(moveX, moveY)
        centerPt.postOffset(moveX, moveY)
        mRect.offset(moveX.toInt(), moveY.toInt())
    }

    /**
     * 获取4个点坐标
     */
    open fun getStickerPoints() {
        if (mRect.isEmpty) {
            Arrays.fill(bitmapPoints, 0F)
        } else {
            getBoundPoints(bounds)
            matrix.mapPoints(bitmapPoints, bounds)
        }
    }


    open fun calculateRotation(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val x = x1 - x2.toDouble()
        val y = y1 - y2.toDouble()
        val radians = atan2(y, x)
        return Math.toDegrees(radians).toFloat()
    }


    /**
     * 设置画笔颜色
     */
   open fun setDoodlePenColor(@ColorInt color: Int) {
        mPaint.color = color

    }

    /**
     * 重新生成 drawable
     */
    open fun resetDrawable() {

    }


    /**
     * 涂鸦是否画完
     */
    open fun  isDrawShapeComplete()= true

    /**
     * 验证图形是否合格
     */
    open fun  qualifiedShape() = true


    /**
     * 获取平移量
     */
    open fun  getTranslateOffset():PointF? = null


    /**
     * 是否支持其它操作
     */
    open  fun   supportOtherHandle():Boolean = true


    /**
     * 获取画笔颜色
     */
    open  fun   getPenColor() = mPaint.color


    /**
     * 是否在画终点
     */
    open  fun   isDrawEndPoint() = !endPt.isEmpty()


}