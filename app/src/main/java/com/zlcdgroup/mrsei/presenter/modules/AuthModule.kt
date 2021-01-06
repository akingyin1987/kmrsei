package com.zlcdgroup.mrsei.presenter.modules


import com.zlcdgroup.mrsei.presenter.UserAuthContract
import com.zlcdgroup.mrsei.presenter.impl.UserAuthPresenterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

/**
 * @ Description:
 * @author king
 * @ Date 2019/5/10 13:46
 * @version V1.0
 */

@Module
@InstallIn(ActivityComponent::class)
abstract class AuthModule {


    @Binds
    abstract  fun    tackPresenter(userAuthPresenterImpl: UserAuthPresenterImpl): UserAuthContract.Presenter

}