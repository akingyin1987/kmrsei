package com.zlcdgroup.mrsei.data

import android.content.Context
import com.akingyin.base.net.IRepositoryManager
import com.zlcdgroup.mrsei.MrmseiApp
import dagger.Lazy
import retrofit2.Retrofit
import java.lang.reflect.Method
import javax.inject.Inject
import javax.inject.Singleton




/**
 * @ Description:
 * @author king
 * @ Date 2019/5/6 11:05
 * @version V1.0
 */

@Singleton
class RepositoryManager :IRepositoryManager {

    @Inject
    lateinit var mrmseiApp:MrmseiApp

    @Inject
    lateinit var mRetrofit: Lazy<Retrofit>



    override fun <T : Any?> obtainCacheService(cache: Class<T>): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clearAllCache() {

    }

    override fun <T : Any?> obtainRetrofitService(service: Class<T>): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContext(): Context {

       return   mrmseiApp
    }


    private inline fun <reified T> getRetrofitMethod( method: Method): Method {

        val  serviceClass = T::class.java
        return serviceClass.getMethod(method.name, *method.parameterTypes)
    }
    fun <T>getRetrofitService(serviceClass :Class<T>):T{
        var retrofitService:T = mRetrofit.get().create(serviceClass)
        return  retrofitService
    }
}