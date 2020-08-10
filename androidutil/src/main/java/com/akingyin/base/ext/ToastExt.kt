package com.akingyin.base.ext


import android.widget.Toast

/**
 * @ Description:
 * @author king
 * @ Date 2019/4/29 15:25
 * @version V1.0
 */
private var toast: Toast? = null


fun toast(msg: Any?, isShort: Boolean = true) {
    msg?.let {
       toast = toast?.apply {
           setText(it.toString())
       }?:Toast.makeText(app, it.toString(), Toast.LENGTH_SHORT)
       toast?.run {
           duration = if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
           show()
       }
    }
}

/**
 * 弹出Toast提示。
 *
 * @param duration 显示消息的时间  Either {@link #LENGTH_SHORT} or {@link #LENGTH_LONG}
 */
fun CharSequence.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(app, this, duration).show()
}