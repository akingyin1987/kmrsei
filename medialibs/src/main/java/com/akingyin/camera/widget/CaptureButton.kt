/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.camera.widget

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.akingyin.media.R

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/20 15:03
 * @version V1.0
 */

@Suppress("ClickableViewAccessibility")
class CaptureButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    //当前按钮状态
    private var state = 0

    //按钮可执行的功能状态（拍照,录制,两者）
    private var buttonState = 0
    //按钮回调接口
     var captureLisenter: CaptureListener? = null
    //长按后处理的逻辑Runnable
    private val longPressRunnable: LongPressRunnable? = null
    //计时器
    private var timer: RecordCountDownTimer? = null

    private val progressColor = -0x11e951ea //进度条颜色

    private val outsideColor = -0x11232324 //外圆背景色

    private val insideColor = -0x1 //内圆背景色

    //Touch_Event_Down时候记录的Y值
    private var eventY = 0f


    private var mPaint: Paint? = null

    //进度条宽度
    private var strokeWidth = 0f

    //长按外圆半径变大的Size
    private var outsideAddSize = 0

    //长安内圆缩小的Size
    private var insideReduceSize = 0

    //中心坐标
    private var centerX = 0f
    private var centerY = 0f

    //按钮半径
    private var buttonRadius = 0f

    //外圆半径
    private var buttonOutsideRadius = 0f

    //内圆半径
    private var buttonInsideRadius = 0f

    //按钮大小
    private var buttonSize = 0

    //录制视频的进度
    private var progress = 0f

    //录制视频最大时间长度
    private var duration = 0

    //最短录制时间限制
    private var minDuration = 0

    //记录当前录制的时间
    private var recordedTime = 0L

    private var rectF: RectF? = null

    init {
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        manager.defaultDisplay.getMetrics(outMetrics)
        val layoutWidth = if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            outMetrics.widthPixels
        } else {
            outMetrics.widthPixels / 2
        }

        buttonSize = ((layoutWidth / 4.5f).toInt())
        buttonRadius = (buttonSize / 2F)

        buttonOutsideRadius = buttonRadius
        buttonInsideRadius = buttonRadius * 0.75f

        strokeWidth = buttonSize / 15.toFloat()
        outsideAddSize = buttonSize / 5
        insideReduceSize = buttonSize / 8

        mPaint = Paint().apply {
           isAntiAlias = true
        }

        progress = 0f

        state = STATE_IDLE //初始化为空闲状态
        buttonState =  attrs?.let {

            context.theme.obtainStyledAttributes(it, R.styleable.CaptureButton,defStyleAttr,0).getInteger(R.styleable.CaptureButton_capture_photo_video,BUTTON_STATE_ONLY_CAPTURE)

        }?:BUTTON_STATE_ONLY_CAPTURE




        duration = 10 * 1000 //默认最长录制时间为10s

        minDuration = 1500 //默认最短录制时间为1.5s
        centerX = (buttonSize + outsideAddSize * 2) / 2.toFloat()
        centerY = (buttonSize + outsideAddSize * 2) / 2.toFloat()

        rectF = RectF(
                centerX - (buttonRadius + outsideAddSize - strokeWidth / 2),
                centerY - (buttonRadius + outsideAddSize - strokeWidth / 2),
                centerX + (buttonRadius + outsideAddSize - strokeWidth / 2),
                centerY + (buttonRadius + outsideAddSize - strokeWidth / 2))

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(buttonSize + outsideAddSize * 2, buttonSize + outsideAddSize * 2)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mPaint?.run {
            style = Paint.Style.FILL
            color = outsideColor //外圆（半透明灰色）
            canvas?.drawCircle(centerX, centerY, buttonOutsideRadius, this)
        }

        mPaint?.run {
            color = insideColor //内圆（白色）
            canvas?.drawCircle(centerX, centerY, buttonInsideRadius,this)
        }
        //如果状态为录制状态，则绘制录制进度条

        //如果状态为录制状态，则绘制录制进度条
        if (state == STATE_RECORDERING) {
            mPaint?.run {
                color = progressColor
                style = Paint.Style.STROKE
                strokeWidth = strokeWidth
                canvas?.drawArc(rectF!!, -90f, progress, false, this)
            }


        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                 println("state--->$state")
                if (event.pointerCount > 1 || state != STATE_IDLE) {
                    return false
                }
                eventY = event.y //记录Y值
                state = STATE_PRESS //修改当前状态为点击按下

                //判断按钮状态是否为可录制状态
                if (buttonState == BUTTON_STATE_ONLY_RECORDER || buttonState == BUTTON_STATE_BOTH) {
                    postDelayed(longPressRunnable, 500) //同时延长500启动长按后处理的逻辑Runnable
                }
            }
            MotionEvent.ACTION_MOVE -> if (captureLisenter != null && state == STATE_RECORDERING && (buttonState == BUTTON_STATE_ONLY_RECORDER || buttonState == BUTTON_STATE_BOTH)) {
                //记录当前Y值与按下时候Y值的差值，调用缩放回调接口
                captureLisenter?.recordZoom(eventY - event.y)
            }
            MotionEvent.ACTION_UP ->                 //根据当前按钮的状态进行相应的处理
                handlerUnpressByState()
        }
        return true
    }

    //内圆动画
    private fun startCaptureAnimation(inside_start: Float) {
        val insideAnim = ValueAnimator.ofFloat(inside_start, inside_start * 0.75f, inside_start)
        insideAnim.addUpdateListener { animation ->
            buttonInsideRadius = animation.animatedValue as Float
            invalidate()
        }
        insideAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                //回调拍照接口
                captureLisenter?.takePictures()
                state = STATE_BAN
            }
        })
        insideAnim.duration = 100
        insideAnim.start()
    }

    //内外圆动画
    private fun startRecordAnimation(outside_start: Float, outside_end: Float, inside_start: Float, inside_end: Float) {
        val outsideAnim = ValueAnimator.ofFloat(outside_start, outside_end)
        val insideAnim = ValueAnimator.ofFloat(inside_start, inside_end)
        //外圆动画监听
        outsideAnim.addUpdateListener { animation ->
            buttonOutsideRadius = animation.animatedValue as Float
            invalidate()
        }
        //内圆动画监听
        insideAnim.addUpdateListener { animation ->
            buttonInsideRadius = animation.animatedValue as Float
            invalidate()
        }
        val set = AnimatorSet()
        //当动画结束后启动录像Runnable并且回调录像开始接口
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                //设置为录制状态
                if (state == STATE_LONG_PRESS) {
                    captureLisenter?.recordStart()
                    state = STATE_RECORDERING
                    timer?.start()
                }
            }
        })
        set.playTogether(outsideAnim, insideAnim)
        set.duration = 100
        set.start()
    }

    //更新进度条
    private fun updateProgress(millisUntilFinished: Long) {
        recordedTime = (duration - millisUntilFinished)
        progress = 360f - millisUntilFinished / duration.toFloat() * 360f
        invalidate()
    }

    //当手指松开按钮时候处理的逻辑
    private fun handlerUnpressByState() {
        removeCallbacks(longPressRunnable) //移除长按逻辑的Runnable
        when (state) {
            STATE_PRESS -> if (captureLisenter != null && (buttonState == BUTTON_STATE_ONLY_CAPTURE || buttonState ==
                            BUTTON_STATE_BOTH)) {
                startCaptureAnimation(buttonInsideRadius)
            } else {
                state = STATE_IDLE
            }
            STATE_RECORDERING -> {
                timer?.cancel() //停止计时器
                recordEnd() //录制结束
            }
        }
    }

    //录制结束
    private fun recordEnd() {
        if (captureLisenter != null) {
            if (recordedTime < minDuration) captureLisenter?.recordShort(recordedTime) //回调录制时间过短
            else captureLisenter?.recordEnd(recordedTime) //回调录制结束
        }
        resetRecordAnim() //重制按钮状态
    }

    //重制状态
     fun resetRecordAnim() {
        state = STATE_IDLE
        progress = 0f //重制进度
        invalidate()
        //还原按钮初始状态动画
        startRecordAnimation(
                buttonOutsideRadius,
                buttonRadius,
                buttonInsideRadius,
                buttonRadius * 0.75f
        )
    }

    //设置最长录制时间
    fun setDuration(duration: Int) {
        this.duration = duration
        timer = RecordCountDownTimer(duration.toLong(), (duration / 360).toLong()) //录制定时器
    }


    companion object{
       const val BUTTON_STATE_ONLY_CAPTURE = 1 //只能拍照

       const val BUTTON_STATE_ONLY_RECORDER = 2 //只能录像

       const val BUTTON_STATE_BOTH = 0 //两者都可以


        const val STATE_IDLE = 0x001 //空闲状态

        const val STATE_PRESS = 0x002 //按下状态

        const val STATE_LONG_PRESS = 0x003 //长按状态

        const val STATE_RECORDERING = 0x004 //录制状态

        const val STATE_BAN = 0x005 //禁止状态
   }

   inner class  RecordCountDownTimer(millisInFuture: Long, countDownInterval: Long):CountDownTimer(millisInFuture, countDownInterval){

        override fun onFinish() {
            updateProgress(0)
            recordEnd()
        }

        override fun onTick(millisUntilFinished: Long) {
           updateProgress(millisUntilFinished)
        }
    }

    inner class LongPressRunnable : Runnable{
        override fun run() {
            state = STATE_LONG_PRESS //如果按下后经过500毫秒则会修改当前状态为长按状态

            //没有录制权限
            //没有录制权限
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                if(ContextCompat.checkSelfPermission(context,Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
                    state = STATE_IDLE
                    if (captureLisenter != null) {
                        captureLisenter?.recordError()
                        return
                    }
                }

            }
            //启动按钮动画，外圆变大，内圆缩小
            //启动按钮动画，外圆变大，内圆缩小
            startRecordAnimation(
                    buttonOutsideRadius,
                    buttonOutsideRadius + outsideAddSize,
                    buttonInsideRadius,
                    buttonInsideRadius - insideReduceSize
            )
        }
    }


    abstract class onClickTakePicturesListener : CaptureListener{
        override fun recordShort(time: Long) {

        }

        override fun recordStart() {

        }

        override fun recordEnd(time: Long) {

        }

        override fun recordZoom(zoom: Float) {

        }

        override fun recordError() {

        }
    }

     interface CaptureListener{
         fun takePictures()

         fun recordShort(time: Long)

         fun recordStart()

         fun recordEnd(time: Long)

         fun recordZoom(zoom: Float)

         fun recordError()
     }

}