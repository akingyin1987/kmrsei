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
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.akingyin.media.R


/**
 * @ Description:
 * @author king
 * @ Date 2020/6/29 11:43
 * @version V1.0
 */
class CheckRadioView : AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var mDrawable: Drawable? = null

    private var mSelectedColor = 0
    private var mUnSelectUdColor = 0

    init {
        viewInit()
    }
    private fun viewInit() {
        mSelectedColor = ResourcesCompat.getColor(
                resources, R.color.item_checkCircle_backgroundColor,
                context.theme)
        mUnSelectUdColor = ResourcesCompat.getColor(
                resources, R.color.check_original_radio_disable,
                context.theme)
        setChecked(false)
    }

    fun setChecked(enable: Boolean) {
        if (enable) {
            setImageResource(R.drawable.ic_preview_radio_on)
            mDrawable = drawable
            mDrawable?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(mSelectedColor, BlendModeCompat.SRC_IN)
        } else {
            setImageResource(R.drawable.ic_preview_radio_off)
            mDrawable = drawable
            mDrawable?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(mUnSelectUdColor,BlendModeCompat.SRC_IN)
        }
    }


    fun setColor(color: Int) {
        if (mDrawable == null) {
            mDrawable = drawable
        }
        mDrawable?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(mSelectedColor, BlendModeCompat.SRC_IN)
    }
}