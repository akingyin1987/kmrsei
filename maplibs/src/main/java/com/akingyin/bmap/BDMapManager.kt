package com.akingyin.bmap

import android.app.Activity
import android.os.Bundle
import com.akingyin.base.ext.messageFormat
import com.akingyin.base.ext.toJson
import com.akingyin.base.net.exception.ApiException
import com.akingyin.base.net.okhttp.OkHttpUtils
import com.akingyin.base.utils.ConvertUtils
import com.akingyin.base.utils.StringUtils
import com.akingyin.map.IMapManager
import com.akingyin.map.IMarker
import com.akingyin.map.base.Weak
import com.alibaba.fastjson.JSON
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.overlayutil.OverlayManager
import com.baidu.mapapi.search.core.PoiInfo
import com.baidu.mapapi.search.core.SearchResult
import com.baidu.mapapi.search.geocode.*
import com.baidu.mapapi.search.poi.*
import okhttp3.Request
import okhttp3.Response
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.math.abs


/**
 * @ Description:
 * @author king
 * @ Date 2020/5/18 11:11
 * @version V1.0
 */

internal typealias Func<T> = (T?) -> Unit

class BDMapManager(var baiduMap: BaiduMap, var mapView: MapView, var activity: Activity, var autoLoc: Boolean = true) : IMapManager() {



    /**
     * 地图是否加载完成
     */
    private var mapLoadComplete = false


    private val bdLocationService by lazy {
        BDLocationService.getLocationServer(activity)
    }

    var poiSearch: PoiSearch? = null

    var geoCoder: GeoCoder? = null


    var act by Weak {
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
    fun initPoiConfig() {
        poiSearch = PoiSearch.newInstance()
    }


    /**
     * 逆地理编码
     */
    fun initGeoCoderConfig() {
        geoCoder = GeoCoder.newInstance()
    }


    /**
     * 地图加载完成
     */
    fun onMapLoad(callBack: () -> Unit) {
        baiduMap.setOnMapLoadedCallback {
            mapLoadComplete = true
            callBack.invoke()
        }
    }


    /**
     * 设置地图中心点
     */
    override fun setMapCenter(lat: Double, lng: Double, zoom: Float) {
        if (lat > 0 && lng > 0) {
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(LatLng(lat, lng), zoom))
        }
    }

    fun getUiSetting(): UiSettings {
        return baiduMap.uiSettings
    }


    var lastMapStatus: MapStatus? = null

    /**
     * 地图状态监听
     */
    fun setMapStatusChange(onChangeStart: Func<MapStatus>? = null, onChange: Func<MapStatus>? = null,
                           onChangeFinish: Func<MapStatus>? = null, onChangeLocation: Func<MapStatus>? = null) {
        baiduMap.setOnMapStatusChangeListener(object : BaiduMap.OnMapStatusChangeListener {
            override fun onMapStatusChangeStart(p0: MapStatus?) {
                onChangeStart?.invoke(p0)
            }

            override fun onMapStatusChangeStart(p0: MapStatus?, p1: Int) {
                onChangeStart?.invoke(p0)
            }

            override fun onMapStatusChange(p0: MapStatus?) {
                onChange?.invoke(p0)
                if (null != p0 && null != lastMapStatus) {
                    lastMapStatus?.let {
                        if (abs(p0.target.latitude - it.target.latitude) > 0.000001) {
                            onChangeLocation?.invoke(p0)
                            return
                        }
                        if (abs(p0.target.longitude - it.target.longitude) > 0.000001) {
                            onChangeLocation?.invoke(p0)
                        }
                    }

                }
                lastMapStatus = p0
            }

            override fun onMapStatusChangeFinish(p0: MapStatus?) {
                onChangeFinish?.invoke(p0)
            }
        })
    }

    fun getMapMaxZoomLevel() = baiduMap.maxZoomLevel

    fun getMapMinZoomLevel() = baiduMap.minZoomLevel

    fun getCurrentZoomLevel() = baiduMap.mapStatus.zoom


