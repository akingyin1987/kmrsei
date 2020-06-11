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
import com.akingyin.base.dialog.TaskShowDialog
import com.akingyin.base.utils.StringUtils
import com.akingyin.bmap.AbstractBaiduMapMarkersActivity
import com.akingyin.img.ImageLoadUtil
import com.baidu.mapapi.clusterutil.clustering.ClusterManager
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.BitmapDescriptor
import com.baidu.mapapi.map.Marker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.model.BdModel
import com.zlcdgroup.nfcsdk.RfidInterface
import kotlinx.android.synthetic.main.activity_test_baidu_marker.*
import kotlinx.android.synthetic.main.layout_bottom_sheet.*


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
       val behavior = from(bottom_sheet_view)
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

             val status  =    when (newState) {
                 STATE_DRAGGING ->  "STATE_DRAGGING" //过渡状态此时用户正在向上或者向下拖动bottom sheet
                 STATE_SETTLING ->  "STATE_SETTLING" // 视图从脱离手指自由滑动到最终停下的这一小段时间
                 STATE_EXPANDED ->  "STATE_EXPANDED" //处于完全展开的状态
                 STATE_COLLAPSED ->  "STATE_COLLAPSED" //默认的折叠状态
                 STATE_HIDDEN ->  "STATE_HIDDEN" //下滑动完全隐藏 bottom sheet
                 STATE_HALF_EXPANDED -> "STATE_HALF_EXPANDED"
                    else ->""
                }
                println(status)
            }
        })


    }

    override fun getLayoutId()= R.layout.activity_test_baidu_marker

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
            println("清玩数据完成---->>>>>>>")
        }
    }

    override fun addClusterManagerData(data: List<BdModel>) {
        super.addClusterManagerData(data)
        println("开始添加聚合数据------>>>>>>")
        data.forEach {
            it.bitmapDescriptor?:getBitmapDescriptor(it)
        }
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

    override fun onMapRefresh() {
        println("onMapRefresh")
        TaskShowDialog().setCallBack {
            println("it=${it}")
        }.showLoadDialog(this,"测试")


    }

    override fun onSearchMapData() {
        println("onSearchMapData")
        TaskShowDialog().showLoadDialog(this,"search")

    }

    override fun onCancelLoading() {
        super.onCancelLoading()
        println("对话框被取消---->>>>")
    }

    override fun onClusterMarkerClick(marker: Marker) {
        super.onClusterMarkerClick(marker)
        println("onClusterMarkerClick")

         clusterManager.findClusterMarkerData(marker)?.let {
             initClickMarkerIcon(marker)
             showMapMarkerListInfo(0, arrayListOf<BdModel>().apply {
                 add(it)
             })
         }

        val cluster  = clusterManager.findClusterMarkersData(marker)
        println("cluster=${cluster?.size}")
    }
}