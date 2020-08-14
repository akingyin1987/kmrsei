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
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.core.content.ContextCompat
import com.akingyin.media.R
import com.akingyin.media.doodle.core.IDoodle
import com.akingyin.media.doodle.core.IDoodleShape
import com.akingyin.media.doodle.core.Sticker
import com.blankj.utilcode.util.SizeUtils

/**
 * 文字类
 * @ Description:
 * @author king
 * @ Date 2020/8/12 18:00
 * @version V1.0
 */
class TextDoodleShape(var context: Context) : IDoodleShape(){
    private var drawable: Drawable?=null

    private var realBounds: Rect
    private var textRect: Rect
    private var textPaint: TextPaint

    /** 处理文字换行 */
    private var staticLayout: StaticLayout? = null
    private var alignment: Layout.Alignment

    /** 文件大小最大、最小值 */
    private var maxTextSizePixels = 0f

    private var minTextSizePixels = 0f
    init {
        drawable = ContextCompat.getDrawable(context, R.drawable.sticker_transparent_background)
        textPaint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
        realBounds = Rect(0, 0, getWidth(), getHeight())
        textRect = Rect(0, 0, getWidth(), getHeight())
        minTextSizePixels = SizeUtils.sp2px(6f).toFloat()
        maxTextSizePixels = SizeUtils.sp2px(32f).toFloat()
        alignment = Layout.Alignment.ALIGN_CENTER
        textPaint.textSize = maxTextSizePixels
    }

    /**
     * 省略号
     */
    private val mEllipsis = "\u2026"


    private var text: String? = null

    fun getText(): String? {
        return text
    }


    fun setText(text: String?): TextDoodleShape {
        this.text = text
        return this
    }
    /**
     * Line spacing multiplier.
     */
    private var lineSpacingMultiplier = 1.0f

    /**
     * Additional line spacing.
     */
    private var lineSpacingExtra = 0.0f

    /**
     * Resize this view's text size with respect to its width and height
     * (minus padding). You should always call this method after the initialization.
     */

