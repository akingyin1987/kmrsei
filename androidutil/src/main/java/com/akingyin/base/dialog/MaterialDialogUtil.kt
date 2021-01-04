package com.akingyin.base.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.text.Spanned
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.afollestad.materialdialogs.list.listItemsSingleChoice

/**
 * @ Description:
 * @author king
 * @ Date 2019/12/29 11:34
 * @version V1.0
 */
object MaterialDialogUtil {


    fun checkNULL(context: Context?): Boolean {
        if (null == context) {
            return false
        }
        if (context is Activity) {
            if (context.isFinishing) {
                return false
            }
        }
        return true
    }


    /**
     * 显示确认对话框，非自动返回关闭
     */
    fun   showConfigNotAutoDismissDialog(context:Context,message:String,callback:(Boolean)->Unit){

       MaterialDialog(context).title(text = "提示").message(text = message)
               .apply {
                   positiveButton(text = "确定")
                   negativeButton(text = "取消")
                   noAutoDismiss()
                   cancelOnTouchOutside(false)
                   cancelable(false)

               }.positiveButton {
                   callback.invoke(true)
               }.negativeButton {
                   callback.invoke(false)
               }.show()
    }


    /**
     * 确认对话框
     */
    fun  showConfigDialog(context:Context,title:String="提示",message:String,positive:String="确定",negative:String="取消",callback:(Boolean)->Unit):MaterialDialog{
       return MaterialDialog(context).title(text = title).message(text = message).show {
           positiveButton(text = positive)

           negativeButton(text = negative)

       }.positiveButton {
           callback.invoke(true)
       }.negativeButton {
           callback.invoke(false)
       }

    }

    /**
     * 确认对话框(内容显示html)
     */
    fun  showConfigDialog(context:Context, title:String="提示", message: Spanned,
                          positive:String="确定", negative:String="取消",
                          callback:(Boolean)->Unit):Dialog{
        return MaterialDialog(context).show {
            title(text =  title)
            message(text = message)
            positiveButton(text = positive)
            negativeButton(text = negative)
            positiveButton {
                callback.invoke(true)
            }
            negativeButton {
                callback.invoke(false)
            }
         }
    }


    /**
     * 单选对话框
     */
    fun <T> showSingleSelectItemDialog(context:Context, title:String,selectIndex:Int = 0,datas:List<T>,callback: (data:T,selectIndex:Int) -> Unit) {
        MaterialDialog(context).title(text = title)
                .listItemsSingleChoice(items=datas.map {
                    println("it.toString->${it.toString()}")
                    it.toString()
                },initialSelection = selectIndex){
                    _, index, _ ->

                    callback(datas[index],index)
                }
                .show()
    }


    /**
     * 多选对话框
     */
    fun <T> showMultiSelectItemDialog(context:Context, title:String,checkedIndex:IntArray =IntArray(0),datas:List<T>,callback: (List<T>) -> Unit) {
        MaterialDialog(context).title(text = title)
                .listItemsMultiChoice(items=datas.map {
                    it.toString()
                },initialSelection = checkedIndex){
                    _, items, _ ->
                   callback(items.map {
                       datas[it]
                   })
                }
                .show()
    }

    /**
     * 显示提示信息
     */
    fun   showInfoDialog(context:Context, message: String):Dialog{
        return MaterialDialog(context).show {
            message(text = message)
            cancelOnTouchOutside(true)
        }

    }

    /**
     * 输入单个对话框
     */
    fun  showEditDialog(context:Context, title: String,inputText:String,hint:String="请输入",minLen :Int=0,maxLen:Int=100,callback: (String) -> Unit){
        MaterialDialog(context).show {
            setTitle(title)

            input(hint,maxLength=maxLen,prefill=inputText,allowEmpty= minLen==0){
                _,text ->
                callback(text.toString())
            }

        }

    }

}