package com.akingyin.bmap

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.akingyin.base.BaseNfcTagActivity
import com.akingyin.base.ext.click
import com.akingyin.map.R
import com.akingyin.map.base.BaiduPanoramaActivity
import com.akingyin.map.base.MapLoadingDialog
import com.baidu.location.BDLocation
import com.baidu.mapapi.map.*

/**
 * @ Description:
 * @author king
 * @ Date 2020/5/18 18:28
 * @version V1.0
 */
/**
 * 百度地图基础类
 * @ Description:
 * @author king
 * @ Date 2020/5/18 15:08
 * @version V1.0
 */
abstract class BaseBDMapActivity : BaseNfcTagActivity(){



    lateinit var   bdMapManager:BDMapManager

    override fun initializationData(savedInstanceState: Bundle?) {
        val  mapView = findViewById<MapView>(R.id.map_content)
        bdMapManager = BDMapManager(mapView.map,mapView,this)
        bdMapManager.onCreate(savedInstanceState)
        bdMapManager.initMapConfig()
        bdMapManager.onMapLoad {
            onMapLoadComplete()
        }
    }


    /** 地图模式（正常，跟随，罗盘）  */
    private lateinit var location_icon: ImageView

    private lateinit var  zoom_in: ImageButton
    private lateinit var zoom_out: ImageButton

    /** 交通  */
    private lateinit var road_condition: ImageButton

    /** 地图类型（普通2d,普通3d,卫星）  */
    private lateinit var map_layers: ImageButton

    /** 全景  */
    private var map_street: ImageButton? = null


    private var vs_seeall: ViewSwitcher? = null
    private var iv_seeall: ImageView? = null

    /** 显示当前位置  */
    private lateinit var vs_showloc: ViewSwitcher
    private lateinit var iv_showloc: ImageView

    /** 地图类型  */
    private lateinit var maplayer: View
    private var mLoadingDialog: MapLoadingDialog? = null

    private var location_progress: ProgressBar? = null



    override fun initView() {
        location_icon = findViewById(R.id.location_icon)
        zoom_out = findViewById(R.id.zoom_out)
        zoom_in = findViewById(R.id.zoom_in)
        road_condition = findViewById(R.id.road_condition)
        vs_showloc = findViewById(R.id.vs_showloc)
        iv_showloc = findViewById(R.id.iv_showloc)
        iv_seeall = findViewById(R.id.iv_seeall)
        vs_seeall = findViewById(R.id.vs_seeall)

        location_icon = findViewById(R.id.location_icon)
        location_progress = findViewById(R.id.location_progress)

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
            when( bdMapManager.baiduMap.locationConfiguration?.locationMode){
                MyLocationConfiguration.LocationMode.NORMAL->{
                    location_icon.setImageResource(R.drawable.main_icon_follow)
                    bdMapManager.setMapLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,true,getLocationBitmap())
                }

                MyLocationConfiguration.LocationMode.COMPASS->{
                    location_icon.setImageResource(R.drawable.main_icon_compass)
                    bdMapManager.setMapLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS,true,getLocationBitmap())
                }

                MyLocationConfiguration.LocationMode.FOLLOWING->{
                    location_icon.setImageResource(R.drawable.main_icon_follow)
                    bdMapManager.setMapLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING,true,getLocationBitmap())
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
                    bdMapManager.baiduMap.mapType = BaiduMap.MAP_TYPE_SATELLITE
                }
                R.id.layer_2d->{

                    bdMapManager.baiduMap.mapType = BaiduMap.MAP_TYPE_NORMAL
                    bdMapManager.baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(MapStatus.Builder().overlook(0F).build()))
                }

                R.id.layer_3d->{
                    bdMapManager.baiduMap.mapType = BaiduMap.MAP_TYPE_NONE
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
            mPopupWindow = PopupWindow(maplayer, RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT, true)
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
            startActivity(Intent(this, BaiduPanoramaActivity::class.java).apply {
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
     * 地图加载完成
     */
    open  fun  onMapLoadComplete(){

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