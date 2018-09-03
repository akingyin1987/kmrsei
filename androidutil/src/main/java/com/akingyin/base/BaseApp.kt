package com.akingyin.base

import android.app.Activity
import android.app.Application
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/3 12:17
 * @version V1.0
 */
  abstract class BaseApp : Application(), HasActivityInjector {

    @Inject
    lateinit    var   dispatchingAndroidInjector :DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            initLeakCanary()

        }
    }

    private fun   initLeakCanary() {
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this)
        }

    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return   dispatchingAndroidInjector
    }

    abstract   fun   initInjection()


}