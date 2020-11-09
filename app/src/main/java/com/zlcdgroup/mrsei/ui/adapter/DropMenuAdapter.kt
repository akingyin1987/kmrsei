/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.zlcdgroup.mrsei.ui.adapter

import android.app.Activity
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.akingyin.fitter.adapter.MenuAdapter
import com.akingyin.fitter.adapter.SimpleTextAdapter
import com.akingyin.fitter.typeview.DoubleListView
import com.akingyin.fitter.view.FilterCheckedTextView
import com.blankj.utilcode.util.SizeUtils
import com.zlcdgroup.mrsei.R


import javax.inject.Inject


/**
 * @ Description:
 * @author king
 * @ Date 2020/11/9 16:21
 * @version V1.0
 */
class DropMenuAdapter @Inject constructor(var context: Activity) :MenuAdapter {

    private  val  titles = arrayOf("分类", "排序", "筛选")
    override fun getMenuCount() = titles.size
    override fun getMenuTitle(position: Int) = titles[position]




    override fun getBottomMargin(position: Int): Int {
        return if (position == titles.size -1) {
            0
        } else SizeUtils.dp2px(140F)

    }

    override fun getView(position: Int, parentContainer: FrameLayout): View {
        var view = parentContainer.getChildAt(position)
        when(position){
            0 -> {
                view = createSingleListView()
            }

            1 -> {
                view = createDoubleListView()
            }

            2 -> {
                view = context.layoutInflater.inflate(R.layout.custom_sort_view, null)
            }
        }
        return  view
    }

    private fun  createDoubleListView():View{
        return DoubleListView<String, String>(context).leftAdapter(object : SimpleTextAdapter<String>() {
            override fun provideText(t: String): String {
                return t
            }

            override fun initCheckedTextView(checkedTextView: FilterCheckedTextView, position: Int) {
                super.initCheckedTextView(checkedTextView, position)
                checkedTextView.setPadding(SizeUtils.dp2px( 44f), SizeUtils.dp2px( 15f), 0, SizeUtils.dp2px( 15f))

            }
        }).rightAdapter(object : SimpleTextAdapter<String>() {
            override fun provideText(t: String): String {
                return t
            }
            override fun initCheckedTextView(checkedTextView: FilterCheckedTextView, position: Int) {
                super.initCheckedTextView(checkedTextView, position)
                checkedTextView.setPadding(SizeUtils.dp2px( 30f), SizeUtils.dp2px( 15f), 0, SizeUtils.dp2px( 15f))
                checkedTextView.setBackgroundResource(android.R.color.white)
            }
        }).apply {
            val datas = mutableListOf<String>()
            for (index in 1..10){
                datas.add("一级类型$index")
            }
            mLeftAdapter.setNewInstance(datas)

        }.onLeftItemClickListener(object : DoubleListView.OnLeftItemClickListener<String, String> {
            override fun provideRightList(leftAdapter: String, position: Int): List<String> {
                val datas = mutableListOf<String>()
                if (position != 5) {
                    for (index in 1..10) {
                        datas.add("$position 二级类型$index")
                    }
                }

                return datas
            }
        }).onRightItemClickListener(object : DoubleListView.OnRightItemClickListener<String, String> {
            override fun onRightItemClick(item: String, childItem: String) {
                println("onRightItemClick")
            }
        })
    }

    private fun  createSingleListView():View{
        return RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context)
            adapter =object :SimpleTextAdapter<String>(){
                override fun provideText(t: String): String {
                  return  t
                }
            }.apply {
                setOnItemClickListener { _, _, position ->
                    println("setOnItemClickListener=${getItem(position)}")
                }
                val datas = mutableListOf<String>()
                for (index in 1..10){
                    datas.add("测试$index")
                }
                setNewInstance(datas)
            }
        }
    }
}