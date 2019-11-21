package com.zlcdgroup.mrsei.utils

import android.os.Build
import com.akingyin.base.ext.app
import com.akingyin.base.ext.currentTimeMillis
import com.akingyin.base.ext.spGetString
import com.akingyin.base.net.retrofitConverter.FastJsonConverterFactory
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.zlcdgroup.mrsei.Constants
import com.zlcdgroup.mrsei.data.source.remote.api.LoginServerApi
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @ Description:
 * @author king
 * @ Date 2019/5/27 17:57
 * @version V1.0
 */
class RetrofitConfig private constructor(){

    private lateinit var mRetrofit: Retrofit
    private lateinit var mCoroutineRetrofit:Retrofit
    private lateinit var mHttpLoggingInterceptor: HttpLoggingInterceptor
    private lateinit var mRequestInterceptor: Interceptor

    private lateinit var mOkHttpClient: OkHttpClient

    private lateinit var mCache: Cache
    private lateinit var baseUrl :String

    companion object {

        private const val READ_TIME_OUT = 5000L// 读取超过时间 单位:毫秒
        private const val WRITE_TIME_OUT = 5000L//写超过时间 单位:毫秒
        private const val FILE_CACHE_SIZE = 1024 * 1024 * 100L//缓存大小100Mb
        private val Instance: RetrofitConfig by lazy { RetrofitConfig() }

        /**
         * 获取默认请求接口
         */
        @JvmStatic
        fun getDefaultService(): LoginServerApi {
            return Instance.apiService
        }

        @JvmStatic
        fun  getDefaultCoroutineServer():LoginServerApi{
            return Instance.apiCoroutines
        }



        fun onResetRetrofit(){
            Instance.initRetrofit()
        }

    }


    private lateinit var apiService: LoginServerApi

    private lateinit var apiCoroutines: LoginServerApi

    init {
        initRequestInterceptor()

        initLoggingInterceptor()
        initCachePathAndSize()
        initOkHttpClient()
        initRetrofit()
    }

    /**
     * 初始化请求拦截，添加缓存头,与全局请求参数
     */
    private fun initRequestInterceptor() {


        mRequestInterceptor = Interceptor { chain ->
            //注意全局请求参数都是死的
            var  request  = chain.request()
            request.headers.names().forEach{
                println("header->>  key=$it  value=${request.headers.get(it)}")
            }
            when(request.method){
                "GET"->{
                   val  parameterNames = request.url.queryParameterNames
                    parameterNames.forEach {
                        println("key=$it value=${request.url.queryParameter(it)}")
                    }

                }
                "POST"->{

                }
            }
            val urlBuilder = chain.request().url
                    .newBuilder()
                    .setEncodedQueryParameter("udid", "d0f6190461864a3a978bdbcb3fe9b48709f1f390")
                    .setEncodedQueryParameter("vc", "225")
                    .setEncodedQueryParameter("vn", "3.12.0")
                    .setEncodedQueryParameter("deviceModel", "Redmi%204")
                    .setEncodedQueryParameter("first_channel", "eyepetizer_xiaomi_market")
                    .setEncodedQueryParameter("last_channel", "eyepetizer_xiaomi_market")
                    .setEncodedQueryParameter("system_version_code", "23")

             request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Cookie", "ky_auth=;sdk=23")
                    .addHeader("model", Build.MODEL)
                     .addHeader("imei",Constants.IMEI)
                     .addHeader("OS","Android")
                     .addHeader("sdk",Build.VERSION.SDK_INT.toString())
                     .addHeader("time",currentTimeMillis.toString())
                    .url(urlBuilder.build())
                    .build()

            chain.proceed(request.newBuilder().build())

        }
    }

    /**
     * 初始化日志拦截
     */
    private fun initLoggingInterceptor() {
        mHttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    /**
     * 配置缓存大小与缓存地址
     */
    private fun initCachePathAndSize() {
        val cacheFile = File(app.cacheDir, "cache")
        mCache = Cache(cacheFile, FILE_CACHE_SIZE)
    }


    /**
     * 配置okHttp
     */
    private fun initOkHttpClient() {
        mOkHttpClient = OkHttpClient.Builder()
                .readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(WRITE_TIME_OUT, TimeUnit.MILLISECONDS)
                .cache(mCache)
                .addInterceptor(mRequestInterceptor)
                .addInterceptor(mHttpLoggingInterceptor)
                .build()


    }

    /**
     * 配置retrofit
     */
    private fun initRetrofit() {
        baseUrl = spGetString("ApiUrl")
        mRetrofit = Retrofit.Builder()
                .client(mOkHttpClient)
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build()

        mCoroutineRetrofit = Retrofit.Builder()
                .client(mOkHttpClient)
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl(baseUrl)
                .build()
        apiService = mRetrofit.create(LoginServerApi::class.java)
        apiCoroutines = mCoroutineRetrofit.create(LoginServerApi::class.java)
    }

}