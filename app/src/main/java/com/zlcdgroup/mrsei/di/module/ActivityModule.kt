package com.zlcdgroup.mrsei.di.module

import android.app.Activity
import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import com.zlcdgroup.mrsei.di.qualifier.ActivityContext
import com.zlcdgroup.mrsei.di.scope.PerActivity
import dagger.Binds
import dagger.Module
import dagger.Provides





/**
 * @ Description:
 * @author king
 * @ Date 2018/9/3 18:15
 * @version V1.0
 */

@Module
abstract class ActivityModule {
    @Binds
    @ActivityContext
     abstract fun bindActivityContext(activity: Activity): Context


    @Provides
    @PerActivity
    fun activityFragmentManager(activity: AppCompatActivity): FragmentManager {
        return activity.supportFragmentManager
    }
}