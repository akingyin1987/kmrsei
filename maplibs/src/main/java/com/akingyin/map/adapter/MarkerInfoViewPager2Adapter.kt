/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.map.adapter


import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.akingyin.base.ext.click
import com.akingyin.base.ext.gone
import com.akingyin.base.ext.visiable
import com.akingyin.base.utils.HtmlUtils
import com.akingyin.map.IMarker
import com.akingyin.map.R
import com.akingyin.map.base.ILoadImage
import com.akingyin.map.base.IOperationListion
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import java.text.MessageFormat

/**
 * @ Description:
 * @author king
 * @ Date 2020/5/25 17:41
 * @version V1.0
 */


class MarkerInfoViewPager2Adapter<T:IMarker> :BaseQuickAdapter<T,BaseViewHolder> (R.layout.openmap_detai){

    var iOperationListen : IOperationListion<T>? = null

    var loadImage:ILoadImage? = null

    override fun convert(holder: BaseViewHolder, item: T) {
        val detai_img = holder.getView<ImageView>(R.id.detai_img)
        detai_img.gone()
        loadImage?.let {
            item.markerDetaiImgPath?.let {
                it1 ->
                 detai_img.visiable()
                it.loadImageView( it1,context,detai_img)
            }
        }
        detai_img.click {
            iOperationListen?.onObjectImg(holder.adapterPosition,item,detai_img)
        }

        val btn_poidetail_showmap: TextView = holder.getView(R.id.btn_poidetail_showmap)


        btn_poidetail_showmap.text = if(item.sortInfo.isNullOrEmpty()){
            MessageFormat.format("详情   {0}/{1}   {2}", holder.adapterPosition + 1, data.size,item.disFromPostion?.let {
                "距离当前位置约："+MessageFormat.format("{0,number,#.##}",it)+"米"
            }?:"")
        }else{
            item.sortInfo+(item.disFromPostion?.let {
                "距离当前位置约："+MessageFormat.format("{0,number,#.##}",it)+"米"
            }?:"")
        }
        holder.getView<View>(R.id.detai_title).gone()
        val detai_info: TextView = holder.getView(R.id.detai_info)
        detai_info.text = HtmlUtils.getTextHtml(item.baseInfo)
        val left: TextView = holder.getView(R.id.openmap_detai_leftbtn)
        val right: TextView = holder.getView(R.id.openmap_detai_rightbtn)
        val center: TextView = holder.getView(R.id.openmap_detai_middlebtn)
        val other: TextView = holder.getView(R.id.openmap_detai_other)
        iOperationListen?.initView(left,center,right,holder.adapterPosition,item,other)
        other.click {
            iOperationListen?.onOtherOperation(holder.adapterPosition,item,other)
        }
        left.click {
            iOperationListen?.onOperation(holder.adapterPosition,item)
        }
        center.click {
            iOperationListen?.onPathPlan(holder.adapterPosition,item)
        }

        right.click {
            iOperationListen?.onTuWen(holder.adapterPosition,item)
        }
    }
}