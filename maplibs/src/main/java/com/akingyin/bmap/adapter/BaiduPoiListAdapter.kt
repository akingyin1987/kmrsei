package com.akingyin.bmap.adapter

import com.akingyin.bmap.vo.PoiInfoVo
import com.akingyin.map.R
import com.baidu.mapapi.search.core.PoiInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * poi 点
 * @ Description:
 * @author king
 * @ Date 2020/5/19 16:42
 * @version V1.0
 */
class BaiduPoiListAdapter :BaseQuickAdapter<PoiInfoVo,BaseViewHolder>(R.layout.item_location_poi) {

    override fun convert(holder: BaseViewHolder, item: PoiInfoVo) {
       with(holder){
           setText(R.id.tvDesc,item.name)
           setText(R.id.tvTitle,item.address)
           if(item.mSelected){
               setImageResource(R.id.iv_select,R.drawable.checkbox_checked)
           }else{
               setImageDrawable(R.id.iv_select,null)
           }
       }
    }
}