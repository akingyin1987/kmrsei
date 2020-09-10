/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.ble.scan

import android.annotation.TargetApi
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.akingyin.base.ble.BleBluetooth
import com.akingyin.base.ble.BleManager
import com.akingyin.base.ble.BleMsg
import com.akingyin.base.ble.callback.*
import com.akingyin.base.ble.exception.GattException
import com.akingyin.base.ble.exception.OtherException
import com.akingyin.base.ble.exception.TimeoutException
import java.util.*


/**
 * @ Description:
 * @author king
 * @ Date 2020/9/3 14:24
 * @version V1.0
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class BleConnector constructor(var bleBluetooth: BleBluetooth) {

    private var mBluetoothGatt = bleBluetooth.bluetoothGatt

    /** Gatt 协议服务 ，每个服务有个UUID 区分*/
    private var mGattService: BluetoothGattService? = null
    /** 一个服务下面的某个特征值，读写订阅都是通过此特征值交互。  通过UUID 区分*/
    private var mCharacteristic: BluetoothGattCharacteristic? = null
    private var mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                BleMsg.MSG_CHA_NOTIFY_START -> {
                    val notifyCallback: BleNotifyCallback = msg.obj as BleNotifyCallback
                    notifyCallback.onNotifyFailure(TimeoutException())
                }
                BleMsg.MSG_CHA_NOTIFY_RESULT -> {
                    notifyMsgInit()
                    val notifyCallback: BleNotifyCallback = msg.obj as BleNotifyCallback
                    val bundle = msg.data
                    val status = bundle.getInt(BleMsg.KEY_NOTIFY_BUNDLE_STATUS)
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        notifyCallback.onNotifySuccess()
                    } else {
                        notifyCallback.onNotifyFailure(GattException(status))
                    }
                }
                BleMsg.MSG_CHA_NOTIFY_DATA_CHANGE -> {
                    val notifyCallback: BleNotifyCallback = msg.obj as BleNotifyCallback
                    val bundle = msg.data
                    val value = bundle.getByteArray(BleMsg.KEY_NOTIFY_BUNDLE_VALUE)
                    notifyCallback.onCharacteristicChanged(value ?: byteArrayOf())
                }
                BleMsg.MSG_CHA_INDICATE_START -> {
                    val indicateCallback: BleIndicateCallback = msg.obj as BleIndicateCallback
                    indicateCallback.onIndicateFailure(TimeoutException())
                }
                BleMsg.MSG_CHA_INDICATE_RESULT -> {
                    indicateMsgInit()
                    val indicateCallback: BleIndicateCallback = msg.obj as BleIndicateCallback
                    val bundle = msg.data
                    val status = bundle.getInt(BleMsg.KEY_INDICATE_BUNDLE_STATUS)
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        indicateCallback.onIndicateSuccess()
                    } else {
                        indicateCallback.onIndicateFailure(GattException(status))
                    }
                }
                BleMsg.MSG_CHA_INDICATE_DATA_CHANGE -> {
                    val indicateCallback: BleIndicateCallback = msg.obj as BleIndicateCallback
                    val bundle = msg.data
                    val value = bundle.getByteArray(BleMsg.KEY_INDICATE_BUNDLE_VALUE)
                    indicateCallback.onCharacteristicChanged(value ?: byteArrayOf())
                }
                BleMsg.MSG_CHA_WRITE_START -> {
                    val writeCallback: BleWriteCallback = msg.obj as BleWriteCallback
                    writeCallback.onWriteFailure(TimeoutException())
                }
                BleMsg.MSG_CHA_WRITE_RESULT -> {
                    writeMsgInit()
                    val writeCallback: BleWriteCallback = msg.obj as BleWriteCallback
                    val bundle = msg.data
                    val status = bundle.getInt(BleMsg.KEY_WRITE_BUNDLE_STATUS)
                    val value = bundle.getByteArray(BleMsg.KEY_WRITE_BUNDLE_VALUE)
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        writeCallback.onWriteSuccess(BleWriteState.DATA_WRITE_SINGLE, BleWriteState.DATA_WRITE_SINGLE, value
                            ?: byteArrayOf())
                    } else {
                        writeCallback.onWriteFailure(GattException(status))
                    }
                }
                BleMsg.MSG_CHA_READ_START -> {
                    val readCallback: BleReadCallback = msg.obj as BleReadCallback
                    readCallback.onReadFailure(TimeoutException())
                }
                BleMsg.MSG_CHA_READ_RESULT -> {
                    readMsgInit()
                    val readCallback: BleReadCallback = msg.obj as BleReadCallback
                    val bundle = msg.data
                    val status = bundle.getInt(BleMsg.KEY_READ_BUNDLE_STATUS)
                    val value = bundle.getByteArray(BleMsg.KEY_READ_BUNDLE_VALUE)
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        readCallback.onReadSuccess(value ?: byteArrayOf())
                    } else {
                        readCallback.onReadFailure(GattException(status))
                    }
                }
                BleMsg.MSG_READ_RSSI_START -> {
                    val rssiCallback: BleRssiCallback = msg.obj as BleRssiCallback
                    rssiCallback.onRssiFailure(TimeoutException())
                }
                BleMsg.MSG_READ_RSSI_RESULT -> {
                    rssiMsgInit()
                    val rssiCallback: BleRssiCallback = msg.obj as BleRssiCallback
                    val bundle = msg.data
                    val status = bundle.getInt(BleMsg.KEY_READ_RSSI_BUNDLE_STATUS)
                    val value = bundle.getInt(BleMsg.KEY_READ_RSSI_BUNDLE_VALUE)
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        rssiCallback.onRssiSuccess(value)
                    } else {
                        rssiCallback.onRssiFailure(GattException(status))
                    }
                }
                BleMsg.MSG_SET_MTU_START -> {
                    val mtuChangedCallback: BleMtuChangedCallback = msg.obj as BleMtuChangedCallback
                    mtuChangedCallback.onSetMTUFailure(TimeoutException())
                }
                BleMsg.MSG_SET_MTU_RESULT -> {
                    mtuChangedMsgInit()
                    val mtuChangedCallback: BleMtuChangedCallback = msg.obj as BleMtuChangedCallback
                    val bundle = msg.data
                    val status = bundle.getInt(BleMsg.KEY_SET_MTU_BUNDLE_STATUS)
                    val value = bundle.getInt(BleMsg.KEY_SET_MTU_BUNDLE_VALUE)
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        mtuChangedCallback.onMtuChanged(value)
                    } else {
                        mtuChangedCallback.onSetMTUFailure(GattException(status))
                    }
                }
            }
        }
    }

    /**
     * 通过UUID 获取蓝牙连接
     */
    private fun withUUID(serviceUUID: UUID, characteristicUUID: UUID): BleConnector {
        mBluetoothGatt?.let {
            mGattService = it.getService(serviceUUID).also { server ->
               mCharacteristic =  server.getCharacteristic(characteristicUUID)
            }
        }

        return this
    }

    fun withUUIDString(serviceUUID: String, characteristicUUID: String): BleConnector {
        return withUUID(formUUID(serviceUUID), formUUID(characteristicUUID))
    }

    private fun formUUID(uuid: String): UUID {
        return  UUID.fromString(uuid)
    }


    /*------------------------------- main operation ----------------------------------- */
    /**
     * 打开通知
     * notify
     */
    fun enableCharacteristicNotify(bleNotifyCallback: BleNotifyCallback?, uuid_notify: String,
                                   userCharacteristicDescriptor: Boolean) {
        mCharacteristic?.let {
            if(it.properties or BluetoothGattCharacteristic.PROPERTY_NOTIFY>0){
                handleCharacteristicNotifyCallback(bleNotifyCallback, uuid_notify)
                setCharacteristicNotification(mBluetoothGatt, mCharacteristic, userCharacteristicDescriptor, true, bleNotifyCallback)
            }
        }?: bleNotifyCallback?.onNotifyFailure(OtherException("this characteristic not support notify!"))

    }

    private fun handleCharacteristicNotifyCallback(bleNotifyCallback: BleNotifyCallback?,
                                                   uuid_notify: String) {
        if (bleNotifyCallback != null) {
            notifyMsgInit()
            bleNotifyCallback.key = uuid_notify
            bleNotifyCallback.handler = mHandler
            bleBluetooth.addNotifyCallback(uuid_notify, bleNotifyCallback)
            mHandler.sendMessageDelayed(
                    mHandler.obtainMessage(BleMsg.MSG_CHA_NOTIFY_START, bleNotifyCallback),
                    BleManager.getInstance().operateTimeout)
        }
    }


    /**
     * notify setting
     */
    private fun setCharacteristicNotification(gatt: BluetoothGatt?,
                                              characteristic: BluetoothGattCharacteristic?,
                                              useCharacteristicDescriptor: Boolean,
                                              enable: Boolean,
                                              bleNotifyCallback: BleNotifyCallback?): Boolean {
        if (gatt == null || characteristic == null) {
            notifyMsgInit()
            bleNotifyCallback?.onNotifyFailure(OtherException("gatt or characteristic equal null"))
            return false
        }
        //设置特定描述通知
        val success1 = gatt.setCharacteristicNotification(characteristic, enable)
        if (!success1) {
            notifyMsgInit()
            bleNotifyCallback?.onNotifyFailure(OtherException("gatt setCharacteristicNotification fail"))
            return false
        }
        val descriptor: BluetoothGattDescriptor? = if (useCharacteristicDescriptor) {
            characteristic.getDescriptor(characteristic.uuid)
        } else {
            characteristic.getDescriptor(formUUID(UUID_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR))
        }
        return if (descriptor == null) {
            notifyMsgInit()
            bleNotifyCallback?.onNotifyFailure(OtherException("descriptor equals null"))
            false
        } else {
            descriptor.value = if (enable) BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE else BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
            val success2 = gatt.writeDescriptor(descriptor)
            if (!success2) {
                notifyMsgInit()
                bleNotifyCallback?.onNotifyFailure(OtherException("gatt writeDescriptor fail"))
            }
            success2
        }
    }

    /**
     * stop notify
     */
    fun disableCharacteristicNotify(useCharacteristicDescriptor: Boolean): Boolean {
       return mCharacteristic?.let {
            if(it.properties or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0){
              return  setCharacteristicNotification(mBluetoothGatt, mCharacteristic,
                        useCharacteristicDescriptor, false, null)
            }
            return false
        }?:false

    }
    /**
     * 有用ble 特征
     * indicate
     */
    fun enableCharacteristicIndicate(bleIndicateCallback: BleIndicateCallback?, uuid_indicate: String,
                                     useCharacteristicDescriptor: Boolean) {
        mCharacteristic?.let {
            if(it.properties or BluetoothGattCharacteristic.PROPERTY_NOTIFY >0){
                handleCharacteristicIndicateCallback(bleIndicateCallback, uuid_indicate)
                setCharacteristicIndication(mBluetoothGatt, it, useCharacteristicDescriptor, true, bleIndicateCallback)
            }
        }?: bleIndicateCallback?.onIndicateFailure(OtherException("this characteristic not support indicate!"))

    }

    /**
     * stop indicate
     */
    fun disableCharacteristicIndicate(userCharacteristicDescriptor: Boolean): Boolean {
        return if (mCharacteristic != null
                && mCharacteristic!!.properties or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
            setCharacteristicIndication(mBluetoothGatt, mCharacteristic,
                    userCharacteristicDescriptor, false, null)
        } else {
            false
        }
    }

    /**
     * indicate setting
     */
    private fun setCharacteristicIndication(gatt: BluetoothGatt?,
                                            characteristic: BluetoothGattCharacteristic?,
                                            useCharacteristicDescriptor: Boolean,
                                            enable: Boolean,
                                            bleIndicateCallback: BleIndicateCallback?): Boolean {
        if (gatt == null || characteristic == null) {
            indicateMsgInit()
            bleIndicateCallback?.onIndicateFailure(OtherException("gatt or characteristic equal null"))
            return false
        }
        val success1 = gatt.setCharacteristicNotification(characteristic, enable)
        if (!success1) {
            indicateMsgInit()
            bleIndicateCallback?.onIndicateFailure(OtherException("gatt setCharacteristicNotification fail"))
            return false
        }
        val descriptor: BluetoothGattDescriptor? = if (useCharacteristicDescriptor) {
            characteristic.getDescriptor(characteristic.uuid)
        } else {
            characteristic.getDescriptor(formUUID(UUID_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR))
        }
        return if (descriptor == null) {
            indicateMsgInit()
            bleIndicateCallback?.onIndicateFailure(OtherException("descriptor equals null"))
            false
        } else {
            descriptor.value = if (enable) BluetoothGattDescriptor.ENABLE_INDICATION_VALUE else BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
            val success2 = gatt.writeDescriptor(descriptor)
            if (!success2) {
                indicateMsgInit()
                bleIndicateCallback?.onIndicateFailure(OtherException("gatt writeDescriptor fail"))
            }
            success2
        }
    }

    /**
     * indicate
     */
    private fun handleCharacteristicIndicateCallback(bleIndicateCallback: BleIndicateCallback?,
                                                     uuid_indicate: String) {
        if (bleIndicateCallback != null) {
            indicateMsgInit()
            bleIndicateCallback.key = uuid_indicate
            bleIndicateCallback.handler = mHandler
            bleBluetooth.addIndicateCallback(uuid_indicate, bleIndicateCallback)
            mHandler.sendMessageDelayed(
                    mHandler.obtainMessage(BleMsg.MSG_CHA_INDICATE_START, bleIndicateCallback),
                    BleManager.getInstance().operateTimeout)
        }
    }

    /**
     * write
     */
    fun writeCharacteristic(data: ByteArray, bleWriteCallback: BleWriteCallback, uuid_write: String) {
        mCharacteristic?.let {
            if(it.properties and (BluetoothGattCharacteristic.PROPERTY_WRITE or BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) == 0){
                bleWriteCallback.onWriteFailure(OtherException("this characteristic not support write!"))
                return
            }
            if(it.setValue(data)){
                handleCharacteristicWriteCallback(bleWriteCallback, uuid_write)
               if(mBluetoothGatt?.writeCharacteristic(it) == false){
                   writeMsgInit()
                   bleWriteCallback.onWriteFailure(OtherException("gatt writeCharacteristic fail"))
               }
            }else{
                bleWriteCallback.onWriteFailure(OtherException("Updates the locally stored value of this characteristic fail"))
            }
        }
    }

    /**
     * write
     */
    private fun handleCharacteristicWriteCallback(bleWriteCallback: BleWriteCallback,
                                                  uuid_write: String) {
        writeMsgInit()
        bleWriteCallback.key = uuid_write
        bleWriteCallback.handler = mHandler

        bleBluetooth.addWriteCallback(uuid_write, bleWriteCallback)
        mHandler.sendMessageDelayed(
                mHandler.obtainMessage(BleMsg.MSG_CHA_WRITE_START, bleWriteCallback),
                BleManager.getInstance().operateTimeout)
    }


    /**
     * read
     */
    fun readCharacteristic(bleReadCallback: BleReadCallback, uuid_read: String) {
        mCharacteristic?.let {
            if(it.properties and BluetoothGattCharacteristic.PROPERTY_READ > 0){
                handleCharacteristicReadCallback(bleReadCallback, uuid_read)
                if(mBluetoothGatt?.readCharacteristic(it) == false){
                    readMsgInit()
                    bleReadCallback.onReadFailure(OtherException("gatt readCharacteristic fail"))
                }
            }else{
                bleReadCallback.onReadFailure(OtherException("this characteristic not support read!"))
            }
        }?: bleReadCallback.onReadFailure(OtherException("this characteristic not support read!"))

    }

    /**
     * rssi
     */
    fun readRemoteRssi(bleRssiCallback: BleRssiCallback) {
        handleRSSIReadCallback(bleRssiCallback)
        if(mBluetoothGatt?.readRemoteRssi() == false){
            rssiMsgInit()
            bleRssiCallback.onRssiFailure(OtherException("gatt readRemoteRssi fail"))
        }
    }

    /**
     * rssi
     */
    private fun handleRSSIReadCallback(bleRssiCallback: BleRssiCallback) {
        rssiMsgInit()
        bleRssiCallback.handler = mHandler
        bleBluetooth.bleRssiCallback = bleRssiCallback
        mHandler.sendMessageDelayed(
                mHandler.obtainMessage(BleMsg.MSG_READ_RSSI_START, bleRssiCallback),
                BleManager.getInstance().operateTimeout)
    }


    /**
     * read
     */
    private fun handleCharacteristicReadCallback(bleReadCallback: BleReadCallback,
                                                 uuid_read: String) {
        readMsgInit()
        bleReadCallback.key = uuid_read
        bleReadCallback.handler = mHandler

        bleBluetooth.addReadCallback(uuid_read, bleReadCallback)
        mHandler.sendMessageDelayed(
                mHandler.obtainMessage(BleMsg.MSG_CHA_READ_START, bleReadCallback),
                BleManager.getInstance().operateTimeout)
    }

    /**
     * set mtu
     */
    private fun handleSetMtuCallback(bleMtuChangedCallback: BleMtuChangedCallback?) {
        bleMtuChangedCallback?.let {
            mtuChangedMsgInit()
            it.handler = mHandler
            bleBluetooth.bleMtuChangedCallback = bleMtuChangedCallback
            mHandler.sendMessageDelayed(
                    mHandler.obtainMessage(BleMsg.MSG_SET_MTU_START, bleMtuChangedCallback),
                    BleManager.getInstance().operateTimeout)
        }

    }

    fun notifyMsgInit() {
        mHandler.removeMessages(BleMsg.MSG_CHA_NOTIFY_START)
    }

    fun indicateMsgInit() {
        mHandler.removeMessages(BleMsg.MSG_CHA_INDICATE_START)
    }

    fun writeMsgInit() {
        mHandler.removeMessages(BleMsg.MSG_CHA_WRITE_START)
    }

    fun readMsgInit() {
        mHandler.removeMessages(BleMsg.MSG_CHA_READ_START)
    }

    fun rssiMsgInit() {
        mHandler.removeMessages(BleMsg.MSG_READ_RSSI_START)
    }

    fun mtuChangedMsgInit() {
        mHandler.removeMessages(BleMsg.MSG_SET_MTU_START)
    }

    companion object{
         const val UUID_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb"
    }
}


