package com.akingyin.base

import org.junit.Test

/**
 * @ Description:
 * @author king
 * @ Date 2019/8/1 12:03
 * @version V1.0
 */
class KotlinTest {

    /**
     * @param str1 参数1
     * @param str2 参数2
     */
    fun getResult(str1: String, str2: String): String {
        println("str1=$str1:$str2")
       return "result is {$str1 , $str2}"
    }

    /**
     * @param p1 参数1
     * @param p2 参数2
     * @param method 方法名称
     */
    fun lock(p1: String, p2: String, method: (str1: String, str2: String) -> String): String {
        return method(p1, p2)
    }


    @Test
    fun   testRestul(){
      var  str3 =  lock("1","2",KotlinTest()::getResult)
      var  str4 = lock("1","2"){
               str1,str2->
            println("str1=$str1:$str2")
            return@lock "$str1:$str2"
        }

        println("$str3:$str4")
    }

}