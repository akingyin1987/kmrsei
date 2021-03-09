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
import com.zlcdgroup.mrsei.databinding.BenchmarkMotionLayoutBinding


/**
 * @ Description:
 * @author king
 * @ Date 2020/6/11 11:28
 * @version V1.0
 */
class MotionTestActivity : SimpleActivity(){
    override fun initInjection() {
    }

    lateinit var viewBinding :BenchmarkMotionLayoutBinding
    override fun getLayoutId()= R.layout.benchmark_motion_layout
    override fun useViewBind()=true

    override fun initViewBind() {
        super.initViewBind()
        viewBinding = BenchmarkMotionLayoutBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }

    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }



    override fun initView() {

        viewBinding.save.click {
           if(it.text == "保存"){
               viewBinding.motionLayout.transitionToEnd()
               onCardCollapsed()
           }else{
               viewBinding.motionLayout.transitionToStart()
               onCardExpanded()
           }

        }
        viewBinding.motionLayout.setTransitionListener(object :MotionLayout.TransitionListener{
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
        viewBinding.save.setText(R.string.save)

        viewBinding.groundTruthAltText.isEnabled = true
        viewBinding.groundTruthLongText.isEnabled = true
        viewBinding.groundTruthAltText.isEnabled = true
        viewBinding.groundTruthLatText.isFocusable = true
        viewBinding.groundTruthLongText.isFocusable = true
        viewBinding.groundTruthLatText.isFocusable = true
    }

    private fun onCardCollapsed() {
        viewBinding.save.setText(R.string.edit)
        viewBinding.groundTruthAltText.isEnabled = false
        viewBinding.groundTruthLongText.isEnabled = false
        viewBinding.groundTruthAltText.isEnabled = false
        viewBinding.groundTruthLatText.isFocusable = false
        viewBinding.groundTruthLongText.isFocusable = false
        viewBinding.groundTruthLatText.isFocusable = false
    }


    override fun startRequest() {

    }
}