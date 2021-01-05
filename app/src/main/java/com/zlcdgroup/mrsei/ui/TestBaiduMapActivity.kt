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
import android.widget.LinearLayout

import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import com.akingyin.base.utils.StringUtils
import com.akingyin.bmap.AbstractBaiduMapMarkersActivity
import com.akingyin.media.ImageLoadUtil
import com.akingyin.map.TestUtil
import com.akingyin.map.adapter.MarkerInfoListRecycleAdapter

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



/**
 * @ Description:
 * @author king
 * @ Date 2020/5/28 11:32
 * @version V1.0
 */
class TestBaiduMapActivity : AbstractBaiduMapMarkersActivity<BdModel>(){




    override fun onFilterMarkerData(data: BdModel): Boolean {
       data.bitmap?:getBitmapDescriptor(data)
       return  true
    }

    override fun getBitmapDescriptor(data: BdModel): BitmapDescriptor {

       return  personDescriptor
    }

    val  list = arrayListOf<BdModel>()
    override fun searchMarkerData(): List<BdModel> {
      if(list.size == 0){
          for (index in 0 until 200){
              val  bdinfo = TestUtil.Latlng()
              list.add(BdModel(StringUtils.getUUID(),bdinfo[0],bdinfo[1]).apply {
                   baseInfo = "test${index}"
                   bitmap = getBitmapDescriptor(this)

              })
          }
          println("size====${list.size}")

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

    lateinit var  behavior : BottomSheetBehavior<LinearLayout>
    override fun initView() {
        super.initView()
        val bar = findViewById<Toolbar>(R.id.toolbar)
        setToolBar(bar,"百度地图marker测试")
        behavior = from(bottom_sheet_view)
        behavior.state = STATE_HIDDEN
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

             val status  =    when (newState) {
                 STATE_DRAGGING ->  "STATE_DRAGGING" //过渡状态此时用户正在向上或者向下拖动bottom sheet
                 STATE_SETTLING ->  "STATE_SETTLING" // 视图从脱离手指自由滑动到最终停下的这一小段时间
                 STATE_EXPANDED ->  "STATE_EXPANDED" //处于完全展开的状态
                 STATE_COLLAPSED ->  "STATE_COLLAPSED" //默认的折叠状态
                 STATE_HIDDEN ->  {
                     initLastMarkerIcon()
                     "STATE_HIDDEN" //下滑动完全隐藏 bottom sheet
                 }
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

    override fun onMarkerTuwen(postion: Int, iMarkerModel: BdModel) {

    }

    override fun onMarkerOperation(postion: Int, iMarkerModel: BdModel) {

    }

    override fun onMarkerObjectImg(postion: Int, iMarkerModel: BdModel, view: View?) {

    }

    override fun loadMarkerImageView(path: String, context: Context, imageView: ImageView) {
        ImageLoadUtil.loadImage(path,context,imageView)
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
        }else{
            bdMapManager.cleanOverlayManagerMarkers()
        }
    }

    override fun addClusterManagerData(data: List<BdModel>) {
        super.addClusterManagerData(data)
        println("开始添加聚合数据------>>>>>>")
        data.forEach {
            it.bitmap?:getBitmapDescriptor(it)
        }
        clusterManager.addItems(data)
        clusterManager.cluster()
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
        println("baidu marker.size=${clusterManager.renderer.findClusterSingleMarkerDatas()?.size}  cluster=${clusterManager.renderer.findClusterMarkerDatas()?.size}")

    }

    override fun onSearchMapData() {
        if(supportMapCluster()){
            behavior.state = STATE_HIDDEN
        }
        if (drawer_view.isDrawerOpen(GravityCompat.END)) {
            println("---------isDrawerOpen---------")
            drawer_view.closeDrawer(GravityCompat.END)
        }else{
            println("openDrawer---------")
            drawer_view.openDrawer(GravityCompat.END,true)
        }

    }

    override fun onCancelLoading() {
        super.onCancelLoading()
        println("对话框被取消---->>>>")
    }

    var  viewPager2Adapter: MarkerInfoListRecycleAdapter<BdModel>?= null
    override fun onClusterMarkerClick(marker: Marker) {
        super.onClusterMarkerClick(marker)
        println("onClusterMarkerClick")

         clusterManager.findClusterMarkerData(marker)?.let {
             initClickMarkerIcon(marker)
             showMapMarkerListInfo(0, arrayListOf<BdModel>().apply {
                 add(it)
             },true)
         }

        clusterManager.findClusterMarkersData(marker)?.let {
            initClickMarkerIcon(marker)
            if(null == viewPager2Adapter){
                viewPager2Adapter = MarkerInfoListRecycleAdapter()
                recycler.adapter = viewPager2Adapter
                recycler.itemAnimator = DefaultItemAnimator()
            }
            viewPager2Adapter?.setNewInstance(it.items.toMutableList())
            behavior.state = STATE_COLLAPSED

        }

    }

    override fun onMapMarkerClick(postion: Int, data: BdModel?, marker: Marker) {
        behavior.state = STATE_HIDDEN
        super.onMapMarkerClick(postion, data, marker)
    }

    override fun onBackPressed() {
        if(behavior.state != STATE_HIDDEN){
            behavior.state = STATE_HIDDEN
            return
        }
        if(drawer_view.isDrawerOpen(GravityCompat.END)){
            drawer_view.closeDrawer(GravityCompat.END)
            return
        }
        super.onBackPressed()
    }
}