/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.zlcdgroup.mrsei.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import com.akingyin.base.SimpleActivity
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.model.FunModel
import com.zlcdgroup.mrsei.ui.adapter.FunListAdapter
import kotlinx.android.synthetic.main.activity_test_fun.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @ Description:
 * @author king
 * @ Date 2020/6/23 13:49
 * @version V1.0
 */
class TestFunActivity : SimpleActivity() {

    override fun initInjection() {

    }

    override fun getLayoutId() = R.layout.activity_test_fun

    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    lateinit var   funListAdapter: FunListAdapter
    override fun initView() {
       val  toolbar  = findViewById<Toolbar>(R.id.toolbar)
        setToolBar(toolbar,"功能测试")
        recycler.itemAnimator = DefaultItemAnimator()
        funListAdapter = FunListAdapter()
        recycler.adapter = funListAdapter
        GlobalScope.launch (Main){
            funListAdapter.setNewInstance(
                    withContext(IO){
                        mutableListOf<FunModel>().apply {
                            add(FunModel("百度地图marker", Intent(this@TestFunActivity,TestBaiduMapActivity::class.java)))
                            add(FunModel("高德地图marker", Intent(this@TestFunActivity,TestAmapActivity::class.java)))
                        }
                    }
            )
        }
        funListAdapter.setOnItemClickListener { _, _, position ->
            startActivity(funListAdapter.getItem(position).intent)
        }
    }

    override fun startRequest() {

    }
}