/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.utils

import android.content.Context
import android.net.Uri
import android.provider.CallLog




/**
 * 通话记录相机
 * @ Description:
 * @author king
 * @ Date 2020/9/30 10:44
 * @version V1.0
 */
object CallLogUtil {
    private val callUri: Uri = CallLog.Calls.CONTENT_URI

    private val columns = arrayOf(CallLog.Calls.CACHED_NAME // 通话记录的联系人
            , CallLog.Calls.NUMBER // 通话记录的电话号码
            , CallLog.Calls.DATE // 通话记录的日期
            , CallLog.Calls.DURATION // 通话时长
            , CallLog.Calls.TYPE) // 通话类型}

    fun  findCurrentTel(context: Context):String{
         context.contentResolver.query(callUri, columns,CallLog.Calls.TYPE+"=?", arrayOf(CallLog.Calls.INCOMING_TYPE.toString()),CallLog.Calls.DEFAULT_SORT_ORDER)?.use {
            var  oldNumber =""
            while (it.moveToNext()){
                var newNumber = it.getString(it.getColumnIndex(CallLog.Calls.NUMBER))
                println("用户电话：$newNumber")
            }
        }
        return ""
    }
}