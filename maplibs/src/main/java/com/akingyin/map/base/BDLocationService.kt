package com.akingyin.map.base

import android.content.Context
import android.webkit.WebView
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption


/**
 * 定位服务
 * @ Description:
 * @author king
 * @ Date 2020/5/18 11:28
 * @version V1.0
 */
class BDLocationService  private constructor(){
    private var client: LocationClient? = null
    private var mOption: LocationClientOption? = null
    private var DIYoption: LocationClientOption? = null

    private var objLock: Any? = null
    companion object{


        private val Instance: BDLocationService by lazy { BDLocationService() }

        /**
         * 获取默认请求接口
         */
        @JvmStatic
        fun getLocationServer(context: Context): BDLocationService {
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
                    client = LocationClient(locationContext).apply {
                        locOption = getDefaultLocationClientOption()
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
    fun registerListener(listener: BDAbstractLocationListener): Boolean {
        return client?.let {
            it.registerLocationListener(listener)
            true
        } ?: false

    }

    fun unregisterListener(listener: BDAbstractLocationListener?) {
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
    fun setLocationOption(option: LocationClientOption): Boolean {
        val isSuccess = false
        client?.let {
            if (it.isStarted) {
                it.stop()
            }
            DIYoption = option
            client!!.locOption = option
        }

        return isSuccess
    }

    /**
     * 开发者应用如果有H5页面使用了百度JS接口，该接口可以辅助百度JS更好的进行定位
     *
     * @param webView 传入webView控件
     */
    fun enableAssistanLocation(webView: WebView?) {
        client?.enableAssistantLocation(webView)

    }

    /**
     * 停止H5辅助定位
     */
    fun disableAssistantLocation() {
        client?.disableAssistantLocation()

    }

    /***
     *
     * @return DefaultLocationClientOption  默认O设置
     */
    fun getDefaultLocationClientOption(): LocationClientOption {

        if (mOption == null) {
            mOption = LocationClientOption().apply {
                locationMode = LocationClientOption.LocationMode.Hight_Accuracy // 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
                setCoorType("bd09ll") // 可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
                setScanSpan(3000) // 可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
                setIsNeedAddress(true) // 可选，设置是否需要地址信息，默认不需要
                setIsNeedLocationDescribe(true) // 可选，设置是否需要地址描述
                setNeedDeviceDirect(false) // 可选，设置是否需要设备方向结果
                isLocationNotify = false // 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
                setIgnoreKillProcess(true) // 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop
                setIsNeedLocationDescribe(true) // 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
                setIsNeedLocationPoiList(true) // 可选，默认false，设置是否需要POI结果，可以在BDLocation
                SetIgnoreCacheException(false) // 可选，默认false，设置是否收集CRASH信息，默认收集
                isOpenGps = true // 可选，默认false，设置是否开启Gps定位
                setIsNeedAltitude(false) // 可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用

            }


        }
        return mOption!!
    }


    /**
     * @return DIYOption 自定义Option设置
     */
    fun getOption(): LocationClientOption {
        if (DIYoption == null) {
            DIYoption = LocationClientOption()
        }
        return DIYoption!!
    }

    fun start() {
        synchronized(objLock!!) {
            if (client != null && !client!!.isStarted) {
                client!!.start()
            }
        }
    }

    fun requestLocation() {
        client?.requestLocation()

    }

    fun stop() {
        synchronized(objLock!!) {
           client?.let {
               if(it.isStarted){
                   it.stop()
               }
           }

        }
    }

    fun isStart(): Boolean {
        return client?.isStarted?:false
    }

    fun requestHotSpotState(): Boolean {
        return client?.requestHotSpotState()?:false
    }
}