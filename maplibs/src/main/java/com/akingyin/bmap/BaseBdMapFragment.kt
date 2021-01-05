package com.akingyin.bmap

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.akingyin.base.BaseNfcTagFragment
import com.akingyin.base.ext.click
import com.akingyin.map.R
import com.akingyin.map.base.MapLoadingDialog
import com.baidu.location.BDLocation
import com.baidu.mapapi.map.*

/**
 * 百度地图基础类
 * @property bdMapManager BDMapManager
 */
abstract class BaseBdMapFragment :BaseNfcTagFragment() {
    lateinit var   bdMapManager:BDMapManager

    lateinit var   rootView : View


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        rootView = view
        val  mapView = view.findViewById<MapView>(R.id.map_content)
        bdMapManager = BDMapManager(mapView.map,mapView,requireContext(),autoLocation())
        bdMapManager.onCreate(savedInstanceState)
        bdMapManager.initMapConfig()

        bdMapManager.onMapLoad {
            onMapLoadComplete()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    /** 地图模式（正常，跟随，罗盘）  */
    lateinit var location_icon: ImageView
    lateinit var location_switcher: ViewSwitcher
    var location_progress: ProgressBar? = null

    private lateinit var  zoom_in: ImageButton
    private lateinit var zoom_out: ImageButton

    /** 交通  */
    private lateinit var road_condition: ImageButton

    /** 地图类型（普通2d,普通3d,卫星）  */
    private lateinit var map_layers: ImageButton

    /** 全景  */
    private var map_street: ImageButton? = null


    private var vs_seeall: ViewSwitcher? = null
    var iv_seeall: ImageView? = null

    /** 显示当前位置  */
    private lateinit var vs_showloc: ViewSwitcher
    private lateinit var iv_showloc: ImageView

    /** 地图类型  */
    private lateinit var maplayer: View
    private var mLoadingDialog: MapLoadingDialog? = null



    override fun initView() {
        location_icon = rootView.findViewById(R.id.location_icon)
        zoom_out = rootView.findViewById(R.id.zoom_out)
        zoom_in = rootView.findViewById(R.id.zoom_in)
        road_condition = rootView.findViewById(R.id.road_condition)
        vs_showloc = rootView.findViewById(R.id.vs_showloc)
        iv_showloc = rootView.findViewById(R.id.iv_showloc)
        iv_seeall = rootView.findViewById(R.id.iv_seeall)
        vs_seeall = rootView.findViewById(R.id.vs_seeall)
        iv_seeall?.click {
            onSeeAllMarkers()
        }
        location_icon = rootView.findViewById(R.id.location_icon)
        location_progress = rootView.findViewById(R.id.location_progress)
        location_switcher = rootView.findViewById(R.id.location)

        map_layers = rootView.findViewById(R.id.map_layers)
        map_street = rootView.findViewById(R.id.map_street)
        iv_showloc.tag = "0"
        iv_showloc.click {
            if(it.tag.toString() == "1"){
                showMyLocationViewInfo()
            }else{
                hidenMyLocationViewInfo()
            }
        }

        location_icon.click {
            when( bdMapManager.baiduMap.locationConfiguration?.locationMode){
                MyLocationConfiguration.LocationMode.NORMAL->{
                    location_icon.setImageResource(R.drawable.main_icon_follow)
                    bdMapManager.setMapLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING,true,getLocationBitmap())
                }

                MyLocationConfiguration.LocationMode.COMPASS->{
                    location_icon.setImageResource(R.drawable.main_icon_location)
                    bdMapManager.setMapLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,true,getLocationBitmap())
                }

                MyLocationConfiguration.LocationMode.FOLLOWING->{
                    location_icon.setImageResource(R.drawable.main_icon_compass)
                    bdMapManager.setMapLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS,true,getLocationBitmap())
                }
            }
        }

        road_condition.click {
            if (bdMapManager.baiduMap.isTrafficEnabled) {
                road_condition.setImageResource(R.drawable.main_icon_roadcondition_off)
                bdMapManager.baiduMap.isTrafficEnabled = false
                bdMapManager.saveMapTraffic(false)
            } else {
                road_condition.setImageResource(R.drawable.main_icon_roadcondition_on)
                bdMapManager.baiduMap.isTrafficEnabled = true
                bdMapManager.saveMapTraffic(true)
            }
        }
        maplayer = LayoutInflater.from(requireContext()).inflate(R.layout.map_layer, null)
        layer_selector = maplayer.findViewById(R.id.layer_selector)
        layer_satellite = maplayer.findViewById(R.id.layer_satellite)
        layer_2d = maplayer.findViewById(R.id.layer_2d)
        layer_3d = maplayer.findViewById(R.id.layer_3d)

        map_layers.click {
            showMapLayerDialog(it, 10, -5)
        }
        layer_selector?.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.layer_satellite->{

                    //卫星
                    bdMapManager.baiduMap.mapType = BaiduMap.MAP_TYPE_SATELLITE
                }
                R.id.layer_2d->{

                    bdMapManager.baiduMap.mapType = BaiduMap.MAP_TYPE_NORMAL
                    bdMapManager.baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(MapStatus.Builder().overlook(0F).build()))
                }

                R.id.layer_3d->{
                    bdMapManager.baiduMap.mapType = BaiduMap.MAP_TYPE_NORMAL
                    bdMapManager.baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(MapStatus.Builder().overlook(-45F).build()))
                }
            }
            bdMapManager.saveMapType(bdMapManager.baiduMap.mapType)
        }
        when(bdMapManager.getShowMapType()){
            1->layer_2d?.isChecked = true
            2->layer_satellite?.isChecked = true
            3->layer_3d?.isChecked = true
        }

        zoom_in.click {
            val  currentZoom = bdMapManager.getCurrentZoomLevel()
            if(currentZoom == bdMapManager.getMapMaxZoomLevel()){
                it.isEnabled = false
                showError("已到支持最大级别")
                return@click
            }
            zoom_out.isEnabled = true
            bdMapManager.setMapZoom(currentZoom+0.5F)
        }
        zoom_out.click {
            val  currentZoom = bdMapManager.getCurrentZoomLevel()
            if(currentZoom <= bdMapManager.getMapMinZoomLevel()){
                it.isEnabled = false
                showError("已到支持最小级别")
                return@click
            }
            zoom_in.isEnabled = true
            bdMapManager.setMapZoom(currentZoom-0.5F)
        }

        map_street?.click {
            goToMapStreet()
        }
        bdMapManager.registerLocationListener({

            changeMyLocation(it)
        },{
            onFristMyLocation(it)
        })
    }

    private var mPopupWindow: PopupWindow? = null
    private var layer_selector: RadioGroup? = null
    private var layer_satellite: RadioButton? = null
    private  var layer_2d: RadioButton? = null
    private  var layer_3d: RadioButton? = null

    open fun showMapLayerDialog(v: View, xoff: Int, yoff: Int) {
        if (mPopupWindow == null) {
            mPopupWindow = PopupWindow(maplayer, ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, true)
            mPopupWindow?.setBackgroundDrawable(BitmapDrawable(resources, null as Bitmap?))
        }
        mPopupWindow?.let {
            dialog->
            if(dialog.isShowing){
                dialog.animationStyle = R.anim.layer_pop_out
                dialog.dismiss()
            }else{
                dialog.animationStyle = R.anim.layer_pop_in
                dialog.showAsDropDown(v, xoff, yoff)
            }
        }
    }


    private   fun  goToMapStreet(){
        bdMapManager.baiduMap.locationData?.let {
            locationData->
            startActivity(Intent(requireContext(), PanoramaBaiduMapActivity::class.java).apply {
                putExtra("lat",locationData.latitude)
                putExtra("lng",locationData.longitude)
            })
        }?:showError("当前没有位置信息无法查看")


    }

    protected open fun hidenMyLocationViewInfo() {
        iv_showloc.setImageResource(R.drawable.ic_visibility_off_black_24dp)
        iv_showloc.tag = "1"
        bdMapManager.baiduMap.isMyLocationEnabled = false
    }
    protected open fun showMyLocationViewInfo() {
        iv_showloc.setImageResource(R.drawable.ic_visibility_black_24dp)
        iv_showloc.tag = "0"
        bdMapManager.baiduMap.isMyLocationEnabled = true
    }
    /**
     * 获取定位图标  空则使用默认
     */
    protected abstract fun getLocationBitmap(): BitmapDescriptor?

    /**
     * 是否自动定位
     */
    protected abstract  fun   autoLocation():Boolean

    /**
     * 地图加载完成
     */
    open  fun  onMapLoadComplete(){

    }

    open  fun    onSeeAllMarkers(){

    }

    /**
     * 收到新的定位信息
     */
    open  fun  changeMyLocation(bdLocation: BDLocation){

        bdMapManager.baiduMap.setMyLocationData(MyLocationData.Builder().apply {
            accuracy(bdLocation.radius)
            direction(bdLocation.direction)
            latitude(bdLocation.latitude)
            longitude(bdLocation.longitude)
        }.build())
    }

    /**
     * 获取到第一次定位信息
     */
    open   fun   onFristMyLocation(bdLocation: BDLocation){
        bdMapManager.setMapCenter(bdLocation.latitude,bdLocation.longitude,bdMapManager.getMapMaxZoomLevel()-1)
    }

    fun   initMapZoomUiEnable(){
        val   zoom  =  bdMapManager.getCurrentZoomLevel()

        if(zoom < bdMapManager.getMapMaxZoomLevel() && zoom > bdMapManager.getMapMinZoomLevel()){
            zoom_in.isEnabled = true
            zoom_out.isEnabled = true
        }else if(zoom >= bdMapManager.getMapMaxZoomLevel()){
            zoom_out.isEnabled = true
            zoom_in.isEnabled = false
        }else if(zoom <= bdMapManager.getMapMinZoomLevel()){
            zoom_out.isEnabled = false
            zoom_in.isEnabled = true
        }
    }

    override fun onResume() {
        super.onResume()
        bdMapManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        bdMapManager.onPause()
    }

    override fun onDestroy() {
        bdMapManager.onDestroy()
        super.onDestroy()
    }

}