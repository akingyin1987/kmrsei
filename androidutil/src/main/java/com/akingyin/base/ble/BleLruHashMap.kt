/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.ble

import java.util.*
import kotlin.math.ceil

/**
 * @ Description:
 * @author king
 * @ Date 2020/9/1 16:19
 * @version V1.0
 */
class BleLruHashMap<K,V>(var  maxSize:Int) : LinkedHashMap<K,V>((ceil(maxSize / 0.75) + 1).toInt(), 0.75f, true) {

    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
        if (size > maxSize ) {
           eldest?.run {
               if(value is BleBluetooth){
                   (value as BleBluetooth).disconnect()
               }
           }
        }
        return size > maxSize
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for ((key, value) in entries) {
            sb.append(String.format("%s:%s ", key, value))
        }
        return sb.toString()
    }

}