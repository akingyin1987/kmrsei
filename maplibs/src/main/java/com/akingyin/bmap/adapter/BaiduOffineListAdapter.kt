/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.bmap.adapter

import android.widget.Button
import com.akingyin.base.ext.gone
import com.akingyin.base.ext.visiable
import com.akingyin.map.R
import com.baidu.mapapi.map.offline.MKOLUpdateElement
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import java.text.MessageFormat
import java.util.*

/**
 * @ Description:
 * @author king
 * @ Date 2020/6/24 11:03
 * @version V1.0
 */
class BaiduOffineListAdapter : BaseQuickAdapter<MKOLUpdateElement,BaseViewHolder>(R.layout.item_offine) {

    fun   updateElement(mkolUpdateElement: MKOLUpdateElement){
        data.forEachIndexed { index, data ->
            if(data.cityID == mkolUpdateElement.cityID){
                setData(index,mkolUpdateElement)
            }
        }
    }

    fun   addOrUpdateElement(mkolUpdateElement: MKOLUpdateElement){
        data.forEachIndexed { index, data ->
            if(data.cityID == mkolUpdateElement.cityID){
                setData(index,mkolUpdateElement)
                return
            }
        }
        addData(mkolUpdateElement)
    }

    override fun convert(holder: BaseViewHolder, item: MKOLUpdateElement) {
         with(holder){
             setText(R.id.title, MessageFormat.format("{0}({1})", item.cityName, item.cityID))
             setText(R.id.update,if(item.update){"可更新"}else{"已最新"})
             getView<Button>(R.id.download).let {
                 if(item.ratio == 100){
                     it.gone()
                 }else{
                     it.visiable()
                     if(item.status == MKOLUpdateElement.DOWNLOADING){
                         it.text = "暂停"
                     }else{
                         it.text = "下载"
                     }
                 }
             }
             setText(R.id.ratio, MessageFormat.format("{0}/{1}", formatDataSize(item.size),
                     formatDataSize(item.serversize)))

         }

    }


    fun formatDataSize(size: Int): String {

       return if (size < 1024 * 1024) {
            String.format(Locale.getDefault(), "%dK", size / 1024)
        } else {
            String.format(Locale.getDefault(), "%.1fM", size / (1024 * 1024.0))
        }

    }
}