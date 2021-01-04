/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/21 15:31
 * @version V1.0
 */
abstract class BaseDataViewBindFragment<DB : ViewDataBinding> : BaseDaggerFragment() {

    lateinit var mDataBind: DB

    override fun useDataBindView()= true


    override fun initDataBindView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mDataBind = DataBindingUtil.inflate(inflater,getLayoutId(),container,false)
        mDataBind.lifecycleOwner = viewLifecycleOwner
        return mDataBind.root
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBind.unbind()
    }
}