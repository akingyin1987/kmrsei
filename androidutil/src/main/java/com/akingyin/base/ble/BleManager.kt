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
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import com.akingyin.base.ble.callback.BleGattCallback
import com.akingyin.base.ble.callback.BleIndicateCallback
import com.akingyin.base.ble.callback.BleNotifyCallback
import com.akingyin.base.ble.callback.BleScanCallback
import com.akingyin.base.ble.exception.OtherException
import com.akingyin.base.ble.scan.BleScanRuleConfig
import com.akingyin.base.ble.scan.BleScanner
import com.akingyin.base.ext.logDebug
import com.akingyin.base.ext.logError
import java.lang.ref.WeakReference
import java.util.*

/**
 * @ Description:
 * @author king
 * @ Date 2020/9/1 16:02
 * @version V1.0
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class BleManager private constructor() {

    lateinit var contextWeakReference: WeakReference<Application>

    /** 最大连接数 */
    private var maxConnectCount = DEFAULT_MAX_MULTIPLE_DEVICE

    /** 操作超时 */
    var operateTimeout = DEFAULT_OPERATE_TIME

    /** 重试连接数 */
    var reConnectCount = DEFAULT_CONNECT_RETRY_COUNT
    var reConnectInterval = DEFAULT_CONNECT_RETRY_INTERVAL.toLong()
    var splitWriteNum = DEFAULT_WRITE_DATA_SPLIT_COUNT

    /** 连接超时时间 */
    var connectOverTime = DEFAULT_CONNECT_OVER_TIME.toLong()
    var multipleBluetoothController: MultipleBluetoothController? = null
    private var bluetoothManager: BluetoothManager? = null
    var bluetoothAdapter: BluetoothAdapter? = null
    private var bleScanRuleConfig: BleScanRuleConfig = BleScanRuleConfig()

    /**
     * 初始华应用
     */
    fun initBleManager(application: Application) {
        contextWeakReference = WeakReference(application)
        if (isSupportBle()) {
            bluetoothManager = application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            multipleBluetoothController = MultipleBluetoothController()
        }
    }

    fun isSupportBle(): Boolean {
        return contextWeakReference.get()?.applicationContext?.packageManager?.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
            ?: false
    }

    fun initScanRule(config: BleScanRuleConfig) {
        this.bleScanRuleConfig = config
    }

    fun getMaxConnectCount(): Int {
        return maxConnectCount
    }

    /**
     * 关闭蓝牙
     */
    fun disableBluetooth() {
        bluetoothAdapter?.let {
            if (it.isEnabled) {
                it.disable()
            }
        }
    }

    /**
     * 蓝牙是否可用
     */
    fun isBlueEnable(): Boolean {
        return bluetoothAdapter != null && bluetoothAdapter?.isEnabled ?: false
    }

    /**
     * 搜索蓝牙设备
     */
    fun scan(callback: BleScanCallback) {

        if (!isBlueEnable()) {
            callback.onScanStarted(false)
            return
        }
        val serviceUuids: Array<UUID> = bleScanRuleConfig.mServiceUuids
        val deviceNames: Array<String> = bleScanRuleConfig.mDeviceNames
        val deviceMac: String = bleScanRuleConfig.mDeviceMac
        val fuzzy: Boolean = bleScanRuleConfig.mFuzzy
        val timeOut: Long = bleScanRuleConfig.mScanTimeOut
        BleScanner.getInstance().scan(serviceUuids, deviceNames, deviceMac, fuzzy, timeOut, callback)
    }

    /**
     * 连接设备
     *
     * @param bleDevice
     * @param bleGattCallback
     * @return
     */
    fun connect(bleDevice: BleDevice, bleGattCallback: BleGattCallback): BluetoothGatt? {

        if (!isBlueEnable()) {
            ("Bluetooth not enable!").logDebug(TAG)
            bleGattCallback.onConnectFail(bleDevice, OtherException("Bluetooth not enable!"))
            return null
        }
        if (Looper.myLooper() == null || Looper.myLooper() != Looper.getMainLooper()) {
            ("Be careful: currentThread is not MainThread!").logError()
        }
        bleDevice.mDevice?.let {

           return multipleBluetoothController?.buildConnectingBle(bleDevice)?.connect(bleDevice,bleScanRuleConfig.mAutoConnect,bleGattCallback)
        }?:bleGattCallback.onConnectFail(bleDevice, OtherException("Not Found Device Exception Occurred!"))


        return null
    }

    /**
     * connect a device through its mac without scan,whether or not it has been connected
     *
     * @param mac
     * @param bleGattCallback
     * @return
     */
    fun connect(mac: String, bleGattCallback: BleGattCallback): BluetoothGatt? {
        return bluetoothAdapter?.getRemoteDevice(mac)?.let {
            connect(BleDevice(it), bleGattCallback)
        }

    }

    /**
     * 获取连接状态
     * @param bleDevice
     * @return State of the profile connection. One of
     * [BluetoothProfile.STATE_CONNECTED],
     * [BluetoothProfile.STATE_CONNECTING],
     * [BluetoothProfile.STATE_DISCONNECTED],
     * [BluetoothProfile.STATE_DISCONNECTING]
     */
    fun getConnectState(bleDevice: BleDevice): Int {
        return bleDevice.mDevice?.let {
            bluetoothManager?.getConnectionState(it, BluetoothProfile.GATT)
                ?: BluetoothProfile.STATE_DISCONNECTED
        } ?: BluetoothProfile.STATE_DISCONNECTED

    }

    fun getAllConnectedDevice(): List<BleDevice> {
        return multipleBluetoothController?.getDeviceList() ?: listOf()

    }

    fun isConnected(bleDevice: BleDevice): Boolean {
        return getConnectState(bleDevice) == BluetoothProfile.STATE_CONNECTED
    }

    fun isConnected(mac: String): Boolean {
        val list: List<BleDevice> = getAllConnectedDevice()
        for (bleDevice in list) {
            if (bleDevice.getMac() == mac) {
                return true
            }
        }
        return false
    }

    fun notify(bleDevice: BleDevice,
               uuid_service: String,
               uuid_notify: String,
               useCharacteristicDescriptor: Boolean = false,
               callback: BleNotifyCallback) {
        multipleBluetoothController?.let {
            it.getBleBluetooth(bleDevice)?.newBleConnector()?.withUUIDString(uuid_service, uuid_notify)?.enableCharacteristicNotify(callback, uuid_notify, useCharacteristicDescriptor)
                ?: callback.onNotifyFailure(OtherException("This device not connect!"))
        }

    }

    @JvmOverloads
    fun indicate(bleDevice: BleDevice,
                 uuid_service: String,
                 uuid_indicate: String,
                 useCharacteristicDescriptor: Boolean = false,
                 callback: BleIndicateCallback) {
        val bleBluetooth = multipleBluetoothController?.getBleBluetooth(bleDevice)
        if (bleBluetooth == null) {
            callback.onIndicateFailure(OtherException("This device not connect!"))
        } else {
            bleBluetooth.newBleConnector()
                    .withUUIDString(uuid_service, uuid_indicate)
                    .enableCharacteristicIndicate(callback, uuid_indicate, useCharacteristicDescriptor)
        }
    }

    @JvmOverloads
    fun stopNotify(bleDevice: BleDevice,
                   uuid_service: String,
                   uuid_notify: String,
                   useCharacteristicDescriptor: Boolean = false): Boolean {
        val bleBluetooth = multipleBluetoothController?.getBleBluetooth(bleDevice)
            ?: return false
        val success: Boolean = bleBluetooth.newBleConnector()
                .withUUIDString(uuid_service, uuid_notify)
                .disableCharacteristicNotify(useCharacteristicDescriptor)
        if (success) {
            bleBluetooth.removeNotifyCallback(uuid_notify)
        }
        return success
    }

    fun cancelScan() {
        BleScanner.getInstance().stopLeScan()
    }
    fun disconnect(bleDevice: BleDevice) {
        multipleBluetoothController?.disconnect(bleDevice)
    }

    fun disconnectAllDevice() {
        multipleBluetoothController?.disconnectAllDevice()
    }

    fun destroy() {
        multipleBluetoothController?.destroy()
    }
    companion object {
        const val DEFAULT_SCAN_TIME = 10000
        private const val DEFAULT_MAX_MULTIPLE_DEVICE = 7
        private const val DEFAULT_OPERATE_TIME = 5000L
        private const val DEFAULT_CONNECT_RETRY_COUNT = 0
        private const val DEFAULT_CONNECT_RETRY_INTERVAL = 5000
        private const val DEFAULT_MTU = 23
        private const val DEFAULT_MAX_MTU = 512
        private const val DEFAULT_WRITE_DATA_SPLIT_COUNT = 20
        private const val DEFAULT_CONNECT_OVER_TIME = 10000

        const val TAG = "BLE-Manager"

        private val bleManager = BleManager()

        @JvmStatic
        fun getInstance(): BleManager {
            return bleManager
        }
    }
}