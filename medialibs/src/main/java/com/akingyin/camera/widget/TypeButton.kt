/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.camera.widget

import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import com.akingyin.media.R


/**
 * @ Description:
 * @author king
 * @ Date 2020/7/20 16:36
 * @version V1.0
 */
class TypeButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var button_type = 0
    private var button_size = 0

    private var center_X = 0f
    private var center_Y = 0f
    private var button_radius = 0f

    private var mPaint: Paint= Paint()
    private var path: Path= Path()
    private var strokeWidth = 0f

    private var index = 0f
    private var rectF: RectF

    init {
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        manager.defaultDisplay.getMetrics(outMetrics)

        val layoutWidth = if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            outMetrics.widthPixels
        } else {
            outMetrics.widthPixels / 2
        }
        button_size = ((layoutWidth / 4.5f).toInt())
        button_type = attrs?.let {
            context.theme.obtainStyledAttributes(it, R.styleable.TypeButton,defStyleAttr,0).getInteger(R.styleable.TypeButton_btnConfigCancel,TYPE_CANCEL)
            0
        }?:TYPE_CANCEL

        button_radius = button_size / 2.0f
        center_X = button_size / 2.0f
        center_Y = button_size / 2.0f
        mPaint = Paint()
        path = Path()
        strokeWidth = button_size / 50f
        index = button_size / 12f
        rectF = RectF(center_X, center_Y - index, center_X + index * 2, center_Y + index)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(button_size, button_size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //如果类型为取消，则绘制内部为返回箭头
        if (button_type == TYPE_CANCEL) {
            mPaint.apply {
                isAntiAlias = true
                color = -0x11232324
                style = Paint.Style.FILL
                canvas.drawCircle(center_X, center_Y, button_radius, this)
            }

            mPaint.apply {
                color = Color.BLACK
                style = Paint.Style.STROKE
                strokeWidth = strokeWidth
            }
            path.apply {
                moveTo(center_X - index / 7, center_Y + index)
                lineTo(center_X + index, center_Y + index)
                arcTo(rectF, 90f, -180f)

                lineTo(center_X - index, center_Y - index)
                canvas.drawPath(this, mPaint)
            }


            mPaint.style = Paint.Style.FILL
            path.run {
                reset()
                moveTo(center_X - index, (center_Y - index * 1.5).toFloat())
                lineTo(center_X - index, (center_Y - index / 2.3).toFloat())
                lineTo((center_X - index * 1.6).toFloat(), center_Y - index)
                close()
                canvas.drawPath(this, mPaint)
            }


        }
        //如果类型为确认，则绘制绿色勾
        if (button_type == TYPE_CONFIRM) {
            mPaint.run {
                isAntiAlias = true
                color = -0x1
                style = Paint.Style.FILL
                canvas.drawCircle(center_X, center_Y, button_radius, this)
            }

            mPaint.run {
                isAntiAlias = true
                style = Paint.Style.STROKE
                color = -0xff3400
                strokeWidth = strokeWidth

                path.run {
                    moveTo(center_X - button_size / 6f, center_Y)
                    lineTo(center_X - button_size / 21.2f, center_Y + button_size / 7.7f)
                    lineTo(center_X + button_size / 4.0f, center_Y - button_size / 8.5f)
                    lineTo(center_X - button_size / 21.2f, center_Y + button_size / 9.4f)
                    close()

                }
                canvas.drawPath(path, this)
            }
        }
    }

    companion object{
        val TYPE_CANCEL = 2
        val TYPE_CONFIRM = 1
    }
}