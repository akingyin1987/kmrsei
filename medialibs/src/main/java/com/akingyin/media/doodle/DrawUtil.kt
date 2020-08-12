/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.doodle


import android.graphics.*
import kotlin.math.*

/**
 * @ Description:
 * @author king
 * @ Date 2020/8/11 17:11
 * @version V1.0
 */
object DrawUtil {


    fun drawArrow(canvas: Canvas, sx: Float, sy: Float, ex: Float,
                  ey: Float, paint: Paint) {
        val arrowSize = paint.strokeWidth
        val H = arrowSize.toDouble() // 箭头高度
        val L = arrowSize / 2.toDouble() // 底边的一�?
        var awrad = atan(L / 2 / H) // 箭头角度
        var arraow_len = sqrt(L / 2 * L / 2 + H * H) - 5 // 箭头的长�?
        var arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true,
                arraow_len)
        var arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true,
                arraow_len)
        var x_3 = (ex - arrXY_1[0]).toFloat() // (x3,y3)是第�?端点
        var y_3 = (ey - arrXY_1[1]).toFloat()
        var x_4 = (ex - arrXY_2[0]).toFloat() // (x4,y4)是第二端�?
        var y_4 = (ey - arrXY_2[1]).toFloat()
        // 画线
        val linePath = Path()
        linePath.moveTo(sx, sy)
        linePath.lineTo(x_3, y_3)
        linePath.lineTo(x_4, y_4)
        linePath.close()
        canvas.drawPath(linePath, paint)
        awrad = atan(L / H) // 箭头角度
        arraow_len = sqrt(L * L + H * H) // 箭头的长�?
        arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len)
        arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len)
        x_3 = (ex - arrXY_1[0]).toFloat() // (x3,y3)是第�?端点
        y_3 = (ey - arrXY_1[1]).toFloat()
        x_4 = (ex - arrXY_2[0]).toFloat() // (x4,y4)是第二端�?
        y_4 = (ey - arrXY_2[1]).toFloat()
        val triangle = Path()
        triangle.moveTo(ex, ey)
        triangle.lineTo(x_3, y_3)
        triangle.lineTo(x_4, y_4)
        triangle.close()
        canvas.drawPath(triangle, paint)
    }

    // 计算 向量（px,py) 旋转ang角度后的新长度
    fun rotateVec(px: Float, py: Float, ang: Double, isChLen: Boolean, newLen: Double): DoubleArray {
        val mathstr = DoubleArray(2)
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度�?�新长度
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

    fun drawLine(canvas: Canvas, sx: Float, sy: Float, dx: Float, dy: Float, paint: Paint) {
        canvas.drawLine(sx, sy, dx, dy, paint)
    }

    fun drawCircle(canvas: Canvas, cx: Float, cy: Float, radius: Float, paint: Paint) {
        canvas.drawCircle(cx, cy, radius, paint)
    }

    fun drawRect(canvas: Canvas, sx: Float, sy: Float, dx: Float, dy: Float, paint: Paint) {

        // 保证　左上角　与　右下角　的对应关系
        if (sx < dx) {
            if (sy < dy) {
                canvas.drawRect(sx, sy, dx, dy, paint)
            } else {
                canvas.drawRect(sx, dy, dx, sy, paint)
            }
        } else {
            if (sy < dy) {
                canvas.drawRect(dx, sy, sx, dy, paint)
            } else {
                canvas.drawRect(dx, dy, sx, sy, paint)
            }
        }
    }

    /**
     * 计算点p2绕p1顺时针旋转的角度
     *
     * @param px1
     * @param py1
     * @param px2
     * @param py2
     * @return 旋转的角度
     */
    fun computeAngle(px1: Float, py1: Float, px2: Float, py2: Float): Float {
        val x = px2 - px1
        val y = py2 - py1
        if(x == 0F){
            return if(y > 0){
                90F
            }else{
                270F
            }
        }
        val arc = atan(y / x.toDouble()).toFloat()
        var angle = (arc / (Math.PI * 2) * 360).toFloat()
        if (x >= 0 && y == 0f) {
            angle = 0f
        } else if (x < 0 && y == 0f) {
            angle = 180f
        } else if (x == 0f && y > 0) {
            angle = 90f
        } else if (x == 0f && y < 0) {
            angle = 270f
        } else if (x > 0 && y > 0) { // 1
        } else if (x < 0 && y > 0) { //2
            angle += 180
        } else if (x < 0 && y < 0) { //3
            angle += 180
        } else if (x > 0 && y < 0) { //4
            angle += 360
        }

        return angle
    }

    // 顺时针旋转
    fun rotatePoint(coords: PointF, degree: Float, x: Float, y: Float, px: Float, py: Float): PointF {
        if (degree % 360 == 0f) {
            coords.x = x
            coords.y = y
            return coords
        }
        /*角度变成弧度*/
        val radian = (degree * Math.PI / 180).toFloat()
        coords.x = ((x - px) * cos(radian.toDouble()) - (y - py) * sin(radian.toDouble()) + px).toFloat()
        coords.y = ((x - px) * sin(radian.toDouble()) + (y - py) * cos(radian.toDouble()) + py).toFloat()
        return coords
    }

    /**
     * 旋转
     *  @param scale 旋转角度
     *  @param px 旋转前后X 坐标距离
     *  @param py 旋转前后Y 坐标距离
     */
    fun scaleRect(rect: Rect, scale: Float, px: Float, py: Float) {

        rect.left = (px - scale * (px - rect.left) + 0.5f).toInt()
        rect.right = (px - scale * (px - rect.right) + 0.5f).toInt()
        rect.top = (py - scale * (py - rect.top) + 0.5f).toInt()
        rect.bottom = (py - scale * (py - rect.bottom) + 0.5f).toInt()
    }

    fun trapToRect( r: RectF,  array: FloatArray) {
        r[Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY] = Float.NEGATIVE_INFINITY
        var i = 1
        while (i < array.size) {
            val x = (array[i - 1] * 10).roundToInt() / 10f
            val y = (array[i] * 10).roundToInt() / 10f
            r.left = if (x < r.left) x else r.left
            r.top = if (y < r.top) y else r.top
            r.right = if (x > r.right) x else r.right
            r.bottom = if (y > r.bottom) y else r.bottom
            i += 2
        }
        r.sort()
    }
}