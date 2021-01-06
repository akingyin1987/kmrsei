package com.zlcdgroup.mrsei.presenter.modules
import com.zlcdgroup.mrsei.presenter.UserListFragmentContract
import com.zlcdgroup.mrsei.presenter.impl.UserListFragmentPresenterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/8 10:53
 * @version V1.0
 */

@Module
@InstallIn(ActivityComponent::class)
abstract class StepModule{

    @Binds
    abstract  fun  tackPresenter(presenter: UserListFragmentPresenterImpl): UserListFragmentContract.Presenter
}