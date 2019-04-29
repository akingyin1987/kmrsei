package com.akingyin.base.ext

import android.annotation.SuppressLint
import android.widget.Toast

/**
 * @ Description:
 * @author king
 * @ Date 2019/4/29 15:25
 * @version V1.0
 */
private var toast: Toast? = null

@SuppressLint("ShowToast")
fun toast(msg: Any?, isShort: Boolean = true) {
    msg?.let {
        if (toast == null) {
            toast = Toast.makeText(app, msg.toString(), Toast.LENGTH_SHORT)
        } else {
            toast!!.setText(msg.toString())
        }
        toast!!.duration = if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
        toast!!.show()
    }
}