/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.fitter

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.akingyin.fitter.adapter.MenuAdapter
import com.akingyin.fitter.view.FixedTabIndicator
import com.blankj.utilcode.util.SizeUtils

/**
 * @ Description:
 * @author king
 * @ Date 2020/11/6 17:22
 * @version V1.0
 */
class DropDownMenu @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), View.OnClickListener, FixedTabIndicator.OnItemClickListener{

    lateinit var fixedTabIndicator: FixedTabIndicator
    lateinit var frameLayoutContainer: FrameLayout

    private var currentView: View? = null

    lateinit var dismissAnimation: Animation
    private var occurAnimation: Animation? = null
    private var alphaDismissAnimation: Animation? = null
    private var alphaOccurAnimation: Animation? = null

    lateinit var mMenuAdapter: MenuAdapter


    init {
        setBackgroundColor(Color.WHITE)
    }


    override fun onFinishInflate() {
        super.onFinishInflate()
        setContentView(findViewById(R.id.mFilterContentView))
    }

    private fun setContentView(contentView: View) {
        removeAllViews()

        /*
         * 1.顶部筛选条
         */
        fixedTabIndicator = FixedTabIndicator(context)
        fixedTabIndicator.id = R.id.fixedTabIndicator
        addView(fixedTabIndicator, -1, SizeUtils.dp2px(50F))
        val params = LayoutParams(-1, -1)
        params.addRule(BELOW, R.id.fixedTabIndicator)

        /*
         * 2.添加contentView,内容界面
         */addView(contentView, params)

        /*
         * 3.添加展开页面,装载筛选器list
         */frameLayoutContainer = FrameLayout(context)
        frameLayoutContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.black_p50))
        addView(frameLayoutContainer, params)
        frameLayoutContainer.visibility = GONE
        initListener()
        initAnimation()
    }

    fun setMenuAdapter(adapter: MenuAdapter) {

        mMenuAdapter = adapter


        //1.设置title
        fixedTabIndicator.setTitleAdapter(mMenuAdapter)

        //2.添加view
        setPositionView()
    }

    /**
     * 可以提供两种方式:
     * 1.缓存所有view,
     * 2.只保存当前view
     *
     *
     * 此处选择第二种
     */
    fun setPositionView() {
        val count: Int = mMenuAdapter.getMenuCount()
        for (position in 0 until count) {
            setPositionView(position, findViewAtPosition(position), mMenuAdapter.getBottomMargin(position))
        }
    }

    fun findViewAtPosition(position: Int): View? {

        var view = frameLayoutContainer.getChildAt(position)
        if (view == null) {
            view = mMenuAdapter.getView(position, frameLayoutContainer)
        }
        return view
    }

    private fun setPositionView(position: Int, view: View?, bottomMargin: Int) {

        check(!(view == null || position > mMenuAdapter.getMenuCount() || position < 0)) { "the view at $position cannot be null" }
        val params = FrameLayout.LayoutParams(-1, -2)
        params.bottomMargin = bottomMargin //添加距离底部高度
        frameLayoutContainer.addView(view, position, params)
        view.visibility = GONE
    }


    fun isShowing(): Boolean {

        return frameLayoutContainer.isShown
    }

    fun isClosed(): Boolean {
        return !isShowing()
    }

    fun close() {
        if (isClosed()) {
            return
        }
        frameLayoutContainer.startAnimation(alphaDismissAnimation)
        fixedTabIndicator.resetCurrentPos()
        currentView?.startAnimation(dismissAnimation)
    }


    fun setPositionIndicatorText(position: Int, text: String) {

        fixedTabIndicator.setPositionText(position, text)
    }

    fun setCurrentIndicatorText(text: String) {

        fixedTabIndicator.setCurrentText(text)
    }

    //=======================之上对外暴漏方法=======================================
    private fun initListener() {
        frameLayoutContainer.setOnClickListener(this)
        fixedTabIndicator.setOnItemClickListener(this)
    }

    override fun onClick(v: View?) {
        if (isShowing()) {
            close()
        }
    }

    override fun onItemClick(v: View?, position: Int, open: Boolean) {
        if (open) {
            close()
        } else {
            currentView = frameLayoutContainer.getChildAt(position)
            if (currentView == null) {
                return
            }
            frameLayoutContainer.getChildAt(fixedTabIndicator.lastIndicatorPosition).visibility = GONE
            frameLayoutContainer.getChildAt(position).visibility = VISIBLE
            if (isClosed()) {
                frameLayoutContainer.visibility = VISIBLE
                frameLayoutContainer.startAnimation(alphaOccurAnimation)

                //可移出去,进行每次展出
                currentView?.startAnimation(occurAnimation)
            }
        }
    }


    private fun initAnimation() {
        occurAnimation = AnimationUtils.loadAnimation(context, R.anim.top_in)

        dismissAnimation = AnimationUtils.loadAnimation(context, R.anim.top_out)
        dismissAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                frameLayoutContainer.visibility = GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
        alphaDismissAnimation = AnimationUtils.loadAnimation(context, R.anim.alpha_to_zero)
        alphaDismissAnimation?.duration = 300
        alphaDismissAnimation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                frameLayoutContainer.visibility = GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
        alphaOccurAnimation = AnimationUtils.loadAnimation(context, R.anim.alpha_to_one)
        alphaOccurAnimation?.duration = 300
    }



}