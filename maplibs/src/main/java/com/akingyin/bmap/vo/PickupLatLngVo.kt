/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.bmap.vo

import java.io.Serializable

/**
 * 坐标拾取参数
 * @ Description:
 * @author king
 * @ Date 2020/5/21 18:24
 * @version V1.0
 */
data class PickupLatLngVo (var  currentLat:Double=0.0
                           ,var currentLng:Double=0.0
                           ,var oldCurrentLat:Double=0.0
                           ,var oldCurrentLng:Double=0.0,
                            var  locationAddr:String=""
                           ,var onlysee:Boolean = false,
                           var  draggable:Boolean = true,
                           var  geoAdder:Boolean = false):Serializable