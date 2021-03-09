package com.zlcdgroup.mrsei.net

import com.akingyin.base.ext.yes

import com.zlcdgroup.mrsei.data.source.remote.api.LoginServerApi
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog
import retrofit2.Retrofit

/**
 * 测试网络接口
 * @ Description:
 * @author king
 * @ Date 2019/5/14 14:30
 * @version V1.0
 */

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE,sdk= intArrayOf(23),shadows = arrayOf(ShadowLog::class))
class ApiTest {


    lateinit var  loginServerApi: LoginServerApi


    @Before
    fun   initTest(){
        ShadowLog.stream = System.out

        loginServerApi = Retrofit.Builder().baseUrl("https://easy-mock.com/mock/5cd90c3fc6690f660b93bd66/api/").client(OkHttpClient.Builder().build()).build().create(LoginServerApi::class.java)
    }

    @Test
    fun   loginApi(){
         loginServerApi.login("test","adb")
                 .subscribe({

                     println(it.toString())
                 }, {
                     it.printStackTrace()
                 })
    }


    @Test
    fun    testBoolean(){

        val  random = java.util.Random().nextInt(10)

        val  boolean = true
        boolean.yes {
            println("yes------------------")
        }
        println("random=$random:$boolean")
    }
}