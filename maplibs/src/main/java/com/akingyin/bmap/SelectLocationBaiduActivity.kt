package com.akingyin.bmap


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.akingyin.base.SimpleActivity
import com.akingyin.base.ext.click
import com.akingyin.base.ext.gone
import com.akingyin.base.ext.startActivity
import com.akingyin.base.ext.visiable
import com.akingyin.bmap.adapter.BaiduPoiListAdapter
import com.akingyin.bmap.vo.PoiInfoVo
import com.akingyin.map.R

import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import kotlinx.android.synthetic.main.activity_select_baidu_location.*



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
        bdMapManager = BDMapManager(map_content.map,map_content,this,true)
        bdMapManager.onCreate(savedInstanceState)
        bdMapManager.initMapConfig()
        bdMapManager.getUiSetting().apply {
            isZoomGesturesEnabled = true

        }

        bdMapManager.setMapStatusChange(onChangeFinish = {
              it?.let {
                  mapStatus ->
                  searchPoiInfos(mapStatus.target)
              }
        },onChangeStart = {
              if(!searchPoiIng){
                  baiduPoiListAdapter.setDiffNewData(null)
                  progress_bar.visiable()
              }

        },onChange = {
            mapStatus ->
            marker?.position = mapStatus?.target

        })
        bdMapManager.initPoiConfig()
        bdMapManager.initGeoCoderConfig()
        bdMapManager.setMapLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,true,null)
    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    private   lateinit var   bdMapManager: BDMapManager

    private   var  marker:Marker? = null
    private   var   fristLoc = false
    override fun initView() {

         setToolBar(findViewById(R.id.toolbar),"位置发送")

        rcv_poi_list.itemAnimator = DefaultItemAnimator()
        baiduPoiListAdapter=BaiduPoiListAdapter()
        baiduPoiListAdapter.onAttachedToRecyclerView(rcv_poi_list)
        baiduPoiListAdapter.setEmptyView(R.layout.empty_view)
        rcv_poi_list.adapter = baiduPoiListAdapter
        location.showNext()
        bdMapManager.onMapLoad {
            println("onLoad->")


        }
        bdMapManager.registerLocationListener({


            bdMapManager.setMyLocationData(it)
            if(!fristLoc){
                bdMapManager.setMapCenter(it.latitude,it.longitude,19F)

                fristLoc = true
                location.showNext()
                if(null == marker){
                    marker =bdMapManager.addSingleMarker(MarkerOptions().apply {
                        position(LatLng(it.latitude,it.longitude))
                        icon(bitmap)
                    })
                }else{
                    marker!!.position = LatLng(it.latitude,it.longitude)

                }
                searchPoiInfos(LatLng(it.latitude,it.longitude))
            }


        },{

        })
        baiduPoiListAdapter.setDiffCallback(object :DiffUtil.ItemCallback<PoiInfoVo>(){
            override fun areItemsTheSame(oldItem: PoiInfoVo, newItem: PoiInfoVo): Boolean {
                println("areItemsTheSame")
               return oldItem.uid == newItem.uid
            }

            override fun areContentsTheSame(oldItem: PoiInfoVo, newItem: PoiInfoVo): Boolean {
                println("areContentsTheSame")
               return  oldItem.name == newItem.name && oldItem.address == newItem.address && oldItem.mSelected == newItem.mSelected
            }
        })
        baiduPoiListAdapter.setOnItemClickListener { _, _, position ->
            baiduPoiListAdapter.data.find {
                it.mSelected
            }?.mSelected = false
            baiduPoiListAdapter.getItem(position).apply {
                mSelected = true
                searchPoiIng = true
                bdMapManager.setMapCenter(location.latitude,location.longitude,bdMapManager.getCurrentZoomLevel())
                marker?.position = LatLng(location.latitude,location.longitude)

            }
            baiduPoiListAdapter.notifyDataSetChanged()

        }
        location_icon.click {
            location.showNext()
            fristLoc = false
        }

        location_progress.click {
            fristLoc = true
            location.showNext()

        }
    }




    override fun startRequest() {

    }

    private var bitmap: BitmapDescriptor = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_marka)

   private  var   searchPoiIng = false
   private fun   searchPoiInfos(latLng: LatLng){
        if(searchPoiIng){
            searchPoiIng = false
            return
        }
        progress_bar.visiable()

        bdMapManager.searchRoundPoiByGeoCoder(latLng.latitude,latLng.longitude){
            data, e ->
            progress_bar.gone()

            data?.let {
                result->

                baiduPoiListAdapter.setDiffNewData(result.map {
                    pointInfo ->
                    PoiInfoVo(pointInfo.name,pointInfo.uid,pointInfo.address,pointInfo.location,false)
                }.filterIndexed { index, poiInfoVo ->
                    if(index == 0){
                        poiInfoVo.mSelected = true
                    }
                    true
                }.toMutableList())
            }?:showError("查询失败${e?.message}")
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_select_location,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_map_send){
          baiduPoiListAdapter.data.find {
                it.mSelected
          }?.let {
              setResult(Activity.RESULT_OK, Intent().apply {
                  putExtra(PanoramaBaiduMapActivity.LAT_KEY,it.location.latitude)
                  putExtra(PanoramaBaiduMapActivity.LNG_KEY,it.location.longitude)
                  putExtra(PanoramaBaiduMapActivity.ADDR_KEY,it.address)
                  putExtra("name",it.name)
                  putExtra("uid",it.uid)
              })

              finish()
          }?:showError("请选择要发送的地址！")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        bitmap.recycle()
        bdMapManager.onDestroy()
    }
}