    fun resizeText(): TextDoodleShape {
        val availableHeightPixels = textRect.height()
        val availableWidthPixels = textRect.width()
        val text: CharSequence? = getText()

        // Safety check
        // (Do not resize if the view does not have dimensions or if there is no text)
        if (text.isNullOrEmpty()|| availableHeightPixels <= 0 || availableWidthPixels <= 0 || maxTextSizePixels <= 0) {
            return this
        }
        var targetTextSizePixels = maxTextSizePixels
        var targetTextHeightPixels: Int = getTextHeightPixels(text, availableWidthPixels, targetTextSizePixels)

        // Until we either fit within our TextView
        // or we have reached our minimum text size,
        // incrementally try smaller sizes
        while (targetTextHeightPixels > availableHeightPixels
                && targetTextSizePixels > minTextSizePixels) {
            targetTextSizePixels = Math.max(targetTextSizePixels - 2, minTextSizePixels)
            targetTextHeightPixels = getTextHeightPixels(text, availableWidthPixels, targetTextSizePixels)
        }

        // If we have reached our minimum text size and the text still doesn't fit,
        // append an ellipsis
        // (NOTE: Auto-ellipsize doesn't work hence why we have to do it here)
        if (targetTextSizePixels == minTextSizePixels
                && targetTextHeightPixels > availableHeightPixels) {
            // Make a copy of the original TextPaint object for measuring
            val textPaintCopy = TextPaint(textPaint)
            textPaintCopy.textSize = targetTextSizePixels

            // Measure using a StaticLayout instance
            val staticLayout=  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(text,0,text.length,textPaintCopy,availableWidthPixels)
                        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                        .setLineSpacing(lineSpacingExtra,lineSpacingMultiplier)
                        .setIncludePad(false)
                        .setMaxLines(Int.MAX_VALUE)
                        .build()
            }else{
                StaticLayout(text, textPaintCopy, availableWidthPixels, Layout.Alignment.ALIGN_NORMAL,
                        lineSpacingMultiplier, lineSpacingExtra, false)

            }

            // Check that we have a least one line of rendered text
            if (staticLayout.lineCount > 0) {
                // Since the line at the specific vertical position would be cut off,
                // we must trim up to the previous line and add an ellipsis
                val lastLine = staticLayout.getLineForVertical(availableHeightPixels) - 1
                if (lastLine >= 0) {
                    val startOffset = staticLayout.getLineStart(lastLine)
                    var endOffset = staticLayout.getLineEnd(lastLine)
                    var lineWidthPixels = staticLayout.getLineWidth(lastLine)
                    val ellipseWidth = textPaintCopy.measureText(mEllipsis)

                    // Trim characters off until we have enough room to draw the ellipsis
                    while (availableWidthPixels < lineWidthPixels + ellipseWidth) {
                        endOffset--
                        lineWidthPixels = textPaintCopy.measureText(text.subSequence(startOffset, endOffset + 1).toString())
                    }
                    setText(text.subSequence(0, endOffset).toString() + mEllipsis)
                }
            }
        }
        textPaint.textSize = targetTextSizePixels
        staticLayout = StaticLayout(this.text, textPaint, textRect.width(), alignment, lineSpacingMultiplier,
                lineSpacingExtra, true)
        return this
    }

    /**
     * Sets the text size of a clone of the view's [TextPaint] object
     * and uses a [StaticLayout] instance to measure the height of the text.
     *
     * @return the height of the text when placed in a view
     * with the specified width
     * and when the text has the specified size.
     */
    protected fun getTextHeightPixels( source: CharSequence?, availableWidthPixels: Int,
                                      textSizePixels: Float): Int {
        textPaint.textSize = textSizePixels
        // It's not efficient to create a StaticLayout instance
        // every time when measuring, we can use StaticLayout.Builder
        // since api 23.
        val staticLayout = StaticLayout(source, textPaint, availableWidthPixels, Layout.Alignment.ALIGN_NORMAL,
                lineSpacingMultiplier, lineSpacingExtra, true)
        return staticLayout.height
    }

    override fun drawHelpers(canvas: Canvas, doodle: IDoodle) {

    }


    fun setTypeface( typeface: Typeface?): TextDoodleShape {
        textPaint.typeface = typeface
        return this
    }


    fun setTextColor(@ColorInt color: Int): TextDoodleShape {
        textPaint.color = color
        return this
    }


    fun setTextAlign( alignment: Layout.Alignment): TextDoodleShape {
        this.alignment = alignment
        return this
    }


    fun setMaxTextSize(@Dimension(unit = Dimension.SP) size: Float): TextDoodleShape {
        textPaint.textSize = SizeUtils.sp2px(size).toFloat()
        maxTextSizePixels = textPaint.textSize
        return this
    }


    fun setMinTextSize(minTextSizeScaledPixels: Float): TextDoodleShape {
        minTextSizePixels = SizeUtils.sp2px(minTextSizeScaledPixels).toFloat()
        return this
    }


    fun setLineSpacing(add: Float, multiplier: Float): TextDoodleShape {
        lineSpacingMultiplier = multiplier
        lineSpacingExtra = add
        return this
    }


    override fun draw(canvas: Canvas) {

        canvas.save()
        canvas.concat(matrix)
        drawable?.let {
            it.bounds = realBounds
            it.draw(canvas)
        }
        canvas.restore()
        canvas.save()
        canvas.concat(matrix)
        if (textRect.width() == getWidth()) {
            val dy = getHeight() / 2 - staticLayout!!.height / 2
            // center vertical
            canvas.translate(0f, dy.toFloat())
        } else {
            staticLayout?.let {
                val dx = textRect.left
                val dy = textRect.top + textRect.height() / 2 - it.height / 2
                canvas.translate(dx.toFloat(), dy.toFloat())
            }

        }
        staticLayout?.draw(canvas)
        canvas.restore()
    }

    override fun getWidth()=drawable?.intrinsicWidth?:0

    override fun getHeight()=drawable?.intrinsicHeight?:0

    override fun setDrawable(drawable: Drawable): Sticker {
       this.drawable = drawable
        realBounds[0, 0, getWidth()] = getHeight()
        textRect[0, 0, getWidth()] = getHeight()
        return this
    }

    override fun getDrawable(): Drawable? {
        return  drawable
    }
    override fun release() {
        super.release()
        if (drawable != null) {
            drawable = null
        }
    }
    override fun setAlpha(alpha: Int): IDoodleShape {
        textPaint.alpha = alpha
        return this
    }


}