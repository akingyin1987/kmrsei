package com.zlcdgroup.mrsei.presenter.modules

import android.app.Activity
import com.zlcdgroup.mrsei.di.scope.PerActivity
import com.zlcdgroup.mrsei.ui.CoroutinesDemo
import dagger.Binds
import dagger.Module

/**
 * @ Description:
 * @author king
 * @ Date 2019/8/1 16:03
 * @version V1.0
 */
@Module
abstract class CoroutinedDemoModule {

    @PerActivity
    @Binds
    abstract  fun   activity(activty: CoroutinesDemo): Activity
}