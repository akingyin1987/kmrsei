/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.ble.scan

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import com.akingyin.base.ble.BleDevice
import com.akingyin.base.ble.BleMsg
import com.akingyin.base.ble.callback.BleScanPresenterImp
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @ Description:
 * @author king
 * @ Date 2020/9/1 16:55
 * @version V1.0
 */
abstract class BleScanPresenter :ScanCallback(),BluetoothAdapter.LeScanCallback {

    private var mDeviceNames: Array<String>?= null
    private var mDeviceMac: String? = null
    private var mFuzzy = false
    private var mNeedConnect = false
    private var mScanTimeout: Long = 0
    private var mBleScanPresenterImp: BleScanPresenterImp? = null

    private val mBleDeviceList: ArrayList<BleDevice> = ArrayList()

    private var mMainHandler = Handler(Looper.getMainLooper())
    private var mHandlerThread: HandlerThread? = null
    private var mHandler: Handler? = null
    private var mHandling = false

    private  fun handleResult(bleDevice: BleDevice) {
        mMainHandler.post { onLeScan(bleDevice) }
        checkDevice(bleDevice)
    }
    abstract fun onLeScan(bleDevice: BleDevice)

    //采用新的API
    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        super.onScanResult(callbackType, result)
        result?.let {
            onLeScan(it.device,it.rssi,it.scanRecord?.bytes)
        }
    }

    override fun onBatchScanResults(results: MutableList<ScanResult>?) {
        super.onBatchScanResults(results)
    }

    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
    }

    override fun onLeScan(device: BluetoothDevice?, rssi: Int, scanRecord: ByteArray?) {
       if(mHandling){
           device?.let {
               mHandler?.let {
                   handler ->
                   val message = handler.obtainMessage()
                   message.what = BleMsg.MSG_SCAN_DEVICE
                   message.obj = BleDevice(it).apply {
                       this.mRssi = rssi
                       this.mScanRecord = scanRecord
                       this.mTimestampNanos = System.currentTimeMillis()
                   }
                   handler.sendMessage(message)
               }


           }
       }
    }

    private  fun checkDevice(bleDevice: BleDevice) {
        if (TextUtils.isEmpty(mDeviceMac) && mDeviceNames.isNullOrEmpty()) {
            correctDeviceAndNextStep(bleDevice)
            return
        }
        if (!TextUtils.isEmpty(mDeviceMac)) {
            if (!mDeviceMac.equals(bleDevice.getMac(), ignoreCase = true)) return
        }
        if(!mDeviceNames.isNullOrEmpty()){
            val equal = AtomicBoolean(false)
            val remoteName: String = bleDevice.getName()
            mDeviceNames?.forEach {
                if (if (mFuzzy) remoteName.contains(it) else remoteName == it) {
                    equal.set(true)
                }
            }
            if (!equal.get()) {
                return
            }

        }

        correctDeviceAndNextStep(bleDevice)
    }


    private  fun correctDeviceAndNextStep(bleDevice: BleDevice) {
        if (mNeedConnect) {

            mBleDeviceList.add(bleDevice)
            mMainHandler.post { BleScanner.getInstance().stopLeScan() }
        } else {
            val hasFound = AtomicBoolean(false)
            for (result in mBleDeviceList) {

                if (result.mDevice == bleDevice.mDevice) {
                    hasFound.set(true)
                }
            }
            if (!hasFound.get()) {
                mBleDeviceList.add(bleDevice)
                mMainHandler.post { onScanning(bleDevice) }
            }
        }
    }
    open fun prepare(names: Array<String>, mac: String, fuzzy: Boolean, needConnect: Boolean,
                     timeOut: Long, bleScanPresenterImp: BleScanPresenterImp) {
        mDeviceNames = names
        mDeviceMac = mac
        mFuzzy = fuzzy
        mNeedConnect = needConnect
        mScanTimeout = timeOut
        mBleScanPresenterImp = bleScanPresenterImp
        mHandlerThread = HandlerThread(BleScanPresenter::class.java.simpleName)
        mHandlerThread?.let {
            it.start()
            mHandler = ScanHandler(it.looper, this)
        }

        mHandling = true
    }

    open fun ismNeedConnect(): Boolean {
        return mNeedConnect
    }

    open fun getBleScanPresenterImp(): BleScanPresenterImp? {
        return mBleScanPresenterImp
    }

    private class ScanHandler(looper: Looper, bleScanPresenter: BleScanPresenter) : Handler(looper) {
        private val mBleScanPresenter: WeakReference<BleScanPresenter> = WeakReference(bleScanPresenter)
        override fun handleMessage(msg: Message) {
            val bleScanPresenter = mBleScanPresenter.get()
            if (bleScanPresenter != null) {
                if (msg.what == BleMsg.MSG_SCAN_DEVICE) {
                    val bleDevice = msg.obj as BleDevice
                    bleScanPresenter.handleResult(bleDevice)
                }
            }
        }

    }

    fun notifyScanStarted(success: Boolean) {
        mBleDeviceList.clear()
        removeHandlerMsg()
        if (success && mScanTimeout > 0) {
            mMainHandler.postDelayed({ BleScanner.getInstance().stopLeScan() }, mScanTimeout)
        }
        mMainHandler.post { onScanStarted(success) }
    }

    fun notifyScanStopped() {
        mHandling = false
        mHandlerThread?.quit()
        removeHandlerMsg()
        mMainHandler.post { onScanFinished(mBleDeviceList) }
    }

    fun removeHandlerMsg() {
        mMainHandler.removeCallbacksAndMessages(null)
        mHandler?.removeCallbacksAndMessages(null)
    }


    abstract fun onScanning(bleDevice: BleDevice)

    abstract fun onScanStarted(success: Boolean)

    abstract fun onScanFinished(bleDeviceList: MutableList<BleDevice>)
}