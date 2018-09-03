package com.zlcdgroup.mrsei.di.component

import com.zlcdgroup.mrsei.MrmseiApp
import com.zlcdgroup.mrsei.di.module.ActivityModule
import com.zlcdgroup.mrsei.di.module.AppModule
import dagger.android.AndroidInjector
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


/**
 * @ Description:
 * @author king
 * @ Date 2018/9/3 18:18
 * @version V1.0
 */

@Singleton
@Component(modules = arrayOf(ActivityModule::class,AppModule::class)
 interface AppComponent : AndroidInjector(com.zlcdgroup.mrsei.MrmseiApp)(){
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: MrmseiApp): AppComponent.Builder

        //  AppComponent.Builder dataModel(DataModule  dataModule);
        fun build(): AppComponent
    }

}