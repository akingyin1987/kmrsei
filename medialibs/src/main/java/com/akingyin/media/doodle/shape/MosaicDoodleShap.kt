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
import com.akingyin.media.doodle.core.IDoodle
import com.akingyin.media.doodle.core.IDoodleShape
import com.akingyin.media.doodle.core.Sticker
import kotlin.math.abs
import kotlin.math.ceil


/**
 * @ Description:
 * @author king
 * @ Date 2020/8/20 16:30
 * @version V1.0
 */
class MosaicDoodleShap(var srcBitmap: Bitmap) : IDoodleShape() {
    private var mosaicImg: Bitmap? = null
    var targetRect: Rect = Rect()
    var srcMosaic: Rect = Rect()

    init {
        mPaint.apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 5F
            isFilterBitmap = true
            isAntiAlias = true
            isDither = true
        }
    }

    override fun drawHelpers(canvas: Canvas, doodle: IDoodle) {
        if (!startPt.isEmpty() && !endPt.isEmpty()) {
            if (!targetRect.isEmpty && !srcMosaic.isEmpty) {
                mosaicImg?.let {
                    canvas.drawBitmap(it, srcMosaic, targetRect, mPaint)
                }
            }
        }
    }

    override fun draw(canvas: Canvas) {
        if (!startPt.isEmpty() && !endPt.isEmpty()) {
            if (!targetRect.isEmpty && !srcMosaic.isEmpty) {
                mosaicImg?.let {
                    canvas.drawBitmap(it, srcMosaic, targetRect, mPaint)
                }
            }
        }
    }

    override fun getWidth() = 0

    override fun getHeight() = 0

    override fun setDrawable(drawable: Drawable): Sticker {
        return this
    }

    override fun getDrawable(): Drawable? {
        return null
    }

    override fun setAlpha(alpha: Int): Sticker {
        return this

    }

    override fun calculation() {
        super.calculation()
        val leftUpX: Int = startPt.x.coerceAtMost(endPt.x)
        val leftUpY: Int = startPt.y.coerceAtMost(endPt.y)
        val rightBottomX: Int = startPt.x.coerceAtLeast(endPt.x)
        val rightBottomY: Int = startPt.y.coerceAtLeast(endPt.y)
        var w = abs(rightBottomX - leftUpX)
        var h = abs(rightBottomY - leftUpY)
        w = w.coerceAtLeast(min_mosaic_block_size)
        h = h.coerceAtLeast(min_mosaic_block_size)
        srcMosaic.set(0, 0, w, h)
        targetRect.set(leftUpX, leftUpY, leftUpX + w, leftUpY + h)
        try {
            mosaicImg = makeMosaic(srcBitmap, targetRect, 24)
        } catch (e: Exception) {
        }
    }

    companion object {
        const val min_mosaic_block_size = 4

        @Throws(OutOfMemoryError::class)
        @JvmStatic
        fun makeMosaic(bitmap: Bitmap, targetRect: Rect, blockLen: Int): Bitmap {
            var blockSize = blockLen
            if (bitmap.width == 0 || bitmap.height == 0 || bitmap.isRecycled) {
                throw RuntimeException("bad bitmap to add mosaic")
            }
            if (blockSize < min_mosaic_block_size) {
                blockSize = min_mosaic_block_size
            }
            val rectW = targetRect.width()
            val rectH = targetRect.height()
            val bitmapPxs = IntArray(rectW * rectH)
            // fetch bitmap pxs
            bitmap.getPixels(bitmapPxs, 0, rectW, targetRect.left, targetRect.top,
                    rectW, rectH)
            //
            val rowCount = ceil(rectH.toFloat() / blockSize.toDouble()).toInt()
            val columnCount = ceil(rectW.toFloat() / blockSize.toDouble()).toInt()
            for (r in 0 until rowCount) { // row loop
                for (c in 0 until columnCount) { // column loop
                    val startX = c * blockSize + 1
                    val startY = r * blockSize + 1
                    dimBlock(bitmapPxs, startX, startY, blockSize, rectW, rectH)
                }
            }
            // return Bitmap.createBitmap(bitmapPxs, rectW, rectH,
            // Config.ARGB_8888);
            val mask = Bitmap.createBitmap(bitmapPxs, rectW, rectH,
                    Bitmap.Config.ARGB_8888)
            val buffer = Bitmap.createBitmap(rectW, rectH, Bitmap.Config.ARGB_8888)
            buffer.eraseColor(Color.RED)
            val canvas = Canvas(buffer)
            canvas.drawBitmap(mask, 0f, 0f, null)
            return buffer
        }

        @JvmStatic
        fun dimBlock(pxs: IntArray, startX: Int, startY: Int, blockSize: Int, width: Int, height: Int) {
            var stopX = startX + blockSize
            var stopY = startY + blockSize
            val maxX = width - 1
            val maxY = height - 1
            if (stopX > maxX) {
                stopX = maxX
            }
            if (stopY > maxY) {
                stopY = maxY
            }
            //
            var sampleColorX = startX + blockSize / 2
            var sampleColorY = startY + blockSize / 2
            //
            if (sampleColorX > maxX) {
                sampleColorX = maxX
            }
            if (sampleColorY > maxY) {
                sampleColorY = maxY
            }
            val colorLinePosition = sampleColorY * width
            val sampleColor = pxs[colorLinePosition + sampleColorX]
            for (y in startY..stopY) {
                val p = y * width
                for (x in startX..stopX) {
                    pxs[p + x] = sampleColor
                }
            }
        }
    }

    override fun supportOtherHandle() = false

    override fun getTranslateOffset()= PointF()
}