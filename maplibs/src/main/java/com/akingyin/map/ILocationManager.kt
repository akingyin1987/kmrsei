package com.akingyin.map

/**
 * @ Description:
 * @author king
 * @ Date 2020/11/13 13:35
 * @version V1.0
 */
interface ILocationManager {

    /**
     * 开始定位
     */
    fun startLoction(call:(lat:Double,lng:Double,addr:String?)->Unit)


    /**
     * 通过经纬度获取地址
     * @param lat Double
     * @param lng Double
     * @param call Function1<[@kotlin.ParameterName] String?, Unit>
     */
    fun getAddrByLocation(lat: Double,lng: Double,call:(addr:String?)->Unit)

    /**
     * 停止定位
     */
    fun stopLoction()

    fun onResume()

    fun onPause()


    fun onDestroy()
}