package com.zlcdgroup.mrsei.net

import com.akingyin.base.ext.yes
import com.akingyin.base.net.RetrofitUtils
import com.akingyin.base.net.okhttp.OkHttpUtils
import com.zlcdgroup.mrsei.data.source.remote.api.LoginServerApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

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

        loginServerApi = RetrofitUtils.createApi(LoginServerApi::class.java,OkHttpUtils.instance," https://easy-mock.com/mock/5cd90c3fc6690f660b93bd66/api/")
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