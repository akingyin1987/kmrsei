package com.akingyin.media.audio

import android.app.Dialog
import android.os.Bundle

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

/**
 * @ Description:
 * @author king
 * @ Date 2019/7/26 11:41
 * @version V1.0
 */
abstract class AudioManagerDialog : DialogFragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layoutID = getContentView()
        if (layoutID <= 0) throw NullPointerException("DialogFragment ContentView is not null")
        return inflater.inflate(getContentView(), container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) = createDialog(savedInstanceState)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init(savedInstanceState)
        dialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN)
                onBackPressed()
            else false
        }
        dialog?.setCanceledOnTouchOutside(true)
    }

    abstract fun getContentView(): Int
    abstract fun createDialog(savedInstanceState: Bundle?): Dialog
    abstract fun init(savedInstanceState: Bundle?)

    /**
     * 返回true 拦截，否则销毁
     */
    open fun onBackPressed() = false
}