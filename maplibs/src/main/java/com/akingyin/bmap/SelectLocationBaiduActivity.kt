package com.akingyin.bmap

import android.Manifest
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.akingyin.base.SimpleActivity
import com.akingyin.base.ext.click
import com.akingyin.base.ext.gone
import com.akingyin.base.ext.visiable
import com.akingyin.bmap.adapter.BaiduPoiListAdapter
import com.akingyin.bmap.vo.PoiInfoVo
import com.akingyin.map.R
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import kotlinx.android.synthetic.main.activity_select_baidu_location.*
import kotlinx.android.synthetic.main.include_toolbar.*
import permissions.dispatcher.ktx.withPermissionsCheck


/**
 * @ Description:
 * @author king
 * @ Date 2020/5/19 16:03
 * @version V1.0
 */
class SelectLocationBaiduActivity :SimpleActivity() {


    private  lateinit var   baiduPoiListAdapter: BaiduPoiListAdapter




    override fun initInjection() {

    }

    override fun getLayoutId() = R.layout.activity_select_baidu_location

    override fun initializationData(savedInstanceState: Bundle?) {
        bdMapManager = BDMapManager(map_content.map,map_content,this,false)
        bdMapManager.onCreate(savedInstanceState)

        bdMapManager.initMapConfig()
        bdMapManager.initPoiConfig()
        bdMapManager.setMapLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,true,null)
    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    private   lateinit var   bdMapManager: BDMapManager

    private   var  marker:Marker? = null
    override fun initView() {
         setToolBar(toolbar,"位置发送")

        rcv_poi_list.itemAnimator = DefaultItemAnimator()
        rcv_poi_list.adapter = baiduPoiListAdapter
        bdMapManager.onMapLoad {
             startMapLocation()

        }
        bdMapManager.registerLocationListener({
            location_icon.visiable()
            bdMapManager.stopLoction()
            bdMapManager.setMyLocationData(it)
            bdMapManager.setMapCenter(it.latitude,it.longitude,bdMapManager.getCurrentZoomLevel())
            if(null == marker){
                marker =bdMapManager.addSingleMarker(MarkerOptions().apply {
                    position(LatLng(it.latitude,it.longitude))
                    icon(bitmap)
                })
            }else{
                marker!!.position = LatLng(it.latitude,it.longitude)

            }
            searchPoiInfos(LatLng(it.latitude,it.longitude))

        },{

        })
        baiduPoiListAdapter.setDiffCallback(object :DiffUtil.ItemCallback<PoiInfoVo>(){
            override fun areItemsTheSame(oldItem: PoiInfoVo, newItem: PoiInfoVo): Boolean {
               return oldItem.uid == newItem.uid
            }

            override fun areContentsTheSame(oldItem: PoiInfoVo, newItem: PoiInfoVo): Boolean {
               return  oldItem.name == newItem.name && oldItem.address == newItem.address
            }
        })
        location_icon.click {
            startMapLocation()
        }
    }


    fun   startMapLocation() = withPermissionsCheck(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
            onShowRationale={
                it.proceed()
            }){
        bdMapManager.startLoction()
        location_icon.gone()
    }

    override fun startRequest() {

    }

    private var bitmap: BitmapDescriptor = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_marka)

    fun   searchPoiInfos(latLng: LatLng){
        progress_bar.visiable()
        bdMapManager.searchRoundPoi(latLng.latitude,latLng.longitude){
            progress_bar.gone()
            it?.let {
                result->
                baiduPoiListAdapter.setDiffNewData(result.map {
                    pointInfo ->
                    PoiInfoVo(pointInfo.name,pointInfo.uid,pointInfo.address,pointInfo.location,false)
                }.toMutableList())
            }
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
        super.onDestroy()
        bitmap.recycle()
        bdMapManager.onDestroy()
    }
}