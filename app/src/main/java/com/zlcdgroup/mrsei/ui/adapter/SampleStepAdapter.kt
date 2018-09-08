package com.zlcdgroup.mrsei.ui.adapter

import android.content.Context
import android.support.v4.app.FragmentManager
import com.stepstone.stepper.Step
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter
import com.zlcdgroup.mrsei.di.qualifier.ActivityContext
import com.zlcdgroup.mrsei.ui.fragment.UserListFragment
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/8 12:05
 * @version V1.0
 */
class SampleStepAdapter  @Inject constructor(@ActivityContext context: Context,fragmentManager: FragmentManager) : AbstractFragmentStepAdapter(fragmentManager,context) {

//    @Inject
//    lateinit var  userListFragment1: UserListFragment
//
//    @Inject
//    lateinit var  userListFragment2: UserListFragment
//
//    @Inject
//    lateinit var  userListFragment3: UserListFragment

    override fun getCount(): Int {
        return  3
    }

    override fun createStep(position: Int): Step {
//        var   userListFragment :UserListFragment = if(position==1) userListFragment1 else if (position == 2) userListFragment2 else userListFragment3
//        var   bundle :Bundle = Bundle()
//        bundle.putInt("postion",position)
//        userListFragment.arguments = bundle
//        return  userListFragment
        return UserListFragment()
    }
}