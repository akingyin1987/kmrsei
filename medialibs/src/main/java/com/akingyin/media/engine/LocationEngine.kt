/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.engine



/**
 * @ Description:
 * @author king
 * @ Date 2020/7/29 10:18
 * @version V1.0
 */
interface LocationEngine {

    /**
     * 获取当前最新位置
     */
    fun   getNewLocation(locType:String,locLat:Double?=null,locLng:Double?=null,call:(lat:Double,lng:Double,addr:String)->Unit)

    /**
     * 通过位置获取定位图片
     */
    fun   getLocationImageUrl(lat: Double,lng: Double,locType: String,localImagePath:String?=null):String

    /**
     * 通过位置获取地址
     */
    fun   getLocationAddr(lat: Double,lng: Double,locType: String):String
}