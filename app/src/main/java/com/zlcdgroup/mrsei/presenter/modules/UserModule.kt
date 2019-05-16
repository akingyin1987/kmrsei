package com.zlcdgroup.mrsei.presenter.modules

import android.app.Activity
import com.zlcdgroup.mrsei.di.scope.PerActivity
import com.zlcdgroup.mrsei.presenter.UserListContract
import com.zlcdgroup.mrsei.presenter.impl.UserListPresenterImpl
import com.zlcdgroup.mrsei.ui.UserListActivity
import com.zlcdgroup.mrsei.ui.UserListDataBindActivity
import dagger.Binds
import dagger.Module
import javax.inject.Named

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
    @Named("binduser")
    abstract  fun   bindUserActivity(activity: UserListDataBindActivity):Activity

     @Binds
     @PerActivity
    abstract  fun    tackPresenter(presenterImpl: UserListPresenterImpl): UserListContract.Presenter
}