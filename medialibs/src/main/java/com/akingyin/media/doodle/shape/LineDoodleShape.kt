/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.doodle.shape

import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.PathShape
import com.akingyin.base.utils.CalculationUtil
import com.akingyin.media.doodle.core.IDoodle
import com.akingyin.media.doodle.core.IDoodleShape
import com.akingyin.media.doodle.core.Sticker
import kotlin.math.*

/**
 * @ Description:
 * @author king
 * @ Date 2020/8/21 16:19
 * @version V1.0
 */
class LineDoodleShape(colorPen:Int = Color.RED) :IDoodleShape() {


    private var drawable: ShapeDrawable = ShapeDrawable()
    private var realBounds: Rect

    init {
        mPaint.apply {
            color = colorPen
            style = Paint.Style.STROKE
            strokeWidth = 5F
            isAntiAlias = true
            isDither = true
        }
        drawable.paint.run {
            color = colorPen
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 5F
            isAntiAlias = true
            isDither = true
        }
        realBounds = Rect(0, 0, getWidth(), getHeight())
    }
    override fun setDoodlePenColor(color: Int) {
        mPaint.color = color
        drawable.paint.color = color
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

    override fun getTranslateOffset(): PointF? {
        return PointF().apply {
            x = startPt.x.toFloat()
            y = startPt.y.toFloat()
        }
    }

    override fun qualifiedShape(): Boolean {
        return CalculationUtil.getPointsDistance(startPt.x.toFloat(), startPt.y.toFloat(), endPt.x.toFloat(), endPt.y.toFloat())> MIN_WIDTH
    }

    override fun resetDrawable() {
        super.resetDrawable()
        drawable.intrinsicWidth = abs(startPt.x-endPt.x).let {
            if(it < MIN_WIDTH){
                MIN_WIDTH*2
            }else{
                it
            }
        }
        drawable.intrinsicHeight = abs(startPt.y - endPt.y).let {
            if(it < MIN_HEIGHT){
                MIN_HEIGHT*2
            }else{
                it
            }
        }
        drawable.shape = PathShape(mPath.apply {
            reset()
            moveTo(0f,0f)
            lineTo((endPt.x - startPt.x).toFloat(), (endPt.y - startPt.y).toFloat())

            close()

        }, getWidth().toFloat(),getHeight().toFloat())

        realBounds = Rect(0, 0, getWidth() , getHeight())

    }
    companion object{
        const val MIN_WIDTH = 20
        const val MIN_HEIGHT = 20
    }
}