/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.amap

import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import com.akingyin.amap.adapter.AmapOffineListAdapter
import com.akingyin.base.SimpleActivity
import com.akingyin.base.dialog.MaterialDialogUtil
import com.akingyin.base.ext.click
import com.akingyin.map.R
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.offlinemap.OfflineMapManager
import com.amap.api.maps.offlinemap.OfflineMapProvince
import com.amap.api.maps.offlinemap.OfflineMapStatus
import kotlinx.android.synthetic.main.activity_baidumap_offline.*

/**
 * @ Description:
 * @author king
 * @ Date 2020/6/24 16:40
 * @version V1.0
 */
class AMapOfflineActivity : SimpleActivity(),OfflineMapManager.OfflineMapDownloadListener {

    override fun initInjection() {

    }

    override fun getLayoutId() = R.layout.activity_baidumap_offline

    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    private lateinit var  offineListAdapter: AmapOffineListAdapter
    private lateinit var  aMapLocationListener: AMapLocationListener
    private lateinit var  aLocationService: ALocationService
    private lateinit var  offlineMapManager: OfflineMapManager
    override fun initView() {
        setToolBar(toolbar,"高德地图离线下载")
        recycler.itemAnimator = DefaultItemAnimator()
        offineListAdapter = AmapOffineListAdapter()
        recycler.adapter = offineListAdapter
        offlineMapManager = OfflineMapManager(this,this)
        offineListAdapter.setNewInstance(offlineMapManager.offlineMapProvinceList)

        offineListAdapter.addChildClickViewIds(R.id.download,R.id.remove)
        offineListAdapter.setOnItemChildClickListener   { _, view, postion ->
            val  data = offineListAdapter.getItem(postion)
            when(view.id){
                R.id.download -> {
                    downloadAndPaseOffline(data)
                }

                R.id.remove -> {
                    removeOfflineElement(data)
                }
            }
        }
        aLocationService = ALocationService.getLocationServer(this)
        aMapLocationListener =  AMapLocationListener{
           it?.let {
               location ->
               if(location.errorCode == AMapLocation.LOCATION_SUCCESS){
                   if(location.city.isNotEmpty()){
                       offlineMapManager.getItemByProvinceName(location.city)?.let {
                           offlineMapCity ->
                           offineListAdapter.addOrUpdateElement(offlineMapCity,true)
                       }

                   }
                   aLocationService.stop()
               }
           }
       }
       aLocationService.registerListener(aMapLocationListener)
       fab_loc.click {
           aLocationService.start()
       }
    }

    override fun startRequest() {

    }
    private  fun   downloadAndPaseOffline(offlineMapCity: OfflineMapProvince){
        if(offlineMapCity.getcompleteCode() != 100){

            if(offlineMapCity.state == OfflineMapStatus.LOADING){
                offlineMapManager.stop()
            }else{
                offlineMapManager.downloadByProvinceName(offlineMapCity.provinceName)
            }
        }else{
            showTips("当前已是最新，无需更新！")
        }
    }

    private fun   removeOfflineElement(mkolUpdateElement: OfflineMapProvince){
        MaterialDialogUtil.showConfigDialog(context = this,message = "确定要删除${mkolUpdateElement.provinceName}地图离线数据?",
                positive = "删除",negative = "再看看"){
            if(it){
                offlineMapManager.remove(mkolUpdateElement.provinceName)
                showSucces("删除成功")
                offineListAdapter.remove(mkolUpdateElement)
            }

        }
    }
    override fun onDownload(status: Int, completeCode: Int, downName: String?) {
        println("status=${status},completecode=${completeCode},downName=${downName}")
        downName?.let {
            offlineMapManager.getItemByProvinceName(it)?.let {
                offlineMapCity ->
                offlineMapCity.setCompleteCode(completeCode)
                offineListAdapter.updateElement(offlineMapCity)
            }
        }

    }

    override fun onCheckUpdate(hasNew: Boolean, name: String?) {

    }

    override fun onRemove(success: Boolean, name: String?, describe: String?) {

    }

    override fun onDestroy() {
        aLocationService.unregisterListener(aMapLocationListener)
        aLocationService.stop()
        offlineMapManager.destroy()
        super.onDestroy()
    }
}