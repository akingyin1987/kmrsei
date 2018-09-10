package com.zlcdgroup.mrsei.ui
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.View
import com.akingyin.base.BaseActivity
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import com.zlcdgroup.mrsei.R

import com.zlcdgroup.mrsei.ui.adapter.SampleStepAdapter
import com.zlcdgroup.mrsei.ui.fragment.OnNavigationBarListener
import kotlinx.android.synthetic.main.activity_stepper.*
import kotlinx.android.synthetic.main.include_toolbar.*
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/7 16:07
 * @version V1.0
 */


class SteperActivity : BaseActivity() , StepperLayout.StepperListener, OnNavigationBarListener {

      @Inject
      lateinit var sampleStepAdapter: SampleStepAdapter

    @Inject
    lateinit var  fragmentManager: FragmentManager

    override fun getLayoutId(): Int = R.layout.activity_stepper

    override fun initializationData(savedInstanceState: Bundle?) {
        stepperLayout.adapter = sampleStepAdapter

        stepperLayout.setListener(this)
    }

    override fun onSaveInstanceData(outState: Bundle?) {
    }

    override fun initView() {
       setToolBar(toolbar,"测试2")
    }

    override fun startRequest() {
    }

    override fun onStepSelected(newStepPosition: Int) {
        println("onStepSelected $newStepPosition")

    }

    override fun onError(verificationError: VerificationError?) {
        println("onError ${verificationError?.errorMessage}")
    }

    override fun onReturn() {

        println("onReturn")
    }

    override fun onCompleted(completeButton: View?) {
        println("onCompleted ")
    }

    override fun onChangeEndButtonsEnabled(enabled: Boolean) {

    }

    override fun onBackPressed() {
        val currentStepPosition = stepperLayout.currentStepPosition
        if (currentStepPosition > 0) {
            stepperLayout.onBackClicked()
        } else {
            super.onBackPressed()
        }

    }
}