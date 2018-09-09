package com.zlcdgroup.mrsei.ui
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.View
import com.akingyin.base.BaseActivity
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.ui.fragment.OnNavigationBarListener
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/7 16:07
 * @version V1.0
 */


class SteperActivity : BaseActivity() , StepperLayout.StepperListener, OnNavigationBarListener {

//      @Inject
//      lateinit var sampleStepAdapter: SampleStepAdapter

    @Inject
    lateinit var  fragmentManager: FragmentManager

    override fun getLayoutId(): Int = R.layout.activity_stepper

    override fun initializationData(savedInstanceState: Bundle?) {
//        stepperLayout.adapter = sampleStepAdapter
//        stepperLayout.setListener(this)
    }

    override fun onSaveInstanceData(outState: Bundle?) {
    }

    override fun initView() {

    }

    override fun startRequest() {
    }

    override fun onStepSelected(newStepPosition: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(verificationError: VerificationError?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onReturn() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCompleted(completeButton: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onChangeEndButtonsEnabled(enabled: Boolean) {

    }
}