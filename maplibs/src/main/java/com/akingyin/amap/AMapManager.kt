/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.amap

import android.app.Activity
import android.location.Location
import android.os.Bundle
import com.akingyin.map.IMapManager
import com.akingyin.map.IMarker
import com.akingyin.map.base.Weak
import com.alibaba.fastjson.JSON
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.UiSettings
import com.amap.api.maps.model.*
import com.amap.overlayutil.OverlayManager

import kotlin.math.abs


/**
 * 高德地图管理器
 * @ Description:
 * @author king
 * @ Date 2020/6/15 16:34
 * @version V1.0
 */

internal typealias Func<T> = (T?) -> Unit
class AMapManager(var aMap: AMap, var mapView: MapView, var activity: Activity, var autoLoc: Boolean = true) : IMapManager() {

    companion object{
        const val  AMAP_MARKER_UUID="amap_marker_uuid"
    }

    /**
     * 地图是否加载完成
     */
    private var mapLoadComplete = false


    private val aLocationService by lazy {
        ALocationService.getLocationServer(activity)
    }




    var act by Weak {
        activity
    }


    /**
     * 初始化地底配制
     */
    fun initMapConfig() {
        aMap.uiSettings.apply {
            isZoomControlsEnabled = true
        }
        //普通地图
        aMap.apply {
            //普通地图
            mapType = getShowMapType()
            //开始交通地图
            isTrafficEnabled = getShowMapTraffic()
            myLocationStyle = MyLocationStyle().showMyLocation(true)
                    .interval(3000).myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER)
            // 开启定位图层
            isMyLocationEnabled = true

        }

    }

    /**
     * 初始化POI
     */
    fun initPoiConfig() {

    }


    /**
     * 逆地理编码
     */
    fun initGeoCoderConfig() {

    }


    /**
     * 地图加载完成
     */
    fun onMapLoad(callBack: () -> Unit) {
        aMap.setOnMapLoadedListener {
            mapLoadComplete = true
            callBack.invoke()
        }
    }


    /**
     * 设置地图中心点
     */
    override fun setMapCenter(lat: Double, lng: Double, zoom: Float) {
        if (lat > 0 && lng > 0) {
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat,lng),zoom))

        }
    }

    fun getUiSetting(): UiSettings {
        return aMap.uiSettings
    }


    var lastMapStatus: CameraPosition? = null

    /**
     * 地图状态监听
     */
    fun setMapStatusChange( onChange: Func<CameraPosition>? = null,
                           onChangeFinish: Func<CameraPosition>? = null, onChangeLocation: Func<CameraPosition>? = null) {
        aMap.setOnCameraChangeListener(object : AMap.OnCameraChangeListener{
            override fun onCameraChangeFinish(p0: CameraPosition?) {
                 onChangeFinish?.invoke(p0)
            }

            override fun onCameraChange(p0: CameraPosition?) {
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
        })

    }

    fun getMapMaxZoomLevel() = aMap.maxZoomLevel

    fun getMapMinZoomLevel() = aMap.minZoomLevel

    fun getCurrentZoomLevel() = aMap.cameraPosition.zoom


    fun setMapZoom(zoomValue: Float) {
        aMap.moveCamera(CameraUpdateFactory.zoomBy(zoomValue))

    }

    fun onCreate(savedInstanceState: Bundle?) {
        mapView.onCreate(savedInstanceState)
    }


    /**
     * 设置当前位置及定位坐标
     */
    fun setMapLocationConfiguration(myLocationStyle: MyLocationStyle) {
       aMap.myLocationStyle = myLocationStyle
    }



    /**
     * 获取当前位置信息
     */
    fun getMyLocationData(): Location? {
        return aMap.myLocation
    }

    /**
     * 是否是第一次定位
     */
    private var firstLocation = true

    /**
     * 定位监听器
     */
    private var aMapLocationListener: AMapLocationListener? = null

    private var locationListener:AMap.OnMyLocationChangeListener?=null


    /**
     * 设置地图默认的定位
     */
    fun   registerAmapDefaultLocationListener(onLocationChange: (Location) -> Unit, onFristLocation: (Location) -> Unit){
      locationListener =  locationListener?: AMap.OnMyLocationChangeListener{

            if(!mapLoadComplete){
                return@OnMyLocationChangeListener
            }
            it?.let{
                location ->
                if (firstLocation) {
                    firstLocation = false
                    onFristLocation(location)
                }
                onLocationChange(location)
            }
        }
        aMap.setOnMyLocationChangeListener(locationListener)
    }

    /**
     * 注册监听定位
     */
    fun registerLocationListener(onLocationChange: (AMapLocation) -> Unit, onFristLocation: (AMapLocation) -> Unit) {

        aMapLocationListener = if (null == aMapLocationListener) {
            AMapLocationListener {
                p0->
                if (!mapLoadComplete) {
                    return@AMapLocationListener
                }
                p0?.let { location ->
                    if (location.errorCode == AMapLocation.LOCATION_SUCCESS ) {

                        if (firstLocation) {
                            firstLocation = false
                            onFristLocation(location)
                        }
                        onLocationChange(location)

                    }
                }

            }
        } else {
            aMapLocationListener
        }
        aMapLocationListener?.let {
            aLocationService.registerListener(it)
        }
    }

    fun addSingleMarker(markerOptions: MarkerOptions): Marker = aMap.addMarker(markerOptions)



    fun  addPolylineMarker(latlngs : List<LatLng>, pathIndex:List<Int>, customList:List<BitmapDescriptor>): Polyline {

        val polylineOptionBg = PolylineOptions()
        // 折线线宽， 默认为 5， 单位：像素
        // 折线线宽， 默认为 5， 单位：像素
        polylineOptionBg.width(20F)
        // 折线是否虚线
        // 折线是否虚线

        polylineOptionBg.isDottedLine = true
        //  polylineOptionBg.color(0xAAFF0000); // 折线颜色
        // 折线坐标点列表:[2,10000]，且不能包含null
        //  polylineOptionBg.color(0xAAFF0000); // 折线颜色
        // 折线坐标点列表:[2,10000]，且不能包含null
        polylineOptionBg.points=latlngs
        // 纹理宽、高是否保持原比例渲染
        // 纹理宽、高是否保持原比例渲染
        polylineOptionBg.customTextureIndex = pathIndex
        polylineOptionBg.customTextureList = customList
        return  addPolylineMarker(polylineOptionBg)

    }

    private fun addPolylineMarker(polygonOptions: PolylineOptions): Polyline = aMap.addPolyline(polygonOptions)

    override fun startLoction() {
        aLocationService.start()
    }

    override fun stopLoction() {
        aLocationService.stop()
    }


    override fun requestLocation() {

    }

    override fun onResume() {
        mapView.onResume()



    }

    override fun onPause() {
        mapView.onPause()



    }

    override fun onDestroy() {
        mapView.onDestroy()

        aLocationService.unregisterListener(aMapLocationListener)
        aLocationService.stop()
    }

    fun recycleMapBitmap(vararg bitmapDescriptor: BitmapDescriptor?) {
        bitmapDescriptor.forEach {
            it?.recycle()
        }
    }

    private var overlayManager: ACustomOverlayManager? = null


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
        aMap.setOnMarkerClickListener {
            callBack.invoke(it)
            true
        }
        overlayManager = ACustomOverlayManager(aMap, callBack)
        aMap.setOnMarkerClickListener(overlayManager)
    }

    /**
     * 将数据添加到地图界面上
     */
    fun   addDataToMap(overlayOptions: List<MarkerOptions>, zoomToSpan: Boolean = false){
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
    fun  <T: IMarker>  onCreateMarkerOptions(data:T): MarkerOptions {
        return    MarkerOptions()
                .position(LatLng(data.getLat(),data.getLng())).snippet(JSON.parseObject(JSON.toJSONString(data)).apply {
                    put(AMAP_MARKER_UUID,data.uuid)
                }.toJSONString())
    }

    fun<T : IMarker>  findMarkerByData(data:T): Marker?{
        println("findMarkerByData=${overlayManager?.getSize()}")
        return overlayManager?.getMarker(data.uuid)
    }

    /**
     * 通过marker 查询数据
     */
    fun <T : IMarker> findMarkerDataAndIndexByMarker(marker: Marker, data: List<T>):Pair<Int,T>?{
        val  uuid = JSON.parseObject(marker.snippet).getString(AMAP_MARKER_UUID)
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

    class ACustomOverlayManager constructor(aMap: AMap, var callBack: (Marker) -> Unit) : OverlayManager(aMap) {
        private val overlays: MutableList<MarkerOptions> = mutableListOf()

        fun addOverlayOptionsAll(overlayOptions: List<MarkerOptions>) {
            overlays.addAll(overlayOptions)
        }

        fun addOverlayOptions(overlayOptions: MarkerOptions) {
            overlays.add(overlayOptions)
        }

        fun removeOverlay(option: MarkerOptions) {
            overlays.remove(option)
        }

        fun   getSize():Int{
            return  overlays.size
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

        override fun getOverlayOptions(): MutableList<MarkerOptions> {
            return overlays
        }

        override fun onPolylineClick(p0: Polyline?) {

        }
    }
}