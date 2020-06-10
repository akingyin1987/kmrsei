/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */
package com.akingyin.img.glide.progress

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.ProgressBar
import androidx.annotation.IntDef
import com.akingyin.util.Utils


/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2020/6/4 12:19
 */
class CircleProgressView : ProgressBar {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int,
                defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    fun  t(){

    }
    private val mReachBarSize: Int = Utils.dp2px(context, 2) // 未完成进度条大小

    private val mNormalBarSize: Int = Utils.dp2px(context, 2) // 未完成进度条大小

    private val mReachBarColor: Int = Color.parseColor("#108ee9") // 已完成进度颜色

    private val mNormalBarColor: Int = Color.parseColor("#FFD3D6DA") // 未完成进度颜色

    private val mTextSize: Int = Utils.sp2px(context, 14) // 进度值字体大小

    private val mTextColor: Int = android.graphics.Color.parseColor("#108ee9") // 进度的值字体颜色

    private val mTextSkewX // 进度值字体倾斜角度
            = 0f
    private val mTextSuffix = "%" // 进度值前缀

    private val mTextPrefix = "" // 进度值后缀

    private val mTextVisible = true // 是否显示进度值

    private val mReachCapRound // 画笔是否使用圆角边界，normalStyle下生效
            = false
    private val mRadius: Int = Utils.dp2px(context, 20) // 半径

    private val mStartArc // 起始角度
            = 0
    private val mInnerBackgroundColor // 内部背景填充颜色
            = 0
    private val mProgressStyle: Int = ProgressStyle.NORMAL // 进度风格

    private val mInnerPadding: Int = Utils.dp2px(context, 1) // 内部圆与外部圆间距

    private val mOuterColor // 外部圆环颜色
            = 0
    private val needDrawInnerBackground // 是否需要绘制内部背景
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

    private fun initPaint() {
        mTextPaint = Paint().apply {
            color = mTextColor
            style = Paint.Style.FILL
            textSize = mTextSize.toFloat()
            textSkewX = mTextSkewX
            isAntiAlias = true
        }

        mNormalPaint = Paint()
        mNormalPaint!!.color = mNormalBarColor
        mNormalPaint!!.style = if (mProgressStyle == ProgressStyle.FILL_IN_ARC) Paint.Style.FILL else Paint.Style.STROKE
        mNormalPaint!!.isAntiAlias = true
        mNormalPaint!!.strokeWidth = mNormalBarSize.toFloat()
        mReachPaint = Paint()
        mReachPaint!!.color = mReachBarColor
        mReachPaint!!.style = if (mProgressStyle == ProgressStyle.FILL_IN_ARC) Paint.Style.FILL else Paint.Style.STROKE
        mReachPaint!!.isAntiAlias = true
        mReachPaint!!.strokeCap = if (mReachCapRound) Paint.Cap.ROUND else Paint.Cap.BUTT
        mReachPaint!!.strokeWidth = mReachBarSize.toFloat()
        if (needDrawInnerBackground) {
            mInnerBackgroundPaint = Paint()
            mInnerBackgroundPaint!!.style = Paint.Style.FILL
            mInnerBackgroundPaint!!.isAntiAlias = true
            mInnerBackgroundPaint!!.color = mInnerBackgroundColor
        }
        if (mProgressStyle == ProgressStyle.FILL_IN_ARC) {
            mOutPaint = Paint()
            mOutPaint!!.style = Paint.Style.STROKE
            mOutPaint!!.color = mOuterColor
            mOutPaint!!.strokeWidth = mOuterSize.toFloat()
            mOutPaint!!.isAntiAlias = true
        }
    }

}