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
import android.graphics.drawable.shapes.OvalShape
import com.akingyin.media.doodle.core.IDoodle
import com.akingyin.media.doodle.core.IDoodleShape
import com.akingyin.media.doodle.core.Sticker
import timber.log.Timber
import kotlin.math.sqrt

/**
 * @ Description:
 * @author king
 * @ Date 2020/8/17 11:43
 * @version V1.0
 */
class CircleDoodleShap (var context: Context) : IDoodleShape(){
    private var drawable = MyShapeDrawable(OvalShape())
    private var realBounds: Rect
    private var radius = 0F

    init {
        mPaint .apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 5F
            isAntiAlias = true
            isDither = true
        }
        drawable.mStrokePaint.run {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 5F
            isAntiAlias = true
            isDither = true
        }
        realBounds = Rect(0, 0, getWidth(), getHeight())

    }
    override fun drawHelpers(canvas: Canvas, doodle: IDoodle) {
        Timber.tag("CircleDoodleShap").d("drawHelpers")
       if(!startPt.isEmpty() && !endPt.isEmpty()){
           calculation()
           canvas.save()
           canvas.drawCircle(centerPt.x.toFloat(),centerPt.y.toFloat(),radius,mPaint)
           canvas.restore()
       }
    }

    override fun draw(canvas: Canvas) {
        Timber.tag("CircleDoodleShap").d("draw")
        canvas.save()
        canvas.concat(matrix)
        drawable.let {
            it.bounds = realBounds
            it.draw(canvas)
        }
        canvas.restore()
    }

    override fun calculation() {
        super.calculation()
        val xd: Int = endPt.x - startPt.x
        val yd: Int = endPt.y - startPt.y
        radius = sqrt(xd * xd + yd * yd.toFloat()) / 2
    }

    override fun getWidth()=drawable.intrinsicWidth

    override fun getHeight()=drawable.intrinsicHeight

    override fun setDrawable(drawable: Drawable): Sticker {

        realBounds[0, 0, getWidth()] = getHeight()

        return this
    }

    override fun setAlpha(alpha: Int): Sticker {
       mPaint.alpha = alpha
        return  this
    }

    override fun getDrawable(): Drawable? {

        return  drawable
    }

    override fun getTranslateOffset(): PointF? {
      return PointF().apply {
          x = centerPt.x.toFloat()
          y = centerPt.y.toFloat()
      }
    }

    override fun resetDrawable() {
        super.resetDrawable()
        drawable.setBounds((centerPt.x -radius).toInt(), (centerPt.y-radius).toInt(), (centerPt.x+radius).toInt(), (centerPt.y+radius).toInt())
        drawable.intrinsicHeight = (radius *2).toInt()
        drawable.intrinsicWidth = (radius *2).toInt()
        realBounds = Rect(0, 0, getWidth(), getHeight())
    }
}