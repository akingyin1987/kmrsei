package com.akingyin.base.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.akingyin.base.R

/**
 * 自定义电池
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/11/18 17:24
 */
class PowerConsumptionRankingsBatteryView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    /**
     * 自定义View的宽
     */
    private var measurewidth = 0

    /**
     * 自定义View的高
     */
    private var measureheight = 0

    /**
     * 抗锯齿标志
     */
    private var drawFilter: DrawFilter? = null

    /**
     * 电池外壳 厚度
     */
    private var shellStrokeWidth = 0

    /**
     * 电池外壳 圆角
     */
    private var shellCornerRadius = 0

    /**
     * 电池外壳 宽度
     */
    private var shellWidth = 0

    /**
     * 电池外壳 宽度
     */
    private var shellHeight = 0

    /**
     * 电池头 圆角
     */
    private var shellHeadCornerRadius = 0

    /**
     * 电池头 宽度
     */
    private var shellHeadWidth = 0

    /**
     * 电池头 高度
     */
    private var shellHeadHeight = 0

    /**
     * 电池宽度
     */
    private var levelWidth = 0

    /**
     * 电池最大高度
     */
    private var levelMaxHeight = 0

    /**
     * 电池高度
     */
    private var levelHeight = 100

    /**
     * 电池外壳和电池等级直接的间距
     */
    private var gap = 0

    /**
     * 电池外壳 画笔
     */
    private var shellPaint: Paint? = null

    /**
     * 电池外壳
     */
    private var shellRectF: RectF? = null

    /**
     * 电池头
     */
    private var shellHeadRect: RectF? = null

    /**
     * 电池电量 画笔
     */
    private var levelPaint: Paint? = null

    /**
     * 电池电量
     */
    private var levelRect: RectF? = null

    /**
     * 低电颜色
     */
    private var lowerPowerColor = 0

    /**
     * 在线颜色
     */
    private var onlineColor = 0

    /**
     * 离线颜色
     */
    private var offlineColor = 0

    init {
        drawFilter = PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        initTypeArray(context, attrs)

        //设置 电池壳画笔的相关属性
        shellPaint = Paint().apply {
           isAntiAlias = true
           color = onlineColor
           strokeWidth = shellStrokeWidth.toFloat()
           isAntiAlias = true
        }


        //设置 电池画笔的相关属性
        levelPaint = Paint().apply {
            color = onlineColor
            style = Paint.Style.FILL
            strokeWidth = levelWidth.toFloat()
        }

        shellRectF = RectF()
        shellHeadRect = RectF()
        levelRect = RectF()
    }



    /**
     * 初始化自定义属性
     */
    private fun initTypeArray(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PowerConsumptionRankingsBatteryView)
        lowerPowerColor = typedArray.getColor(R.styleable.PowerConsumptionRankingsBatteryView_batteryLowerPowerColor,
                ContextCompat.getColor(context, R.color.lowerPowerColor))
        onlineColor = typedArray.getColor(R.styleable.PowerConsumptionRankingsBatteryView_batteryOnlineColor,
                ContextCompat.getColor(context, R.color.onlineColor))
        offlineColor = typedArray.getColor(R.styleable.PowerConsumptionRankingsBatteryView_batteryOfflineColor,
                ContextCompat.getColor(context, R.color.offlineColor))
        //外壳的相关信息
        shellCornerRadius = typedArray.getDimensionPixelOffset(R.styleable.PowerConsumptionRankingsBatteryView_batteryShellCornerRadius,
                resources.getDimensionPixelOffset(R.dimen.power_consumption_rankings_dimen_main_battery_view_shell_corner))
        shellWidth = typedArray.getDimensionPixelOffset(R.styleable.PowerConsumptionRankingsBatteryView_batteryShellWidth,
                resources.getDimensionPixelOffset(R.dimen.power_consumption_rankings_dimen_main_battery_view_shell_width))
        shellHeight = typedArray.getDimensionPixelOffset(R.styleable.PowerConsumptionRankingsBatteryView_batteryShellHeight,
                resources.getDimensionPixelOffset(R.dimen.power_consumption_rankings_dimen_main_battery_view_shell_height))
        shellStrokeWidth = typedArray.getDimensionPixelOffset(R.styleable.PowerConsumptionRankingsBatteryView_batteryShellStrokeWidth,
                resources.getDimensionPixelOffset(R.dimen.power_consumption_rankings_dimen_main_battery_view_shell_stroke_width))

        //电池头的相关信息
        shellHeadCornerRadius = typedArray.getDimensionPixelOffset(R.styleable.PowerConsumptionRankingsBatteryView_batteryShellHeadCornerRadius,
                resources.getDimensionPixelOffset(R.dimen.power_consumption_rankings_dimen_main_battery_view_head_corner))
        shellHeadWidth = typedArray.getDimensionPixelOffset(R.styleable.PowerConsumptionRankingsBatteryView_batteryShellHeadWidth,
                resources.getDimensionPixelOffset(R.dimen.power_consumption_rankings_dimen_main_battery_view_head_width))
        shellHeadHeight = typedArray.getDimensionPixelOffset(R.styleable.PowerConsumptionRankingsBatteryView_batteryShellHeadHeight,
                resources.getDimensionPixelOffset(R.dimen.power_consumption_rankings_dimen_main_battery_view_head_height))

        //电池最大高度
        levelMaxHeight = typedArray.getDimensionPixelOffset(R.styleable.PowerConsumptionRankingsBatteryView_batteryLevelMaxHeight,
                resources.getDimensionPixelOffset(R.dimen.power_consumption_rankings_dimen_main_battery_level_max_height))
        //电池宽度
        levelWidth = typedArray.getDimensionPixelOffset(R.styleable.PowerConsumptionRankingsBatteryView_batteryLevelWidth,
                resources.getDimensionPixelOffset(R.dimen.power_consumption_rankings_dimen_main_battery_level_width))

        //电池外壳和电池等级直接的间距
        gap = typedArray.getDimensionPixelOffset(R.styleable.PowerConsumptionRankingsBatteryView_batteryGap,
                resources.getDimensionPixelOffset(R.dimen.power_consumption_rankings_dimen_main_battery_view_gap))
        //回收typedArray
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //对View上的內容进行测量后得到的View內容占据的宽度
        measurewidth = measuredWidth
        //对View上的內容进行测量后得到的View內容占据的高度
        measureheight = measuredHeight
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawFilter = drawFilter

        // 电池头 矩形的坐标
        shellHeadRect?.apply {
            //坐标 left：控件整体宽度的一半 减去 电池头宽度的一半
            left = (measurewidth shr 1) - (shellHeadWidth shr 1).toFloat()
            //坐标 top： 0
            top = 0f
            //坐标 right：控件整体宽度的一半 加上 电池头宽度的一半
            right = (measurewidth shr 1) + (shellHeadWidth shr 1).toFloat()
            //坐标 bottom：电池头的高度
            bottom = shellHeadHeight.toFloat()
        }


        // 电池壳 矩形的坐标
        shellRectF?.apply {
            //坐标 left：电池壳厚度的一半
            left = shellStrokeWidth / 2.toFloat()
            //坐标 left：电池壳厚度的一半 加上 电池头的高度
            top = shellStrokeWidth / 2 + shellHeadHeight.toFloat()
            //坐标 right：控件整体宽度 减去 电池壳厚度的一半
            right = measurewidth - shellStrokeWidth / 2.toFloat()
            //坐标 bottom：控件整体高度 减去 电池壳厚度的一半
            bottom = measureheight - shellStrokeWidth / 2.toFloat()
        }


        // 电池电量 矩形的坐标
        levelRect?.apply {
            //坐标 left：电池壳厚度的一半 加上 电池外壳和电池等级直接的间距
            left = shellStrokeWidth + gap.toFloat()

            //电池满格时候的最大高度 ：（控件整体高度  减去电池壳厚度 减去电池头高度 减去 电池外壳和电池等级直接的间距的两倍）
            //topOffset: 电池满格时候的最大高度 * （电池满格100 - 当前的电量）/ 电池满格100
            val topOffset = (measureheight - shellHeadHeight - gap * 2 - shellStrokeWidth) * (MAX_LEVEL - levelHeight) / MAX_LEVEL.toFloat()
            //坐标 top：电池头的高度 + 电池壳的厚度 + 电池外壳和电池等级直接的间距 + topOffset
            top = shellHeadHeight + shellStrokeWidth + gap + topOffset

            //坐标 right：控件整体宽度 减去 电池壳厚度的一半 减去 电池外壳和电池等级直接的间距
            right = measurewidth - shellStrokeWidth - gap.toFloat()

            //坐标 bottom：控件整体宽度 减去 电池壳厚度的一半 减去 电池外壳和电池等级直接的间距
            bottom = measureheight - shellStrokeWidth - gap.toFloat()
        }


        //绘制电池头

        shellPaint?.let {shellPaint->
            shellPaint.style = Paint.Style.FILL
            shellHeadRect?.let {
                canvas.drawRoundRect(it, shellHeadCornerRadius.toFloat(), shellHeadCornerRadius.toFloat(), shellPaint)
                //由于电池头的左下角和右下角不是圆角的，因此我们需要画一个矩形 覆盖圆角
                //绘制左下角矩形
                canvas.drawRect(it.left, it.bottom - shellHeadCornerRadius,
                        it.left + shellHeadCornerRadius, it.bottom, shellPaint)
                //绘制右下角矩形
                canvas.drawRect(it.right - shellHeadCornerRadius, it.bottom - shellHeadCornerRadius,
                        it.right, it.bottom, shellPaint)
            }
        }

        //绘制电池壳
        shellPaint?.let {shellPaint->
            shellPaint.style = Paint.Style.STROKE
            shellRectF?.let {shellRectF->
                canvas.drawRoundRect(shellRectF, shellCornerRadius.toFloat(), shellCornerRadius.toFloat(), shellPaint)

                //绘制电池等级
                canvas.drawRect(levelRect!!, levelPaint!!)
            }
        }




    }

    /**
     * 设置电池电量
     *
     * @param level
     */
    fun setLevelHeight(level: Int) {
        levelHeight = level
        if (levelHeight < 0) {
            levelHeight = MAX_LEVEL
        } else if (levelHeight > MAX_LEVEL) {
            levelHeight = MAX_LEVEL
        }
        postInvalidate()
    }

    /**
     * 设置在线 重绘
     */
    fun setOnline() {
        shellPaint?.color = onlineColor
        levelPaint?.color = onlineColor
        postInvalidate()
    }

    /**
     * 设置离线 重绘
     */
    fun setOffline() {
        shellPaint?.color = offlineColor
        levelPaint?.color = offlineColor
        postInvalidate()
    }

    /**
     * 设置低电 重绘
     */
    fun setLowerPower() {
        shellPaint?.color = lowerPowerColor
        levelPaint?.color = lowerPowerColor
        postInvalidate()
    }

    companion object {
        /**
         * 电池最大电量
         */
        const val MAX_LEVEL = 100

        /**
         * 电池默认电量
         */
        const val DEFAULT_LEVEL = 40
    }
}