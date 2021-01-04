package com.akingyin.base.dialog


import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import com.akingyin.base.R
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.widget.popup.QMUIPopup
import com.qmuiteam.qmui.widget.popup.QMUIPopups
import com.qmuiteam.qmui.widget.popup.QMUIQuickAction

/**
 * @ Description:
 * @author king
 * @ Date 2020/11/25 16:49
 * @version V1.0
 */
object QmuiDialogUtil {

    fun  <T> showlistPopup(context: Context,anchor:View,data:List<T>,callback: (data:T,selectIndex:Int) -> Unit):QMUIPopup{

        val adapter = ArrayAdapter(context, R.layout.simple_list_item, data)
        return QMUIPopups.listPopup(context, QMUIDisplayHelper.dp2px(context, 250),
                QMUIDisplayHelper.dp2px(context, 300),adapter) { _, _, pos, _ ->
          callback.invoke(data[pos],pos)


        }.animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
          .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .offsetYIfTop(QMUIDisplayHelper.dp2px(context, 5))
                .shadow(true)
                .show(anchor)


    }

    fun  showQmuiQuickAction(context: Context,anchor:View,actions:List<QMUIQuickAction.Action>){
        QMUIPopups.quickAction(context,
                QMUIDisplayHelper.dp2px(context, 56),
                QMUIDisplayHelper.dp2px(context, 56))
                .shadow(true)

                .edgeProtection(QMUIDisplayHelper.dp2px(context, 20)).apply {
                    actions.forEach {
                        addAction(it)
                    }
                }.show(anchor)
    }
}