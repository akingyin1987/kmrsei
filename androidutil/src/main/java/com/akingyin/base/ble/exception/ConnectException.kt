/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.ble.exception

import android.bluetooth.BluetoothGatt

/**
 * @ Description:
 * @author king
 * @ Date 2020/9/7 11:43
 * @version V1.0
 */
class ConnectException (var bluetoothGatt: BluetoothGatt?,var gattStatus:Int) :BleException(ERROR_CODE_GATT,"Gatt Exception Occurred! "){
}