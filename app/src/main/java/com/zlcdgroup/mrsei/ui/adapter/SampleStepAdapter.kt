package com.zlcdgroup.mrsei.ui.adapter

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
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
class SampleStepAdapter   constructor( context: Context, fragmentManager: FragmentManager) : AbstractFragmentStepAdapter(fragmentManager,context) {


     var  userListFragment1: UserListFragment = UserListFragment()


     var  userListFragment2: UserListFragment = UserListFragment()


     var  userListFragment3: UserListFragment = UserListFragment()

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