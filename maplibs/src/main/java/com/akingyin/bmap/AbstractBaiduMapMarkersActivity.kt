/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.bmap

import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.viewpager2.widget.ViewPager2
import com.akingyin.base.ext.click
import com.akingyin.map.IMarker
import com.akingyin.map.R
import com.akingyin.map.adapter.MarkerInfoViewPager2Adapter
import com.akingyin.map.base.MapPathPlanUtil
import com.baidu.location.BDLocation
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.utils.DistanceUtil
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.set


/**
 * 需要使用基础的地图界面
 * 百度地图处理显示markers
 * @ Description:
 * @author king
 * @ Date 2020/5/26 10:32
 * @version V1.0
 */
abstract class AbstractBaiduMapMarkersActivity<T:IMarker> :BaseBDMapActivity(){

    var pathRead = BitmapDescriptorFactory.fromAsset("icon_road_red_arrow.png")
    var pathGreen = BitmapDescriptorFactory.fromAsset("icon_road_green_arrow.png")
    protected var readBitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark)
    protected var startBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_start)
    protected var endBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_end)
    protected var personDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.person)
    override fun initView() {
        super.initView()
        popView = LayoutInflater.from(this).inflate(R.layout.item_openmap_recyclerview, null)
        closeButton = popView.findViewById(R.id.close)
        closeButton.click {
            onCloseWindow()
        }
        mShowAction = AnimationUtils.loadAnimation(this, R.anim.layer_pop_in)
        mHiddenAction = AnimationUtils.loadAnimation(this, R.anim.layer_pop_out)
        viewPager2 = popView.findViewById(R.id.map_recycler)
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        markerInfoViewPager2Adapter = MarkerInfoViewPager2Adapter()
        viewPager2.adapter = markerInfoViewPager2Adapter
        viewPager2.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                onViewPageSelected(markerInfoViewPager2Adapter.getItem(position))

            }
        })
        bdMapManager.initMapMarkerConfig {
            bdMapManager.findMarkerDataAndIndexByMarker(it,dataQueue)?.let {
                pair ->
                if(null != mCurrentMarker && null != lastClickMarkerIcon){
                    mCurrentMarker?.icon = lastClickMarkerIcon
                }
                mCurrentMarker = it
                lastClickMarkerIcon = it.icon
                mCurrentMarker?.icon = readBitmap
                onMapMarkerClick(pair.first,pair.second,it)
            }
        }
    }

    override fun onMapLoadComplete() {
        super.onMapLoadComplete()
        loadMarkerData()
    }


    private   var   loadMarkerData = AtomicBoolean(false)
    /**
     * 加载地图marker数据
     */
    open  fun    loadMarkerData(){
        if(loadMarkerData.get()){
            showError("当前数据正在加载中请稍后再试")
            return
        }
        try {
            loadMarkerData.set(true)
            GlobalScope.launch (Main){
                showLoading()
                withContext(IO){
                    dataQueue.clear()
                    dataQueue.addAll(searchMarkerData())
                    dataKeyMap.clear()
                    dataQueue.forEach {
                        dataKeyMap[it.uuid] = it
                    }
                }
                hideLoadDialog()
                loadMarkerDataComplete()
            }
        }catch (e : Exception){
            e.printStackTrace()
        }finally {
            loadMarkerData.set(false)
            loadMarkerDataComplete()
        }
    }


    /**
     * 数据加载完成UI 线程
     */
    open   fun    loadMarkerDataComplete(){
        loadMapMarker()
    }



     private   var   fristLoadMarker = true
    /**
     * 地图marker 加载完成
     */
    open  fun    loadMapMarkerViewComplete(){
        if(fristLoadMarker){
            fristLoadMarker = false
            bdMapManager.zoomToSpan()
        }
    }


    private   var  lastLocation:BDLocation?=null
    override fun changeMyLocation(bdLocation: BDLocation) {
        super.changeMyLocation(bdLocation)
        if(!loadMarkerData.get() && showPathPlan()){
            lastLocation = bdLocation
        }
    }



    private var polylineMarker: Polyline? = null
    private var startMarker:Marker?=null
    private var endMarker :Marker?=null

    /** 构造纹理队列  */
    var customList= mutableListOf<BitmapDescriptor>()
    /**
     * 通过当前坐标记录最佳轨迹路线图
     */
    open   fun   onFilterMapMarkerBestPath(latlng:LatLng){
        if(customList.isEmpty()){
            customList.add(pathGreen)
            customList.add(pathRead)
        }
        pathDataQueue.clear()
        pathDataQueue.addAll(dataQueue)
        pathDataQueue.filter {
            !it.isComplete()
        }
        pathDataQueue = MapPathPlanUtil.getOptimalPathPlan(pathDataQueue,latlng).toMutableList()

        val  pathIndex = mutableListOf<Int>()
        val  pathLatlngs = pathDataQueue.map {
            pathIndex.add(1)
            LatLng(it.getLat(),it.getLng())

        }
        //处理经纬度，将排序后的计算距离小于2米的直接去掉一个
        val iterator2: MutableListIterator<LatLng> = pathLatlngs.toMutableList().listIterator()

        var latLng: LatLng? = null
        while (iterator2.hasNext()) {
            val temp = iterator2.next()
            if (null != latLng) {
                if (DistanceUtil.getDistance(latLng, temp) < 2) {
                    iterator2.remove()
                }
            }
            latLng = temp
        }
        polylineMarker?.remove()
        startMarker?.remove()
        endMarker?.remove()
        if(pathLatlngs.size>1){
            polylineMarker =  bdMapManager.addPolylineMarker(pathLatlngs,pathIndex,customList)
            val startmarker: MarkerOptions = MarkerOptions()
                    .position(pathLatlngs[0]).animateType(
                            MarkerOptions.MarkerAnimateType.drop).icon(startBitmap).draggable(false)
            startMarker = bdMapManager.addSingleMarker(startmarker)
            val endmarker: MarkerOptions = MarkerOptions()
                    .position(pathLatlngs[pathLatlngs.size-1]).animateType(
                            MarkerOptions.MarkerAnimateType.drop).icon(endBitmap).draggable(false)
            endMarker = bdMapManager.addSingleMarker(endmarker)
            showMapMarkerListInfo(0,pathDataQueue)
        }

    }

    private var mPopupBottonWindow: PopupWindow? = null
    lateinit var popView: View
    lateinit var closeButton: ImageView
    lateinit var  viewPager2: ViewPager2
    lateinit var  markerInfoViewPager2Adapter: MarkerInfoViewPager2Adapter<T>
    /**
     * 显示当前marker详情
     */
      fun    showMapMarkerListInfo(postion: Int,viewDatas:List<T>){
        try {
            mPopupBottonWindow?.let {
                if(it.isShowing){
                    it.dismiss()
                }
            }
            initPopupWindow()
            val showMarkers = mutableListOf<IMarker>()
            viewDatas.forEachIndexed { index, t ->
               if(null != t.markers && t.markers!!.isNotEmpty()){
                   t.sortInfo = "详情   ${viewDatas.size} - ${index+1} (${t.markers!!.size+1}-1)"
                   showMarkers.add(t)
                   t.markers?.forEachIndexed { index2, iMarker ->
                       iMarker.sortInfo = "详情   ${viewDatas.size} - ${index+1} (${t.markers!!.size+1}-${index2+2})"
                       showMarkers.add(iMarker)
                   }
               }else{
                   t.sortInfo = "详情   ${viewDatas.size} - ${index+1} "
                   showMarkers.add(t)
               }
            }
            markerInfoViewPager2Adapter.setNewInstance(showMarkers.map {
                it as T
            }.toMutableList())
            viewPager2.startAnimation(mShowAction)
            viewPager2.currentItem = postion
            if (showPathPlan()) {
                mPopupBottonWindow?.isOutsideTouchable = false
                mPopupBottonWindow?.isFocusable = false
            } else {
                mPopupBottonWindow?.isOutsideTouchable = true
                mPopupBottonWindow?.isFocusable = true
            }
            initLastMarkerIcon()
            mCurrentMarker = bdMapManager.findMarkerByData(viewDatas[postion])
            if(null != mCurrentMarker){
                lastClickMarkerIcon = mCurrentMarker?.icon
            }
            mCurrentMarker?.icon = readBitmap
            if (mPopupBottonWindow!!.isShowing) {
                mPopupBottonWindow!!.dismiss()
            } else {
                mPopupBottonWindow!!.showAtLocation(closeButton, Gravity.BOTTOM, 0, 0)
            }
        }catch (e : Exception){
            e.printStackTrace()
        }


    }

    private   fun    initLastMarkerIcon(){
        if(null != mCurrentMarker && null != lastClickMarkerIcon){
            mCurrentMarker?.icon = lastClickMarkerIcon
        }
    }

    open   fun   onViewPageSelected(data:T){
        bdMapManager.findMarkerByData(data)?.let {
            initLastMarkerIcon()
            mCurrentMarker = it
            lastClickMarkerIcon = mCurrentMarker?.icon
            mCurrentMarker?.icon = readBitmap
            bdMapManager.setMapCenter(it.position.latitude,it.position.longitude,bdMapManager.getCurrentZoomLevel())
        }
    }

    open   fun   initPopupWindow(){
        if(null == mPopupBottonWindow){
            mPopupBottonWindow = PopupWindow(this).apply {
                contentView = popView
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                isFocusable = true
                setBackgroundDrawable(BitmapDrawable())
                setOnDismissListener {
                    if(null != mCurrentMarker && null != lastClickMarkerIcon){
                        mCurrentMarker?.icon = lastClickMarkerIcon
                    }
                }
            }

        }
    }

    override fun onSeeAllMarkers() {
        super.onSeeAllMarkers()
        bdMapManager.zoomToSpan()
    }

    /**
     * 关闭详情框
     */
    open   fun   onCloseWindow(){
        mPopupBottonWindow?.dismiss()
    }

    /**
     * 加载地图marker
     */
    open   fun    loadMapMarker(){
        if(loadMarkerData.get()){
            showError("当前数据正在加载中请稍后再试")
            return
        }
        try {
          showLoading()
          GlobalScope.launch(Main) {
              withContext(IO){
                  var   list = mutableListOf<T>().apply {
                      dataQueue.forEach {
                          add(it)
                      }
                  }.filter {
                      onFilterMarkerData(it)
                  }
                  if(displayInOrder()){
                    list =  list.sortedBy {
                          it.appointSort
                      }
                  }


                  val  overlays = list.map {
                      data->
                     bdMapManager.onCreateMarkerOptions(data)
                             .animateType(getMapMarkerAnimate())
                             .icon(getBitmapDescriptor(data))
                             .draggable(getMapMarkerDrag())

                  }
                  bdMapManager.addDataToMap(overlays)

              }
              hideLoadDialog()
              loadMapMarkerViewComplete()
          }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    /**
     * 是否安指定顺序显示
     * @return
     */
    open fun displayInOrder(): Boolean = true

    /**
     * 是否显示基于marker点的路径规划
     * @return
     */
    open fun showPathPlan(): Boolean = true


    /**
     * 最短距离刷新路径规划
     * @return
     */
    open fun getMinDisFlushPath(): Int = 50






    /**
     * 地图marker显示动画
     */
    open   fun     getMapMarkerAnimate():MarkerOptions.MarkerAnimateType = MarkerOptions.MarkerAnimateType.none

    open   fun     getMapMarkerDrag():Boolean = true
    /**
     *过滤数据
     */
    abstract  fun    onFilterMarkerData(data:T):Boolean

    /**
     * 地图上的图标
     */
    abstract   fun    getBitmapDescriptor(data:T):BitmapDescriptor


        /**
     * 查询地图数据
     */
    abstract  fun   searchMarkerData():List<T>

    protected var mShowAction: Animation? = null
    protected  var mHiddenAction:Animation? = null
    protected var mCurrentMarker: Marker? = null
    protected var lastClickMarkerIcon: BitmapDescriptor? = null
    /**
     * 当前marker点被点击
     */
    open   fun    onMapMarkerClick( postion:Int,data:T,marker: Marker){
        showMapMarkerListInfo(postion,dataQueue)
    }

    /**
     * 展现的所有marker数据
     */
    private var dataQueue: MutableList<T> = mutableListOf()

    /**
     * 显示路径数据
     */
    private var pathDataQueue:MutableList<T> = mutableListOf()

    private var dataKeyMap :HashMap<String,T> = HashMap()


    private   var   firstonResume = true
    override fun onResume() {
        super.onResume()
        if(firstonResume){
            firstonResume = false
            return
        }
        loadMarkerData()
    }

    override fun onDestroy() {
        super.onDestroy()
        bdMapManager.recycleMapBitmap(pathGreen,pathRead,readBitmap,startBitmap,endBitmap,personDescriptor)
    }
}