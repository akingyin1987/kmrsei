package com.zlcdgroup.mrsei.presenter

import android.app.Activity
import com.zlcdgroup.mrsei.di.scope.PerActivity
import com.zlcdgroup.mrsei.di.scope.PerFragment
import com.zlcdgroup.mrsei.ui.SteperActivity
import com.zlcdgroup.mrsei.ui.fragment.UserListFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/8 10:53
 * @version V1.0
 */

@Module
abstract class StepModule {

    @Binds
    @PerActivity
    abstract fun  activity(activity: SteperActivity):Activity


    @ContributesAndroidInjector
    @PerFragment
    abstract fun  fragment(): UserListFragment


    @Binds
    @PerActivity
    abstract  fun  tackPresenter(presenter: UserListFragmentPresenterImpl):UserListFragmentContract.Presenter
}