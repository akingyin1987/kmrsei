package com.zlcdgroup.mrsei.ui.adapter

import android.app.Activity
import android.os.Bundle
import com.stepstone.stepper.Step
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter
import com.stepstone.stepper.viewmodel.StepViewModel
import com.zlcdgroup.mrsei.ui.fragment.UserListFragment
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/8 12:05
 * @version V1.0
 */
class SampleStepAdapter  @Inject constructor(context: Activity, fragmentManager: androidx.fragment.app.FragmentManager) : AbstractFragmentStepAdapter(fragmentManager,context) {

    @Inject
    lateinit var  userListFragment1: UserListFragment

    @Inject
    lateinit var  userListFragment2: UserListFragment

    @Inject
    lateinit var  userListFragment3: UserListFragment

    override fun getCount(): Int {
        return  3
    }

    override fun createStep(position: Int): Step {
        var   userListFragment :UserListFragment = if(position==1) userListFragment1 else if (position == 2) userListFragment2 else userListFragment3
        var   bundle :Bundle = Bundle()
        bundle.putString("postion",position.toString())
        userListFragment.arguments = bundle
        return  userListFragment
       // return UserListFragment()
    }

    override fun getViewModel(position: Int): StepViewModel {
        when(position){
            0 -> return StepViewModel.Builder(context).setTitle("测试$position").create()

            1 -> return StepViewModel.Builder(context).setTitle("测试1$position").create()

            2 -> return StepViewModel.Builder(context).setTitle("测试2$position").create()
        }
        println("getViewModel  StepViewModel")
        return StepViewModel.Builder(context).create()
    }
}