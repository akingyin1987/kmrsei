/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.akingyin.media.R

/**
 * @ Description:
 * @author king
 * @ Date 2020/6/29 12:09
 * @version V1.0
 */
class CheckView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    val UNCHECKED = Int.MIN_VALUE
    private val STROKE_WIDTH = 3.0f // dp

    private val SHADOW_WIDTH = 6.0f // dp

    private val SIZE = 48 // dp

    private val STROKE_RADIUS = 11.5f // dp

    private val BG_RADIUS = 11.0f // dp

    private val CONTENT_SIZE = 16 // dp

    private var mCountable = false
    private var mChecked = false
    private var mCheckedNum = 0
    private var mStrokePaint: Paint? = null
    private var mBackgroundPaint: Paint? = null
    private var mTextPaint: TextPaint? = null
    private var mShadowPaint: Paint? = null
    private var mCheckDrawable: Drawable? = null
    private var mDensity = 0f
    private var mCheckRect: Rect? = null
    private var mEnabled = true

      init {
          viewInit(context)
      }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // fixed size 48dp x 48dp

        // fixed size 48dp x 48dp
        val sizeSpec = MeasureSpec.makeMeasureSpec((SIZE * mDensity).toInt(), MeasureSpec.EXACTLY)
        super.onMeasure(sizeSpec, sizeSpec)
    }

    private fun viewInit(context: Context) {
        mDensity = context.resources.displayMetrics.density
        mStrokePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
            strokeWidth = STROKE_WIDTH * mDensity
        }

        val ta = getContext().theme.obtainStyledAttributes(intArrayOf(R.attr.item_checkCircle_borderColor))
        val defaultColor = ResourcesCompat.getColor(
                resources, R.color.item_checkCircle_borderColor,
                getContext().theme)
        val color = ta.getColor(0, defaultColor)
        ta.recycle()
        mStrokePaint?.color = color
        mCheckDrawable = ResourcesCompat.getDrawable(context.resources,
                R.drawable.ic_check_white_18dp, context.theme)
    }

    fun setChecked(checked: Boolean) {
        check(!mCountable) { "CheckView is countable, call setCheckedNum() instead." }
        mChecked = checked
        invalidate()
    }

    fun setCountable(countable: Boolean) {
        mCountable = countable
    }

    fun setCheckedNum(checkedNum: Int) {
        check(mCountable) { "CheckView is not countable, call setChecked() instead." }
        require(!(checkedNum != UNCHECKED && checkedNum <= 0)) { "checked num can't be negative." }
        mCheckedNum = checkedNum
        invalidate()
    }

    override fun setEnabled(enabled: Boolean) {
        if (mEnabled != enabled) {
            mEnabled = enabled
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // draw outer and inner shadow
        initShadowPaint()
        canvas.drawCircle(SIZE.toFloat() * mDensity / 2, SIZE.toFloat() * mDensity / 2,
                (STROKE_RADIUS +STROKE_WIDTH / 2 +SHADOW_WIDTH) * mDensity, mShadowPaint!!)

        // draw white stroke
        canvas.drawCircle(SIZE.toFloat() * mDensity / 2, SIZE.toFloat() * mDensity / 2, STROKE_RADIUS * mDensity, mStrokePaint!!)

        // draw content
        if (mCountable) {
            if (mCheckedNum != UNCHECKED) {
                initBackgroundPaint()
                canvas.drawCircle(SIZE.toFloat() * mDensity / 2, SIZE.toFloat() * mDensity / 2,
                        BG_RADIUS * mDensity, mBackgroundPaint!!)
                initTextPaint()
                mTextPaint?.let {
                    val text = mCheckedNum.toString()
                    val baseX = (width - it.measureText(text)).toInt() / 2
                    val baseY = (height - it.descent() - it.ascent()).toInt() / 2
                    canvas.drawText(text, baseX.toFloat(), baseY.toFloat(), it)
                }

            }
        } else {
            if (mChecked) {
                initBackgroundPaint()
                mBackgroundPaint?.let {
                    canvas.drawCircle(SIZE.toFloat() * mDensity / 2, SIZE.toFloat() * mDensity / 2,
                            BG_RADIUS * mDensity, it)
                }

                mCheckDrawable!!.bounds = getCheckRect()!!
                mCheckDrawable!!.draw(canvas)
            }
        }

        // enable hint
        alpha = if (mEnabled) 1.0f else 0.5f
    }

    private fun initShadowPaint() {
        if (mShadowPaint == null) {
            mShadowPaint = Paint()
            mShadowPaint?.isAntiAlias = true
            // all in dp
            val outerRadius: Float = STROKE_RADIUS + STROKE_WIDTH / 2
            val innerRadius: Float = outerRadius - STROKE_WIDTH
            val gradientRadius: Float = outerRadius + SHADOW_WIDTH
            val stop0: Float = (innerRadius - SHADOW_WIDTH) / gradientRadius
            val stop1 = innerRadius / gradientRadius
            val stop2 = outerRadius / gradientRadius
            val stop3 = 1.0f
            mShadowPaint?.shader = RadialGradient(SIZE.toFloat() * mDensity / 2,
                    SIZE.toFloat() * mDensity / 2,
                    gradientRadius * mDensity, intArrayOf(Color.parseColor("#00000000"), Color.parseColor("#0D000000"),
                    Color.parseColor("#0D000000"), Color.parseColor("#00000000")), floatArrayOf(stop0, stop1, stop2, stop3),
                    Shader.TileMode.CLAMP)
        }
    }

    private fun initBackgroundPaint() {
        if (mBackgroundPaint == null) {
            mBackgroundPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
            }

            val ta = context.theme
                    .obtainStyledAttributes(intArrayOf(R.attr.item_checkCircle_backgroundColor))
            val defaultColor = ResourcesCompat.getColor(
                    resources, R.color.item_checkCircle_backgroundColor,
                    context.theme)
            val color = ta.getColor(0, defaultColor)
            ta.recycle()
            mBackgroundPaint?.color = color
        }
    }

    private fun initTextPaint() {
        if (mTextPaint == null) {
            mTextPaint = TextPaint().apply {
                isAntiAlias = true
                color = Color.WHITE
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textSize = 12.0f * mDensity
            }

        }
    }

    // rect for drawing checked number or mark
    private fun getCheckRect(): Rect? {
        if (mCheckRect == null) {
            val rectPadding = (SIZE * mDensity / 2 - CONTENT_SIZE * mDensity / 2).toInt()
            mCheckRect = Rect(rectPadding, rectPadding,
                    (SIZE * mDensity - rectPadding).toInt(), (SIZE * mDensity - rectPadding).toInt())
        }
        return mCheckRect
    }
}