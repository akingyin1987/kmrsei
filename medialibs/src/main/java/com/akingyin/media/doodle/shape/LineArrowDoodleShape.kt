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
import com.akingyin.media.doodle.core.IDoodle
import com.akingyin.media.doodle.core.IDoodleShape
import com.akingyin.media.doodle.core.Sticker
import kotlin.math.abs



/**
 * @ Description:
 * @author king
 * @ Date 2020/8/25 11:26
 * @version V1.0
 */
class LineArrowDoodleShape(colorPen:Int = Color.RED) :IDoodleShape(){
    private var drawable: ShapeDrawable = ShapeDrawable()
    private var realBounds: Rect

    private var lineDoodleShape = LineDoodleShape(colorPen)
    private var arrawDoodleShape = ArrowDoodleShape(colorPen)


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


    override fun setStartLocation(x: Float, y: Float) {
        if(lineDoodleShape.isDrawEndPoint()){
            println("开始画箭头")
            arrawDoodleShape.setStartLocation(lineDoodleShape.endPt.x.toFloat(),lineDoodleShape.endPt.y.toFloat())
           arrawDoodleShape.setEndLocation(x,y)

        }else{
            println("开始画直线")
            lineDoodleShape.setStartLocation(x,y)
        }
    }

    override fun setEndLocation(x: Float, y: Float) {
        if(lineDoodleShape.isDrawEndPoint() && !arrawDoodleShape.startPt.isEmpty()){
            arrawDoodleShape.setEndLocation(x,y)
        }else{

            lineDoodleShape.setEndLocation(x,y)
        }
    }

    override fun isDrawShapeComplete(): Boolean {
        return lineDoodleShape.isDrawEndPoint()&&arrawDoodleShape.isDrawEndPoint()
    }

    override fun setDoodlePenColor(color: Int) {
        super.setDoodlePenColor(color)
        drawable.paint.color = color
    }

    override fun qualifiedShape(): Boolean {
        return lineDoodleShape.qualifiedShape() && arrawDoodleShape.qualifiedShape()
    }

    override fun drawHelpers(canvas: Canvas, doodle: IDoodle) {
       lineDoodleShape.drawHelpers(canvas,doodle)
       if(!arrawDoodleShape.startPt.isEmpty()){
           arrawDoodleShape.drawHelpers(canvas,doodle)
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

    override fun getDrawable()=drawable

    override fun setAlpha(alpha: Int): Sticker {
       mPaint.alpha = alpha
        drawable.paint.alpha = alpha
        return this
    }

    private   var  offsetPointF = PointF()
    override fun getTranslateOffset() = offsetPointF

    override fun resetDrawable() {
        offsetPointF.apply {
            x = lineDoodleShape.startPt.x.coerceAtMost(lineDoodleShape.endPt.x).coerceAtMost(arrawDoodleShape.endPt.x).toFloat()
            y = lineDoodleShape.startPt.y.coerceAtMost(lineDoodleShape.endPt.y).coerceAtMost(arrawDoodleShape.endPt.y).toFloat()
        }
        val maxPointX = lineDoodleShape.startPt.x.coerceAtLeast(lineDoodleShape.endPt.x).coerceAtLeast(arrawDoodleShape.endPt.x)
        val maxPointY = lineDoodleShape.startPt.y.coerceAtLeast(lineDoodleShape.endPt.y).coerceAtLeast(arrawDoodleShape.endPt.y)
        drawable.intrinsicWidth = (maxPointX-offsetPointF.x).toInt().let {
            if(it < ArrowDoodleShape.MIN_WIDTH){
                ArrowDoodleShape.MIN_WIDTH *2
            }else{
                it
            }
        }
        drawable.intrinsicHeight = (maxPointY-offsetPointF.y).toInt().let {
            if(it < ArrowDoodleShape.MIN_HEIGHT){
                ArrowDoodleShape.MIN_HEIGHT *2
            }else{
                it
            }
        }

        drawable.shape = PathShape(mPath.apply {
            reset()
            moveTo(lineDoodleShape.startPt.x.toFloat()- offsetPointF.x, lineDoodleShape.startPt.y.toFloat()-offsetPointF.y)
            lineTo(lineDoodleShape.endPt.x.toFloat()- offsetPointF.x,lineDoodleShape.endPt.y.toFloat()-offsetPointF.y)
            moveTo(lineDoodleShape.endPt.x.toFloat()- offsetPointF.x, lineDoodleShape.endPt.y.toFloat()-offsetPointF.y)
            lineTo(arrawDoodleShape.endPt.x.toFloat() - offsetPointF.x,arrawDoodleShape.endPt.y.toFloat()-offsetPointF.y)
            moveTo(arrawDoodleShape.ex - offsetPointF.x,arrawDoodleShape.ey - offsetPointF.y)
            lineTo((arrawDoodleShape.x3 - offsetPointF.x), (arrawDoodleShape.y3 - offsetPointF.y))
            lineTo((arrawDoodleShape.x4 - offsetPointF.x), (arrawDoodleShape.y4 - offsetPointF.y))
            close()

        }, abs(maxPointX - offsetPointF.x), abs(maxPointY-offsetPointF.y))
        realBounds = Rect(0,0, abs(maxPointX - offsetPointF.x).toInt(), abs(maxPointY-offsetPointF.y).toInt())
    }
}