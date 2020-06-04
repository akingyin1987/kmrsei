/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.zlcdgroup.mrsei.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.akingyin.base.utils.StringUtils
import com.akingyin.bmap.AbstractBaiduMapMarkersActivity
import com.akingyin.img.ImageLoadUtil
import com.baidu.mapapi.clusterutil.clustering.ClusterManager
import com.baidu.mapapi.map.BaiduMap

import com.baidu.mapapi.map.BitmapDescriptor

import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.model.BdModel
import com.zlcdgroup.nfcsdk.RfidInterface


/**
 * @ Description:
 * @author king
 * @ Date 2020/5/28 11:32
 * @version V1.0
 */
class TestBaiduMapActivity : AbstractBaiduMapMarkersActivity<BdModel>(){

    override fun onFilterMarkerData(data: BdModel)=true

    override fun getBitmapDescriptor(data: BdModel): BitmapDescriptor {

       return  personDescriptor
    }

    val  list = arrayListOf<BdModel>()
    override fun searchMarkerData(): List<BdModel> {
      if(list.size == 0){
          for (index in 0..200){
              list.add(BdModel(StringUtils.getUUID()).apply {
                  baseInfo="test${index}  --->${supportMapCluster()}---${null == bitmap}"
                  if(supportMapCluster() && (null == bitmap  || bitmap!!.bitmap.isRecycled)){
                      bitmap = getBitmapDescriptor(this)
                  }

              })
          }

      }

      list.sortWith(Comparator { o1, o2 ->
          when {
              o1.getLat()>o2.getLat() -> {
                  return@Comparator 1
              }
              o1.getLat()<o2.getLat() -> {
                  return@Comparator -1
              }
              else -> {
                  return@Comparator 0
              }
          }
      })
      return   list
    }



    override fun getLocationBitmap(): BitmapDescriptor? {
        return null
    }

    override fun autoLocation()= true

    override fun handTag(rfid: String?, rfidInterface: RfidInterface?) {

    }

    override fun initInjection() {

    }

    override fun initView() {
        super.initView()
        val bar = findViewById<Toolbar>(R.id.toolbar)
        setToolBar(bar,"百度地图marker测试")
    }

    override fun getLayoutId()= R.layout.activity_test_map_show_markers

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    override fun loadImageView(path: String, context: Context, imageView: ImageView) {
        ImageLoadUtil.loadImage(path,context,imageView)
    }

    override fun onOperation(postion: Int, iMarkerModel: BdModel) {
    }

    override fun onTuWen(postion: Int, iMarkerModel: BdModel) {
    }

    override fun onObjectImg(postion: Int, iMarkerModel: BdModel, view: View?) {
    }


    private lateinit  var   clusterManager : ClusterManager<BdModel>
    override fun initClusterManager(baiduMap: BaiduMap) {
        super.initClusterManager(baiduMap)
        clusterManager = ClusterManager(this,bdMapManager.baiduMap)

        clusterManager.setOnClusterClickListener {
            println("点击---->>>>")
            return@setOnClusterClickListener true
        }
    }

    override fun bindClusterManagerMapStatusChange(): BaiduMap.OnMapStatusChangeListener? {
        return clusterManager
    }

    override fun onShowClusterManagerChange(change: Boolean) {
        super.onShowClusterManagerChange(change)
        if(!change){
            println("清除---->>>>>>")
            clusterManager.clearItems()
            clusterManager.markerCollection.clear()
            clusterManager.clusterMarkerCollection.clear()
        }
    }

    override fun addClusterManagerData(data: List<BdModel>) {
        super.addClusterManagerData(data)
        clusterManager.addItems(data)
    }

    override fun loadMapClusterMarker(frist: Boolean) {
        super.loadMapClusterMarker(frist)
        if(frist){
            clusterManager.zoomToSpan()
        }
    }

    override fun startRequest() {

    }


}