package com.zlcdgroup.mrsei

import android.content.Context
import android.widget.Toast
import com.akingyin.base.BaseApp
import com.akingyin.base.ext.Ext
import com.akingyin.base.net.mode.ApiHost
import com.umeng.commonsdk.UMConfigure
import com.zlcdgroup.mrsei.di.component.DaggerAppComponent
import com.zlcdgroup.mrsei.di.module.ClientModule
import com.zlcdgroup.mrsei.di.module.GlobalConfigModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit


/**
 * @ Description:
 * @author king
 * @ Date 2018/9/3 17:27
 * @version V1.0
 */
class MrmseiApp :BaseApp() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val  configmodel : GlobalConfigModule.GlobalProvideModule =GlobalConfigModule.Builder().okhttpConfiguration(object :ClientModule.ClientProvideModule.OkhttpConfiguration{
            override fun configOkhttp(context: Context, builder: OkHttpClient.Builder) {
                println("configOkhttp")
            }
        }).retrofitConfiguration(object :ClientModule.ClientProvideModule.RetrofitConfiguration{
            override fun configRetrofit(context: Context, builder: Retrofit.Builder) {
              builder.baseUrl(ApiHost.getHost())
              println("configRetrofit")
            }
        }).addInterceptor(HttpLoggingInterceptor())
                .build()
       return  DaggerAppComponent.builder().application(this).globalConfigModule(configmodel).build()
    }

    override fun onCreate() {
        super.onCreate()

        Ext.with(this)
        showDebugDBAddressLogToast(this)
        ApiHost.setHost("http://114.215.108.130:38280/mrmsei/")
        UMConfigure.setLogEnabled(true)
        UMConfigure.init(this,UMConfigure.DEVICE_TYPE_PHONE,"5cd152274ca357112b000a24")

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
     // applicationInjector()

    }
}