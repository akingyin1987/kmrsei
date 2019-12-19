package com.akingyin.base

import com.squareup.leakcanary.LeakCanary
import dagger.android.DaggerApplication
import timber.log.Timber

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/3 12:17
 * @version V1.0
 */
  abstract class BaseApp : DaggerApplication() {


    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
           // initLeakCanary()
        }
        initInjection()
    }

    private fun   initLeakCanary() {
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this)
        }

    }



    abstract   fun   initInjection()


}