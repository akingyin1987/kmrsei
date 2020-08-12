/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.doodle.core

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.ContextCompat
import com.akingyin.media.R
import com.blankj.utilcode.util.SizeUtils

/**
 * 文字类
 * @ Description:
 * @author king
 * @ Date 2020/8/12 18:00
 * @version V1.0
 */
class TextDoodleShape(var context: Context,var iDoodlePen: IDoodlePen) :IDoodleShape(){
    private var drawable: Drawable?=null

    private var realBounds: Rect? = null
    private var textRect: Rect? = null
    private var textPaint: TextPaint? = null

    private var staticLayout: StaticLayout? = null
    private var alignment: Layout.Alignment? = null
    /**
     * Upper bounds for text size.
     * This acts as a starting point for resizing.
     */
    private var maxTextSizePixels = 0f

    /**
     * Lower bounds for text size.
     */
    private var minTextSizePixels = 0f
    init {
        drawable = ContextCompat.getDrawable(context, R.drawable.sticker_transparent_background)
        textPaint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
        realBounds = Rect(0, 0, getWidth(), getHeight())
        textRect = Rect(0, 0, getWidth(), getHeight())
        minTextSizePixels = SizeUtils.sp2px(6f).toFloat()
        maxTextSizePixels = SizeUtils.sp2px(32f).toFloat()
        alignment = Layout.Alignment.ALIGN_CENTER
        textPaint!!.textSize = maxTextSizePixels
    }

    /**
     * Our ellipsis string.
     */
    private val mEllipsis = "\u2026"


    private var text: String? = null



    /**
     * Line spacing multiplier.
     */
    private val lineSpacingMultiplier = 1.0f

    /**
     * Additional line spacing.
     */
    private val lineSpacingExtra = 0.0f


    override fun drawHelpers(canvas: Canvas, doodle: IDoodle) {
        TODO("Not yet implemented")
    }


    override fun draw(canvas: Canvas) {
        TODO("Not yet implemented")
    }

    override fun getWidth(): Int {
        TODO("Not yet implemented")
    }

    override fun getHeight(): Int {
        TODO("Not yet implemented")
    }

    override fun setDrawable(drawable: Drawable): Sticker {
        TODO("Not yet implemented")
    }

    override fun getDrawable(): Drawable {
        TODO("Not yet implemented")
    }

    override fun setAlpha(alpha: Int): Sticker {
        TODO("Not yet implemented")
    }

    /**
     * @return the number of pixels which scaledPixels corresponds to on the device.
     */
    private fun convertSpToPx(scaledPixels: Float): Float {

        return scaledPixels * context.resources.displayMetrics.scaledDensity
    }
}