/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.zlcdgroup.mrsei.ui.mvvm

import com.akingyin.base.mvvm.viewmodel.BaseViewModel
import com.akingyin.base.net.mode.ApiResult
import com.zlcdgroup.mrsei.data.db.dao.UserEntityDao
import com.zlcdgroup.mrsei.data.source.remote.api.LoginServerApi
import com.zlcdgroup.mrsei.data.source.remote.model.LoginResultModel
import com.zlcdgroup.mrsei.utils.RetrofitConfig
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * @ Description:
 * @author king
 * @ Date 2020/7/13 11:44
 * @version V1.0
 */
class LoginViewModel @Inject constructor(userEntityDao: UserEntityDao) : BaseViewModel() {

   private  val  loginServerApi: LoginServerApi by lazy {
         RetrofitConfig.getDefaultCoroutineServer()

     }

    fun  Login(account:String,passWord:String){
        submit {
            println("Thread1->${Thread.currentThread().id},name=${Thread.currentThread().name}")
              loginServerApi.loginK(account,passWord)

        }.onSuccess {
           it.data
            println("Thread2->${Thread.currentThread().id},name=${Thread.currentThread().name}")
        }.onError {
            it.printStackTrace()
        }



    }

}