/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.amap.adapter

import android.widget.Button
import com.akingyin.base.ext.gone
import com.akingyin.base.ext.visiable
import com.akingyin.map.R
import com.amap.api.maps.offlinemap.OfflineMapProvince
import com.amap.api.maps.offlinemap.OfflineMapStatus
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import java.text.MessageFormat
import java.util.*

/**
 * @ Description:
 * @author king
 * @ Date 2020/6/24 16:42
 * @version V1.0
 */
class AmapOffineListAdapter : BaseQuickAdapter<OfflineMapProvince,BaseViewHolder>(R.layout.item_offine) {


    fun   updateElement(offlineMapCity: OfflineMapProvince){
        data.forEachIndexed { index, data ->
            if(data.provinceCode == offlineMapCity.provinceCode){
                setData(index,offlineMapCity)
            }
        }
    }

    fun   addOrUpdateElement(offlineMapCity: OfflineMapProvince,sortFrist:Boolean = false ){
        data.forEachIndexed { index, data ->
            if(data.provinceCode == offlineMapCity.provinceCode){
                if(sortFrist){
                    val  frist = getItem(0)
                    setData(0,offlineMapCity)
                    setData(index,frist)
                }else{
                    setData(index,offlineMapCity)
                }

                return
            }
        }
        addData(0,offlineMapCity)
    }


    override fun convert(holder: BaseViewHolder, item: OfflineMapProvince) {
       with(holder){
           setText(R.id.title,MessageFormat.format("{0}({1})",item.provinceName,item.provinceCode))
           setText(R.id.update,if(item.getcompleteCode()<100 || item.state == OfflineMapStatus.NEW_VERSION){"可更新"}else{"已最新"})
           getView<Button>(R.id.download).let {
               if(item.getcompleteCode() == 100){
                   it.gone()
               }else{
                   it.visiable()
                   if(item.state == OfflineMapStatus.LOADING){
                       it.text = "暂停"
                   }else{
                       it.text = "下载"
                   }
               }
           }
           setText(R.id.ratio, MessageFormat.format("大小：{0},下载进度：{1}%",formatDataSize(item.size),item.getcompleteCode()))
       }
    }


    fun formatDataSize(size: Long): String {

        return if (size < 1024 * 1024) {
            String.format(Locale.getDefault(), "%dK", size / 1024)
        } else {
            String.format(Locale.getDefault(), "%.1fM", size / (1024 * 1024.0))
        }

    }
}