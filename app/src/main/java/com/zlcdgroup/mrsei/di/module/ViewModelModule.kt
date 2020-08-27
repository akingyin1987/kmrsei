/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.zlcdgroup.mrsei.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zlcdgroup.mrsei.data.db.dao.UserEntityDao
import com.zlcdgroup.mrsei.ui.mvvm.LoginViewModel
import com.zlcdgroup.mrsei.viewModel.UserViewModel
import dagger.Module
import dagger.Provides

/**
 * viewModel 注入
 * @ Description:
 * @author king
 * @ Date 2020/8/27 11:16
 * @version V1.0
 */

@Module
abstract class ViewModelModule {

    @Module(includes = arrayOf(ViewModelModule::class))
    class  ViewModelProvideModule{

        @Provides
        fun  provideLoginModel(userEntityDao: UserEntityDao):LoginViewModel{
            return  LoginViewModelFactory(userEntityDao).create(LoginViewModel::class.java)
        }

    }

    class LoginViewModelFactory(var userEntityDao: UserEntityDao): ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LoginViewModel(userEntityDao) as T
        }
    }
}