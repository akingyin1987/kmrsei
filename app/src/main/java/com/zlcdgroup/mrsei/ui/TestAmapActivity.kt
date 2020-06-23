package com.zlcdgroup.mrsei.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.akingyin.amap.AbstractAmapMarkersActivity
import com.akingyin.base.utils.StringUtils
import com.akingyin.map.TestUtil
import com.amap.api.maps.AMap
import com.amap.api.maps.model.*
import com.amap.clustering.ClusterManager
import com.amap.clustering.algo.GridBasedAlgorithm
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.model.AMarker
import com.zlcdgroup.nfcsdk.RfidInterface


/**
 * @ Description:
 * @author king
 * @ Date 2020/1/6 15:55
 * @version V1.0
 */
class TestAmapActivity : AbstractAmapMarkersActivity<AMarker>() {

    override fun getLocationBitmap(): BitmapDescriptor? =null

    override fun autoLocation()=false

    override fun handTag(rfid: String?, rfidInterface: RfidInterface?) {

    }

    override fun initView() {
        super.initView()
        aMapManager.aMap.myLocationStyle = MyLocationStyle().showMyLocation(true).interval(3000)
                .myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
        val bar = findViewById<Toolbar>(R.id.toolbar)
        setToolBar(bar,"高德地图marker测试")
    }

    override fun initInjection() {

    }

    override fun getLayoutId() = R.layout.activity_amap_test

    override fun onSaveInstanceData(outState: Bundle?) {

    }



    val  list = arrayListOf<AMarker>()

    override fun searchMarkerData(): List<AMarker> {
        if (list.size == 0) {
            for (index in 0 until 200) {
                val localInfo = TestUtil.Latlng()
                list.add(AMarker(StringUtils.getUUID(), localInfo[0], localInfo[1]).apply {
                    baseInfo = "test${index}  --->${supportMapCluster()}---${null == bitmapDescriptor}"
                     abitmap?:getBitmapDescriptor(this)


                })
            }
            println("size====${list.size}")


        }
        return  list

    }

    override fun onFilterMarkerData(data: AMarker):Boolean{
        data.abitmap?:getBitmapDescriptor(data)

        return true
    }

    override fun getBitmapDescriptor(data: AMarker): BitmapDescriptor {


        return  personADescriptor?:BitmapDescriptorFactory.fromResource(R.drawable.person)
    }

    override fun startRequest() {

    }

    override fun loadImageView(path: String, context: Context, imageView: ImageView) {

    }

    override fun onOperation(postion: Int, iMarkerModel: AMarker) {

    }

    override fun onTuWen(postion: Int, iMarkerModel: AMarker) {

    }

    override fun onObjectImg(postion: Int, iMarkerModel: AMarker, view: View?) {

    }



    override fun onMapMarkerClick(postion: Int, data: AMarker?, marker: Marker) {
        println("onMapMarkerClick-->>>>>")
        super.onMapMarkerClick(postion, data, marker)
    }

    lateinit var  clusterManager : ClusterManager<AMarker>
    override fun initClusterManager(aMap: AMap) {
        super.initClusterManager(aMap)
        clusterManager = ClusterManager(this,aMap)
        clusterManager.setAnimation(true)

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
            aMapManager.cleanOverlayManagerMarkers()
        }
    }

    override fun addClusterManagerData(data: List<AMarker>) {
        super.addClusterManagerData(data)
        println("开始添加聚合数据------>>>>>>")
        data.forEach {
           it.abitmap = getBitmapDescriptor(it)

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

    override fun bindClusterManagerMapStatusChange(): AMap.OnCameraChangeListener? {
        return clusterManager
    }

    override fun onClusterMarkerClick(marker: Marker) {
        super.onClusterMarkerClick(marker)
        println("onClusterMarkerClick-->${marker.options.icon},${marker.options.icon == personADescriptor}")

        clusterManager.findClusterMarkerData(marker)?.let {
            initClickMarkerIcon(marker)
            showMapMarkerListInfo(0, arrayListOf<AMarker>().apply {
                add(it)
            },true)
        }

        clusterManager.findClusterMarkersData(marker)?.let {
            initClickMarkerIcon(marker)

        }

    }

    override fun onMapRefresh() {
       println("amap marker.size=${clusterManager.renderer.findClusterSingleMarkerDatas()?.size}  cluster=${clusterManager.renderer.findClusterMarkerDatas()?.size}")

    }
}