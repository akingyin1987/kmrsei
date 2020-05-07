package com.akingyin.base.ext

import android.os.SystemClock

/**
 * @ Description:
 * @author king
 * @ Date 2019/5/28 11:21
 * @version V1.0
 */
class AppTime  private constructor(){

     /** 服务器时间 */
     private   var   serverTime : Long = 0L

     /** 手机运行时间 */
     private   var   elapsedRealtime : Long = 0L

     /** 登录超期时间 */
     private   var   extendedTime:Long = 0L

    companion object{
         private val Instance: AppTime by lazy { AppTime() }


        /**
         * 获取当前时间
         */
        fun   getAppTime():Long{
             if(Instance.serverTime == 0L){
                 Instance.serverTime = spGetLong("serverTime",0L)
             }
             if(Instance.elapsedRealtime == 0L){
                 Instance.serverTime = spGetLong("elapsedRealtime",0L)
             }
             if(Instance.serverTime == 0L || Instance.elapsedRealtime == 0L){
                 return  System.currentTimeMillis()
             }


             return  Instance.serverTime + (SystemClock.elapsedRealtime() - Instance.elapsedRealtime)
         }

         fun   saveServerTime(server:Long){
             Instance.serverTime = server
             Instance.elapsedRealtime = SystemClock.elapsedRealtime()
             spSetLong("serverTime",server)
             spSetLong("elapsedRealtime", Instance.elapsedRealtime)
         }

         fun   saveExtendedTime(extendedTime:Long){
              Instance.extendedTime = extendedTime
              spSetLong("extendedTime",extendedTime)
         }

         fun  getExtendedTime():Long{
             if(Instance.extendedTime == 0L){
                 Instance.extendedTime = spGetLong("extendedTime")
             }
             return  Instance.extendedTime
         }

    }


}