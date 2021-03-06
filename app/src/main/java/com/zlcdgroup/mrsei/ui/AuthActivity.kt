package com.zlcdgroup.mrsei.ui

import android.content.Intent
import android.os.Bundle

import com.akingyin.base.BaseDaggerActivity


import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.presenter.UserAuthContract
import com.zlcdgroup.mrsei.ui.adapter.AuthAdapter
import dagger.hilt.android.AndroidEntryPoint

import javax.inject.Inject


/**
 * @ Description:
 * @author king
 * @ Date 2019/5/10 13:45
 * @version V1.0
 */

@AndroidEntryPoint
class AuthActivity :BaseDaggerActivity(),UserAuthContract.View{

    override fun getLayoutId() = R.layout.activity_auth

    override fun initializationData(savedInstanceState: Bundle?) {
    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }


    @Inject
    lateinit var  authAdapter :AuthAdapter


    override fun initView() {

    }

    override fun startRequest() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}