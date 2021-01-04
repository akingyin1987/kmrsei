package com.akingyin.base


import android.app.Application

import timber.log.Timber

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/3 12:17
 * @version V1.0
 */
  abstract class BaseApp : Application() {


    override fun onCreate() {
        initInjection()
        super.onCreate()
        println("onCreate->app")
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
           // initLeakCanary()
        }


    }





    abstract   fun   initInjection()


}