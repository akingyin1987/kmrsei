package com.zlcdgroup.mrsei.di.module

import android.app.Application
import android.content.Context
import com.zlcdgroup.mrsei.di.qualifier.ApplicationContext
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjectionModule



/**
 * @ Description:
 * @author king
 * @ Date 2018/9/3 17:58
 * @version V1.0
 */
@Module(includes = arrayOf(AndroidInjectionModule::class))
abstract class AppModule {

    @Binds
    @ApplicationContext
     abstract fun bindContext(application: Application): Context
}