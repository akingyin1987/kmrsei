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
 * @ Date 2020/6/12 11:28
 * @version V1.0
 */
class MarkerInfoListRecycleAdapter<T: IMarker> : BaseQuickAdapter<T, BaseViewHolder>(R.layout.openmap_detai_bottom){
    var iOperationListen : IOperationListion<T>? = null

    var loadImage: ILoadImage? = null

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
            iOperationListen?.onObjectImg(holder.bindingAdapterPosition,item,detai_img)
        }

        val tv_index: TextView = holder.getView(R.id.tv_index)

        tv_index.text=MessageFormat.format("{0}",holder.bindingAdapterPosition+1)

        holder.getView<View>(R.id.detai_title).gone()
        val detai_info: TextView = holder.getView(R.id.detai_info)
        detai_info.text = HtmlUtils.getTextHtml(item.baseInfo)
        val left: TextView = holder.getView(R.id.openmap_detai_leftbtn)
        val right: TextView = holder.getView(R.id.openmap_detai_rightbtn)
        val center: TextView = holder.getView(R.id.openmap_detai_middlebtn)
        val other: TextView = holder.getView(R.id.openmap_detai_other)

        val  iv1 : ImageView = holder.getView(R.id.iv_line1)
        val  iv2 : ImageView = holder.getView(R.id.iv_line2)
        val  iv3 : ImageView = holder.getView(R.id.iv_line3)
        iOperationListen?.initView(left,center,right,holder.bindingAdapterPosition,item,other,iv1,iv2,iv3)
        other.click {
            iOperationListen?.onOtherOperation(holder.bindingAdapterPosition,item,other)
        }
        left.click {
            iOperationListen?.onOperation(holder.bindingAdapterPosition,item)
        }
        center.click {
            iOperationListen?.onPathPlan(holder.bindingAdapterPosition,item)
        }

        right.click {
            iOperationListen?.onTuWen(holder.bindingAdapterPosition,item)
        }
    }
}