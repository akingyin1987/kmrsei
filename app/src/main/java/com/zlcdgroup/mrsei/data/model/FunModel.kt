/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.zlcdgroup.mrsei.data.model
import android.content.Context
import android.content.Intent
import com.zlcdgroup.mrsei.ui.TestBaiduMapActivity


/**
 * @ Description:
 * @author king
 * @ Date 2020/6/23 13:52
 * @version V1.0
 */
data class FunModel(var info: String, var intent: Intent) {
    fun gotoActivity(content: Context) {
        content.startActivity(intent)
    }
}