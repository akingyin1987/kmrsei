/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.zlcdgroup.mrsei.data.model

import com.akingyin.map.IMarker
import com.akingyin.map.TestUtil
import com.baidu.mapapi.clusterutil.clustering.ClusterItem
import com.baidu.mapapi.map.BitmapDescriptor
import com.baidu.mapapi.model.LatLng


/**
 * @ Description:
 * @author king
 * @ Date 2020/5/28 11:32
 * @version V1.0
 */
class BdModel(uuid: String,var bdlat:Double,var bdlng:Double ) : IMarker(uuid), ClusterItem {


    override fun getLat() = bdlat


    override fun getLng() =  bdlng

    override fun isComplete()= false

    override fun getPosition(): LatLng {
      return LatLng(getLat(),getLng())
    }

    var   bitmap: BitmapDescriptor?=null

    override fun getBitmapDescriptor(): BitmapDescriptor? {
        println("bitmap=${bitmap == null}")
        return  bitmap
    }
}