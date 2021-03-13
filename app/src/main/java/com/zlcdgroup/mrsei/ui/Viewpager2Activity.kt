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
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import androidx.viewpager2.widget.ViewPager2
import com.akingyin.base.SimpleActivity
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.databinding.ActivityViewpager2Binding
import com.zlcdgroup.mrsei.ui.fragment.FragmentLayzTest


class Viewpager2Activity :SimpleActivity() {

    override fun initInjection() {

    }
    lateinit var bindView:ActivityViewpager2Binding

    override fun getLayoutId() = R.layout.activity_viewpager2
    override fun useViewBind() = true

    override fun initViewBind() {
        super.initViewBind()
        bindView = ActivityViewpager2Binding.inflate(layoutInflater)
        setContentView(bindView.root)
    }

    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    private  var  selectPostion = 0
    override fun initView() {
       setToolBar(bindView.topBar.toolbar, "")
         val adapter = FragmentListAdapter()
        bindView.viewpager.adapter = adapter
        bindView.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        bindView.viewpager.isUserInputEnabled = true

        bindView.viewpager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                println("onPageScrolled=$position,$positionOffset,$positionOffsetPixels")
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bindView.viewpager.isUserInputEnabled = false
                if(selectPostion<position){
                    if(!adapter.getItemFragment(selectPostion).isComplete()){
                        bindView.viewpager.currentItem = selectPostion
                    }
                }
                selectPostion = position

                bindView.viewpager.isUserInputEnabled = true
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                println("onPageScrollStateChanged=$state")
            }
        })
       // viewpager.isSaveEnabled
    }

    override fun startRequest() {

    }

    inner  class FragmentListAdapter : FragmentStateAdapter(supportFragmentManager, lifecycle){

         private  val  fragments = mutableListOf<FragmentLayzTest>()
          init {
              fragments.add(FragmentLayzTest.newInstance("test1"))
              fragments.add(FragmentLayzTest.newInstance("test2"))
              fragments.add(FragmentLayzTest.newInstance("test3"))
              fragments.add(FragmentLayzTest.newInstance("test4"))
              fragments.add(FragmentLayzTest.newInstance("test5"))
              fragments.add(FragmentLayzTest.newInstance("test6"))
              fragments.add(FragmentLayzTest.newInstance("test7"))
              fragments.add(FragmentLayzTest.newInstance("test8"))
              fragments.add(FragmentLayzTest.newInstance("test9"))
              fragments.add(FragmentLayzTest.newInstance("test10"))
              fragments.add(FragmentLayzTest.newInstance("test11"))
          }
        override fun getItemCount() = fragments.size

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }

        fun  getItemFragment(position: Int) = fragments[position]


        override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
            super.onBindViewHolder(holder, position, payloads)

        }
    }
}