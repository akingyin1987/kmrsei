package com.akingyin.bmap

import android.content.Context
import com.akingyin.map.ILocationManager
import com.baidu.location.BDAbstractLocationListener
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.geocode.*

/**
 * @ Description:
 * @author king
 * @ Date 2020/11/13 13:40
 * @version V1.0
 */
class BmapLocationManager  constructor(var context: Context) : ILocationManager {

    private  val bdLocationService by lazy {
        BDLocationService.getLocationServer(context)
    }

    private  var  geoCoder: GeoCoder = GeoCoder.newInstance()

    private  var  listener: BDAbstractLocationListener?= null

    override fun startLoction(call: (lat: Double, lng: Double, addr: String?) -> Unit) {
        bdLocationService.unregisterListener(listener)
        listener = BDLocationService.MyLocationListenner{
            call.invoke(it.latitude,it.longitude,it.addrStr)
        }
        listener?.let {
            bdLocationService.registerListener(it)
        }

        bdLocationService.start()
    }

    private var onGetGeoCoderResultListener: OnGetGeoCoderResultListener? = null
    override fun getAddrByLocation(lat: Double, lng: Double, call: (addr: String?) -> Unit) {
        if(null == onGetGeoCoderResultListener){
            onGetGeoCoderResultListener = object :OnGetGeoCoderResultListener{
                override fun onGetGeoCodeResult(p0: GeoCodeResult?) {

                }

                override fun onGetReverseGeoCodeResult(result: ReverseGeoCodeResult?) {
                   result?.let {
                       call.invoke(it.address)
                   }
                }
            }
        }
        geoCoder.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener)
        geoCoder.reverseGeoCode(ReverseGeoCodeOption().location(LatLng(lat,lng)))
    }

    override fun stopLoction() {
       bdLocationService.unregisterListener(listener)
       bdLocationService.stop()
    }

    override fun onResume() {

    }

    override fun onPause() {

    }

    override fun onDestroy() {
        stopLoction()
        geoCoder.destroy()
    }
}