    fun setMapZoom(zoom: Float) {
        baiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(zoom))
    }

    fun onCreate(savedInstanceState: Bundle?) {
        mapView.onCreate(act, savedInstanceState)
    }


    /**
     * 设置当前位置及定位坐标
     */
    fun setMapLocationConfiguration(locationMode: MyLocationConfiguration.LocationMode, b: Boolean, bitmapDescriptor: BitmapDescriptor?) {
        baiduMap.setMyLocationConfiguration(MyLocationConfiguration(locationMode, b, bitmapDescriptor))
    }

    /**
     * 设置当前定位信息
     */
    fun setMyLocationData(bdLocation: BDLocation) {
        baiduMap.setMyLocationData(MyLocationData.Builder().apply {
            accuracy(bdLocation.radius)
            direction(bdLocation.direction)
            latitude(bdLocation.latitude)
            longitude(bdLocation.longitude)
        }.build())
    }

    /**
     * 获取当前位置信息
     */
    fun getMyLocationData(): MyLocationData? {
        return baiduMap.locationData
    }

    /**
     * 是否是第一次定位
     */
    private var firstLocation = true

    /**
     * 定位监听器
     */
    private var bdAbstractLocationListener: BDAbstractLocationListener? = null


    /**
     * 注册监听定位
     */
    fun registerLocationListener(onLocationChange: (BDLocation) -> Unit, onFristLocation: (BDLocation) -> Unit) {
        bdAbstractLocationListener = if (null == bdAbstractLocationListener) {
            object : BDAbstractLocationListener() {
                override fun onReceiveLocation(p0: BDLocation?) {

                    if (!mapLoadComplete) {
                        return
                    }
                    p0?.let { location ->
                        if (location.locType == BDLocation.TypeGpsLocation ||
                                location.locType == BDLocation.TypeNetWorkLocation) {
                            if (firstLocation) {
                                firstLocation = false
                                onFristLocation(location)
                            }
                            onLocationChange(location)

                        }
                    }

                }
            }
        } else {
            bdAbstractLocationListener
        }
        bdAbstractLocationListener?.let {
            bdLocationService.registerListener(it)

        }

    }


    private var onGetGeoCoderResultListener: OnGetGeoCoderResultListener? = null

    fun searchRoundPoiByGeoCoder(lat: Double, lng: Double, r: Int = 1000, callBack: (data: List<PoiInfo>?, e: Exception?) -> Unit) {
        try {
            if (null == onGetGeoCoderResultListener) {
                onGetGeoCoderResultListener = object : OnGetGeoCoderResultListener {
                    override fun onGetGeoCodeResult(p0: GeoCodeResult?) {
                    }

                    override fun onGetReverseGeoCodeResult(p0: ReverseGeoCodeResult?) {
                        p0?.let { reverseGeoCodeResult ->
                            if (reverseGeoCodeResult.error == SearchResult.ERRORNO.NO_ERROR) {
                                callBack(reverseGeoCodeResult.poiList, null)
                            } else {
                                callBack(null, ApiException("未查询到数据"))
                            }
                        } ?: callBack(null, ApiException("未查询到数据"))
                    }
                }
            }
            geoCoder?.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener)

            geoCoder?.reverseGeoCode(ReverseGeoCodeOption().location(LatLng(lat, lng)).radius(r).newVersion(1))
        } catch (e: Exception) {
            e.printStackTrace()
            callBack.invoke(null, e)
        }

    }

    /**
     * 搜索 某点附近的POI
     */
    fun searchRoundPoi(lat: Double, lng: Double, r: Int = 1000, keyword: String, callBack: (data: List<PoiInfo>?, e: Exception?) -> Unit) {
        try {
            poiSearch?.setOnGetPoiSearchResultListener(object : OnGetPoiSearchResultListener {
                override fun onGetPoiIndoorResult(p0: PoiIndoorResult?) {
                }

                override fun onGetPoiResult(p0: PoiResult?) {
                    p0?.let { poiResult ->
                        callBack.invoke(poiResult.allPoi, null)
                    }
                }

                override fun onGetPoiDetailResult(p0: PoiDetailResult?) {
                }

                override fun onGetPoiDetailResult(p0: PoiDetailSearchResult?) {
                }
            })
            poiSearch?.searchNearby(PoiNearbySearchOption().apply {
                radius(r)
                location(LatLng(lat, lng))
                pageNum(0)
                keyword(keyword)
                pageCapacity(10)
            })
        } catch (e: Exception) {
            callBack(null, e)
        }


    }


    fun addSingleMarker(markerOptions: MarkerOptions): Marker = baiduMap.addOverlay(markerOptions) as Marker



    fun  addPolylineMarker(latlngs : List<LatLng>,pathIndex:List<Int>,customList:List<BitmapDescriptor>):Polyline{

        val polylineOptionBg = PolylineOptions()
        // 折线线宽， 默认为 5， 单位：像素
        // 折线线宽， 默认为 5， 单位：像素
        polylineOptionBg.width(20)
        // 折线是否虚线
        // 折线是否虚线
        polylineOptionBg.dottedLine(true)
        //  polylineOptionBg.color(0xAAFF0000); // 折线颜色
        // 折线坐标点列表:[2,10000]，且不能包含null
        //  polylineOptionBg.color(0xAAFF0000); // 折线颜色
        // 折线坐标点列表:[2,10000]，且不能包含null
        polylineOptionBg.points(latlngs)
        // 纹理宽、高是否保持原比例渲染
        // 纹理宽、高是否保持原比例渲染
        polylineOptionBg.keepScale(true)
        polylineOptionBg.textureIndex(pathIndex)
        polylineOptionBg.customTextureList(customList)
        return  addPolylineMarker(polylineOptionBg)

    }

    private fun addPolylineMarker(polygonOptions: PolylineOptions):Polyline = baiduMap.addOverlay(polygonOptions) as Polyline

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

        if (autoLoc) {
            bdLocationService.start()
        }

    }

    override fun onPause() {
        mapView.onPause()
        if (autoLoc) {
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

    fun recycleMapBitmap(vararg bitmapDescriptor: BitmapDescriptor?) {
        bitmapDescriptor.forEach {
            it?.recycle()
        }
    }

    private var overlayManager: BdCustomOverlayManager? = null


    /**
     * 清除当前管理器的marker数据
     */
    fun   cleanOverlayManagerMarkers(){
        overlayManager?.removeAll()
        overlayManager?.removeFromMap()
    }

    /**
     * 初始化地图marker 相关类
     */
    fun initMapMarkerConfig(callBack: (Marker) -> Unit) {
        baiduMap.setOnMarkerClickListener {
            callBack.invoke(it)
            true
        }
        overlayManager = BdCustomOverlayManager(baiduMap, callBack)
        baiduMap.setOnMarkerClickListener(overlayManager)
    }

    /**
     * 将数据添加到地图界面上
     */
    fun   addDataToMap(overlayOptions: List<OverlayOptions>,zoomToSpan: Boolean = false){
        overlayManager?.let {
            it.removeAll()
            it.addOverlayOptionsAll(overlayOptions)
            it.addToMap()
            if(zoomToSpan){
                it.zoomToSpan()
            }

        }
    }

    fun    zoomToSpan(){
        overlayManager?.zoomToSpan()
    }

    /**
     * 创建marker 点
     */
    fun  <T:IMarker>  onCreateMarkerOptions(data:T):MarkerOptions{
        return    MarkerOptions()
                .position(LatLng(data.getLat(),data.getLng()))
                .extraInfo(Bundle().apply {
                    putString(BAIDU_MARKER_UUID,data.uuid)
                    putString(BAIDU_MARKER_DATA,JSON.toJSONString(data.data))
                })
    }

    fun<T : IMarker>  findMarkerByData(data:T):Marker?{
      return overlayManager?.getMarker(data.uuid)
    }

    /**
     * 通过marker 查询数据
     */
    fun <T : IMarker> findMarkerDataAndIndexByMarker(marker: Marker, data: List<T>):Pair<Int,T>?{
       val  uuid = marker.extraInfo?.getString(BAIDU_MARKER_UUID)?:""
       if(uuid.isEmpty()){
           return null
       }
       data.forEachIndexed { index, t ->
           if(t.uuid == uuid) {
               return  index to t
           }
       }
       return null
    }

    class BdCustomOverlayManager constructor(baiduMap: BaiduMap, var callBack: (Marker) -> Unit) : OverlayManager(baiduMap) {
        private val overlays: MutableList<OverlayOptions> = mutableListOf()

        fun addOverlayOptionsAll(overlayOptions: List<OverlayOptions>) {
            overlays.addAll(overlayOptions)
        }

        fun addOverlayOptions(overlayOptions: OverlayOptions) {
            overlays.add(overlayOptions)
        }

        fun removeOverlay(option: OverlayOptions) {
            overlays.remove(option)
        }

        fun removeAll() {
            overlays.clear()
        }

        override fun onMarkerClick(p0: Marker?): Boolean {
            p0?.let {
                callBack.invoke(it)
            }
            return true
        }

        override fun getOverlayOptions(): MutableList<OverlayOptions> {
            return overlays
        }

        override fun onPolylineClick(p0: Polyline?) = false
    }
    companion object{
        const val  BAIDU_MARKER_UUID="baidu_marker_uuid"
        const val  BAIDU_MARKER_DATA="baidu_marker_data"
        const val BAIDU_STATIC_BASE_URL="http://api.map.baidu.com/staticimage/v2"
        const val BAIDU_AK="0ppzKkGXRpDcG9zt6ASrhtbKm2PGG9b6"
        const val BAIDU_SK="nrS5YfWW6n9PA540uhNG9dTLFU645qRa"


        @JvmStatic
        fun  getBdMapGeocoderAddr(lat: Double,lng: Double,localType: String):String{
            val  sn  = LinkedHashMap<String,String>().apply {

                put("output","json")
                put("location","$lat,$lng")
                put("coordtype",localType)
                put("ak", BAIDU_AK)
                put("sk", BAIDU_SK)
            }.run {
                ConvertUtils.MD5(toQueryString(this))
            }?:""
            val  url ="http://api.map.baidu.com/geocoder?output=json&location=$lat,$lng&coordtype=$localType&key=$BAIDU_AK&sn=$sn"
            val  request = Request.Builder().url(url).build()
             val  response = OkHttpUtils.getInstance().newCall(request).execute()
            if(response.isSuccessful){
               val result  = response.body?.string()?:"{}"
               return JSON.parseObject(result).let {
                   if(it.getString("status") == "OK"){
                      it.getJSONObject("result").getString("formatted_address")
                   }else{
                       ""
                   }
               }
            }
            return ""
        }

        @JvmStatic
        fun  getBdMapStaticImageUrl(lat: Double,lng: Double,localType:String):String{
            val  sn  = LinkedHashMap<String,String>().apply {
                put("ak", BAIDU_AK)
                put("width","300")
                put("height","150")
                put("markers","$lat,$lng")
                put("zoom","18")
                put("dpiType","ph")
                put("coordtype",localType)
                put("markerStyles","l,A,0xff0000")
                put("mcode","android")
                put("sk", BAIDU_SK)
            }.run {
                ConvertUtils.MD5(toQueryString(this))
            }?:""

          return  BAIDU_STATIC_BASE_URL+"?ak=$BAIDU_AK&width=300&height=150&markers=$lat,$lng&zoom=18&dpiType=ph&" +
                  "coordtype=$localType&markerStyles=l,A,0xff0000&mcode=android&sn=$sn"
        }

        // 对Map内所有value作utf8编码，拼接返回结果
        @Throws(UnsupportedEncodingException::class)
        fun toQueryString(data: Map<*, *>): String {
            val queryString = StringBuffer()
            for ((key, value) in data) {
                queryString.append(key.toString() + "=")
                queryString.append(URLEncoder.encode(value as String , "UTF-8").toString() + "&")
            }
            if (queryString.isNotEmpty()) {
                queryString.deleteCharAt(queryString.length - 1)
            }
            return queryString.toString()
        }

        // 来自stackoverflow的MD5计算方法，调用了MessageDigest库函数，并把byte数组结果转换成16进制
        fun MD5(md5: String): String? {
            try {
                val md = MessageDigest.getInstance("MD5")
                val array = md.digest(md5.toByteArray())
                val sb = StringBuffer()
                for (i in array.indices) {

                    sb.append(Integer.toHexString(((array[i].toInt() and 0xFF) or 0x100))
                            .substring(1, 3))
                }
                return sb.toString()
            } catch (e: NoSuchAlgorithmException) {
            }
            return null
        }
    }

}