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
import android.bluetooth.*
import android.os.*
import androidx.annotation.IntDef
import com.akingyin.base.ble.callback.*
import com.akingyin.base.ble.data.BleConnectStateParameter
import com.akingyin.base.ble.exception.ConnectException
import com.akingyin.base.ble.exception.OtherException
import com.akingyin.base.ble.exception.TimeoutException
import com.akingyin.base.ble.scan.BleConnector
import com.akingyin.base.ext.logDebug
import java.util.*

/**
 * @ Description:
 * @author king
 * @ Date 2020/9/1 16:22
 * @version V1.0
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class BleBluetooth(var bleDevice: BleDevice){
    private var bleGattCallback: BleGattCallback? = null
     var bleRssiCallback: BleRssiCallback? = null
     var bleMtuChangedCallback: BleMtuChangedCallback? = null
    private val bleNotifyCallbackHashMap: HashMap<String, BleNotifyCallback> = HashMap<String, BleNotifyCallback>()
    private val bleIndicateCallbackHashMap: HashMap<String, BleIndicateCallback> = HashMap<String, BleIndicateCallback>()
    private val bleWriteCallbackHashMap: HashMap<String, BleWriteCallback> = HashMap<String, BleWriteCallback>()
    private val bleReadCallbackHashMap: HashMap<String, BleReadCallback> = HashMap<String, BleReadCallback>()

    private var lastState: Int = LastState.CONNECT_IDLE
    private var isActiveDisconnect = false

     var bluetoothGatt: BluetoothGatt? = null
    private var mainHandler: MainHandler = MainHandler(Looper.getMainLooper())
    private var connectRetryCount = 0

    @Synchronized
    fun disconnect() {
        isActiveDisconnect = true
        disconnectGatt()
    }


    fun getDeviceKey(): String {
        return bleDevice.getKey()
    }

    @Synchronized
    fun addConnectGattCallback(callback: BleGattCallback) {
        bleGattCallback = callback
    }

    @Synchronized
    fun removeNotifyCallback(uuid: String) {
        if (bleNotifyCallbackHashMap.containsKey(uuid)) {
            bleNotifyCallbackHashMap.remove(uuid)
        }
    }
    private val coreGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            //连接状态发生改变
            bluetoothGatt = gatt
            mainHandler.removeMessages(BleMsg.MSG_CONNECT_OVER_TIME)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                "ble连接成功,通知gatt discoverServices，让onServicesDiscovered 显示所有services".logDebug(BleManager.TAG)
                val message = mainHandler.obtainMessage()
                message.what = BleMsg.MSG_DISCOVER_SERVICES
                mainHandler.sendMessageDelayed(message, 500)

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                "Ble连接被断开".logDebug(BleManager.TAG)
                if (lastState == LastState.CONNECT_CONNECTING) {
                    val message = mainHandler.obtainMessage()
                    message.what = BleMsg.MSG_CONNECT_FAIL
                    message.obj =  BleConnectStateParameter().apply {
                        this.status = status
                    }
                    mainHandler.sendMessage(message)
                } else if (lastState == LastState.CONNECT_CONNECTED) {
                    val message = mainHandler.obtainMessage()
                    message.what = BleMsg.MSG_DISCONNECTED
                    val para = BleConnectStateParameter().apply {
                        this.status = status
                        isActive = isActiveDisconnect
                    }
                    message.obj = para
                    mainHandler.sendMessage(message)
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            bluetoothGatt = gatt
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val message = mainHandler.obtainMessage()
                message.what = BleMsg.MSG_DISCOVER_SUCCESS
                message.obj =  BleConnectStateParameter().apply {
                    this.status = status
                }
                mainHandler.sendMessage(message)
            } else {
                val message = mainHandler.obtainMessage()
                message.what = BleMsg.MSG_DISCOVER_FAIL
                mainHandler.sendMessage(message)
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            super.onCharacteristicChanged(gatt, characteristic)
            bleNotifyCallbackHashMap.forEach{
                if(characteristic.uuid.toString() == it.value.key){
                    it.value.handler.sendMessage(it.value.handler.obtainMessage().apply {
                        what = BleMsg.MSG_CHA_NOTIFY_DATA_CHANGE
                        obj = it.value
                        data = Bundle().apply {
                            putByteArray(BleMsg.KEY_NOTIFY_BUNDLE_VALUE, characteristic.value)
                        }
                    })
                }
            }

            bleIndicateCallbackHashMap.forEach {
                if(characteristic.uuid.toString() == it.value.key){
                    it.value.handler.sendMessage(it.value.handler.obtainMessage().apply {
                        what = BleMsg.MSG_CHA_INDICATE_DATA_CHANGE
                        obj = it.value
                        data = Bundle().apply {
                            putByteArray(BleMsg.KEY_INDICATE_BUNDLE_VALUE, characteristic.value)
                        }
                    })
                }
            }


        }

        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            super.onDescriptorWrite(gatt, descriptor, status)



            bleNotifyCallbackHashMap.forEach{
                if(descriptor.characteristic.uuid.toString() == it.value.key){
                    it.value.handler.sendMessage(it.value.handler.obtainMessage().apply {
                        what = BleMsg.MSG_CHA_NOTIFY_RESULT
                        obj = it.value
                        data = Bundle().apply {
                            putByteArray(BleMsg.KEY_NOTIFY_BUNDLE_STATUS, descriptor.characteristic.value)
                        }
                    })
                }
            }

            bleIndicateCallbackHashMap.forEach {
                if(descriptor.characteristic.uuid.toString() == it.value.key){
                    it.value.handler.sendMessage(it.value.handler.obtainMessage().apply {
                        what = BleMsg.MSG_CHA_INDICATE_RESULT
                        obj = it.value
                        data = Bundle().apply {
                            putByteArray(BleMsg.KEY_INDICATE_BUNDLE_STATUS, descriptor.characteristic.value)
                        }
                    })
                }
            }

        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            bleWriteCallbackHashMap.forEach {
                if(characteristic.uuid.toString() == it.key){
                    it.value.handler.sendMessage(it.value.handler.obtainMessage().apply {
                        what = BleMsg.MSG_CHA_WRITE_RESULT
                        obj = it.value
                        data = Bundle().apply {
                            putInt(BleMsg.KEY_WRITE_BUNDLE_STATUS, status)
                            putByteArray(BleMsg.KEY_WRITE_BUNDLE_VALUE, characteristic.value)
                        }
                    })
                }
            }

        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicRead(gatt, characteristic, status)
            bleReadCallbackHashMap.forEach {
                if(it.key == characteristic.uuid.toString() ){
                    it.value.handler.sendMessage(it.value.handler.obtainMessage().apply {
                        what = BleMsg.MSG_CHA_READ_RESULT
                        obj = it.value
                        data = Bundle().apply {
                            putInt(BleMsg.KEY_READ_BUNDLE_STATUS, status)
                            putByteArray(BleMsg.KEY_READ_BUNDLE_VALUE, characteristic.value)
                        }
                    })
                }
            }

        }

        override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
            super.onReadRemoteRssi(gatt, rssi, status)
            bleRssiCallback?.run {
                handler.sendMessage(handler.obtainMessage().apply {
                    what = BleMsg.MSG_READ_RSSI_RESULT
                    obj = bleRssiCallback
                    data = Bundle().apply {
                        putInt(BleMsg.KEY_READ_RSSI_BUNDLE_STATUS, status)
                        putInt(BleMsg.KEY_READ_RSSI_BUNDLE_VALUE, rssi)
                    }
                })
            }

        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            bleMtuChangedCallback?.run {
                handler.sendMessage(handler.obtainMessage().apply {
                    what = BleMsg.MSG_SET_MTU_RESULT
                    obj = bleMtuChangedCallback
                    data = Bundle().apply {
                        putInt(BleMsg.KEY_SET_MTU_BUNDLE_STATUS, status)
                        putInt(BleMsg.KEY_SET_MTU_BUNDLE_VALUE, mtu)
                    }
                })
            }
        }
    }

    @Synchronized
    @JvmOverloads
    fun connect(bleDevice: BleDevice,
                autoConnect: Boolean,
                callback: BleGattCallback,
                connectRetryCount: Int = 0): BluetoothGatt? {
        this.connectRetryCount = connectRetryCount
        addConnectGattCallback(callback)

        lastState = LastState.CONNECT_CONNECTING

        bluetoothGatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bleDevice.mDevice?.connectGatt(BleManager.getInstance().contextWeakReference.get(),
                    autoConnect, coreGattCallback, BluetoothDevice.TRANSPORT_LE)
        } else {
            bleDevice.mDevice?.connectGatt(BleManager.getInstance().contextWeakReference.get(),
                    autoConnect, coreGattCallback)
        }
        bluetoothGatt?.let {
            bleGattCallback?.onStartConnect()
            mainHandler.sendMessageDelayed(mainHandler.obtainMessage().apply {
                what = BleMsg.MSG_CONNECT_OVER_TIME
            }, BleManager.getInstance().connectOverTime)
        }
        if(null == bluetoothGatt){
            disconnectGatt()
            refreshDeviceCache()
            closeBluetoothGatt()
            lastState = LastState.CONNECT_FAILURE
            BleManager.getInstance().multipleBluetoothController?.removeConnectingBle(this@BleBluetooth)
            bleGattCallback?.onConnectFail(bleDevice, OtherException("GATT connect exception occurred!"))

        }
        return bluetoothGatt
    }

    @Synchronized
    fun removeConnectGattCallback() {
        bleGattCallback = null
    }
    @Synchronized
    fun destroy() {
        lastState = LastState.CONNECT_IDLE
        disconnectGatt()
        refreshDeviceCache()
        closeBluetoothGatt()
        removeConnectGattCallback()
        removeRssiCallback()
        removeMtuChangedCallback()
        clearCharacterCallback()
        mainHandler.removeCallbacksAndMessages(null)
    }

    @Synchronized
    fun addNotifyCallback(uuid: String, bleNotifyCallback: BleNotifyCallback) {
        bleNotifyCallbackHashMap[uuid] = bleNotifyCallback
    }

    @Synchronized
    fun addIndicateCallback(uuid: String, bleIndicateCallback: BleIndicateCallback) {
        bleIndicateCallbackHashMap[uuid] = bleIndicateCallback
    }

    @Synchronized
    fun addWriteCallback(uuid: String, bleWriteCallback: BleWriteCallback) {
        bleWriteCallbackHashMap[uuid] = bleWriteCallback
    }

    @Synchronized
    fun addReadCallback(uuid: String, bleReadCallback: BleReadCallback) {
        bleReadCallbackHashMap[uuid] = bleReadCallback
    }
    /**
     * 状态
     */
    @IntDef(value = [LastState.CONNECT_IDLE, LastState.CONNECT_CONNECTED, LastState.CONNECT_CONNECTING,
        LastState.CONNECT_DISCONNECT, LastState.CONNECT_FAILURE])
    @Retention(AnnotationRetention.SOURCE)
    annotation class LastState {
        companion object {
            const val  CONNECT_IDLE =  0
            const val CONNECT_CONNECTING = 1
            const val CONNECT_CONNECTED = 2
            const val CONNECT_FAILURE = 3
            const val CONNECT_DISCONNECT = 4

        }
    }

    inner  class MainHandler internal constructor(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                BleMsg.MSG_CONNECT_FAIL -> {
                    disconnectGatt()
                    refreshDeviceCache()
                    closeBluetoothGatt()
                    if (connectRetryCount < BleManager.getInstance().reConnectCount) {

                        val message: Message = mainHandler.obtainMessage()
                        message.what = BleMsg.MSG_RECONNECT
                        mainHandler.sendMessageDelayed(message, BleManager.getInstance().reConnectInterval)
                    } else {
                        lastState = LastState.CONNECT_FAILURE
                        BleManager.getInstance().multipleBluetoothController?.removeConnectingBle(this@BleBluetooth)
                        val para: BleConnectStateParameter = msg.obj as BleConnectStateParameter
                        bleGattCallback?.onConnectFail(bleDevice, ConnectException(bluetoothGatt, para.status))

                    }
                }
                BleMsg.MSG_DISCONNECTED -> {
                    lastState = LastState.CONNECT_DISCONNECT
                    BleManager.getInstance().multipleBluetoothController?.removeBleBluetooth(this@BleBluetooth)
                    disconnect()
                    refreshDeviceCache()
                    closeBluetoothGatt()
                    removeRssiCallback()
                    removeMtuChangedCallback()
                    clearCharacterCallback()
                    mainHandler.removeCallbacksAndMessages(null)
                    val para: BleConnectStateParameter = msg.obj as BleConnectStateParameter
                    bleGattCallback?.onDisConnected(para.isActive, bleDevice, bluetoothGatt, para.status)

                }
                BleMsg.MSG_RECONNECT -> {
                    bleGattCallback?.let {
                        connect(bleDevice, false, it, connectRetryCount)
                    }

                }
                BleMsg.MSG_CONNECT_OVER_TIME -> {
                    disconnectGatt()
                    refreshDeviceCache()
                    closeBluetoothGatt()
                    lastState = LastState.CONNECT_FAILURE
                    BleManager.getInstance().multipleBluetoothController?.removeConnectingBle(this@BleBluetooth)
                    bleGattCallback?.onConnectFail(bleDevice, TimeoutException())

                }
                BleMsg.MSG_DISCOVER_SERVICES -> {
                    bluetoothGatt?.discoverServices()?.let {
                        if (!it) {
                            val message: Message = mainHandler.obtainMessage()
                            message.what = BleMsg.MSG_DISCOVER_FAIL
                            mainHandler.sendMessage(message)
                        }
                    } ?: mainHandler.sendMessage(mainHandler.obtainMessage().apply {
                        what = BleMsg.MSG_DISCOVER_FAIL
                    })

                }
                BleMsg.MSG_DISCOVER_FAIL -> {
                    disconnectGatt()
                    refreshDeviceCache()
                    closeBluetoothGatt()
                    lastState = LastState.CONNECT_FAILURE
                    BleManager.getInstance().multipleBluetoothController?.removeConnectingBle(this@BleBluetooth)
                    bleGattCallback?.onConnectFail(bleDevice, OtherException("GATT discover services exception occurred!"))

                }
                BleMsg.MSG_DISCOVER_SUCCESS -> {
                    lastState = LastState.CONNECT_CONNECTED
                    isActiveDisconnect = false
                    BleManager.getInstance().multipleBluetoothController?.removeConnectingBle(this@BleBluetooth)
                    BleManager.getInstance().multipleBluetoothController?.addBleBluetooth(this@BleBluetooth)
                    val para: BleConnectStateParameter = msg.obj as BleConnectStateParameter
                    bleGattCallback?.onConnectSuccess(bleDevice, bluetoothGatt, para.status)
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    @Synchronized
    private fun disconnectGatt() {
        bluetoothGatt?.disconnect()
    }

    @Synchronized
    private fun refreshDeviceCache() {
        try {
            BluetoothGatt::class.java.getMethod("refresh").run {
                bluetoothGatt?.let {
                    invoke(it)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Synchronized
    private fun closeBluetoothGatt() {
        bluetoothGatt?.close()
    }

    @Synchronized
    fun removeMtuChangedCallback() {
        bleMtuChangedCallback = null
    }

    @Synchronized
    fun removeRssiCallback() {
        bleRssiCallback = null
    }

    @Synchronized
    fun clearCharacterCallback() {
        bleNotifyCallbackHashMap.clear()
        bleIndicateCallbackHashMap.clear()
        bleWriteCallbackHashMap.clear()
        bleReadCallbackHashMap.clear()
    }
    fun newBleConnector(): BleConnector {
        return BleConnector(this)
    }
}