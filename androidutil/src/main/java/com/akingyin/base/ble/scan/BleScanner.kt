/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.ble.scan


import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import com.akingyin.base.ble.BleDevice
import com.akingyin.base.ble.BleManager
import com.akingyin.base.ble.callback.BleScanAndConnectCallback
import com.akingyin.base.ble.callback.BleScanCallback
import com.akingyin.base.ble.callback.BleScanPresenterImp
import com.akingyin.base.ext.logDebug
import java.util.*
import kotlin.collections.ArrayList

/**
 * 扫描管理器
 * @ Description:
 * @author king
 * @ Date 2020/9/1 17:16
 * @version V1.0
 */
class BleScanner  private constructor(){

    private var mBleScanState = BleScanState.STATE_IDLE

    private var mBleScanPresenter: BleScanPresenter = object : BleScanPresenter() {
        override fun onLeScan(bleDevice: BleDevice) {
            this.getBleScanPresenterImp()?.let {
                if(this.ismNeedConnect()){
                    if(this.getBleScanPresenterImp() is BleScanAndConnectCallback ){
                        (this.getBleScanPresenterImp() as BleScanAndConnectCallback).onLeScan(bleDevice)
                    }
                }else{
                    if(this.getBleScanPresenterImp() is BleScanCallback ){
                        (this.getBleScanPresenterImp() as BleScanCallback).onLeScan(bleDevice)
                    }
                }
            }

        }

        override fun onScanning(bleDevice: BleDevice) {
          getBleScanPresenterImp()?.onScanning(bleDevice)
        }

        override fun onScanStarted(success: Boolean) {
            this.getBleScanPresenterImp()?.onScanStarted(success)
        }

        override fun onScanFinished(bleDeviceList: MutableList<BleDevice>) {
           getBleScanPresenterImp()?.let {
               if(this.ismNeedConnect()){
                   if(this.getBleScanPresenterImp() is BleScanAndConnectCallback ){
                       val presenter = getBleScanPresenterImp() as BleScanAndConnectCallback
                       (this.getBleScanPresenterImp() as BleScanAndConnectCallback).onScanFinished(if (bleDeviceList.isEmpty()) {
                           null
                       } else {
                           bleDeviceList[0]
                       })
                       if(bleDeviceList.isNotEmpty()){
                           presenter.onScanFinished(bleDeviceList[0])
                           Handler(Looper.getMainLooper()).postDelayed({ BleManager.getInstance().connect(bleDeviceList[0], presenter) }, 100)
                       }else{
                           presenter.onScanFinished(null)
                       }
                   }

               }else{
                   if(this.getBleScanPresenterImp() is BleScanCallback ){
                       (this.getBleScanPresenterImp() as BleScanCallback).onScanFinished(bleDeviceList)
                   }
               }
           }
        }

    }

    @Synchronized
    private fun startLeScan(serviceUuids: Array<UUID>, names: Array<String>, mac: String="", fuzzy: Boolean=false,
                            needConnect: Boolean=false, timeOut: Long=0L, imp: BleScanPresenterImp) {
        if (mBleScanState !== BleScanState.STATE_IDLE) {
            ("scan action already exists, complete the previous scan action first").logDebug(BleManager.TAG)
            imp.onScanStarted(false)
            return
        }
        mBleScanPresenter.prepare(names, mac, fuzzy, needConnect, timeOut, imp)
       val success = BleManager.getInstance().bluetoothAdapter?.bluetoothLeScanner?.run {

           startScan(ArrayList<ScanFilter>().apply {
               serviceUuids.forEach {
                   add(ScanFilter.Builder().setServiceUuid(ParcelUuid(it))
                           .build())
               }
               names.forEach {
                   add(ScanFilter.Builder().setDeviceName(it).build())
               }
               if(mac.isNotEmpty()){
                   add(ScanFilter.Builder().setDeviceAddress(mac).build())
               }
           }, ScanSettings.Builder().build(), mBleScanPresenter)
           true
        }?:false
        //使用新的API
       // val success: Boolean = BleManager.getInstance().bluetoothAdapter?.startLeScan(serviceUuids, mBleScanPresenter)?:false

        mBleScanState = if (success) BleScanState.STATE_SCANNING else BleScanState.STATE_IDLE
        mBleScanPresenter.notifyScanStarted(success)
    }
    fun scan(serviceUuids: Array<UUID>, names: Array<String>, mac: String="", fuzzy: Boolean=false,
             timeOut: Long=0L,  callback: BleScanCallback) {
        startLeScan(serviceUuids, names, mac, fuzzy, false, timeOut, callback)
    }

    fun scanAndConnect(serviceUuids: Array<UUID>, names: Array<String>, mac: String="", fuzzy: Boolean=false,
                       timeOut: Long=0L,  callback: BleScanAndConnectCallback) {
        startLeScan(serviceUuids, names, mac, fuzzy, true, timeOut, callback)
    }

    @Synchronized
    fun stopLeScan() {
        BleManager.getInstance().bluetoothAdapter?.bluetoothLeScanner?.stopScan(mBleScanPresenter)
        mBleScanState = BleScanState.STATE_IDLE
        mBleScanPresenter.notifyScanStopped()
    }


    fun getScanState(): BleScanState {
        return mBleScanState
    }

    companion object{
        private  val bleScanner = BleScanner()

        @JvmStatic
        fun  getInstance():BleScanner{
            return  bleScanner
        }
    }
}