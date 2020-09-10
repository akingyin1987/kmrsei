/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.ble.ui

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import com.akingyin.base.R
import com.akingyin.base.ble.BleDevice
import com.akingyin.base.ble.BleManager
import com.akingyin.base.ext.gone
import com.akingyin.base.ext.visiable
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * @ Description:
 * @author king
 * @ Date 2020/9/8 15:17
 * @version V1.0
 */
class BleDeviceListAdapter :BaseQuickAdapter<BleDevice, BaseViewHolder>(R.layout.item_ble_device){

    override fun convert(holder: BaseViewHolder, item: BleDevice) {
       with(holder){
           setText(R.id.txt_name, item.getName())
           setText(R.id.txt_mac, item.getMac())
           setText(R.id.txt_rssi, item.mRssi.toString())
           addChildClickViewIds(R.id.btn_connect)
           if(BleManager.getInstance().isConnected(item)){
               getView<ImageView>(R.id.img_blue).setImageResource(R.mipmap.ic_blue_connected)
               getView<TextView>(R.id.txt_name).setTextColor(-0xe2164a)
               getView<TextView>(R.id.txt_mac).setTextColor(-0xe2164a)
               getView<Group>(R.id.group_rssi).visiable()
               getView<Button>(R.id.btn_connect).text="断开"
           }else{
               getView<Button>(R.id.btn_connect).text="连接"
               getView<Group>(R.id.group_rssi).visiable()
               getView<ImageView>(R.id.img_blue).setImageResource(R.mipmap.ic_blue_remote)
               getView<TextView>(R.id.txt_name).setTextColor(-0x1000000)
               getView<TextView>(R.id.txt_mac).setTextColor(-0x1000000)
           }
       }
    }
}