/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.ble.exception

import java.io.Serializable

/**
 * @ Description:
 * @author king
 * @ Date 2020/9/3 14:38
 * @version V1.0
 */
abstract class BleException( code:Int=ERROR_CODE_OTHER,  description:String="") : Serializable{


    private val serialVersionUID = 8004414918500865564L



    private var code = 0
    private var description: String=""



    open fun getCode(): Int {
        return code
    }

    open fun setCode(code: Int): BleException {
        this.code = code
        return this
    }

    open fun getDescription(): String {
        return description
    }

    open fun setDescription(description: String): BleException {
        this.description = description
        return this
    }

    override fun toString(): String {
        return "BleException { " +
                "code=" + code +
                ", description='" + description + '\'' +
                '}'
    }
    companion object{
        const val ERROR_CODE_TIMEOUT = 100
        const val ERROR_CODE_GATT = 101
        const val ERROR_CODE_OTHER = 102
    }
}