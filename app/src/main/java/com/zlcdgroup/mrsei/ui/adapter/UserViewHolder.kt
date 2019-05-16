package com.zlcdgroup.mrsei.ui.adapter

import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.entity.UserEntity

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/6 15:19
 * @version V1.0
 */
class UserViewHolder (var  view: View) : BaseViewHolder(view) {

   var   name:TextView=view.findViewById(R.id.tv_name)
   var   age :TextView=view.findViewById(R.id.tv_age)


    fun bind( userEntity: UserEntity?){
        userEntity?.let {
            name.text=it.name
            age.text = it.age.toString()
        }

    }


}