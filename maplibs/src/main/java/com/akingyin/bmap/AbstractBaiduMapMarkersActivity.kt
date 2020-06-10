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
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import com.akingyin.base.ext.click
import com.akingyin.base.ext.startActivity
import com.akingyin.base.utils.DateUtil
import com.akingyin.map.IMarker
import com.akingyin.map.R
import com.akingyin.map.adapter.MarkerInfoViewPager2Adapter
import com.akingyin.map.base.ILoadImage
import com.akingyin.map.base.IOperationListion
import com.akingyin.map.base.MapPathPlanUtil
import com.akingyin.map.ui.MapSettingActivity
import com.baidu.location.BDLocation
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.utils.DistanceUtil
import com.blankj.utilcode.util.SPUtils
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
@Suppress("UNCHECKED_CAST")
abstract class AbstractBaiduMapMarkersActivity<T:IMarker> :BaseBDMapActivity(),ILoadImage,IOperationListion<T>{

    var pathRead = BitmapDescriptorFactory.fromAsset("icon_road_red_arrow.png")
    var pathGreen = BitmapDescriptorFactory.fromAsset("icon_road_green_arrow.png")
    protected var readBitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark)
    protected var startBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_start)
    protected var endBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_end)
    protected var personDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.person)

    private   var  supportMapClusterValue = false
    private   var  supportSuggestedPathValue = false
    override fun initializationData(savedInstanceState: Bundle?) {
        super.initializationData(savedInstanceState)
        supportMapClusterValue = SPUtils.getInstance("map_setting").getBoolean("support_map_cluster",false)
        supportSuggestedPathValue = SPUtils.getInstance("map_setting").getBoolean("map_suggest_path_key",false)
        initClusterManager(bdMapManager.baiduMap)
    }


    private   var   lastZoomChangeTime = 0L
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
        markerInfoViewPager2Adapter.setDiffCallback(object : DiffUtil.ItemCallback<T>(){
            override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.uuid == newItem.uuid

            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                return oldItem.baseInfo == newItem.baseInfo &&
                        oldItem.disFromPostion == newItem.disFromPostion

            }
        })
        viewPager2.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                onViewPageSelected(markerInfoViewPager2Adapter.getItem(position))
               // initSelectedIndexViewDis(position)
            }
        })
        bdMapManager.initMapMarkerConfig {
            if(supportMapCluster()){
                bindClusterManagerMarkerClick()?.onMarkerClick(it)
            }
            bdMapManager.findMarkerDataAndIndexByMarker(it,dataQueue)?.let {
                pair ->
                initLastMarkerIcon()
                mCurrentMarker = it
                lastClickMarkerIcon = it.icon
                mCurrentMarker?.icon = readBitmap
                onMapMarkerClick(pair.first,pair.second,it)
            }
        }
        bdMapManager.setMapStatusChange(onChange = {

            it?.let {
                mapStatus ->
                if(supportMapCluster()){
                    bindClusterManagerMapStatusChange()?.onMapStatusChange(mapStatus)
                }
            }

        })

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
        println("loadMarkerData->")
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
                    addClusterManagerData(dataQueue)
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
            loadMarkerDataComplete()
        }finally {
            loadMarkerData.set(false)
        }
    }


    /**
     * 数据加载完成UI 线程
     */
    open   fun    loadMarkerDataComplete(){
        println("loadMarkerDataComplete")
        loadMapMarker(firstLoadMarker)
        firstLoadMarker = false
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
            lastLocation?.let {
                val  dis = DistanceUtil.getDistance(LatLng(it.latitude,it.longitude),LatLng(bdLocation.latitude,bdLocation.longitude))
                if(dis>= getMinDisFlushPath()){
                    onFilterMapMarkerBestPath(LatLng(bdLocation.latitude,bdLocation.longitude))
                }
            }
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
         try {
             GlobalScope.launch(Main) {
                 if(customList.isEmpty()){
                     customList.add(pathGreen)
                     customList.add(pathRead)
                 }
                 pathDataQueue.clear()
                 pathDataQueue.addAll(dataQueue)
                 pathDataQueue.filter {
                     !it.isComplete()
                 }
                 pathDataQueue =  withContext(IO){

                     MapPathPlanUtil.getOptimalPathPlan(pathDataQueue,latlng).toMutableList()
                 }

                 val  pathIndex = mutableListOf<Int>()
                 val  pathLatlngs = pathDataQueue.map {
                     pathIndex.add(if(it.isComplete()){0}else{1})

                     LatLng(it.getLat(),it.getLng())

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
         }catch (e : Exception){
             e.printStackTrace()
         }
    }


    /**
     * 清除当前推荐的路线图
     */
    open   fun   onCleanMapMarkerBestPath(){
        polylineMarker?.remove()
        startMarker?.remove()
        endMarker?.remove()
    }

    private var mPopupBottonWindow: PopupWindow? = null
    lateinit var popView: View
    lateinit var closeButton: ImageView
    lateinit var  viewPager2: ViewPager2
    lateinit var  markerInfoViewPager2Adapter: MarkerInfoViewPager2Adapter<T>
    /**
     * 显示当前marker详情
     */
     private fun    showMapMarkerListInfo(postion: Int,viewDatas:List<T>){
        try {

            mPopupBottonWindow?.let {
                if(it.isShowing){
                    it.dismiss()
                }
            }
            initPopupWindow()
            val showMarkers = mutableListOf<IMarker>()
            viewDatas.forEachIndexed { index, t ->
                bdMapManager.getMyLocationData().apply {
                    if(latitude>0 && longitude>0){
                        t.disFromPostion = DistanceUtil.getDistance(LatLng(latitude,longitude), LatLng(t.getLat(),t.getLng()))
                    }
                }
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

            markerInfoViewPager2Adapter.setDiffNewData(showMarkers.map {
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

    /**
     * 设置选中点与当前手机位置距离
     */
    private   fun    initSelectedIndexViewDis(postion: Int){
        markerInfoViewPager2Adapter.getItem(postion).apply {
            disFromPostion = when {
                bdMapManager.getMyLocationData().longitude>1 -> {
                    DistanceUtil.getDistance(LatLng(getLat(),getLng()), LatLng(bdMapManager.getMyLocationData().latitude,bdMapManager.getMyLocationData().longitude))
                }
                else -> {
                    null
                }
            }
        }
        viewPager2.post {
            markerInfoViewPager2Adapter.notifyItemChanged(postion)

        }


    }

    private   fun    initLastMarkerIcon(){
        if(null != mCurrentMarker && null != lastClickMarkerIcon){
            mCurrentMarker?.icon = lastClickMarkerIcon
        }
    }

    open   fun   onViewPageSelected(data:T){
        println("onViewPageSelected=${data.uuid}")
        bdMapManager.findMarkerByData(data)?.let {
            initLastMarkerIcon()
            mCurrentMarker = it
            lastClickMarkerIcon = mCurrentMarker?.icon
            mCurrentMarker?.icon = readBitmap
            bdMapManager.setMapCenter(it.position.latitude,it.position.longitude,bdMapManager.getCurrentZoomLevel())
        }?:showError("未找到当前对应的marker")
    }

    open   fun   initPopupWindow(){
        if(null == mPopupBottonWindow){
            mPopupBottonWindow = PopupWindow(this).apply {
                contentView = popView
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                isFocusable = true
                setBackgroundDrawable(BitmapDrawable(resources,""))
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
    open   fun    loadMapMarker(firstLoad:Boolean = false){
        println("loadMapMarker")
        if(loadMarkerData.get()){
            showError("当前数据正在加载中请稍后再试")
            return
        }
        try {
          showLoading()
          GlobalScope.launch(Main) {
              withContext(IO){
                  val   list = mutableListOf<T>().apply {
                      dataQueue.forEach {
                          add(it)
                      }
                  }.filter {
                      onFilterMarkerData(it)
                  }

                  if(supportMapCluster()){
                      loadMapClusterMarker(firstLoad)
                  }else{
                      val  overlays = list.map {
                          data->
                          bdMapManager.onCreateMarkerOptions(data)
                                  .animateType(getMapMarkerAnimate())
                                  .icon(getBitmapDescriptor(data))
                                  .draggable(getMapMarkerDrag())

                      }
                      println("addDataToMap->"+DateUtil.getNowTimeString(DateUtil.HH_MM_SS_SSS)+":"+Thread.currentThread().name+":"+Thread.currentThread().id)
                      bdMapManager.addDataToMap(overlays)
                      if(firstLoad){
                          bdMapManager.zoomToSpan()
                      }
                  }


              }
              hideLoadDialog()
              loadMapMarkerViewComplete()
          }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }


    open   fun   loadMapClusterMarker(frist:Boolean){

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
    open fun showPathPlan(): Boolean = supportSuggestedPathValue


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

    /** 第一次加载 marker */
    private   var   firstLoadMarker = true
    private   var   firstonResume = true
    override fun onResume() {
        super.onResume()
        val  value  = SPUtils.getInstance("map_setting").getBoolean("support_map_cluster",false)

        val  value2  =  SPUtils.getInstance("map_setting").getBoolean("map_suggest_path_key",false)
        if(supportMapClusterValue != value){
            onShowClusterManagerChange(value)
            supportMapClusterValue = value
        }
        if(supportSuggestedPathValue != value2){
            onShowSuggestedPathChange(value2)
            supportSuggestedPathValue = value2
        }
        if(firstonResume){
            firstonResume = false
            return
        }
        println("------onResume------")
       loadMarkerData()
    }

    /**
     * 显示聚合点状态发生改变
     */
    open   fun    onShowClusterManagerChange(change:Boolean){

    }

    /**
     * 显示推荐路径发生改变
     */
    open   fun   onShowSuggestedPathChange(change: Boolean){
        if(change){
            bdMapManager.getMyLocationData().apply {
                if(latitude>0 && longitude>0){
                    onFilterMapMarkerBestPath(LatLng(latitude,longitude))
                }else{
                    showError("当前无位置信息，无法推荐最佳路径")
                }
            }

        }else{
             onCleanMapMarkerBestPath()
            mPopupBottonWindow?.let {
                if(it.isShowing){
                    it.dismiss()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bdMapManager.recycleMapBitmap(pathGreen,pathRead,readBitmap,startBitmap,endBitmap,personDescriptor)
    }

    override fun onFristMyLocation(bdLocation: BDLocation) {
        if(showPathPlan()){
            onFilterMapMarkerBestPath(LatLng(bdLocation.latitude,bdLocation.longitude))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_map_setting,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_map_item_setting -> showMapSettingInfo()
            
            R.id.action_map_refresh ->{
                onMapRefresh()
            }


            R.id.action_map_search ->{
                onSearchMapData()
            }
        }
       
        return super.onOptionsItemSelected(item)
    }

    override fun initView(left: TextView?, center: TextView?, right: TextView?, postion: Int, iMarkerModel: T?, vararg views: View?) {

    }



    override fun onPathPlan(postion: Int, iMarkerModel: T) {

    }



    override fun onOtherOperation(postion: Int, iMarkerModel: T, view: View?) {

    }

    /**
     * 是否支持点聚合
     */
    open   fun    supportMapCluster()=supportMapClusterValue



    /**
     * 初始化聚合
     */
    open  fun    initClusterManager(baiduMap: BaiduMap){

    }

    /**
     * 绑定 聚合管理器
     */
    open fun    bindClusterManagerMapStatusChange():BaiduMap.OnMapStatusChangeListener?{
          return null
    }

    /**
     * 获取marker 点击事件
     */
    open  fun   bindClusterManagerMarkerClick():BaiduMap.OnMarkerClickListener?{
        return null
    }



    /**
     * 添加聚合数据
     */
    open   fun    addClusterManagerData(data:List<T>){

    }



    open   fun    showMapSettingInfo(){
        startActivity<MapSettingActivity>()
    }

    open  fun     onMapRefresh(){
        loadMarkerData()
    }

    open  fun     onSearchMapData(){

    }
}