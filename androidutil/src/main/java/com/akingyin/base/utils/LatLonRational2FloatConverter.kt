/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.utils






/**
 * DMS 转换成 DD
 * 度分秒 格式 的经纬度 转换成 double类型的经纬度
 * @ Description:
 * @author king
 * @ Date 2020/8/6 16:07
 * @version V1.0
 */
object LatLonRational2FloatConverter {


    /**
     * @param rationalString 度分秒格式的经纬度字符串,形如: 114/1,23/1,538547/10000 或 30/1,28/1,432120/10000
     * @param ref 东西经 或 南北纬 的标记 S南纬 W西经
     * @return double格式的 经纬度
     */
    fun  convertRationalLatLonToFloat(rationalString:String,ref:String):Double{
        try {
            val parts = rationalString.split(",")
            var pair = parts[0].split("/")
            val degrees = pair[0].trim().toDouble()/pair[1].trim().toDouble()

            pair = parts[1].split("/")
            val minutes =pair[0].trim().toDouble()/pair[1].trim().toDouble()

            pair = parts[2].split("/")
            val seconds = pair[0].trim().toDouble()/pair[1].trim().toDouble()
            val result = degrees + minutes / 60.0 + seconds / 3600.0
            if (("S" == ref || "W"==ref)) {
                return result * -1.0
            }
            return  result
        }catch (e : Exception){
            e.printStackTrace()
            return 0.0
        }
    }

    /**
     * 将gps的经纬度变成度分秒
     */
    fun degressToString(digitalDegree: Double): String {
        val num = 60.0
        val degree = digitalDegree.toInt()
        val tmp = (digitalDegree - degree) * num
        val minute = tmp.toInt()
        val second = (10000 * (tmp - minute) * num).toInt()
        return "$degree/1,$minute/1,$second/10000"
    }
}