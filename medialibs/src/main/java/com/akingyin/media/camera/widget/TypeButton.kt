/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.camera.widget

import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.use
import com.akingyin.media.R
import com.qmuiteam.qmui.util.QMUIDisplayHelper


/**
 * @ Description:
 * @author king
 * @ Date 2020/7/20 16:36
 * @version V1.0
 */
class TypeButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var buttonType = 0
    private var buttonSize = 90
    private var  btnConfigCancelText=""

    private var centerX = 0f
    private var centerY = 0f
    private var buttonRadius = 0f

    private var mPaint: Paint= Paint()
    private var path: Path= Path()
    private var strokeWidth = 0f

    private var index = 0f
    private var rectF: RectF

    init {
        val outMetrics = QMUIDisplayHelper.getDisplayMetrics(context)


        val layoutWidth = if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            outMetrics.widthPixels
        } else {
            outMetrics.widthPixels / 2
        }
        buttonSize = ((layoutWidth / 4.5f).toInt())
        buttonType = attrs?.let {
            context.theme.obtainStyledAttributes(it, R.styleable.TypeButton, defStyleAttr, 0).use { typed->
                btnConfigCancelText = typed.getString(R.styleable.TypeButton_btnConfigCancelText)?:""
                typed.getInteger(R.styleable.TypeButton_btnConfigCancel, TYPE_CANCEL)
            }

        }?:TYPE_CANCEL

        buttonRadius = buttonSize / 2.0f
        centerX = buttonSize / 2.0f
        centerY = buttonSize / 2.0f
        mPaint = Paint()
        path = Path()
        strokeWidth = buttonSize / 50f
        index = buttonSize / 12f
        rectF = RectF(centerX, centerY - index, centerX + index * 2, centerY + index)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(buttonSize, buttonSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //如果类型为取消，则绘制内部为返回箭头
        if (buttonType == TYPE_CANCEL) {
            mPaint.apply {
                isAntiAlias = true
                color = -0x11232324
                style = Paint.Style.FILL
                canvas.drawCircle(centerX, centerY, buttonRadius, this)
            }

            mPaint.apply {
                color = Color.BLACK
                style = Paint.Style.STROKE
                strokeWidth = strokeWidth
            }
            path.apply {
                moveTo(centerX - index / 7, centerY + index)
                lineTo(centerX + index, centerY + index)
                arcTo(rectF, 90f, -180f)

                lineTo(centerX - index, centerY - index)
                canvas.drawPath(this, mPaint)
            }


            mPaint.style = Paint.Style.FILL
            path.run {
                reset()
                moveTo(centerX - index, (centerY - index * 1.5).toFloat())
                lineTo(centerX - index, (centerY - index / 2.3).toFloat())
                lineTo((centerX - index * 1.6).toFloat(), centerY - index)
                close()
                canvas.drawPath(this, mPaint)
            }

        }
        //如果类型为确认，则绘制绿色勾
        if (buttonType == TYPE_CONFIRM) {
            mPaint.run {
                isAntiAlias = true
                color = -0x1
                style = Paint.Style.FILL
                canvas.drawCircle(centerX, centerY, buttonRadius, this)
            }

            mPaint.run {
                isAntiAlias = true
                style = Paint.Style.STROKE
                color = -0xff3400
                strokeWidth = strokeWidth

                path.run {
                    moveTo(centerX - buttonSize / 6f, centerY)
                    lineTo(centerX - buttonSize / 21.2f, centerY + buttonSize / 7.7f)
                    lineTo(centerX + buttonSize / 4.0f, centerY - buttonSize / 8.5f)
                    lineTo(centerX - buttonSize / 21.2f, centerY + buttonSize / 9.4f)
                    close()

                }
                canvas.drawPath(path, this)
            }

        }
        if(buttonType == TYPE_CUSTOM){
            mPaint.reset()
            mPaint.run {
                isAntiAlias = true
                color = -0x1
                style = Paint.Style.FILL
                canvas.drawCircle(centerX, centerY, buttonRadius, this)
            }
            mPaint.run {
                color = Color.RED
                textSize = 50f
                style = Paint.Style.STROKE
                //获取文本宽度
                val textWidth: Float = measureText(btnConfigCancelText)
                val x = width / 2 - textWidth / 2
                val fontMetrics: Paint.FontMetrics = fontMetrics
                val dy: Float = (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent
                val y = height / 2 + dy
                canvas.drawText(btnConfigCancelText, x, y, this)
            }
        }
    }

    companion object{
       const val TYPE_CANCEL = 2
       const val TYPE_CONFIRM = 1
        const val TYPE_CUSTOM=3
    }
}