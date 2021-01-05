/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.bmap

import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import com.akingyin.base.SimpleActivity
import com.akingyin.base.dialog.MaterialDialogUtil
import com.akingyin.base.ext.click
import com.akingyin.base.ext.no
import com.akingyin.base.ext.yes
import com.akingyin.bmap.adapter.BaiduOffineListAdapter
import com.akingyin.map.R
import com.baidu.mapapi.map.offline.MKOLUpdateElement
import com.baidu.mapapi.map.offline.MKOfflineMap
import com.baidu.mapapi.map.offline.MKOfflineMapListener
import kotlinx.android.synthetic.main.activity_baidumap_offline.*

/**
 * 百度地图 离线下载
 * @ Description:
 * @author king
 * @ Date 2020/6/24 10:59
 * @version V1.0
 */
class BaiduMapOfflineActivity : SimpleActivity(), MKOfflineMapListener {


    override fun initInjection() {

    }

    override fun getLayoutId() = R.layout.activity_baidumap_offline

    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    private lateinit var   offineListAdapter: BaiduOffineListAdapter
    private lateinit var  mkOfflineMap:MKOfflineMap
    private lateinit var  bdLocationService: BDLocationService
    private lateinit var  myLocationListenner: BDLocationService.MyLocationListenner
    override fun initView() {
         setToolBar(toolbar,"百度地图离线下载")
         mkOfflineMap = MKOfflineMap()
         mkOfflineMap.init(this)
         recycler.itemAnimator = DefaultItemAnimator()
         offineListAdapter = BaiduOffineListAdapter()
         bdLocationService = BDLocationService.getLocationServer(this)
         recycler.adapter = offineListAdapter
         offineListAdapter.setNewInstance(mkOfflineMap.allUpdateInfo)
         offineListAdapter.addChildClickViewIds(R.id.download,R.id.remove)
         offineListAdapter.setOnItemChildClickListener { _, view, position ->
             val data  = offineListAdapter.getItem(position)
             when(view.id){
                 R.id.remove ->{
                     removeOfflineElement(data)
                 }
                 R.id.download -> {
                     downloadAndPaseOffline(data)
                 }
             }
         }
         myLocationListenner = BDLocationService.MyLocationListenner {
             val list = mkOfflineMap.searchCity(it.city)
             if(list.isNotEmpty()){
                 val mkolUpdateElement = MKOLUpdateElement().apply {
                     cityID = list[0].cityID
                     cityName = list[0].cityName
                     update = true
                 }
                 offineListAdapter.addOrUpdateElement(mkolUpdateElement)
             }
             bdLocationService.stop()
         }
         bdLocationService.registerListener(myLocationListenner)
        fab_loc.click {

            bdLocationService.start()
        }
    }

    private  fun   downloadAndPaseOffline(mkolUpdateElement: MKOLUpdateElement){
        if(mkolUpdateElement.ratio != 100){
            println("mkolUpdateElement.status=${mkolUpdateElement.status}")
            if(mkolUpdateElement.status == MKOLUpdateElement.DOWNLOADING){
                mkOfflineMap.pause(mkolUpdateElement.cityID)
            }else{
                mkOfflineMap.start(mkolUpdateElement.cityID)
            }
        }else{
            showTips("当前已是最新，无需更新！")
        }
    }

    private fun   removeOfflineElement(mkolUpdateElement: MKOLUpdateElement){
        MaterialDialogUtil.showConfigDialog(context = this,message = "确定要删除${mkolUpdateElement.cityName}地图离线数据?",
         positive = "删除",negative = "再看看"){
            if(it){
                mkOfflineMap.remove(mkolUpdateElement.cityID).yes {
                    showSucces("删除成功")
                    offineListAdapter.remove(mkolUpdateElement)
                }.no {
                    showError("删除失败")
                }
            }

        }
    }

    override fun startRequest() {

    }




    override fun onGetOfflineMapState(type:Int, state:Int) {
         when(type){
             MKOfflineMap.TYPE_DOWNLOAD_UPDATE -> {

               mkOfflineMap.getUpdateInfo(state)?.let {
                   offineListAdapter.updateElement(it)
               }
             }

             MKOfflineMap.TYPE_NEW_OFFLINE -> {

             }

             MKOfflineMap.TYPE_VER_UPDATE -> {

             }
         }
    }

    override fun onDestroy() {
        mkOfflineMap.destroy()
        bdLocationService.unregisterListener(myLocationListenner)
        bdLocationService.stop()
        super.onDestroy()
    }
}