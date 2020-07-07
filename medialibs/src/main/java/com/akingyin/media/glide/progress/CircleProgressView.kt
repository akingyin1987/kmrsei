/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */
package com.akingyin.media.glide.progress

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ProgressBar
import androidx.annotation.IntDef
import com.akingyin.media.R
import com.akingyin.util.Utils


/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2020/6/4 12:19
 */
class CircleProgressView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : ProgressBar(context, attrs, defStyleAttr, defStyleRes) {


    private var mReachBarSize: Int = Utils.dp2px(context, 2) // 未完成进度条大小

    private var mNormalBarSize: Int = Utils.dp2px(context, 2) // 未完成进度条大小

    private var mReachBarColor: Int = Color.parseColor("#108ee9") // 已完成进度颜色

     var mNormalBarColor: Int = Color.parseColor("#FFD3D6DA") // 未完成进度颜色

    private var mTextSize: Int = Utils.sp2px(context, 14) // 进度值字体大小

    private var mTextColor: Int = Color.parseColor("#108ee9") // 进度的值字体颜色

    private var mTextSkewX // 进度值字体倾斜角度
            = 0f
    private var mTextSuffix = "%" // 进度值前缀

    private var mTextPrefix = "" // 进度值后缀

    private var mTextVisible = true // 是否显示进度值

    private var mReachCapRound // 画笔是否使用圆角边界，normalStyle下生效
            = false
    private var mRadius: Int = Utils.dp2px(context, 20) // 半径

    private var mStartArc // 起始角度
            = 0
    private var mInnerBackgroundColor // 内部背景填充颜色
            = 0
    private var mProgressStyle: Int = ProgressStyle.NORMAL // 进度风格

    private var mInnerPadding: Int = Utils.dp2px(context, 1) // 内部圆与外部圆间距

    private var mOuterColor // 外部圆环颜色
            = 0
    private var needDrawInnerBackground // 是否需要绘制内部背景
            = false

    // 外部圆环绘制区域
    private var rectF: RectF? = null
    // 内部圆环绘制区域
    private var rectInner: RectF? = null
    private var mOuterSize: Int = Utils.dp2px(context, 1) // 外层圆环宽度
    // 绘制进度值字体画笔
    lateinit var mTextPaint: Paint

    // 绘制未完成进度画笔
    private var mNormalPaint: Paint? = null

    // 绘制已完成进度画笔
    private var mReachPaint: Paint? = null

    // 内部背景画笔
    private var mInnerBackgroundPaint: Paint? = null

    // 外部圆环画笔
    private var mOutPaint: Paint? = null

    private var mRealWidth = 0
    private var mRealHeight = 0

    @IntDef(ProgressStyle.NORMAL, ProgressStyle.FILL_IN, ProgressStyle.FILL_IN_ARC)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ProgressStyle {
        companion object {
            const val  NORMAL = 0
            const val FILL_IN = 1
            const val FILL_IN_ARC = 2
        }
    }

    /**
     * 初始化画笔
     */
    private fun initPaint() {
        mTextPaint = Paint().apply {
            color = mTextColor
            style = Paint.Style.FILL
            textSize = mTextSize.toFloat()
            textSkewX = mTextSkewX
            isAntiAlias = true
        }

        mNormalPaint = Paint().apply {
            color = mNormalBarColor
            style = if (mProgressStyle == ProgressStyle.FILL_IN_ARC) Paint.Style.FILL else Paint.Style.STROKE
            isAntiAlias = true
            strokeWidth = mNormalBarSize.toFloat()
        }

        mReachPaint = Paint().apply {
           color = mReachBarColor
           style = if (mProgressStyle == ProgressStyle.FILL_IN_ARC) Paint.Style.FILL else Paint.Style.STROKE
           isAntiAlias = true
            strokeCap = if (mReachCapRound) Paint.Cap.ROUND else Paint.Cap.BUTT
            strokeWidth = mReachBarSize.toFloat()
        }

        if (needDrawInnerBackground) {
            mInnerBackgroundPaint = Paint().apply {
               style = Paint.Style.FILL
               isAntiAlias = true
                color = mInnerBackgroundColor
            }

        }
        if (mProgressStyle == ProgressStyle.FILL_IN_ARC) {
            mOutPaint = Paint().apply {
               style = Paint.Style.STROKE
               color = mOuterColor
               strokeWidth = mOuterSize.toFloat()
               isAntiAlias = true
            }

        }
    }


