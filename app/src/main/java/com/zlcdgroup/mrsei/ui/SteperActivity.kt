package com.zlcdgroup.mrsei.ui

import android.os.Bundle
import com.akingyin.base.SimpleActivity
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.ui.fragment.UserListFragment
import dagger.android.AndroidInjection
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/7 16:07
 * @version V1.0
 */


class SteperActivity : SimpleActivity() {

    @Inject
    lateinit var userListFragment: UserListFragment

    override fun initInjection() {
         AndroidInjection.inject(this)
    }

    override fun getLayoutId(): Int = R.layout.activity_stepper

    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {
    }

    override fun initView() {

    }

    override fun startRequest() {
    }
}