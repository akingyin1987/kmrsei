package com.zlcdgroup.mrsei.presenter.modules

import android.app.Activity
import com.zlcdgroup.mrsei.di.scope.PerActivity
import com.zlcdgroup.mrsei.presenter.UserAuthContract
import com.zlcdgroup.mrsei.presenter.impl.UserAuthPresenterImpl
import com.zlcdgroup.mrsei.ui.AuthActivity
import dagger.Binds
import dagger.Module

/**
 * @ Description:
 * @author king
 * @ Date 2019/5/10 13:46
 * @version V1.0
 */

@Module
abstract class AuthModule {
    @PerActivity
    @Binds
    abstract  fun   activity(activty: AuthActivity): Activity

    @Binds
    @PerActivity
    abstract  fun    tackPresenter(userAuthPresenterImpl: UserAuthPresenterImpl): UserAuthContract.Presenter

}