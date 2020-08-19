/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.doodle.shape

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.PathShape
import com.akingyin.media.doodle.core.IDoodle
import com.akingyin.media.doodle.core.IDoodleShape
import com.akingyin.media.doodle.core.Sticker
import kotlin.math.*

/**
 * 画箭头
 * @ Description:
 * @author king
 * @ Date 2020/8/17 16:28
 * @version V1.0
 */
class ArrowDoodleShap(context: Context) : IDoodleShape() {

    private var drawable: ShapeDrawable = ShapeDrawable()
    private var realBounds: Rect

    init {
        mPaint.apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 5F
            isAntiAlias = true
            isDither = true
        }
        realBounds = Rect(0, 0, getWidth(), getHeight())
    }

    override fun drawHelpers(canvas: Canvas, doodle: IDoodle) {
        if (!startPt.isEmpty() && !endPt.isEmpty()) {
            calculation()
            canvas.save()
            mPath.reset()
            mPaint.style = Paint.Style.STROKE
            mPath.moveTo(startPt.x.toFloat(), startPt.y.toFloat())
            mPath.lineTo(endPt.x.toFloat(),endPt.y.toFloat())
            canvas.drawPath(mPath,mPaint)
            if(!path.isEmpty){
                mPaint.style = Paint.Style.FILL
                canvas.drawPath(path,mPaint)
            } 

            canvas.restore()
        }
    }

    override fun draw(canvas: Canvas) {
        canvas.save()
        canvas.concat(matrix)
        drawable.let {
            it.bounds = realBounds
            it.draw(canvas)
        }
        canvas.restore()
    }

    override fun getWidth() = drawable.intrinsicWidth

    override fun getHeight() = drawable.intrinsicHeight

    override fun setDrawable(drawable: Drawable): Sticker {
        return this
    }

    override fun getDrawable(): Drawable? {
        return drawable
    }

    override fun setAlpha(alpha: Int): Sticker {
        mPaint.alpha = alpha
        return this
    }

    val offx = 4f
    var ex = 0f
    var ey = 0f
    var sx = 0f
    var sy = 0f
    var H = 40.0
    var L = 14.0
    var x3 = 0
    var y3 = 0
    var x4 = 0
    var y4 = 0
    private var path: Path = Path()
    override fun calculation() {
        super.calculation()
        ex = endPt.x.toFloat()
        ey = endPt.y.toFloat()
        sx = startPt.x.toFloat()
        sy = startPt.y.toFloat()


        val awrad = atan(L / H)
        val arraow_len = sqrt(L * L + H * H)
        val arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len)
        val arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len)
        val x_3 = ex - arrXY_1[0]
        val y_3 = ey - arrXY_1[1]
        val x_4 = ex - arrXY_2[0]
        val y_4 = ey - arrXY_2[1]
        x3 = x_3.toInt()
        y3 = y_3.toInt()
        x4 = x_4.toInt()
        y4 = y_4.toInt()

        if (ey == sy) {
            ey += 4
        } else if (ex == ey) {
            ex += 4
        } else {
            val slope = atan((ey - sy) / (ex - sx).toDouble())
            if (ex > sx) {
                ex = ((ex + offx * cos(slope)).toFloat())
                ey = ((ey + offx * sin(slope)).toFloat())
            } else {
                ex = (ex - offx * cos(slope)).toFloat()
                ey = (ey - offx * sin(slope)).toFloat()
            }
        }
        path.reset()
        path.moveTo(ex, ey)
        path.lineTo(x3.toFloat(), y3.toFloat())
        path.lineTo(x4.toFloat(), y4.toFloat())
        path.close()
    }

    // 计算
    private fun rotateVec(px: Float, py: Float, ang: Double, isChLen: Boolean,
                  newLen: Double): DoubleArray {
        val mathstr = DoubleArray(2)
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度
        var vx = px * cos(ang) - py * sin(ang)
        var vy = px * sin(ang) + py * cos(ang)
        if (isChLen) {
            val d = sqrt(vx * vx + vy * vy)
            vx = vx / d * newLen
            vy = vy / d * newLen

        }
        mathstr[0] = vx
        mathstr[1] = vy
        return mathstr
    }

    override fun resetDrawable() {
        super.resetDrawable()
        drawable.shape = PathShape(mPath.apply {
            reset()
            moveTo(startPt.x.toFloat(),startPt.y.toFloat())
            lineTo(endPt.x.toFloat(),endPt.y.toFloat())
            moveTo(ex,ey)
            lineTo(x3.toFloat(), y3.toFloat())
            lineTo(x4.toFloat(), y4.toFloat())
            close()

        }, abs(startPt.x-endPt.x).toFloat(), abs(startPt.y - endPt.y).toFloat())
        realBounds = Rect(0, 0, getWidth(), getHeight())
    }
}