package com.zlcdgroup.mrsei.ui

import android.os.Bundle
import com.akingyin.base.BaseActivity
import com.akingyin.base.dialog.DialogUtil
import com.akingyin.base.ext.click
import com.akingyin.base.ext.goActivity
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.presenter.UserLoginContract
import com.zlcdgroup.mrsei.presenter.impl.UserLoginPersenterImpl
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2018/10/8 12:16
 * @version V1.0
 */
class LoginActivity  : BaseActivity() ,UserLoginContract.View{

    @Inject
    lateinit var userLoginPersenterImpl: UserLoginPersenterImpl



    override fun getLayoutId(): Int = R.layout.activity_login

    override fun initializationData(savedInstanceState: Bundle?) {
        userLoginPersenterImpl.attachView(this)
    }

    override fun onSaveInstanceData(outState: Bundle?) {
    }

    override fun initView() {

        println("btn_login2")
        btn_login.click {
            userLoginPersenterImpl.login(et_mobile.text.toString(),et_password.text.toString())
        }


    }

    override fun startRequest() {


    }

    override fun dismissLoading() {
        super.dismissLoading()
        userLoginPersenterImpl.cancelSubscribe()
    }

    override fun showConfigDialog(message: String) {
        DialogUtil.showConfigDialog(this,message) {
            aBoolean -> if(aBoolean){

        }

        }
    }

    override fun goToMainActivity() {
        goActivity<UserListActivity>()
    }
}