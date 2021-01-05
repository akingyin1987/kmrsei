package com.akingyin.base.dialog

import android.content.Context
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import com.akingyin.base.R
import com.akingyin.base.rx.RxUtil
import com.akingyin.base.utils.HtmlUtils
import com.qmuiteam.qmui.skin.QMUISkinHelper
import com.qmuiteam.qmui.skin.QMUISkinValueBuilder
import com.qmuiteam.qmui.util.QMUIResHelper
import com.qmuiteam.qmui.widget.QMUILoadingView
import com.qmuiteam.qmui.widget.dialog.QMUITipDialogView
import com.qmuiteam.qmui.widget.textview.QMUISpanTouchFixTextView
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import java.text.MessageFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author king
 * @version V1.0
 * @ Description:
 * 采用 qumi 组件，使用是需要其样式支持，请参照 app -> Base.Theme.Sunflower
 *
 * @ Date 2020/7/28 15:30
 */
class TaskShowDialog(var context: Context, var callBack: (Boolean) -> Unit) {

    private lateinit var loadingDialog: AppCompatDialog
    private val isLoading: Boolean
        get() = loadingDialog.isShowing
    private var mDisposable: Disposable? = null
    private var currentTime: Long = 0
    private var msg: String? = null
    private lateinit var tipView: TextView


    /**
     * 创建对话框
     */
    fun  createDialog():AppCompatDialog {
        loadingDialog= AppCompatDialog(context, R.style.MyDialogStyle)
        val dialogContext = loadingDialog.context
        val dialogView = QMUITipDialogView(dialogContext)
        dialogView.orientation = LinearLayout.HORIZONTAL
        val builder = QMUISkinValueBuilder.acquire()
        val loadingView = QMUILoadingView(dialogContext)
        loadingView.setColor(QMUIResHelper.getAttrColor(dialogContext,
                R.attr.qmui_skin_support_tip_dialog_loading_color))
        loadingView.setSize(
                QMUIResHelper.getAttrDimen(dialogContext, R.attr.qmui_tip_dialog_loading_size))
        builder.tintColor(R.attr.qmui_skin_support_tip_dialog_loading_color)
        QMUISkinHelper.setSkinValue(loadingView, builder)
        dialogView.addView(loadingView, onCreateIconOrLoadingLayoutParams(dialogContext))
        tipView = QMUISpanTouchFixTextView(context).apply {
            ellipsize = TextUtils.TruncateAt.END
            gravity = Gravity.CENTER
        }
        tipView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                QMUIResHelper.getAttrDimen(context, R.attr.qmui_tip_dialog_text_size).toFloat())
        tipView.setTextColor(QMUIResHelper.getAttrColor(context,
                R.attr.qmui_skin_support_tip_dialog_text_color))
        builder.clear()
        builder.textColor(R.attr.qmui_skin_support_tip_dialog_text_color)
        QMUISkinHelper.setSkinValue(tipView, builder)
        dialogView.addView(tipView, onCreateTextLayoutParams(dialogContext))
        builder.release()
        loadingDialog.setContentView(dialogView)
        return  loadingDialog
    }


    fun setTipWord(message: String) {
        tipView.text = HtmlUtils.getTextHtml(message)
    }

    fun showLoadDialog(message: String) {
        msg = message
        try {
            if (isLoading) {
                return
            }
            currentTime = System.currentTimeMillis()
            if (!TextUtils.isEmpty(message)) {
                tipView.text = MessageFormat.format("{0} ,耗时({1}s)", message,
                        (System.currentTimeMillis() - currentTime) / 1000)
            } else {
                tipView.text = String.format(Locale.getDefault(), "处理中...耗时(%ds)", (System.currentTimeMillis() - currentTime) / 1000)
            }
            loadingDialog.apply {
                setCancelable(true)
                setCanceledOnTouchOutside(false)
                setOnCancelListener {
                    callBack.invoke(true)
                    mDisposable?.dispose()
                }
                setOnDismissListener {
                    mDisposable?.dispose()
                }
            }.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mDisposable = Observable.interval(1, 1, TimeUnit.SECONDS)
                .compose(RxUtil.IO_Main())
                .subscribe({
                    if (!TextUtils.isEmpty(msg)) {
                        setTipWord(
                                msg + " ,耗时(" + (System.currentTimeMillis() - currentTime) / 1000 + "s" + ")")
                    } else {
                        setTipWord(
                                "处理中...耗时(" + (System.currentTimeMillis() - currentTime) / 1000 + "s" + ")")
                    }
                }) { throwable -> throwable.printStackTrace() }
    }

    private fun onCreateIconOrLoadingLayoutParams(context: Context): LinearLayout.LayoutParams {
        val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.leftMargin = QMUIResHelper.getAttrDimen(context, R.attr.qmui_tip_dialog_text_margin_top)
        lp.rightMargin = QMUIResHelper.getAttrDimen(context, R.attr.qmui_tip_dialog_text_margin_top)
        lp.gravity = Gravity.CENTER
        return lp
    }

    private fun onCreateTextLayoutParams(context: Context): LinearLayout.LayoutParams {
        val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.leftMargin = QMUIResHelper.getAttrDimen(context, R.attr.qmui_tip_dialog_text_margin_top)
        lp.rightMargin = QMUIResHelper.getAttrDimen(context, R.attr.qmui_tip_dialog_text_margin_top)
        lp.gravity = Gravity.CENTER
        return lp
    }

    fun hideDialog() {
        mDisposable?.dispose()
        if (isLoading) {
            loadingDialog.dismiss()
        }

    }

    fun flushDialog(message: String) {
        msg = message
        if (isLoading) {
            if (!TextUtils.isEmpty(msg)) {
                setTipWord(
                        msg + " .耗时(" + (System.currentTimeMillis() - currentTime) / 1000 + "s" + ")")
            } else {
                setTipWord(
                        "处理中...耗时(" + (System.currentTimeMillis() - currentTime) / 1000 + "s" + ")")
            }
        }
    }
}