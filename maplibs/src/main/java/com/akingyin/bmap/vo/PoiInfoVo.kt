package com.akingyin.bmap.vo

import com.baidu.mapapi.model.LatLng

/**
 * @ Description:
 * @author king
 * @ Date 2020/5/19 16:52
 * @version V1.0
 */
data class PoiInfoVo(
    var  name:String,
    var  uid:String,
    var  address:String,
    var  location:LatLng,
    var  mSelected:Boolean
)