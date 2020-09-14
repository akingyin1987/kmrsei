/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.ble.ui

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.akingyin.base.R
import com.akingyin.base.SimpleActivity
import com.akingyin.base.ble.BleDevice
import com.akingyin.base.ble.BleManager
import com.akingyin.base.ble.callback.BleGattCallback
import com.akingyin.base.ble.callback.BleNotifyCallback
import com.akingyin.base.ble.callback.BleScanCallback
import com.akingyin.base.ble.exception.BleException
import com.akingyin.base.dialog.MaterialDialogUtil
import com.akingyin.base.ext.click
import com.akingyin.base.ext.gone
import com.akingyin.base.ext.visiable
import com.akingyin.base.utils.ConvertUtils
import kotlinx.android.synthetic.main.activity_search_ble_devices.*
import permissions.dispatcher.ktx.constructPermissionsRequest
import kotlin.experimental.xor
import kotlin.properties.Delegates


/**
 * 搜索 ble 设备
 * @ Description:
 * @author king
 * @ Date 2020/9/8 15:32
 * @version V1.0
 */
class SearchDeviceListActivity :SimpleActivity(){

    lateinit var bleDeviceListAdapter: BleDeviceListAdapter


    private var search  :Boolean by Delegates.observable(false){ _, _, newValue ->
        if(newValue){
           // progressWheel.visiable()
        }else{
            progressWheel.gone()
        }
        invalidateOptionsMenu()
    }

      override fun initInjection() {

    }

    override fun getLayoutId() = R.layout.activity_search_ble_devices
    override fun initializationData(savedInstanceState: Bundle?) {
        bleDeviceListAdapter = BleDeviceListAdapter()
        BleManager.getInstance().initBleManager(application)

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }


    override fun initView() {
        setToolBar(toolbar, "Ble设备")
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.adapter = bleDeviceListAdapter

        bleDeviceListAdapter.setEmptyView(R.layout.empty_view)
        bleDeviceListAdapter.emptyLayout?.click {
            search = true
            checkPermissionsAndSearchBleDevice()
        }
        bleDeviceListAdapter.addChildClickViewIds(R.id.btn_connect)
        bleDeviceListAdapter.setOnItemChildClickListener { _, view, position ->
            print("setOnItemChildClickListener=$position")
            bleDeviceListAdapter.getItem(position).run {
                print("-------bleDeviceListAdapter---------")
                if(BleManager.getInstance().isConnected(this)){
                    BleManager.getInstance().disconnect(this)
                    (view as Button).text = "连接"
                }else{
                    showLoadDialog(""){
                        BleManager.getInstance().disconnect(this)
                    }
                    bleGattCallback.view = view
                    BleManager.getInstance().connect(this, bleGattCallback)
                }
            }

        }
        bleDeviceListAdapter.setDiffCallback(object : DiffUtil.ItemCallback<BleDevice>() {
            override fun areItemsTheSame(oldItem: BleDevice, newItem: BleDevice): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: BleDevice, newItem: BleDevice): Boolean {
                return oldItem.toString() == newItem.toString()
            }
        })
    }

    private  val bleGattCallback = object :BleGattCallback(){
        var  view :View?= null
        override fun onStartConnect() {
        }

        override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {
            showError("连接失败${exception?.getDescription()}")
            hideLoadDialog()
        }

        override fun onConnectSuccess(bleDevice: BleDevice?, gatt: BluetoothGatt?, status: Int) {
            showSucces("连接成功")
            (view as Button).text = "断开"
            hideLoadDialog()
            bleDevice?.let {
                addNotify(it)
            }


        }

        override fun onDisConnected(isActiveDisConnected: Boolean, device: BleDevice?, gatt: BluetoothGatt?, status: Int) {
            (view as Button).text = "连接"
        }
    }
    private  fun  addNotify(bleDevice: BleDevice){
        BleManager.getInstance().notify(bleDevice, "0000fee9-0000-1000-8000-00805f9b34fb", "0000feea-0000-1000-8000-00805f9b34fb", callback = object : BleNotifyCallback() {
            override fun onNotifySuccess() {

            }

            override fun onNotifyFailure(exception: BleException) {
                showError("获取通知失败${exception.getDescription()}")
            }

            override fun onCharacteristicChanged(data: ByteArray) {
                showSucces(data.let {
                    var temp = it[0]
                    it.forEachIndexed { index, byte ->
                        if (index > 0) {
                            temp = temp xor byte
                        }
                    }
                    print("temp=${temp.toInt()},last=${it.last().toInt()}")
                    if(temp.toInt() == it.last().toInt() && it.last().toInt()>0){
                        ConvertUtils.bytesToHexString(it.copyOf(temp.toInt()))
                    }else{
                        "数据出错"
                    }
                } ?: "无数据")
            }
        })
    }

   private  fun  checkPermissionsAndSearchBleDevice(){
         println("搜索蓝牙设备")
         if(progressWheel.isVisible){
             showTips("正常在搜索设备中,请稍候再试！")
             return
         }
         progressWheel.visiable()
         if(!BleManager.getInstance().isBlueEnable()){
             MaterialDialogUtil.showConfigDialog(this, message = "当前蓝牙未打开!", positive = "打开蓝牙"){
                 if(it){
                     val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                     registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ activityResult ->
                         if(activityResult.resultCode == Activity.RESULT_OK){
                             if(BleManager.getInstance().isBlueEnable()){
                                 checkPermissionsAndSearchBleDevice()
                             }else{
                                 search = false
                             }
                         }else{
                             search = false
                         }
                     }.launch(intent)
                 }else{
                     search = false
                 }
             }
         }else{
             constructPermissionsRequest(Manifest.permission.ACCESS_FINE_LOCATION){
                 println("开始搜索设备")
                 BleManager.getInstance().disconnectAllDevice()
                 BleManager.getInstance().scan(object : BleScanCallback() {
                     override fun onScanFinished(scanResultList: MutableList<BleDevice>?) {
                         search = false
                         bleDeviceListAdapter.setDiffNewData(scanResultList)
                     }

                     override fun onScanStarted(success: Boolean) {

                     }

                     override fun onScanning(bleDevice: BleDevice?) {

                     }
                 })
             }.launch()
         }




    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search_ble, menu)
        menu?.findItem(R.id.action_search)?.isVisible = !progressWheel.isVisible
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        println("onOptionsItemSelected")

        if(item.itemId == R.id.action_search){
            println("开始搜索")
            search = true
            checkPermissionsAndSearchBleDevice()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun startRequest() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        BleManager.getInstance().cancelScan()
    }
}