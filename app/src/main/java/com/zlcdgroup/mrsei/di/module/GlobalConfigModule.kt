package com.zlcdgroup.mrsei.di.module

import android.text.TextUtils
import androidx.core.util.Preconditions
import com.akingyin.base.net.mode.BaseUrl
import dagger.Module
import dagger.Provides
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.internal.Util
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlin.properties.Delegates


/**
 *
 * 向框架中注入外部配置的自定义参数
 * @ Description:
 * @author king
 * @ Date 2019/4/30 15:14
 * @version V1.0
 */

@Module
abstract class  GlobalConfigModule  {

    class Builder  constructor() {
         var apiUrl: HttpUrl? = null
         var baseUrl: BaseUrl? = null

         var interceptors: MutableList<Interceptor> = mutableListOf()

         var cacheFile: File? = null
         var retrofitConfiguration: ClientModule.ClientProvideModule.RetrofitConfiguration by Delegates.notNull()
         var okhttpConfiguration: ClientModule.ClientProvideModule.OkhttpConfiguration  by Delegates.notNull()

         var executorService: ExecutorService by Delegates.notNull()

        fun baseurl(baseUrl: String): Builder {//基础url
            if (TextUtils.isEmpty(baseUrl)) {
                throw NullPointerException("BaseUrl can not be empty")
            }
            this.apiUrl = HttpUrl.parse(baseUrl)
            return this
        }

        fun baseurl(baseUrl: BaseUrl): Builder {
            this.baseUrl = Preconditions.checkNotNull(baseUrl,  "can not be null.")
            return this
        }


        //动态添加任意个interceptor
        fun addInterceptor(interceptor: Interceptor): Builder {


            this.interceptors.add(interceptor)
            return this
        }



        fun cacheFile(cacheFile: File): Builder {
            this.cacheFile = cacheFile
            return this
        }

        fun retrofitConfiguration(retrofitConfiguration: ClientModule.ClientProvideModule.RetrofitConfiguration): Builder {
            this.retrofitConfiguration = retrofitConfiguration
            return this
        }

        fun okhttpConfiguration(okhttpConfiguration: ClientModule.ClientProvideModule.OkhttpConfiguration): Builder {
            this.okhttpConfiguration = okhttpConfiguration
            return this
        }




        fun executorService(executorService: ExecutorService): Builder {
            this.executorService = executorService
            return this
        }

        fun build(): GlobalConfigModule.GlobalProvideModule {
            return GlobalConfigModule.GlobalProvideModule(this)
        }
    }

    @Module(includes = arrayOf(GlobalConfigModule::class))
    class GlobalProvideModule constructor(){

        constructor(builder: Builder) : this() {
            this.mApiUrl = builder.apiUrl
            this.mBaseUrl = builder.baseUrl
            this.mInterceptors = builder.interceptors
            this.mCacheFile = builder.cacheFile
            this.mRetrofitConfiguration = builder.retrofitConfiguration
            this.mOkhttpConfiguration = builder.okhttpConfiguration
            this.mExecutorService = builder.executorService
        }



        var mApiUrl: HttpUrl? = null
        var mBaseUrl: BaseUrl? = null

        var mInterceptors: MutableList<Interceptor> = mutableListOf()

        var mCacheFile: File? = null
        var mRetrofitConfiguration: ClientModule.ClientProvideModule.RetrofitConfiguration by Delegates.notNull()
        var mOkhttpConfiguration: ClientModule.ClientProvideModule.OkhttpConfiguration by Delegates.notNull()

        var mExecutorService: ExecutorService by Delegates.notNull()



        @Singleton
        @Provides
        fun  provideInterceptors():List<Interceptor>{
            return mInterceptors
        }


        @Singleton
        @Provides
        fun provideBaseUrl(): HttpUrl {
            var url = mBaseUrl?.url()
            if(null != url){
                return  url
            }
            if(null != mApiUrl){
                url = mApiUrl
            }else{
                url  = HttpUrl.parse("https://api.github.com/")
            }
            return url!!
        }


        @Singleton
        @Provides
        fun  provideRetrofitConfiguration(): ClientModule.ClientProvideModule.RetrofitConfiguration {
            return mRetrofitConfiguration
        }


        @Singleton
        @Provides
        fun  provideOkhttpConfiguration():ClientModule.ClientProvideModule.OkhttpConfiguration{
            return mOkhttpConfiguration
        }
        @Singleton
        @Provides
        fun provideExecutorService():ExecutorService{
            return if(null == mExecutorService) ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    SynchronousQueue<Runnable>(), Util.threadFactory("Arms Executor", false)) else mExecutorService
        }



    }

    fun builder(): Builder {
        return Builder()
    }


//    @Singleton
//    @Binds
//   abstract  fun   provideOkhttp(okHttpClient: OkHttpClient):OkHttpClient



}