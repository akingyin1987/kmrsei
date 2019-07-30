package com.zlcdgroup.mrsei

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Test

/**
 * @ Description:
 * @author king
 * @ Date 2019/7/3 11:42
 * @version V1.0
 */
class CoroutinesTest {
    /**
     * 挂起函数
     */
    suspend fun  requestToken():String{

        println("requestToken")
        return "token"
    }

    suspend fun createPost(token:String,item:String):String{
        println("token=$token  item=$item")
        return "post"
    }

    @Test
    fun postItem(item:String){
        /**
         * 创建一个新协程
         */
        GlobalScope.launch {
            val token = requestToken()
            val post = createPost(token, item)

        }
    }
}

