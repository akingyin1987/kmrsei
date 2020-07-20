package com.akingyin.base.dialog

import android.content.Context
import android.widget.Toast
import com.akingyin.base.utils.StringUtils
import es.dmoral.toasty.Toasty

/**
 * Created by Administrator on 2017/8/29.
 */
object ToastUtil {
    fun showError(context: Context, message: String?) {
        Toasty.error(context, StringUtils.isEmptyOrNull(message), Toast.LENGTH_SHORT, true).show()
    }

    fun showSucces(context: Context, message: String?) {
        Toasty.success(context, StringUtils.isEmptyOrNull(message), Toast.LENGTH_SHORT, true).show()
    }

    fun showInfo(context: Context, message: String?) {
        Toasty.info(context, StringUtils.isEmptyOrNull(message), Toast.LENGTH_SHORT, true).show()
    }

    fun showWarning(context: Context, message: String?) {
        Toasty.warning(context, StringUtils.isEmptyOrNull(message), Toast.LENGTH_SHORT, true).show()
    }

    fun showNormal(context: Context, message: String?) {
        Toasty.normal(context, StringUtils.isEmptyOrNull(message), Toast.LENGTH_SHORT).show()
    }
}