package com.zlcdgroup.mrsei

import android.content.Context
import android.widget.Toast
import com.akingyin.base.BaseApp
import com.akingyin.base.net.mode.ApiHost
import com.zlcdgroup.mrsei.di.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication


/**
 * @ Description:
 * @author king
 * @ Date 2018/9/3 17:27
 * @version V1.0
 */
class MrmseiApp :BaseApp() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
       return  DaggerAppComponent.builder().application(this).build()
    }

    override fun onCreate() {
        super.onCreate()

        showDebugDBAddressLogToast(this)
        ApiHost.setHost("http://114.215.108.130:38280/mrmsei/")
    }

    fun showDebugDBAddressLogToast(context: Context) {
        if (BuildConfig.DEBUG) {
            println("showDebugDBAddressLogToast")
            try {
                val debugDB = Class.forName("com.amitshekhar.DebugDB")
                val getAddressLog = debugDB.getMethod("getAddressLog")
                val value = getAddressLog.invoke(null)
                Toast.makeText(context, value as String, Toast.LENGTH_LONG).show()
            } catch (ignore: Exception) {
                ignore.printStackTrace()
            }

        }
    }

    override fun initInjection() {

    }
}