package com.zlcdgroup.mrsei.di.component

import android.content.Context
import com.zlcdgroup.mrsei.MrmseiApp
import com.zlcdgroup.mrsei.di.module.*
import com.zlcdgroup.mrsei.di.qualifier.ApplicationContext
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


/**
 * @ Description:
 * @author king
 * @ Date 2018/9/4 10:36
 * @version V1.0
 */


@Singleton
@Component(modules = arrayOf(
        ViewModelModule::class,
        ViewModelModule.ViewModelProvideModule::class,
        ActivityModule::class,
        DataModule::class,
        ActivityModule::class,
        DataModule.DataProvidesModule::class,
        ClientModule::class,
        ClientModule.ClientProvideModule::class,
        GlobalConfigModule::class,
        GlobalConfigModule.GlobalProvideModule::class,
        AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class))
interface AppComponent : AndroidInjector<MrmseiApp> {


    @Component.Builder
    interface Builder {

        @BindsInstance
        fun applicationContext(@ApplicationContext context: Context): AppComponent.Builder

        @BindsInstance
        fun application(app: MrmseiApp): AppComponent.Builder

        fun globalConfigModule(globalConfigModule: GlobalConfigModule.GlobalProvideModule): AppComponent.Builder

        fun build(): AppComponent


    }


}