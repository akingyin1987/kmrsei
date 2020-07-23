/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.channel

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/23 11:58
 * @version V1.0
 */
class Bus<T>(val event:T,val tag:String ="") {
    override fun toString(): String {
        return "event = $event, tag = $tag"
    }

}