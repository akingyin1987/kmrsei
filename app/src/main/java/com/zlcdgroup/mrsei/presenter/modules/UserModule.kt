package com.zlcdgroup.mrsei.presenter.modules

import android.app.Activity
import com.zlcdgroup.mrsei.di.scope.PerActivity
import com.zlcdgroup.mrsei.presenter.UserListContract
import com.zlcdgroup.mrsei.presenter.impl.UserListPresenterImpl
import com.zlcdgroup.mrsei.ui.UserListActivity
import dagger.Binds
import dagger.Module

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/6 16:28
 * @version V1.0
 */
 @Module
 abstract class UserModule {

    @Binds
    @PerActivity
    abstract  fun   activity(activity: UserListActivity):Activity

     @Binds
     @PerActivity
    abstract  fun    tackPresenter(presenterImpl: UserListPresenterImpl): UserListContract.Presenter
}