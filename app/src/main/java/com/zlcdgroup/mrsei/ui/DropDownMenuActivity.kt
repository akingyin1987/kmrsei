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
import androidx.activity.viewModels

import com.akingyin.base.mvvm.BaseVMActivity
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.databinding.ActivityDropdownMenuBinding
import com.zlcdgroup.mrsei.ui.adapter.DropMenuAdapter
import com.zlcdgroup.mrsei.viewModel.DropDownMenuViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.include_toolbar.*
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2020/11/9 11:08
 * @version V1.0
 */
@AndroidEntryPoint
class DropDownMenuActivity :BaseVMActivity<ActivityDropdownMenuBinding, DropDownMenuViewModel>() {

    private val  dropDownMenuViewModel:DropDownMenuViewModel by viewModels()

    @Inject
    lateinit var  dropMenuAdapter: DropMenuAdapter

    override fun getLayoutId()= R.layout.activity_dropdown_menu

    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    override fun initView() {
       setToolBar(toolbar, "测试筛选")

        mDataBind.dropDownMenu.setMenuAdapter(dropMenuAdapter)
    }

    override fun startRequest() {

    }

    override fun initVM(): DropDownMenuViewModel=dropDownMenuViewModel
}