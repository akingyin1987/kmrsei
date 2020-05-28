/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.zlcdgroup.mrsei.ui

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.akingyin.base.utils.StringUtils
import com.akingyin.bmap.AbstractBaiduMapMarkersActivity

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

    override fun searchMarkerData(): List<BdModel> {
        val  list = arrayListOf<BdModel>()
      for (index in 0..100){
          list.add(BdModel(StringUtils.getUUID()).apply {
              baseInfo="test${index}"
          })
      }
      return  list
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

    override fun startRequest() {

    }
}