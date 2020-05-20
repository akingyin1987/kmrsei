package com.akingyin.bmap

import android.app.Activity
import android.os.Bundle
import com.akingyin.base.net.exception.ApiException
import com.akingyin.map.IMapManager
import com.akingyin.map.base.Weak
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.core.PoiInfo
import com.baidu.mapapi.search.core.SearchResult
import com.baidu.mapapi.search.geocode.*
import com.baidu.mapapi.search.poi.*

/**
 * @ Description:
 * @author king
 * @ Date 2020/5/18 11:11
 * @version V1.0
 */

internal typealias Func<T> = (T?) -> Unit


class BDMapManager (var baiduMap: BaiduMap,var mapView: MapView,var activity: Activity,var autoLoc:Boolean = true): IMapManager() {


    /**
     * 地图是否加载完成
     */
    private   var    mapLoadComplete = false


    private   val  bdLocationService  by  lazy{
        BDLocationService.getLocationServer(activity)
    }

   var   poiSearch: PoiSearch?=null

   var   geoCoder :GeoCoder? = null





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
     * 初始化POI
     */
    fun   initPoiConfig(){
        poiSearch= PoiSearch.newInstance()
    }


    /**
     * 逆地理编码
     */
    fun   initGeoCoderConfig(){
         geoCoder = GeoCoder.newInstance()
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


    override fun setMapCenter(lat: Double, lng: Double,zoom: Float) {
       if(lat>0 && lng>0){
           baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(LatLng(lat,lng),zoom))
       }
    }

    fun    getUiSetting():UiSettings{
        return  baiduMap.uiSettings
    }

    fun   setMapStatusChange(onChangeStart:Func<MapStatus>?=null,onChange: Func<MapStatus>?=null,onChangeFinish:Func<MapStatus>?=null){
        baiduMap.setOnMapStatusChangeListener(object :BaiduMap.OnMapStatusChangeListener{
            override fun onMapStatusChangeStart(p0: MapStatus?) {
                   onChangeStart?.invoke(p0)
            }

            override fun onMapStatusChangeStart(p0: MapStatus?, p1: Int) {
                    onChangeStart?.invoke(p0)
            }

            override fun onMapStatusChange(p0: MapStatus?) {
                onChange?.invoke(p0)
            }

            override fun onMapStatusChangeFinish(p0: MapStatus?) {
                onChangeFinish?.invoke(p0)
            }
        })
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
     * 设置当前定位信息
     */
    fun  setMyLocationData(bdLocation: BDLocation){
        baiduMap.setMyLocationData(MyLocationData.Builder().apply {
            accuracy(bdLocation.radius)
            direction(bdLocation.direction)
            latitude(bdLocation.latitude)
            longitude(bdLocation.longitude)
        }.build())
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
                    println("onReceiveLocation->${p0?.locType}$mapLoadComplete")
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








    private var   onGetGeoCoderResultListener:OnGetGeoCoderResultListener ?= null

    fun  searchRoundPoiByGeoCoder(lat: Double, lng: Double, r: Int = 1000,callBack: (data: List<PoiInfo>?, e: Exception?) -> Unit){
        try {
            if(null == onGetGeoCoderResultListener){
                onGetGeoCoderResultListener = object :OnGetGeoCoderResultListener{
                    override fun onGetGeoCodeResult(p0: GeoCodeResult?) {
                    }

                    override fun onGetReverseGeoCodeResult(p0: ReverseGeoCodeResult?) {
                        p0?.let {
                            reverseGeoCodeResult ->
                            if(reverseGeoCodeResult.error == SearchResult.ERRORNO.NO_ERROR){
                                callBack(reverseGeoCodeResult.poiList,null)
                            }else{
                                callBack(null,ApiException("未查询到数据"))
                            }
                        }?:callBack(null,ApiException("未查询到数据"))
                    }
                }
            }
            geoCoder?.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener)

            geoCoder?.reverseGeoCode(ReverseGeoCodeOption().location(LatLng(lat,lng)).radius(r).newVersion(1))
        }catch (e:Exception){
            e.printStackTrace()
            callBack.invoke(null,e)
        }

    }

    /**
     * 搜索 某点附近的POI
     */
    fun searchRoundPoi(lat: Double, lng: Double, r: Int = 1000,keyword:String,callBack: (data:List<PoiInfo>?, e:Exception?) -> Unit) {
      try {
          poiSearch?.setOnGetPoiSearchResultListener(object :OnGetPoiSearchResultListener{
              override fun onGetPoiIndoorResult(p0: PoiIndoorResult?) {
              }

              override fun onGetPoiResult(p0: PoiResult?) {
                  p0?.let {
                      poiResult ->
                      callBack.invoke(poiResult.allPoi,null)
                  }
              }

              override fun onGetPoiDetailResult(p0: PoiDetailResult?) {
              }

              override fun onGetPoiDetailResult(p0: PoiDetailSearchResult?) {
              }
          })
          poiSearch?.searchNearby(PoiNearbySearchOption().apply {
              radius(r)
              location(LatLng(lat,lng))
              pageNum(0)
              keyword(keyword)
              pageCapacity(10)
          })
      }catch ( e: Exception){
          callBack(null,e)
      }


    }


    fun    addSingleMarker(markerOptions: MarkerOptions):Marker=baiduMap.addOverlay(markerOptions) as Marker

    override fun startLoction() {
        bdLocationService.start()
    }

    override fun stopLoction() {
       bdLocationService.stop()
    }




    override fun requestLocation() {
        bdLocationService.requestLocation()
    }

    override fun onResume() {
       mapView.onResume()

       if(autoLoc){
           bdLocationService.start()
       }

    }

    override fun onPause() {
       mapView.onPause()
        if(autoLoc){
            bdLocationService.stop()
        }


    }

    override fun onDestroy() {
        mapView.onDestroy()
        poiSearch?.destroy()
        geoCoder?.destroy()
        bdLocationService.unregisterListener(bdAbstractLocationListener)
        bdLocationService.stop()
    }
}