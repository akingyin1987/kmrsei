package com.zlcdgroup.mrsei.di.module

import android.content.Context
import androidx.annotation.NonNull
import com.akingyin.base.ext.app
import com.akingyin.base.net.retrofitConverter.FastJsonConverterFactory
import com.zlcdgroup.mrsei.MrmseiApp
import dagger.Module
import dagger.Provides
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/**
 * 提供一些三方库客户端实例
 * @ Description:
 * @author king
 * @ Date 2019/4/30 11:29
 * @version V1.0
 */

@Module
abstract class ClientModule {



    @Module(includes = arrayOf(ClientModule::class))
    class  ClientProvideModule{

        private val TIME_OUT = 10L
        /**
         * [Retrofit] 自定义配置接口
         */
        interface RetrofitConfiguration {
            fun configRetrofit(@NonNull context: Context, @NonNull builder: Retrofit.Builder)
        }

        /**
         * 提供Retrofit
         */
        @Singleton
        @Provides
        fun   getRetrofit(context: MrmseiApp, @NonNull configuration: RetrofitConfiguration, builder:Retrofit.Builder,
                          okHttpClient: OkHttpClient, httpUrl: HttpUrl):Retrofit{
            builder.baseUrl(httpUrl)
                    .client(okHttpClient)
            builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(FastJsonConverterFactory.create())
            configuration.configRetrofit(context,builder)
            return  builder.build()
        }



        @Singleton
        @Provides
        fun  provideOkhttp(  configuration: OkhttpConfiguration,
                           builder: OkHttpClient.Builder,  interceptors:MutableList<Interceptor>):OkHttpClient{
            builder.connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(TIME_OUT, TimeUnit.SECONDS)

            interceptors.let {
                for (interceptor in it){
                    builder.addInterceptor(interceptor)
                }
            }

                configuration.configOkhttp(app,builder)

             return  builder.build()

        }



        @Singleton
        @Provides
        fun   provideRetrofitBuilder() : Retrofit.Builder{
            return  Retrofit.Builder()
        }

        @Singleton
        @Provides
        fun  provideClientBuilder() :OkHttpClient.Builder{
            return  OkHttpClient.Builder()
        }


     /**
     * [OkHttpClient] 自定义配置接口
     */

      interface OkhttpConfiguration {
        fun configOkhttp(@NonNull context: Context, @NonNull builder: OkHttpClient.Builder)
      }
    }







}