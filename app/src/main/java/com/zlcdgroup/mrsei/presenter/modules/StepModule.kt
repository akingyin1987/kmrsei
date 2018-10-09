package com.zlcdgroup.mrsei.presenter.modules

import android.app.Activity
import android.support.v4.app.FragmentManager
import com.zlcdgroup.mrsei.di.scope.PerActivity
import com.zlcdgroup.mrsei.di.scope.PerFragment
import com.zlcdgroup.mrsei.presenter.UserListFragmentContract
import com.zlcdgroup.mrsei.presenter.impl.UserListFragmentPresenterImpl
import com.zlcdgroup.mrsei.ui.SteperActivity
import com.zlcdgroup.mrsei.ui.fragment.UserListFragment
import dagger.Binds
import dagger.Module
import dagger.Provides
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



    @Module(includes = arrayOf(StepModule::class))
    class StepModuleFragmentManagerModule {

        @Provides
        @PerActivity
        fun provideFragmentManager(activity: SteperActivity):FragmentManager{
            return activity.supportFragmentManager
        }
    }





    @Binds
    @PerActivity
    abstract  fun  tackPresenter(presenter: UserListFragmentPresenterImpl): UserListFragmentContract.Presenter
}