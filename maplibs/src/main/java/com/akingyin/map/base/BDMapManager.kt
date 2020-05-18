package com.akingyin.map.base

import android.app.Activity
import android.os.Bundle
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng

/**
 * @ Description:
 * @author king
 * @ Date 2020/5/18 11:11
 * @version V1.0
 */
class BDMapManager (var baiduMap: BaiduMap,var mapView: MapView,var activity: Activity):IMapManager() {


    /**
     * 地图是否加载完成
     */
    private   var    mapLoadComplete = false


    private   val  bdLocationService  by  lazy{
        BDLocationService.getLocationServer(activity)
    }



    var act by Weak{
        activity
    }



    /**
     * 初始化地底配制
     */
    fun initMapConfig() {
        mapView.showZoomControls(false)
        //普通地图
        baiduMap.apply {
            //普通地图
            mapType = getShowMapType()
            //开始交通地图
            isTrafficEnabled = getShowMapTraffic()
            // 开启定位图层
            isMyLocationEnabled = true
        }

    }


    /**
     * 地图加载完成
     */
    fun    onMapLoad(callBack:()->Unit){
        baiduMap.setOnMapLoadedCallback {
            mapLoadComplete = true
            callBack.invoke()
        }
    }


    override fun setMapCenter(lat: Double, lng: Double) {
       if(lat>0 && lng>0){
           baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(LatLng(lat,lng),15F))
       }
    }


    fun   getMapMaxZoomLevel()=baiduMap.maxZoomLevel

    fun   getMapMinZoomLevel() = baiduMap.minZoomLevel

    fun   getCurrentZoomLevel() = baiduMap.mapStatus.zoom


    fun   setMapZoom(zoom : Float){
        baiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(zoom))
    }

    fun onCreate(savedInstanceState:Bundle?) {
       mapView.onCreate(act,savedInstanceState)
    }


    /**
     * 设置当前位置及定位坐标
     */
    fun  setMapLocationConfiguration(locationMode: MyLocationConfiguration.LocationMode,b:Boolean,bitmapDescriptor: BitmapDescriptor?){
        baiduMap.setMyLocationConfiguration(MyLocationConfiguration(locationMode,b,bitmapDescriptor))
    }


    /**
     * 是否是第一次定位
     */
    private  var   firstLocation = true

    /**
     * 定位监听器
     */
    private   var  bdAbstractLocationListener :BDAbstractLocationListener?=null


    /**
     * 注册监听定位
     */
    fun   registerLocationListener(onLocationChange:(BDLocation)->Unit,onFristLocation:(BDLocation)->Unit){
        bdAbstractLocationListener = if(null == bdAbstractLocationListener){
            object :BDAbstractLocationListener(){
                override fun onReceiveLocation(p0: BDLocation?) {
                    if(!mapLoadComplete){
                        return
                    }
                    p0?.let {
                        location->
                        if(location.locType == BDLocation.TypeGpsLocation ||
                                location.locType == BDLocation.TypeNetWorkLocation){
                            if(firstLocation){
                                firstLocation = false
                                onFristLocation(location)
                            }
                            onLocationChange(location)

                        }
                    }

                }
            }
        }else{
            bdAbstractLocationListener
        }
        bdAbstractLocationListener?.let {
            bdLocationService.registerListener(it)

        }

    }

    override fun onResume() {
       mapView.onResume()
       bdLocationService.start()
    }

    override fun onPause() {
       mapView.onPause()
        bdLocationService.stop()

    }

    override fun onDestroy() {
        mapView.onDestroy()
        bdLocationService.unregisterListener(bdAbstractLocationListener)
        bdLocationService.stop()
    }
}