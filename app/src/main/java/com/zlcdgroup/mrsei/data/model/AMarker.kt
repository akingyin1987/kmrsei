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
import com.amap.api.maps.model.BitmapDescriptor
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.clustering.ClusterItem


/**
 * @ Description:
 * @author king
 * @ Date 2020/6/17 16:18
 * @version V1.0
 */
 class AMarker constructor(uuid:String,var alat:Double,var alng:Double): IMarker(uuid), ClusterItem {
    override fun getLat()= alat

    override fun getLng() = alng

    override fun isComplete() = false

    override fun getPosition(): LatLng {
       return  LatLng(alat,alng)
    }


   override fun getTitle()= titleStr


    private   var  snippetsJson = ""

    override fun getSnippet():String{
       return  snippetsJson
    }

   var   amarker : Marker? =null
    fun getMarker(): Marker? {
     return amarker
   }

    var  abitmap : BitmapDescriptor?= null
   override fun getBitmapDescriptor(): BitmapDescriptor? {

       return abitmap
    }
}