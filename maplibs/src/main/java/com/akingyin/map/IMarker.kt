/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.map




/**
 * @ Description:
 * @author king
 * @ Date 2020/5/25 17:28
 * @version V1.0
 */
abstract  class  IMarker constructor(var uuid:String ) {


    /**
     * 当前地图使用 经度
     * @return
     */
     abstract  fun getLat(): Double

    /**
     * 当前地图使用 纬度
     * @return
     */
     abstract fun getLng(): Double



     var    appointSort : Int = 0



    /**
     * 定位对象基础图片路径
     * @return
     */
    var   markerDetaiImgPath:String?=null



    /**
     * 定位对像基础信息
     * @return
     */
   var  baseInfo: String = ""

    /**
     * marker 在地图上显示的说明
     * @return
     */
     var   titleStr: String?=null

    /**
     * 定位对象
     * @return
     */
     var  data: Any? = null

    /**
     * 同一个经纬度或聚合定位对象
     * @return
     */
    var   markers : List<IMarker>? = null


    /**
     * 顺序信息
     * @return
     */
   var sortInfo: String?=null


   /** 距离当前位置 */
   var  disFromPostion:Double?=null




    /**
     * 是否已完成
     * @return
     */
   abstract fun isComplete(): Boolean


}