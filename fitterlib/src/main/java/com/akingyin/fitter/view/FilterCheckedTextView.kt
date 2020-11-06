package com.akingyin.fitter.view


import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable

import androidx.appcompat.widget.AppCompatTextView


/**
 * author: baiiu
 * date: on 16/1/17 21:04
 * description:
 */
class FilterCheckedTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatTextView(context, attrs, defStyleAttr), Checkable {
    private var mChecked = false
    override fun setChecked(checked: Boolean) {
        if (checked != mChecked) {
            mChecked = checked
            refreshDrawableState()
        }
    }

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun toggle() {
        isChecked = !mChecked
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }

    companion object {
        private val CHECKED_STATE_SET = intArrayOf(
                android.R.attr.state_checked
        )
    }
}