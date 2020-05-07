package com.akingyin.base.adapter

import android.graphics.Color
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.akingyin.base.R

import com.akingyin.base.ext.gone

import com.akingyin.base.ext.visiable
import com.akingyin.base.utils.HtmlUtils
import com.blankj.utilcode.util.ConvertUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.qmuiteam.qmui.widget.QMUIRadiusImageView

/**
 * @ Description:
 * @author king
 * @ Date 2020/5/7 17:17
 * @version V1.0
 */
class PropertyListViewAdapter constructor(var onLoadImage:(url:String, imageView : ImageView)->Unit) : BaseQuickAdapter<PropertyVo, BaseViewHolder>(R.layout.item_property) {

    override fun convert(holder: BaseViewHolder, item: PropertyVo) {
        with(item) {
            with((holder)) {
                getView<TextView>(R.id.property_value).apply {
                    text = propertyName
                }
                getView<TextView>(R.id.property_name).apply {
                    setTextColor(if (color > 0) {
                        color
                    } else {
                        Color.BLACK
                    })
                    text = HtmlUtils.getTextHtml(propertyValue)
                    movementMethod = if (linkify > 0) {
                        LinkMovementMethod.getInstance()
                    } else {
                        null
                    }
                }
                if(image.isNullOrEmpty() && video.isNullOrEmpty()){
                    getView<RelativeLayout>(R.id.rl_arrow).gone()
                    getView<View>(R.id.ll_qmui_content).gone()
                }else{
                    getView<RelativeLayout>(R.id.rl_arrow).gone()
                    getView<LinearLayout>(R.id.ll_qmui_content).apply {
                        visiable()
                        removeAllViews()
                        image?.let {
                            it.split(",").forEach {
                                url->
                                val qmuiRadiusImageView = QMUIRadiusImageView(context)
                                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ConvertUtils.dp2px(100f))
                                params.setMargins(ConvertUtils.dp2px(5f), ConvertUtils.dp2px(5f), ConvertUtils.dp2px(5f), ConvertUtils.dp2px(5f))
                                qmuiRadiusImageView.layoutParams = params
                                onLoadImage(url,qmuiRadiusImageView)
                                addView(qmuiRadiusImageView)
                            }
                        }

                        video?.let {

                        }

                    }
                }
            }

        }
    }



}