package com.zlcdgroup.mrsei.ui.adapter


import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.entity.UserEntity
import com.zlcdgroup.mrsei.databinding.ItemUserBinding
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject



/**
 * @ Description:
 * @author king
 * @ Date 2019/5/15 15:58
 * @version V1.0
 */
class DataBindUserListAdapter @Inject constructor(@ActivityContext var activity:Context)  : BaseQuickAdapter<UserEntity,BaseDataBindingHolder<ItemUserBinding>>(R.layout.item_user) {


    override fun convert(holder: BaseDataBindingHolder<ItemUserBinding>, item: UserEntity) {
        holder.dataBinding?.user = item
    }


}