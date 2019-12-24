package com.zlcdgroup.mrsei.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.entity.UserEntity
import javax.inject.Inject
import javax.inject.Named


/**
 * @ Description:
 * @author king
 * @ Date 2019/5/15 15:58
 * @version V1.0
 */
class DataBindUserListAdapter @Inject constructor(@Named("binduser") var activity:Activity)  : BaseQuickAdapter<UserEntity,DataBindUserViewHolder>(null) {

    val inflater = LayoutInflater.from(activity)


    override fun convert(helper: DataBindUserViewHolder, item: UserEntity?) {

        helper.let {
          val viewDataBinding =  it.getBinding()
           viewDataBinding.user = item

        }
    }


    override fun getItemView(layoutResId: Int, parent: ViewGroup?): View {
        val  viewDataBinding = DataBindingUtil.inflate<ViewDataBinding>(inflater,R.layout.item_user,parent,false)
           ?: return  super.getItemView(layoutResId, parent)

        val view = viewDataBinding.root
        view.setTag(R.id.BaseQuickAdapter_databinding_support, viewDataBinding)
        return  view
    }
}