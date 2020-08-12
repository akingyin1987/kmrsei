/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.doodle.core

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import com.akingyin.media.doodle.DrawUtil
import kotlin.math.atan2

/**
 * @ Description:
 * @author king
 * @ Date 2020/8/11 18:33
 * @version V1.0
 */
abstract class Sticker {


    @IntDef(flag = true, value = [Position.CENTER, Position.TOP, Position.BOTTOM, Position.LEFT, Position.RIGHT])
    @Retention(AnnotationRetention.SOURCE)
    annotation class Position {
        companion object {
            const val CENTER = 1
            const val TOP = 1 shl 1
            const val LEFT = 1 shl 2
            const val RIGHT = 1 shl 3
            const val BOTTOM = 1 shl 4
        }
    }

    private val matrixValues = FloatArray(9)
    private val unrotatedWrapperCorner = FloatArray(8)
    private val unrotatedPoint = FloatArray(2)
    private val boundPoints = FloatArray(8)
    private val mappedBounds = FloatArray(8)
    private val trappedRect = RectF()
    val matrix = Matrix()
    private var isFlippedHorizontally = false
    private var isFlippedVertically = false

    open fun isFlippedHorizontally(): Boolean {
        return isFlippedHorizontally
    }


    open fun setFlippedHorizontally(flippedHorizontally: Boolean): Sticker{
        isFlippedHorizontally = flippedHorizontally
        return this
    }

    open fun isFlippedVertically(): Boolean {
        return isFlippedVertically
    }


    open fun setFlippedVertically(flippedVertically: Boolean): Sticker{
        isFlippedVertically = flippedVertically
        return this
    }




    open fun setMatrix(matrix: Matrix): Sticker {
        this.matrix.set(matrix)
        return this
    }

    abstract fun draw( canvas: Canvas)

    abstract fun getWidth(): Int

    abstract fun getHeight(): Int

    abstract fun setDrawable( drawable: Drawable): Sticker


    abstract fun getDrawable(): Drawable


    abstract fun setAlpha(@androidx.annotation.IntRange(from = 0, to = 255) alpha: Int): Sticker

    open fun getBoundPoints(): FloatArray {
        val points = FloatArray(8)
        getBoundPoints(points)
        return points
    }

    open fun getBoundPoints( points: FloatArray) {
        if (!isFlippedHorizontally) {
            if (!isFlippedVertically) {
                points[0] = 0f
                points[1] = 0f
                points[2] = getWidth().toFloat()
                points[3] = 0f
                points[4] = 0f
                points[5] = getHeight().toFloat()
                points[6] = getWidth().toFloat()
                points[7] = getHeight().toFloat()
            } else {
                points[0] = 0f
                points[1] = getHeight().toFloat()
                points[2] = getWidth().toFloat()
                points[3] = getHeight().toFloat()
                points[4] = 0f
                points[5] = 0f
                points[6] = getWidth().toFloat()
                points[7] = 0f
            }
        } else {
            if (!isFlippedVertically) {
                points[0] = getWidth().toFloat()
                points[1] = 0f
                points[2] = 0f
                points[3] = 0f
                points[4] = getWidth().toFloat()
                points[5] = getHeight().toFloat()
                points[6] = 0f
                points[7] = getHeight().toFloat()
            } else {
                points[0] = getWidth().toFloat()
                points[1] = getHeight().toFloat()
                points[2] = 0f
                points[3] = getHeight().toFloat()
                points[4] = getWidth().toFloat()
                points[5] = 0f
                points[6] = 0f
                points[7] = 0f
            }
        }
    }


    open fun getMappedBoundPoints(): FloatArray {
        val dst = FloatArray(8)
        getMappedPoints(dst, getBoundPoints())
        return dst
    }


    open fun getMappedPoints(src: FloatArray): FloatArray {
        val dst = FloatArray(src.size)
        matrix.mapPoints(dst, src)
        return dst
    }

    open fun getMappedPoints( dst: FloatArray,  src: FloatArray) {
        matrix.mapPoints(dst, src)
    }


    open fun getBound(): RectF {
        val bound = RectF()
        getBound(bound)
        return bound
    }

    open fun getBound(dst: RectF) {
        dst[0f, 0f, getWidth().toFloat()] = getHeight().toFloat()
    }


    open fun getMappedBound(): RectF{
        val dst = RectF()
        getMappedBound(dst, getBound())
        return dst
    }

    open fun getMappedBound(dst: RectF,  bound: RectF) {
        matrix.mapRect(dst, bound)
    }


    open fun getCenterPoint(): PointF {
        val center = PointF()
        getCenterPoint(center)
        return center
    }

    open fun getCenterPoint( dst: PointF) {
        dst[getWidth() * 1f / 2] = getHeight() * 1f / 2
    }


    open fun getMappedCenterPoint(): PointF{
        val pointF = getCenterPoint()
        getMappedCenterPoint(pointF, FloatArray(2), FloatArray(2))
        return pointF
    }

    open fun getMappedCenterPoint( dst: PointF, mappedPoints: FloatArray,
                                 src: FloatArray) {
        getCenterPoint(dst)
        src[0] = dst.x
        src[1] = dst.y
        getMappedPoints(mappedPoints, src)
        dst[mappedPoints[0]] = mappedPoints[1]
    }

    open fun getCurrentScale(): Float {
        return getMatrixScale(matrix)
    }

    open fun getCurrentHeight(): Float {
        return getMatrixScale(matrix) * getHeight()
    }

    open fun getCurrentWidth(): Float {
        return getMatrixScale(matrix) * getWidth()
    }

    /**
     * This method calculates scale value for given Matrix object.
     */
    open fun getMatrixScale( matrix: Matrix): Float {
        return Math.sqrt(Math.pow(getMatrixValue(matrix, Matrix.MSCALE_X).toDouble(), 2.0) + Math.pow(
                getMatrixValue(matrix, Matrix.MSKEW_Y).toDouble(), 2.0)).toFloat()
    }

    /**
     * @return - current image rotation angle.
     */
    open fun getCurrentAngle(): Float {
        return getMatrixAngle(matrix)
    }

    /**
     * This method calculates rotation angle for given Matrix object.
     */
    open fun getMatrixAngle(matrix: Matrix): Float {
        return Math.toDegrees(-atan2(getMatrixValue(matrix, Matrix.MSKEW_X).toDouble(),
                getMatrixValue(matrix, Matrix.MSCALE_X).toDouble())).toFloat()
    }

    open fun getMatrixValue(matrix: Matrix, @IntRange(from = 0, to = 9) valueIndex: Int): Float {
        matrix.getValues(matrixValues)
        return matrixValues[valueIndex]
    }

    open fun contains(x: Float, y: Float): Boolean {
        return contains(floatArrayOf(x, y))
    }

    open  fun contains( point: FloatArray): Boolean {
        val tempMatrix = Matrix()
        tempMatrix.setRotate(-getCurrentAngle())
        getBoundPoints(boundPoints)
        getMappedPoints(mappedBounds, boundPoints)
        tempMatrix.mapPoints(unrotatedWrapperCorner, mappedBounds)
        tempMatrix.mapPoints(unrotatedPoint, point)
        DrawUtil.trapToRect(trappedRect, unrotatedWrapperCorner)
        return trappedRect.contains(unrotatedPoint[0], unrotatedPoint[1])
    }

    open fun release() {}
}