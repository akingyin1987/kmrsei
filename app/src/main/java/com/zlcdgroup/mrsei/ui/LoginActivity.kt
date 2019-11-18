package com.zlcdgroup.mrsei.ui

import android.os.Bundle
import android.os.Environment
import com.akingyin.base.BaseActivity
import com.akingyin.base.dialog.DialogUtil
import com.akingyin.base.ext.*
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.presenter.UserLoginContract
import com.zlcdgroup.mrsei.presenter.impl.UserLoginPersenterImpl
import com.zlcdgroup.mrsei.utils.ThemeHelper
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

        val person = userLoginPersenterImpl.getLastPerson()
        person?.let {

            et_mobile.setText(it.personAccount.isEmptyOrNull())
            et_password.setText(it.personPassword.isEmptyOrNull())
        }
        println("btn_login2")

        btn_login.click {

            userLoginPersenterImpl.login(et_mobile.text.toString(),et_password.text.toString())
        }
        app_theme.click {
            app_theme.isChecked.yes {
                println("yes----------->>>>")
            }
            app_theme.isChecked.yes {
                userLoginPersenterImpl.saveAppTheme(ThemeHelper.DARK_MODE)
            }.no {
                userLoginPersenterImpl.saveAppTheme(ThemeHelper.LIGHT_MODE)
            }
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

//        ARouter.getInstance().build("/user/list").withString("name","nametest")
//                .withInt("age",2).navigation()
       //  goActivity<UserListActivity>()
       //   goActivity<CoroutinesDemo>()
       // goActivity<CameraXActivity>()
        mContext?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.apply {
            println("abs$absolutePath")
        }
        startActivity<SimpleCameraActivity>("1" to "2")
    }

    override fun setAppTheme(theme: String) {
        when(theme){
            ThemeHelper.LIGHT_MODE -> {
                app_theme.setText("Light")
                app_theme.isChecked = false
            }

            ThemeHelper.DARK_MODE ->{
                app_theme.setText("DARK")
                app_theme.isChecked = true
            }
            else ->{
                app_theme.setText("DEFAULT")
                app_theme.isChecked = false
            }
        }
    }
}