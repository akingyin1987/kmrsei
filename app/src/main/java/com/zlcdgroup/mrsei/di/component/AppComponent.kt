package com.zlcdgroup.mrsei.di.component

import com.zlcdgroup.mrsei.MrmseiApp
import com.zlcdgroup.mrsei.di.module.ActivityModule
import com.zlcdgroup.mrsei.di.module.AppModule
import com.zlcdgroup.mrsei.di.module.DataModule
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
@Component(modules = arrayOf(AppModule::class
                      ,ActivityModule::class,
                  DataModule::class,
                ActivityModule::class,
                DataModule.DataProvidesModule::class,
                ActivityModule.SupportFragmentManagerModule::class,
                AndroidInjectionModule::class,
                AndroidSupportInjectionModule::class))
interface AppComponent :AndroidInjector<MrmseiApp>{


    @Component.Builder
    interface  Builder{

        @BindsInstance
       fun   application(app:MrmseiApp):AppComponent.Builder

       fun   build():AppComponent
    }
}