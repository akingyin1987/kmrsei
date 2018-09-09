package com.zlcdgroup.mrsei.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.entity.UserEntity
import com.zlcdgroup.mrsei.di.qualifier.ActivityContext
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/6 15:29
 * @version V1.0
 */
class UserListAdapter @Inject constructor(@ActivityContext var  context: Context) : BaseQuickAdapter<UserEntity, UserViewHolder>(null) {

    var    layoutInflater:LayoutInflater= LayoutInflater.from(context)


    override fun convert(helper: UserViewHolder?, item: UserEntity?) {
       helper?.bind(item )
    }

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): UserViewHolder {
       return  UserViewHolder(layoutInflater.inflate(R.layout.item_user,parent,false))
    }


}
