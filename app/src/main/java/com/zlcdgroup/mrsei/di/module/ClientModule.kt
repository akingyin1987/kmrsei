package com.zlcdgroup.mrsei.di.module
import com.akingyin.base.net.retrofitConverter.FastJsonConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
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
@InstallIn(SingletonComponent::class)
 class ClientModule {


    /**
     * 提供Retrofit
     */
    @Singleton
    @Provides
    fun   getRetrofit(builder:Retrofit.Builder,
                      okHttpClient: OkHttpClient, httpUrl: HttpUrl):Retrofit{
        builder.baseUrl(httpUrl)
                .client(okHttpClient)
        builder.addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(FastJsonConverterFactory.create())

        return  builder.build()
    }



    @Singleton
    @Provides
    fun  provideOkhttp(builder: OkHttpClient.Builder,  interceptors:MutableList<Interceptor>):OkHttpClient{
        builder.connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)

        interceptors.let {
            for (interceptor in it){
                builder.addInterceptor(interceptor)
            }
        }

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

    @Singleton
    @Provides
    fun provideHttpUrl():HttpUrl{
        return "http://test.zlcdgroup.cn/api/".toHttpUrl()
    }

    @Singleton
    @Provides
    fun provideHttpinterceptors():MutableList<Interceptor>{
        return mutableListOf()
    }

}