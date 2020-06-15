/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.amap

import android.content.Context
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener


/**
 * @ Description:
 * @author king
 * @ Date 2020/6/15 16:38
 * @version V1.0
 */
class ALocationService private constructor(){

    private var client: AMapLocationClient? = null
    private var mOption: AMapLocationClientOption? = null
    private var DIYoption: AMapLocationClientOption? = null

    private var objLock: Any? = null
    companion object{


        private val Instance: ALocationService by lazy { ALocationService() }

        /**
         * 获取默认请求接口
         */
        @JvmStatic
        fun getLocationServer(context: Context): ALocationService {
            Instance.client = if(null == Instance.client){
                Instance.LocationService(context.applicationContext)
                Instance.client
            }else{
                Instance.client
            }

            return Instance
        }

    }



    /***
     * 初始化 LocationClient
     *
     * @param locationContext
     */

    fun LocationService(locationContext: Context) {
        objLock = Any()
        objLock?.let {
            synchronized(it){
                if (client == null) {
                    client = AMapLocationClient(locationContext).apply {
                        setLocationOption( getDefaultLocationClientOption())
                    }
                }
            }
        }

    }

    /***
     * 注册定位监听
     *
     * @param listener
     * @return
     */
    fun registerListener(listener: AMapLocationListener): Boolean {
        return client?.let {
            it.setLocationListener(listener)
            true
        } ?: false

    }

    fun unregisterListener(listener: AMapLocationListener?) {
        client?.unRegisterLocationListener(listener)

    }

    /**
     * @return 获取sdk版本
     */
    fun getSDKVersion(): String? {
        return client?.version ?: ""

    }

    /***
     * 设置定位参数
     *
     * @param option
     * @return isSuccessSetOption
     */
    fun setLocationOption(option: AMapLocationClientOption): Boolean {
        val isSuccess = false
        client?.let {
            if (it.isStarted) {
                it.stopLocation()
            }
            DIYoption = option
            client!!.setLocationOption( option)
        }

        return isSuccess
    }





    /***
     *
     * @return DefaultLocationClientOption  默认O设置
     */
    fun getDefaultLocationClientOption(): AMapLocationClientOption {
      mOption =   mOption?:AMapLocationClientOption().apply {
            locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy // 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            interval = 3000 // 可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        }

       return  mOption!!
    }


    /**
     * @return DIYOption 自定义Option设置
     */
    fun getOption(): AMapLocationClientOption {
        if (DIYoption == null) {
            DIYoption = AMapLocationClientOption()
        }
        return DIYoption!!
    }

    fun start() {
        objLock?.let {
            synchronized(it){
                client?.let {
                    aMapLocationClient ->
                    if(!aMapLocationClient.isStarted){
                        aMapLocationClient.startLocation()
                    }
                }
            }
        }

    }


    fun stop() {
        synchronized(objLock!!) {
            client?.let {
                if(it.isStarted){
                    it.stopLocation()
                }
            }

        }
    }

    fun isStart(): Boolean {
        return client?.isStarted?:false
    }


}