/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.ble.exception

/**
 * @ Description:
 * @author king
 * @ Date 2020/9/3 14:39
 * @version V1.0
 */
class GattException( gattStatus:Int ) : BleException(code = ERROR_CODE_GATT,description = "Gatt Exception Occurred! "){
}