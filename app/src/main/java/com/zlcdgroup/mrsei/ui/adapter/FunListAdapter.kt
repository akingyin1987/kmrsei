/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.zlcdgroup.mrsei.ui.adapter


import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.model.FunModel

/**
 * @ Description:
 * @author king
 * @ Date 2020/6/23 13:52
 * @version V1.0
 */
class FunListAdapter : BaseQuickAdapter<FunModel,BaseViewHolder>(R.layout.item_test_fun) {

    override fun convert(holder: BaseViewHolder, item: FunModel) {
         holder.setText(R.id.tv_info,item.info)
    }
}