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
import androidx.constraintlayout.motion.widget.MotionLayout
import com.akingyin.base.SimpleActivity
import com.akingyin.base.ext.click
import com.zlcdgroup.mrsei.R
import kotlinx.android.synthetic.main.benchmark_motion_layout.*

/**
 * @ Description:
 * @author king
 * @ Date 2020/6/11 11:28
 * @version V1.0
 */
class MotionTestActivity : SimpleActivity(){
    override fun initInjection() {
    }

    override fun getLayoutId()= R.layout.benchmark_motion_layout

    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }



    override fun initView() {
        save.click {
           if(it.text == "保存"){
               motion_layout.transitionToEnd()
               onCardCollapsed()
           }else{
               motion_layout.transitionToStart()
               onCardExpanded()
           }

        }
        motion_layout.setTransitionListener(object :MotionLayout.TransitionListener{
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {

            }

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {

            }

            override fun onTransitionCompleted(motionLayout:MotionLayout?,  currentId:Int) {
                println("onTransitionCompleted=${currentId}")
//                if (currentId == R.id.expanded) {
//                    onCardExpanded()
//                } else {
//                    onCardCollapsed()
//                }
            }
        })
    }


    private fun onCardExpanded() {
        save.setText(R.string.save)

        ground_truth_lat_text.isEnabled = true
        ground_truth_long_text.isEnabled = true
        ground_truth_alt_text.isEnabled = true
        ground_truth_lat_text.isFocusable = true
        ground_truth_long_text.isFocusable = true
        ground_truth_alt_text.isFocusable = true
    }

    private fun onCardCollapsed() {
        save.setText(R.string.edit)
        ground_truth_lat_text.isEnabled = false
        ground_truth_long_text.isEnabled = false
        ground_truth_alt_text.isEnabled = false
        ground_truth_lat_text.isFocusable = false
        ground_truth_long_text.isFocusable = false
        ground_truth_alt_text.isFocusable = false
    }


    override fun startRequest() {

    }
}