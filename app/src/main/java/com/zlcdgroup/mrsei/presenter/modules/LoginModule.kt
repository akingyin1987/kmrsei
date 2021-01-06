package com.zlcdgroup.mrsei.presenter.modules

import android.app.Activity
import com.zlcdgroup.mrsei.di.scope.PerActivity
import com.zlcdgroup.mrsei.presenter.UserLoginContract
import com.zlcdgroup.mrsei.presenter.impl.UserLoginPersenterImpl
import com.zlcdgroup.mrsei.ui.LoginActivity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

/**
 * @ Description:
 * @author king
 * @ Date 2018/10/10 14:35
 * @version V1.0
 */
@Module
@InstallIn(ActivityComponent::class)
abstract class LoginModule {




    @Binds
    @PerActivity
    abstract  fun    tackPresenter(loginPersenterImpl: UserLoginPersenterImpl):UserLoginContract.Presenter

}