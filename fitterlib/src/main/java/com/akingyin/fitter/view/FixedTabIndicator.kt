package com.akingyin.fitter.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.akingyin.fitter.R
import com.akingyin.fitter.adapter.MenuAdapter
import com.blankj.utilcode.util.SizeUtils


class FixedTabIndicator @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val mTabVisibleCount = 4 // tab数量

    /*
     * 分割线
     */
    private var mDividerPaint: Paint? = null
    private val mDividerColor = -0x222223 // 分割线颜色
    private var mDividerPadding = 13 // 分割线距离上下padding

    /*
     * 上下两条线
     */
    private var mLinePaint: Paint? = null
    private val mLineHeight = 1f
    private val mLineColor = -0x111112
    private val mTabTextSize = 13 // 指针文字的大小,sp
    private val mTabDefaultColor = -0x99999a // 未选中默认颜色
    private val mTabSelectedColor = -0xff720e // 指针选中颜色
    private var drawableRight = 10

    private var mTabCount // 设置的条目数量
            = 0
    var currentIndicatorPosition // 上一个指针选中条目
            = 0
        private set
    var lastIndicatorPosition // 上一个指针选中条目
            = 0
        private set
    private var mOnItemClickListener: OnItemClickListener? = null

    init {

        orientation = HORIZONTAL
        setBackgroundColor(Color.WHITE)
        setWillNotDraw(false)
        mDividerPaint = Paint().apply {
            isAntiAlias = true
            color = mDividerColor
        }

        mLinePaint = Paint().apply {
            color = mLineColor
        }
        mDividerPadding = SizeUtils.dp2px(mDividerPadding.toFloat())
        drawableRight = SizeUtils.dp2px(drawableRight.toFloat())
    }

    /**
     * 条目点击事件
     */
    interface OnItemClickListener {
        /**
         * 回调方法
         *
         * @param v        当前点击的view
         * @param position 当前点击的position
         * @param open     当前的状态，蓝色为open,筛选器为打开状态
         */
        fun onItemClick(v: View?, position: Int, open: Boolean)
    }

    fun setOnItemClickListener(itemClickListenner: OnItemClickListener?) {
        mOnItemClickListener = itemClickListenner
    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0 until mTabCount - 1) { // 分割线的个数比tab的个数少一个
            val child = getChildAt(i)
            if (child == null || child.visibility == GONE) {
                continue
            }
            mDividerPaint?.let {
                canvas.drawLine(child.right.toFloat(), mDividerPadding.toFloat(), child.right.toFloat(), measuredHeight - mDividerPadding.toFloat(), it)
            }

        }

        mLinePaint?.let {
            //上边黑线
            canvas.drawRect(0f, 0f, measuredWidth.toFloat(), mLineHeight, it)

            //下边黑线
            canvas.drawRect(0f, measuredHeight - mLineHeight, measuredWidth.toFloat(), measuredHeight.toFloat(), it)
        }

    }

    /**
     * 添加相应的布局进此容器
     */
    fun setTitles(list: List<String>) {

        removeAllViews()
        mTabCount = list.size
        for (pos in 0 until mTabCount) {
            addView(generateTextView(list[pos], pos))
        }
        postInvalidate()
    }



    fun setTitleAdapter(menuAdapter: MenuAdapter) {

        removeAllViews()
        mTabCount = menuAdapter.getMenuCount()
        for (pos in 0 until mTabCount) {
            addView(generateTextView(menuAdapter.getMenuTitle(pos), pos))
        }
        postInvalidate()
    }

    private fun switchTab(pos: Int) {
        val tv = getChildAtCurPos(pos)
        val drawable = tv.compoundDrawables[2]
        val level = drawable.level
        if (mOnItemClickListener != null) {
            mOnItemClickListener!!.onItemClick(tv, pos, level == 1)
        }
        if (lastIndicatorPosition == pos) {
            // 点击同一个条目时
            tv.setTextColor(if (level == 0) mTabSelectedColor else mTabDefaultColor)
            drawable.level = 1 - level
            return
        }
        currentIndicatorPosition = pos
        resetPos(lastIndicatorPosition)

        //highLightPos(pos);
        tv.setTextColor(mTabSelectedColor)
        tv.compoundDrawables[2].level = 1
        lastIndicatorPosition = pos
    }

    /**
     * 重置字体颜色
     */
    fun resetPos(pos: Int) {
        val tv = getChildAtCurPos(pos)
        tv.setTextColor(mTabDefaultColor)
        tv.compoundDrawables[2].level = 0
    }

    /**
     * 重置当前字体颜色
     */
    fun resetCurrentPos() {
        resetPos(currentIndicatorPosition)
    }

    /**
     * 获取当前pos内的TextView
     */
    fun getChildAtCurPos(pos: Int): TextView {
        return (getChildAt(pos) as ViewGroup).getChildAt(0) as TextView
    }

    /**
     * 直接用TextView使用weight不能控制图片，需要用用父控件包裹
     */
    private fun generateTextView(title: String?, pos: Int): View {
        // 子空间TextView
        val tv = TextView(context)
        tv.gravity = Gravity.CENTER
        tv.text = title
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTabTextSize.toFloat())
        tv.setTextColor(mTabDefaultColor)
        tv.setSingleLine()
        tv.ellipsize = TextUtils.TruncateAt.END
        tv.maxEms = 6 //限制4个字符

        val drawable =  ContextCompat.getDrawable(context,R.drawable.level_filter)
        tv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        tv.compoundDrawablePadding = drawableRight

        // 将TextView添加到父控件RelativeLayout
        val rl = RelativeLayout(context)
        val rlParams = RelativeLayout.LayoutParams(-2, -2)
        rlParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        rl.addView(tv, rlParams)
        rl.id = pos

        // 再将RelativeLayout添加到LinearLayout中
        val params = LayoutParams(-1, -1)
        params.weight = 1f
        params.gravity = Gravity.CENTER
        rl.layoutParams = params
        rl.setOnClickListener { v -> // 设置点击事件
            switchTab(v.id)
        }
        return rl
    }

    /**
     * 高亮字体颜色
     */
    fun highLightPos(pos: Int) {
        val tv = getChildAtCurPos(pos)
        tv.setTextColor(mTabSelectedColor)
        tv.compoundDrawables[2].level = 1
    }

    fun setCurrentText(text: String) {
        setPositionText(currentIndicatorPosition, text)
    }

    fun setPositionText(position: Int, text: String) {
        require(!(position < 0 || position > mTabCount - 1)) { "position 越界" }
        val tv = getChildAtCurPos(position)
        tv.setTextColor(mTabDefaultColor)
        tv.text = text
        tv.compoundDrawables[2].level = 0
    }
}