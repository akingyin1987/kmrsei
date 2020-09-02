/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.ble

import android.annotation.TargetApi
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.os.Build
import android.os.Message
import com.akingyin.base.ble.callback.BleGattCallback

/**
 * @ Description:
 * @author king
 * @ Date 2020/9/1 16:22
 * @version V1.0
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class BleBluetooth (var bleDevice: BleDevice){

    @Synchronized
    fun disconnect() {

    }


    fun getDeviceKey(): String {
        return bleDevice.getKey()
    }

    @Synchronized
    @JvmOverloads
    fun connect(bleDevice: BleDevice,
                autoConnect: Boolean,
                callback: BleGattCallback,
                connectRetryCount: Int = 0): BluetoothGatt? {
     return null
    }

    @Synchronized
    fun destroy() {

    }
}