package com.zlcdgroup.mrsei.presenter.modules


import com.zlcdgroup.mrsei.di.scope.PerActivity
import com.zlcdgroup.mrsei.presenter.UserListContract
import com.zlcdgroup.mrsei.presenter.impl.UserListPresenterImpl

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent


/**
 * @ Description:
 * @author king
 * @ Date 2018/9/6 16:28
 * @version V1.0
 */
 @Module
 @InstallIn(ActivityComponent::class)
 abstract class UserModule {






     @Binds
     @PerActivity
    abstract  fun    tackPresenter(presenterImpl: UserListPresenterImpl): UserListContract.Presenter
}