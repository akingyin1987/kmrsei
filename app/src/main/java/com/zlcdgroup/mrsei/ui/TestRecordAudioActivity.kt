/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.zlcdgroup.mrsei.ui

import android.os.Bundle
import com.akingyin.base.SimpleActivity
import com.akingyin.base.ext.click
import com.akingyin.base.utils.CallLogUtil
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.databinding.ActivityRecordAudioBinding


/**
 * @ Description:
 * @author king
 * @ Date 2020/9/30 11:04
 * @version V1.0
 */
class TestRecordAudioActivity :SimpleActivity() {

    override fun initInjection() {

    }

    override fun getLayoutId() = R.layout.activity_record_audio

    lateinit var bindView:ActivityRecordAudioBinding

    override fun useViewBind()=true

    override fun initViewBind() {
        super.initViewBind()
        bindView = ActivityRecordAudioBinding.inflate(layoutInflater)
        setContentView(bindView.root)
    }

    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    override fun initView() {
        setToolBar(bindView.bar.toolbar,"测试录音")
        bindView.tvTel.click {
            CallLogUtil.findCurrentTel(this)
        }
    }

    override fun startRequest() {

    }
}