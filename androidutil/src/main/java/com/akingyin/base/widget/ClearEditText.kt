/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.base.widget


import androidx.appcompat.widget.AppCompatEditText
import android.text.TextWatcher
import android.graphics.drawable.Drawable

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation

import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import androidx.core.content.ContextCompat
import com.akingyin.base.R
import java.lang.IllegalArgumentException

/**
 * 输入清空数据的小组件
 * @author king
 */
class ClearEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = R.attr.editTextStyle) : AppCompatEditText(context, attrs, defStyle), View.OnFocusChangeListener, TextWatcher {
    private var mClearDrawable: Drawable? = null
    var onPasteCallback: OnPasteCallback? = null
    // 是否点击了粘贴
    private var isClickPaste = false
    private var hasFoucs = false

    init {
        mClearDrawable = compoundDrawables[2]
        if (mClearDrawable == null) {
            mClearDrawable= ContextCompat.getDrawable(context, R.drawable.delete_selector)?.apply {
                setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            }

        }

        setClearIconVisible(false)
        onFocusChangeListener = this
        addTextChangedListener(this)
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            if (compoundDrawables[2] != null) {
                val touchable = event.x > width - totalPaddingRight && event.x < width - paddingRight && isEnabled
                if (touchable) {
                    this.setText("")
                }
            }
        }
        try {
            return super.onTouchEvent(event)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
        return false
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        hasFoucs = hasFocus
        if (hasFocus) {
            setClearIconVisible(text!!.length > 0 && isEnabled)
        } else {
            setClearIconVisible(false)
        }
    }

    private fun setClearIconVisible(visible: Boolean) {
        val right = if (visible) mClearDrawable else null
        setCompoundDrawables(compoundDrawables[0],
                compoundDrawables[1], right, compoundDrawables[3])
    }

    override fun onTextChanged(s: CharSequence, start: Int, count: Int,
                               after: Int) {
        if (hasFoucs) {
            setClearIconVisible(s.isNotEmpty())
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                   after: Int) {
    }

    override fun afterTextChanged(s: Editable) {
        if (isClickPaste) {
            isClickPaste = false
            if (null != onPasteCallback) {
                onPasteCallback!!.onPaste()
            }
        }
    }

    fun setShakeAnimation() {
        this.animation = shakeAnimation(5)
    }

    override fun onTextContextMenuItem(id: Int): Boolean {
        when (id) {
            android.R.id.cut -> {
            }
            android.R.id.copy -> {
            }
            android.R.id.paste ->         // 粘贴
                // 是否点击了粘贴
                isClickPaste = true
            else -> {
            }
        }
        return super.onTextContextMenuItem(id)
    }

    interface OnPasteCallback {
        fun onPaste()
    }

    companion object {
        fun shakeAnimation(counts: Int): Animation {
            val translateAnimation: Animation = TranslateAnimation(0F, 10f, 0f, 0f)
            translateAnimation.interpolator = CycleInterpolator(counts.toFloat())
            translateAnimation.duration = 1000
            return translateAnimation
        }
    }


}