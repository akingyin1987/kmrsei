package com.zlcdgroup.mrsei.ui.adapter

import android.app.Activity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.entity.UserEntity
import com.zlcdgroup.mrsei.databinding.ItemUserBinding
import javax.inject.Inject
import javax.inject.Named


/**
 * @ Description:
 * @author king
 * @ Date 2019/5/15 15:58
 * @version V1.0
 */
class DataBindUserListAdapter @Inject constructor(@Named("binduser") var activity:Activity)  : BaseQuickAdapter<UserEntity,BaseDataBindingHolder<ItemUserBinding>>(R.layout.item_user) {


    override fun convert(holder: BaseDataBindingHolder<ItemUserBinding>, item: UserEntity) {
        holder.dataBinding?.user = item
    }


}