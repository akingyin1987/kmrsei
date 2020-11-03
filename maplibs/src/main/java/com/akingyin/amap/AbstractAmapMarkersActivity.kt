/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.amap

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.SystemClock
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

import com.akingyin.map.IMarker
import com.akingyin.map.R
import com.akingyin.map.adapter.MarkerInfoViewPager2Adapter
import com.akingyin.map.base.ILoadImage
import com.akingyin.map.base.IOperationListion
import com.akingyin.map.base.MapPathPlanUtil
import com.akingyin.map.ui.MapSettingActivity
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.*



import com.blankj.utilcode.util.SPUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * 高德地图显示marker
 * @ Description:
 * @author king
 * @ Date 2020/6/17 11:42
 * @version V1.0
 */
abstract class AbstractAmapMarkersActivity<T : IMarker> : BaseAMapActivity(), ILoadImage, IOperationListion<T> {

    var pathRead = BitmapDescriptorFactory.fromAsset("icon_road_red_arrow.png")
    var pathGreen = BitmapDescriptorFactory.fromAsset("icon_road_green_arrow.png")
    protected var readBitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark)
    protected var startBitmap = BitmapDescriptorFactory.fromResource(R.drawable.amap_start)
    protected var endBitmap = BitmapDescriptorFactory.fromResource(R.drawable.amap_end)
    protected var personADescriptor = BitmapDescriptorFactory.fromResource(R.drawable.person)

    private   var  supportMapClusterValue = false
    private   var  supportSuggestedPathValue = false
    override fun initializationData(savedInstanceState: Bundle?) {
        super.initializationData(savedInstanceState)
        supportMapClusterValue = SPUtils.getInstance("map_setting").getBoolean("support_map_cluster",false)
        supportSuggestedPathValue = SPUtils.getInstance("map_setting").getBoolean("map_suggest_path_key",false)
        initClusterManager(aMapManager.aMap)
    }




    private   var   lastZoomChangeTime = 0L
    @SuppressLint("InflateParams")
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
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(!supportMapCluster()){
                    onViewPageSelected(markerInfoViewPager2Adapter.getItem(position))

                }
                // initSelectedIndexViewDis(position)
            }
        })
        aMapManager.initMapMarkerConfig {
            if(supportMapCluster()){

                onMapMarkerClick(0,null,it)
            }else{
                aMapManager.findMarkerDataAndIndexByMarker(it,dataQueue)?.let {
                    pair ->
                    initLastMarkerIcon()
                    mCurrentMarker = it
                    lastClickMarkerIcon = it.options.icon
                    mCurrentMarker?.setIcon(readBitmap)
                    onMapMarkerClick(pair.first,pair.second,it)
                }
            }

        }
        aMapManager.setMapStatusChange(onChangeFinish= {

            it?.let {
                mapStatus ->
                initMapZoomUiEnable()
                if(supportMapCluster()){
                    bindClusterManagerMapStatusChange()?.onCameraChangeFinish(mapStatus)
                }
            }

        })

    }




    override fun onMapLoadComplete() {
        super.onMapLoadComplete()
        loadMarkerData()
    }


    private   var   loadMarkerData = AtomicBoolean(false)
    private   var   lock : Lock = ReentrantLock()
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
            loadMarkerComplete.set(false)
            lock.lock()
            GlobalScope.launch (Dispatchers.Main){
                showLoading()
                withContext(Dispatchers.IO){
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
            lock.unlock()
        }
    }


    /**
     * 数据加载完成UI 线程
     */
    open   fun    loadMarkerDataComplete(){

        mPopupBottonWindow?.dismiss()
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
            aMapManager.zoomToSpan()
        }
    }


    private   var  lastLocation: Location?=null
    private   var  lastLocationTimme = 0L
    override fun changeMyLocation(bdLocation: Location) {
        super.changeMyLocation(bdLocation)

        if(!loadMarkerData.get() && showPathPlan() && getMapLoadMarkerComplete()){
            lastLocation?.let {
                val  dis = AMapUtils.calculateLineDistance(LatLng(it.latitude,it.longitude), LatLng(bdLocation.latitude,bdLocation.longitude))
                if(dis>= getMinDisFlushPath()){
                    onFilterMapMarkerBestPath(LatLng(bdLocation.latitude, bdLocation.longitude))
                    return@let
                }
                if(SystemClock.elapsedRealtime() - lastLocationTimme > getMinTimeFlushPath() *60 *1000){
                    onFilterMapMarkerBestPath(LatLng(bdLocation.latitude, bdLocation.longitude))
                }
            }?:onFilterMapMarkerBestPath(LatLng(bdLocation.latitude, bdLocation.longitude))


        }
    }



    private var polylineMarker: Polyline? = null
    private var startMarker: Marker?=null
    private var endMarker : Marker?=null

    /** 构造纹理队列  */
    var customList= mutableListOf<BitmapDescriptor>()




    /**
     * 通过当前坐标记录最佳轨迹路线图
     */
    open   fun   onFilterMapMarkerBestPath(latlng: LatLng){

        try {
            lastLocation = Location(LocationManager.GPS_PROVIDER)
            lastLocation?.let {
                it.latitude = latlng.latitude
                it.longitude = latlng.longitude
            }
            lastLocationTimme = SystemClock.elapsedRealtime()
            GlobalScope.launch(Dispatchers.Main) {
                if(customList.isEmpty()){
                    pathGreen = pathGreen?:BitmapDescriptorFactory.fromAsset("icon_road_green_arrow.png")
                    pathRead = pathRead?:BitmapDescriptorFactory.fromAsset("icon_road_red_arrow.png")
                    customList.add(pathGreen)
                    customList.add(pathRead)
                }
                pathDataQueue.clear()
                pathDataQueue.addAll(dataQueue)
                pathDataQueue.filter {
                    !it.isComplete()
                }
                println("路径显示：${pathDataQueue.size}")
                pathDataQueue =  withContext(Dispatchers.IO){

                    MapPathPlanUtil.getOptimalPathPlan(pathDataQueue,com.baidu.mapapi.model.LatLng(latlng.latitude,latlng.longitude)).toMutableList()
                }
                println("计算后路径显示：${pathDataQueue.size}")

                val  pathIndex = mutableListOf<Int>()
                val  pathLatlngs = pathDataQueue.map {
                    pathIndex.add(if(it.isComplete()){0}else{1})

                    LatLng(it.getLat(),it.getLng())

                }

                polylineMarker?.remove()
                startMarker?.remove()
                endMarker?.remove()

                if(pathLatlngs.size>1){
                    startBitmap = startBitmap?:BitmapDescriptorFactory.fromResource(R.drawable.amap_start)
                    endBitmap = endBitmap?:BitmapDescriptorFactory.fromResource(R.drawable.amap_end)
                    println("startBitmap=${null == startBitmap}")
                    polylineMarker =  aMapManager.addPolylineMarker(pathLatlngs,pathIndex,customList)
                    val startmarker: MarkerOptions = MarkerOptions()
                            .position(pathLatlngs[0]).icon(startBitmap).draggable(getMapMarkerDrag())
                    startMarker = aMapManager.addSingleMarker(startmarker)
                    val endmarker: MarkerOptions = MarkerOptions()
                            .position(pathLatlngs[pathLatlngs.size-1]).icon(endBitmap).draggable(getMapMarkerDrag())
                    endMarker = aMapManager.addSingleMarker(endmarker)
                    if(!supportMapClusterValue){
                        showMapMarkerListInfo(0,pathDataQueue)

                    }

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
    @Suppress("UNCHECKED_CAST")
    fun    showMapMarkerListInfo(postion: Int, viewDatas:List<T>, notsetMarker: Boolean = false){
        try {

            mPopupBottonWindow?.let {
                if(it.isShowing){
                    it.dismiss()
                }
            }
            initPopupWindow()
            val showMarkers = mutableListOf<IMarker>()
            viewDatas.forEachIndexed { index, t ->
                aMapManager.getMyLocationData()?.apply {
                    if(latitude>0 && longitude>0){
                        t.disFromPostion = AMapUtils.calculateLineDistance(LatLng(latitude,longitude), LatLng(t.getLat(),t.getLng())).toDouble()
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
            viewPager2.setCurrentItem(postion,false)
            if (showPathPlan()) {
                mPopupBottonWindow?.isOutsideTouchable = false
                mPopupBottonWindow?.isFocusable = false
            } else {
                mPopupBottonWindow?.isOutsideTouchable = true
                mPopupBottonWindow?.isFocusable = true
            }
            if(!notsetMarker){
                println("showMapMarkerListInfo->notsetMarker")
                mCurrentMarker = aMapManager.findMarkerByData(viewDatas[postion])
                if(null != mCurrentMarker){

                    lastClickMarkerIcon = mCurrentMarker?.options?.icon

                    println("找到上一次marker=${null == lastClickMarkerIcon}")
                }
                mCurrentMarker?.setIcon(readBitmap)
            }

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
            disFromPostion = aMapManager.getMyLocationData()?.let {
                location ->
                AMapUtils.calculateLineDistance(LatLng(getLat(),getLng()), LatLng(location.latitude,location.longitude)).toDouble()
            }

        }
        viewPager2.post {
            markerInfoViewPager2Adapter.notifyItemChanged(postion)

        }


    }

    private fun    initLastMarkerIcon(){
        println("还原当前marker 图标--->${null== lastClickMarkerIcon}")
        if(null != mCurrentMarker && null != lastClickMarkerIcon){
            println("last=${lastClickMarkerIcon == readBitmap},${lastClickMarkerIcon == personADescriptor}")
            mCurrentMarker?.setIcon(lastClickMarkerIcon)
            lastClickMarkerIcon = null
        }
    }

    /**
     * 处理当前点击的marker
     */
     fun   initClickMarkerIcon(marker: Marker){
        mCurrentMarker = marker
        lastClickMarkerIcon = marker.options.icon
        readBitmap = readBitmap?:BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark)
        mCurrentMarker?.setIcon(readBitmap)
        aMapManager.setMapCenter(marker.position.latitude,marker.position.longitude,aMapManager.getCurrentZoomLevel())
    }

    open   fun   onViewPageSelected(data:T){

        aMapManager.findMarkerByData(data)?.let {
            println("onViewPageSelected->")
            initLastMarkerIcon()
            initClickMarkerIcon(it)
        }
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
                     println("setOnDismissListener--->${null == lastClickMarkerIcon}")
                     initLastMarkerIcon()
                }
            }

        }
    }

    override fun onSeeAllMarkers() {
        super.onSeeAllMarkers()
        aMapManager.zoomToSpan()
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
            loadMarkerComplete.set(false)
            GlobalScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO){
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
                            aMapManager.onCreateMarkerOptions(data)
                                    .icon(getBitmapDescriptor(data))
                                    .draggable(getMapMarkerDrag())

                        }
                        aMapManager.addDataToMap(overlays)
                        if(firstLoad){
                            aMapManager.zoomToSpan()
                        }
                    }
                }
                hideLoadDialog()
                loadMapMarkerViewComplete()
            }
        }catch (e : Exception){
            e.printStackTrace()
        }finally {
            loadMarkerComplete.set(true)
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
    private var  minDisFlushPath = 50
    private var  minTimeFlushPath = 3


    open fun getMinDisFlushPath(): Int = minDisFlushPath

    open fun getMinTimeFlushPath(): Int = minTimeFlushPath








    open   fun     getMapMarkerDrag():Boolean = true
    /**
     *过滤数据
     */
    abstract  fun    onFilterMarkerData(data:T):Boolean

    /**
     * 地图上的图标
     */
    abstract   fun    getBitmapDescriptor(data:T): BitmapDescriptor


    /**
     * 查询地图数据
     */

    abstract  fun   searchMarkerData():List<T>

    protected var mShowAction: Animation? = null
    protected  var mHiddenAction: Animation? = null
    protected var mCurrentMarker: Marker? = null
    protected var lastClickMarkerIcon: BitmapDescriptor? = null
    /**
     * 当前marker点被点击
     */
    open   fun    onMapMarkerClick( postion:Int,data:T?,marker: Marker){
        println("onMapMarkerClick->")

        mPopupBottonWindow?.dismiss()
        if(supportMapCluster()){
            onClusterMarkerClick(marker)
        }else{
            showMapMarkerListInfo(postion,dataQueue,true)
        }

    }

    /**
     * 聚合点，点击事件
     */
    open   fun   onClusterMarkerClick(marker: Marker){

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

    /** 地图marker 是否加载完成 */
    private   var   loadMarkerComplete = AtomicBoolean(false)

    /** 地图上marker 是否加载完成 */
    open fun    getMapLoadMarkerComplete() = loadMarkerComplete.get()

    override fun onResume() {
        super.onResume()

        minDisFlushPath = SPUtils.getInstance("map_setting").getString("map_path_min_dis","50").toInt()
        minTimeFlushPath = SPUtils.getInstance("map_setting").getString("map_path_min_time","3").toInt()
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
            aMapManager.getMyLocationData()?.apply {
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
        aMapManager.recycleMapBitmap(pathGreen,pathRead,readBitmap,startBitmap,endBitmap,personADescriptor)
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
    open  fun    initClusterManager(aMap: AMap){

    }

    /**
     * 绑定 聚合管理器
     */
    open fun    bindClusterManagerMapStatusChange(): AMap.OnCameraChangeListener?{
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