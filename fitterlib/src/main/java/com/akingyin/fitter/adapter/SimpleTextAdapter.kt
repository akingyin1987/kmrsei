package com.akingyin.fitter.adapter

import android.util.SparseBooleanArray
import androidx.core.util.contains
import androidx.core.util.forEach
import androidx.core.util.getOrDefault
import androidx.core.util.remove
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
    private var selectedItems: SparseBooleanArray = SparseBooleanArray()

    override fun convert(holder: BaseViewHolder, item: T) {
        println("刷新第${holder.absoluteAdapterPosition}")
        holder.getView<FilterCheckedTextView>(R.id.tv_item_filter).let {
            it.setPadding(0, paddingSize, 0, paddingSize)
            initCheckedTextView(it,holder.absoluteAdapterPosition)
            it.text = provideText(item)
        }


    }

    open fun isSelected(position: Int): Boolean {
        return selectedItems.contains(position)
    }


    open fun setItemChecked(position: Int){
        selectedItems.forEach { key, _ ->
            println("key===>$key")
            if(position != key){
                selectedItems.delete(key)

                notifyItemChanged(key)
            }
        }
        println("selectedItems=$selectedItems")
        selectedItems.put(position, true)
        notifyItemChanged(position)
    }

    open fun setMultipleItemChecked(position: Int){
        selectedItems.put(position, true)
        notifyItemChanged(position)
    }


    open fun toggleSelection(position: Int,selectSingle:Boolean = false) {
        if(selectSingle){
            selectedItems.forEach { key, _ ->
                if(position != key){

                    selectedItems.delete(key)
                    notifyItemChanged(key)
                }
            }
        }
        if (selectedItems[position, false]) {
            selectedItems.delete(position)
        } else {
            selectedItems.put(position, true)
        }
        notifyItemChanged(position)
    }

    open fun clearSelection() {
        selectedItems.forEach { key, _ ->
            notifyItemChanged(key)
        }
        selectedItems.clear()
    }
    abstract fun provideText(t: T): String




    open fun initCheckedTextView(checkedTextView: FilterCheckedTextView,position: Int) {
        checkedTextView.isChecked = selectedItems.getOrDefault(position,false)
    }


}