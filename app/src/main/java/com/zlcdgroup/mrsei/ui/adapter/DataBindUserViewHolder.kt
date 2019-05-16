package com.zlcdgroup.mrsei.ui.adapter

import android.view.View
import com.chad.library.adapter.base.BaseViewHolder
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.databinding.ItemUserBinding

/**
 * @ Description:
 * @author king
 * @ Date 2019/5/15 12:30
 * @version V1.0
 */
class DataBindUserViewHolder(view: View) :BaseViewHolder(view){

    fun getBinding():ItemUserBinding {
        return itemView.getTag(R.id.BaseQuickAdapter_databinding_support) as ItemUserBinding
    }

}