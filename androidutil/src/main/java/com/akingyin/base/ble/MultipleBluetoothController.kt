/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.ble

import android.bluetooth.BluetoothDevice
import android.os.Build

import java.util.*
import kotlin.collections.HashMap

/**
 * @ Description:
 * @author king
 * @ Date 2020/9/2 12:34
 * @version V1.0
 */
class MultipleBluetoothController {


    private var bleLruHashMap: BleLruHashMap<String, BleBluetooth> = BleLruHashMap(BleManager.getInstance().getMaxConnectCount())
    private var bleTempHashMap: HashMap<String, BleBluetooth>  = HashMap()


    /**
     * 缓存连接中设备
     */
    @Synchronized
    fun buildConnectingBle(bleDevice: BleDevice): BleBluetooth {
        val bleBluetooth = BleBluetooth(bleDevice)
        if (!bleTempHashMap.containsKey(bleBluetooth.getDeviceKey())) {
            bleTempHashMap[bleBluetooth.getDeviceKey()] = bleBluetooth
        }
        return bleBluetooth
    }

    /**
     * 移除连接中的设备
     */
    @Synchronized
    fun removeConnectingBle(bleBluetooth: BleBluetooth?) {

        bleBluetooth?.run {
            bleTempHashMap.remove(getDeviceKey())
        }

    }


    /**
     * 添加已连接的蓝牙设备
     */
    @Synchronized
    fun addBleBluetooth(bleBluetooth: BleBluetooth) {
        if (!bleLruHashMap.containsKey(bleBluetooth.getDeviceKey())) {
            bleLruHashMap[bleBluetooth.getDeviceKey()] = bleBluetooth
        }
    }

    /**
     * 移除已连接的蓝牙 设备
     */
    @Synchronized
    fun removeBleBluetooth(bleBluetooth: BleBluetooth?) {
        if (bleBluetooth == null) {
            return
        }
        if (bleLruHashMap.containsKey(bleBluetooth.getDeviceKey())) {
            bleLruHashMap.remove(bleBluetooth.getDeviceKey())
        }
    }

    @Synchronized
    fun isContainDevice(bleDevice: BleDevice?): Boolean {
        return bleDevice != null && bleLruHashMap.containsKey(bleDevice.getKey())
    }

    @Synchronized
    fun isContainDevice(bluetoothDevice: BluetoothDevice): Boolean {
        return bleLruHashMap.containsKey(bluetoothDevice.name + bluetoothDevice.address)
    }

    @Synchronized
    fun getBleBluetooth(bleDevice: BleDevice): BleBluetooth? {
        if (bleLruHashMap.containsKey(bleDevice.getKey())) {
            return bleLruHashMap[bleDevice.getKey()]
        }
        return null
    }

    @Synchronized
    fun disconnect(bleDevice: BleDevice) {
        if (isContainDevice(bleDevice)) {
            getBleBluetooth(bleDevice)?.disconnect()
        }
    }

    @Synchronized
    fun disconnectAllDevice() {
        for ((_, value) in bleLruHashMap) {
            value.disconnect()
        }
        bleLruHashMap.clear()
    }

    @Synchronized
    fun destroy() {
        for ((_, value) in bleLruHashMap) {
            value.destroy()
        }
        bleLruHashMap.clear()
        for ((_, value) in bleTempHashMap) {
            value.destroy()
        }
        bleTempHashMap.clear()
    }

    @Synchronized
    fun getBleBluetoothList(): List<BleBluetooth> {
        return ArrayList(bleLruHashMap.values).sortedBy {
            it.getDeviceKey()
        }
    }

    @Synchronized
    fun getDeviceList(): List<BleDevice> {
        refreshConnectedDevice()
        return getBleBluetoothList().map {
            it.bleDevice
        }

    }

    fun refreshConnectedDevice() {
        val bluetoothList = getBleBluetoothList()
        var i = 0
        while ( i < bluetoothList.size) {
            val bleBluetooth = bluetoothList[i]
            if (!BleManager.getInstance().isConnected(bleBluetooth.bleDevice)) {
                removeBleBluetooth(bleBluetooth)
            }
            i++
        }
    }

}