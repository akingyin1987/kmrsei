package com.akingyin.fitter.adapter

import com.akingyin.fitter.R
import com.akingyin.fitter.view.FilterCheckedTextView
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * Created by baiiu on 15/12/23.
 * 菜单条目适配器
 */
abstract class SimpleTextAdapter<T> : BaseQuickAdapter<T, BaseViewHolder>(R.layout.lv_item_filter) {

    private val paddingSize: Int by lazy {
        SizeUtils.dp2px(15F)
    }

    override fun convert(holder: BaseViewHolder, item: T) {
        holder.getView<FilterCheckedTextView>(R.id.tv_item_filter).let {
            it.setPadding(0, paddingSize, 0, paddingSize)
            initCheckedTextView(it)
            it.text = provideText(item)
        }


    }


    abstract fun provideText(t: T): String


    fun initCheckedTextView(checkedTextView: FilterCheckedTextView) {}


}