    private fun obtainAttributes(attrs: AttributeSet) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView)
        mProgressStyle = ta.getInt(R.styleable.CircleProgressView_progressStyle, ProgressStyle.NORMAL)
        // 获取三种风格通用的属性
        mNormalBarSize = ta.getDimension(R.styleable.CircleProgressView_progressNormalSize, mNormalBarSize.toFloat()).toInt()
        mNormalBarColor = ta.getColor(R.styleable.CircleProgressView_progressNormalColor, mNormalBarColor)
        mReachBarSize = ta.getDimension(R.styleable.CircleProgressView_progressReachSize, mReachBarSize.toFloat()).toInt()
        mReachBarColor = ta.getColor(R.styleable.CircleProgressView_progressReachColor, mReachBarColor)
        mTextSize = ta.getDimension(R.styleable.CircleProgressView_progressTextSize, mTextSize.toFloat()).toInt()
        mTextColor = ta.getColor(R.styleable.CircleProgressView_progressTextColor, mTextColor)
        mTextSkewX = ta.getDimension(R.styleable.CircleProgressView_progressTextSkewX, 0f)
        if (ta.hasValue(R.styleable.CircleProgressView_progressTextSuffix)) {
            mTextSuffix = ta.getString(R.styleable.CircleProgressView_progressTextSuffix)!!
        }
        if (ta.hasValue(R.styleable.CircleProgressView_progressTextPrefix)) {
            mTextPrefix = ta.getString(R.styleable.CircleProgressView_progressTextPrefix)!!
        }
        mTextVisible = ta.getBoolean(R.styleable.CircleProgressView_progressTextVisible, mTextVisible)
        mRadius = ta.getDimension(R.styleable.CircleProgressView_radius, mRadius.toFloat()).toInt()
        rectF = RectF((-mRadius).toFloat(), (-mRadius).toFloat(), mRadius.toFloat(), mRadius.toFloat())
        when (mProgressStyle) {
            ProgressStyle.FILL_IN -> {
                mReachBarSize = 0
                mNormalBarSize = 0
                mOuterSize = 0
            }
            ProgressStyle.FILL_IN_ARC -> {
                mStartArc = ta.getInt(R.styleable.CircleProgressView_progressStartArc, 0) + 270
                mInnerPadding = ta.getDimension(R.styleable.CircleProgressView_innerPadding, mInnerPadding.toFloat()).toInt()
                mOuterColor = ta.getColor(R.styleable.CircleProgressView_outerColor, mReachBarColor)
                mOuterSize = ta.getDimension(R.styleable.CircleProgressView_outerSize, mOuterSize.toFloat()).toInt()
                mReachBarSize = 0 // 将画笔大小重置为0
                mNormalBarSize = 0
                if (!ta.hasValue(R.styleable.CircleProgressView_progressNormalColor)) {
                    mNormalBarColor = Color.TRANSPARENT
                }
                val mInnerRadius = mRadius - mOuterSize / 2 - mInnerPadding
                rectInner = RectF((-mInnerRadius).toFloat(), (-mInnerRadius).toFloat(), mInnerRadius.toFloat(), mInnerRadius.toFloat())
            }
            ProgressStyle.NORMAL -> {
                mReachCapRound = ta.getBoolean(R.styleable.CircleProgressView_reachCapRound, true)
                mStartArc = ta.getInt(R.styleable.CircleProgressView_progressStartArc, 0) + 270
                if (ta.hasValue(R.styleable.CircleProgressView_innerBackgroundColor)) {
                    mInnerBackgroundColor = ta.getColor(R.styleable.CircleProgressView_innerBackgroundColor, Color.argb(0, 0, 0, 0))
                    needDrawInnerBackground = true
                }
            }
        }
        ta.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val maxBarPaintWidth = Math.max(mReachBarSize, mNormalBarSize)
        val maxPaintWidth = Math.max(maxBarPaintWidth, mOuterSize)
        var height = 0
        var width = 0
        when (mProgressStyle) {
            ProgressStyle.FILL_IN -> {
                height = (paddingTop + paddingBottom // 边距
                        + Math.abs(mRadius * 2)) // 直径
                width = (paddingLeft + paddingRight // 边距
                        + Math.abs(mRadius * 2)) // 直径
            }
            ProgressStyle.FILL_IN_ARC -> {
                height = (paddingTop + paddingBottom // 边距
                        + Math.abs(mRadius * 2) // 直径
                        + maxPaintWidth) // 边框
                width = (paddingLeft + paddingRight // 边距
                        + Math.abs(mRadius * 2) // 直径
                        + maxPaintWidth) // 边框
            }
            ProgressStyle.NORMAL -> {
                height = (paddingTop + paddingBottom // 边距
                        + Math.abs(mRadius * 2) // 直径
                        + maxBarPaintWidth) // 边框
                width = (paddingLeft + paddingRight // 边距
                        + Math.abs(mRadius * 2) // 直径
                        + maxBarPaintWidth) // 边框
            }
        }

        mRealWidth = View.resolveSize(width, widthMeasureSpec)
        mRealHeight = View.resolveSize(height, heightMeasureSpec)

        setMeasuredDimension(mRealWidth, mRealHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            when (mProgressStyle) {
                ProgressStyle.NORMAL -> drawNormalCircle(it)
                ProgressStyle.FILL_IN -> drawFillInCircle(it)
                ProgressStyle.FILL_IN_ARC -> drawFillInArcCircle(it)
            }
        }

    }


    /**
     * 绘制PROGRESS_STYLE_FILL_IN_ARC圆形
     */
    private fun drawFillInArcCircle(canvas: Canvas) {
        canvas.save()
        canvas.translate(mRealWidth / 2.toFloat(), mRealHeight / 2.toFloat())
        // 绘制外层圆环
        canvas.drawArc(rectF!!, 0f, 360f, false, mOutPaint!!)
        // 绘制内层进度实心圆弧
        // 内层圆弧半径
        val reachArc = progress * 1.0f / max * 360
        canvas.drawArc(rectInner!!, mStartArc.toFloat(), reachArc, true, mReachPaint!!)

        // 绘制未到达进度
        if (reachArc != 360f) {
            canvas.drawArc(rectInner!!, reachArc + mStartArc, 360 - reachArc, true, mNormalPaint!!)
        }
        canvas.restore()
    }

    /**
     * 绘制PROGRESS_STYLE_FILL_IN圆形
     */
    private fun drawFillInCircle(canvas: Canvas) {
        canvas.save()
        canvas.translate(mRealWidth / 2.toFloat(), mRealHeight / 2.toFloat())
        val progressY = progress * 1.0f / max * (mRadius * 2)
        val angle = (Math.acos((mRadius - progressY) / mRadius.toDouble()) * 180 / Math.PI).toFloat()
        val startAngle = 90 + angle
        val sweepAngle = 360 - angle * 2
        // 绘制未到达区域
        rectF = RectF((-mRadius).toFloat(), (-mRadius).toFloat(), mRadius.toFloat(), mRadius.toFloat())
        mNormalPaint!!.style = Paint.Style.FILL
        canvas.drawArc(rectF!!, startAngle, sweepAngle, false, mNormalPaint!!)
        // 翻转180度绘制已到达区域
        canvas.rotate(180f)
        mReachPaint!!.style = Paint.Style.FILL
        canvas.drawArc(rectF!!, 270 - angle, angle * 2, false, mReachPaint!!)
        // 文字显示在最上层最后绘制
        canvas.rotate(180f)
        // 绘制文字
        if (mTextVisible) {
            val text = mTextPrefix + progress + mTextSuffix
            val textWidth = mTextPaint.measureText(text)
            val textHeight = mTextPaint.descent() + mTextPaint.ascent()
            canvas.drawText(text, -textWidth / 2, -textHeight / 2, mTextPaint)
        }
    }

    /**
     * 绘制PROGRESS_STYLE_NORMAL圆形
     */
    private fun drawNormalCircle(canvas: Canvas) {
        canvas.save()
        canvas.translate(mRealWidth / 2.toFloat(), mRealHeight / 2.toFloat())
        // 绘制内部圆形背景色
        if (needDrawInnerBackground) {
            canvas.drawCircle(0f, 0f, mRadius - Math.min(mReachBarSize, mNormalBarSize) / 2.toFloat(),
                    mInnerBackgroundPaint!!)
        }
        // 绘制文字
        if (mTextVisible) {
            val text = mTextPrefix + progress + mTextSuffix
            val textWidth = mTextPaint.measureText(text)
            val textHeight = mTextPaint.descent() + mTextPaint.ascent()
            canvas.drawText(text, -textWidth / 2, -textHeight / 2, mTextPaint)
        }
        // 计算进度值
        val reachArc = progress * 1.0f / max * 360
        // 绘制未到达进度
        if (reachArc != 360f) {
            canvas.drawArc(rectF!!, reachArc + mStartArc, 360 - reachArc, false, mNormalPaint!!)
        }
        // 绘制已到达进度
        canvas.drawArc(rectF!!, mStartArc.toFloat(), reachArc, false, mReachPaint!!)
        canvas.restore()
    }

    /**
     * 动画进度(0-当前进度)
     *
     * @param duration 动画时长
     */
    fun runProgressAnim(duration: Long) {
        setProgressInTime(0, duration)
    }

    /**
     * @param progress 进度值
     * @param duration 动画播放时间
     */
    fun setProgressInTime(progress: Int, duration: Long) {
        setProgressInTime(progress, getProgress(), duration)
    }

    /**
     * @param startProgress 起始进度
     * @param progress      进度值
     * @param duration      动画播放时间
     */
    fun setProgressInTime(startProgress: Int, progress: Int, duration: Long) {
        val valueAnimator = ValueAnimator.ofInt(startProgress, progress)
        valueAnimator.addUpdateListener { animator -> //获得当前动画的进度值，整型，1-100之间
            val currentValue = animator.animatedValue as Int
            setProgress(currentValue)
        }
        val interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.interpolator = interpolator
        valueAnimator.duration = duration
        valueAnimator.start()
    }
    fun getReachBarSize(): Int {
        return mReachBarSize
    }

    fun setReachBarSize(reachBarSize: Int) {
        mReachBarSize = Utils.dp2px(context, reachBarSize)
        invalidate()
    }

    fun getNormalBarSize(): Int {
        return mNormalBarSize
    }

    fun setNormalBarSize(normalBarSize: Int) {
        mNormalBarSize = Utils.dp2px(context, normalBarSize)
        invalidate()
    }

    fun getReachBarColor(): Int {
        return mReachBarColor
    }

    fun setReachBarColor(reachBarColor: Int) {
        mReachBarColor = reachBarColor
        invalidate()
    }

    fun getNormalBarColor(): Int {
        return mNormalBarColor
    }

    fun setNormalBarColor(normalBarColor: Int) {
        mNormalBarColor = normalBarColor
        invalidate()
    }

    fun getTextSize(): Int {
        return mTextSize
    }

    fun setTextSize(textSize: Int) {
        mTextSize = Utils.sp2px(context, textSize)
        invalidate()
    }

    fun getTextColor(): Int {
        return mTextColor
    }

    fun setTextColor(textColor: Int) {
        mTextColor = textColor
        invalidate()
    }

    fun getTextSkewX(): Float {
        return mTextSkewX
    }

    fun setTextSkewX(textSkewX: Float) {
        mTextSkewX = textSkewX
        invalidate()
    }

    fun getTextSuffix(): String? {
        return mTextSuffix
    }

    fun setTextSuffix(textSuffix: String) {
        mTextSuffix = textSuffix
        invalidate()
    }

    fun getTextPrefix(): String? {
        return mTextPrefix
    }

    fun setTextPrefix(textPrefix: String) {
        mTextPrefix = textPrefix
        invalidate()
    }

    fun isTextVisible(): Boolean {
        return mTextVisible
    }

    fun setTextVisible(textVisible: Boolean) {
        mTextVisible = textVisible
        invalidate()
    }

    fun isReachCapRound(): Boolean {
        return mReachCapRound
    }

    fun setReachCapRound(reachCapRound: Boolean) {
        mReachCapRound = reachCapRound
        invalidate()
    }

    fun getRadius(): Int {
        return mRadius
    }

    fun setRadius(radius: Int) {
        mRadius = Utils.dp2px(context, radius)
        invalidate()
    }

    fun getStartArc(): Int {
        return mStartArc
    }

    fun setStartArc(startArc: Int) {
        mStartArc = startArc
        invalidate()
    }

    fun getInnerBackgroundColor(): Int {
        return mInnerBackgroundColor
    }

    fun setInnerBackgroundColor(innerBackgroundColor: Int) {
        mInnerBackgroundColor = innerBackgroundColor
        invalidate()
    }

    fun getProgressStyle(): Int {
        return mProgressStyle
    }

    fun setProgressStyle(progressStyle: Int) {
        mProgressStyle = progressStyle
        invalidate()
    }

    fun getInnerPadding(): Int {
        return mInnerPadding
    }

    fun setInnerPadding(innerPadding: Int) {
        mInnerPadding = Utils.dp2px(context, innerPadding)
        val mInnerRadius = mRadius - mOuterSize / 2 - mInnerPadding
        rectInner = RectF((-mInnerRadius).toFloat(), (-mInnerRadius).toFloat(), mInnerRadius.toFloat(), mInnerRadius.toFloat())
        invalidate()
    }

    fun getOuterColor(): Int {
        return mOuterColor
    }

    fun setOuterColor(outerColor: Int) {
        mOuterColor = outerColor
        invalidate()
    }

    fun getOuterSize(): Int {
        return mOuterSize
    }

    fun setOuterSize(outerSize: Int) {
        mOuterSize = Utils.dp2px(context, outerSize)
        invalidate()
    }

    private val STATE = "state"
    private val PROGRESS_STYLE = "progressStyle"
    private val TEXT_COLOR = "textColor"
    private val TEXT_SIZE = "textSize"
    private val TEXT_SKEW_X = "textSkewX"
    private val TEXT_VISIBLE = "textVisible"
    private val TEXT_SUFFIX = "textSuffix"
    private val TEXT_PREFIX = "textPrefix"
    private val REACH_BAR_COLOR = "reachBarColor"
    private val REACH_BAR_SIZE = "reachBarSize"
    private val NORMAL_BAR_COLOR = "normalBarColor"
    private val NORMAL_BAR_SIZE = "normalBarSize"
    private val IS_REACH_CAP_ROUND = "isReachCapRound"
    private val RADIUS = "radius"
    private val START_ARC = "startArc"
    private val INNER_BG_COLOR = "innerBgColor"
    private val INNER_PADDING = "innerPadding"
    private val OUTER_COLOR = "outerColor"
    private val OUTER_SIZE = "outerSize"

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(STATE, super.onSaveInstanceState())
        // 保存当前样式
        bundle.putInt(PROGRESS_STYLE, getProgressStyle())
        bundle.putInt(RADIUS, getRadius())
        bundle.putBoolean(IS_REACH_CAP_ROUND, isReachCapRound())
        bundle.putInt(START_ARC, getStartArc())
        bundle.putInt(INNER_BG_COLOR, getInnerBackgroundColor())
        bundle.putInt(INNER_PADDING, getInnerPadding())
        bundle.putInt(OUTER_COLOR, getOuterColor())
        bundle.putInt(OUTER_SIZE, getOuterSize())
        // 保存text信息
        bundle.putInt(TEXT_COLOR, getTextColor())
        bundle.putInt(TEXT_SIZE, getTextSize())
        bundle.putFloat(TEXT_SKEW_X, getTextSkewX())
        bundle.putBoolean(TEXT_VISIBLE, isTextVisible())
        bundle.putString(TEXT_SUFFIX, getTextSuffix())
        bundle.putString(TEXT_PREFIX, getTextPrefix())
        // 保存已到达进度信息
        bundle.putInt(REACH_BAR_COLOR, getReachBarColor())
        bundle.putInt(REACH_BAR_SIZE, getReachBarSize())

        // 保存未到达进度信息
        bundle.putInt(NORMAL_BAR_COLOR, getNormalBarColor())
        bundle.putInt(NORMAL_BAR_SIZE, getNormalBarSize())
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val bundle = state
            mProgressStyle = bundle.getInt(PROGRESS_STYLE)
            mRadius = bundle.getInt(RADIUS)
            mReachCapRound = bundle.getBoolean(IS_REACH_CAP_ROUND)
            mStartArc = bundle.getInt(START_ARC)
            mInnerBackgroundColor = bundle.getInt(INNER_BG_COLOR)
            mInnerPadding = bundle.getInt(INNER_PADDING)
            mOuterColor = bundle.getInt(OUTER_COLOR)
            mOuterSize = bundle.getInt(OUTER_SIZE)
            mTextColor = bundle.getInt(TEXT_COLOR)
            mTextSize = bundle.getInt(TEXT_SIZE)
            mTextSkewX = bundle.getFloat(TEXT_SKEW_X)
            mTextVisible = bundle.getBoolean(TEXT_VISIBLE)
            mTextSuffix = bundle.getString(TEXT_SUFFIX)!!
            mTextPrefix = bundle.getString(TEXT_PREFIX)!!
            mReachBarColor = bundle.getInt(REACH_BAR_COLOR)
            mReachBarSize = bundle.getInt(REACH_BAR_SIZE)
            mNormalBarColor = bundle.getInt(NORMAL_BAR_COLOR)
            mNormalBarSize = bundle.getInt(NORMAL_BAR_SIZE)
            initPaint()
            super.onRestoreInstanceState(bundle.getParcelable(STATE))
            return
        }
        super.onRestoreInstanceState(state)
    }

    override fun invalidate() {
        initPaint()
        super.invalidate()
    }

    init {
        attrs?.let {
            obtainAttributes(it)
        }
        initPaint()
    }
}