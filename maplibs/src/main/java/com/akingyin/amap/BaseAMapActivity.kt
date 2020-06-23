/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.amap

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.akingyin.base.BaseNfcTagActivity
import com.akingyin.base.ext.click

import com.akingyin.map.R
import com.akingyin.map.base.BaiduPanoramaActivity
import com.akingyin.map.base.MapLoadingDialog
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.BitmapDescriptor
import com.amap.api.maps.model.MyLocationStyle


/**
 * 高德地图 基础类
 * @ Description:
 * @author king
 * @ Date 2020/6/16 16:42
 * @version V1.0
 */
abstract class BaseAMapActivity : BaseNfcTagActivity() {


    lateinit var   aMapManager: AMapManager

    override fun initializationData(savedInstanceState: Bundle?) {
        val  mapView = findViewById<MapView>(R.id.map_content)
        aMapManager = AMapManager(mapView.map,mapView,this,autoLocation())
        aMapManager.onCreate(savedInstanceState)
        aMapManager.initMapConfig()
        aMapManager.onMapLoad {
            onMapLoadComplete()
        }
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
        location_icon = findViewById(R.id.location_icon)
        zoom_out = findViewById(R.id.zoom_out)
        zoom_in = findViewById(R.id.zoom_in)
        road_condition = findViewById(R.id.road_condition)
        vs_showloc = findViewById(R.id.vs_showloc)
        iv_showloc = findViewById(R.id.iv_showloc)
        iv_seeall = findViewById(R.id.iv_seeall)
        vs_seeall = findViewById(R.id.vs_seeall)
        iv_seeall?.click {
            onSeeAllMarkers()
        }
        location_icon = findViewById(R.id.location_icon)
        location_progress = findViewById(R.id.location_progress)
        location_switcher = findViewById(R.id.location)

        map_layers = findViewById(R.id.map_layers)
        map_street = findViewById(R.id.map_street)
        iv_showloc.tag = "0"
        iv_showloc.click {
            if(it.tag.toString() == "1"){
                showMyLocationViewInfo()
            }else{
                hidenMyLocationViewInfo()
            }
        }

        location_icon.click {
            when( aMapManager.aMap.myLocationStyle.myLocationType){
                MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER->{
                    location_icon.setImageResource(R.drawable.main_icon_follow)
                    aMapManager.aMap.myLocationStyle = MyLocationStyle().interval(3000).showMyLocation(true)
                            .myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW)
                }

                MyLocationStyle.LOCATION_TYPE_FOLLOW->{
                    location_icon.setImageResource(R.drawable.main_icon_location)
                    aMapManager.aMap.myLocationStyle = MyLocationStyle().interval(3000).showMyLocation(true)
                            .myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)

                }

                MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER->{
                    location_icon.setImageResource(R.drawable.main_icon_compass)
                    aMapManager.aMap.myLocationStyle = MyLocationStyle().interval(3000).showMyLocation(true)
                            .myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER)

                }
            }
        }

        road_condition.click {
            if (aMapManager.aMap.isTrafficEnabled) {
                road_condition.setImageResource(R.drawable.main_icon_roadcondition_off)
                aMapManager.aMap.isTrafficEnabled = false
                aMapManager.saveMapTraffic(false)
            } else {
                road_condition.setImageResource(R.drawable.main_icon_roadcondition_on)
                aMapManager.aMap.isTrafficEnabled = true
                aMapManager.saveMapTraffic(true)
            }
        }
        maplayer = LayoutInflater.from(this).inflate(R.layout.map_layer, null)
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
                    aMapManager.aMap.mapType = AMap.MAP_TYPE_SATELLITE
                }
                R.id.layer_2d->{

                    aMapManager.aMap.mapType = AMap.MAP_TYPE_NORMAL
                    aMapManager.aMap.moveCamera(CameraUpdateFactory.changeBearing(0F))

                }

                R.id.layer_3d->{
                    aMapManager.aMap.mapType = AMap.MAP_TYPE_NORMAL
                    aMapManager.aMap.moveCamera(CameraUpdateFactory.changeBearing(-45F))

                }
            }
            aMapManager.saveMapType(aMapManager.aMap.mapType)
        }
        when(aMapManager.getShowMapType()){
            1->layer_2d?.isChecked = true
            2->layer_satellite?.isChecked = true
            3->layer_3d?.isChecked = true
        }

        zoom_in.click {
            val  currentZoom = aMapManager.getCurrentZoomLevel()
            println("zoom=${currentZoom}")
            if(currentZoom >= aMapManager.getMapMaxZoomLevel()){
                it.isEnabled = false
                showError("已到支持最大级别")
                return@click
            }
            zoom_out.isEnabled = true
            aMapManager.setMapZoom(currentZoom+0.5F)
        }
        zoom_out.click {
            val  currentZoom = aMapManager.aMap.cameraPosition.zoom
            if(currentZoom <= aMapManager.getMapMinZoomLevel()){
                it.isEnabled = false
                showError("已到支持最小级别")
                return@click
            }
            zoom_in.isEnabled = true
            aMapManager.setMapZoom(currentZoom-0.5F)
        }

        map_street?.click {
            goToMapStreet()
        }
        aMapManager.registerAmapDefaultLocationListener({

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
        aMapManager.getMyLocationData()?.let {
            locationData->
            startActivity(Intent(this, BaiduPanoramaActivity::class.java).apply {
                putExtra("lat",locationData.latitude)
                putExtra("lng",locationData.longitude)
            })
        }


    }

    protected open fun hidenMyLocationViewInfo() {
        iv_showloc.setImageResource(R.drawable.ic_visibility_off_black_24dp)
        iv_showloc.tag = "1"
        aMapManager.aMap.isMyLocationEnabled = false
    }
    protected open fun showMyLocationViewInfo() {
        iv_showloc.setImageResource(R.drawable.ic_visibility_black_24dp)
        iv_showloc.tag = "0"
        aMapManager.aMap.isMyLocationEnabled = true
    }

    /**
     * 获取定位图标  空则使用默认
     */
    abstract fun getLocationBitmap(): BitmapDescriptor?

    /**
     * 是否自动定位
     */
    protected abstract  fun   autoLocation():Boolean


    /**
     * 地图加载完成
     */
    open  fun  onMapLoadComplete(){

    }

    fun   initMapZoomUiEnable(){
        val   zoom  =  aMapManager.aMap.cameraPosition.zoom
        println("initMapZoomUiEnable=${zoom}")
        if(zoom < aMapManager.getMapMaxZoomLevel() && zoom > aMapManager.getMapMinZoomLevel()){
            zoom_in.isEnabled = true
            zoom_out.isEnabled = true
        }else if(zoom >= aMapManager.getMapMaxZoomLevel()){
            zoom_out.isEnabled = true
            zoom_in.isEnabled = false
        }else if(zoom <= aMapManager.getMapMinZoomLevel()){
            zoom_out.isEnabled = false
            zoom_in.isEnabled = true
        }
    }

    open  fun    onSeeAllMarkers(){

    }

    /**
     * 收到新的定位信息
     */
    open  fun  changeMyLocation(bdLocation: Location){


    }

    /**
     * 获取到第一次定位信息
     */
    open   fun   onFristMyLocation(bdLocation: Location){
        println("第一次获取定位----》")
        aMapManager.setMapCenter(bdLocation.latitude,bdLocation.longitude,aMapManager.getMapMaxZoomLevel()-1)
    }

    override fun onResume() {
        super.onResume()
        aMapManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        aMapManager.onPause()
    }

    override fun onDestroy() {
        aMapManager.onDestroy()
        super.onDestroy()
    }
}