package com.akingyin.base.dialog

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import com.akingyin.base.R
import com.akingyin.base.utils.HtmlUtils
import com.wang.avi.AVLoadingIndicatorView

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/12/7 13:38
 */
class LoadingDialog(context: Context) : AppCompatDialog(context, R.style.MyDialogStyle) {
    private var content: TextView? = null
    var loadingIndicatorView: AVLoadingIndicatorView? = null
    fun init() {
        setContentView(R.layout.custom_loading_view)
        setCanceledOnTouchOutside(true)
        content = findViewById(R.id.tips_msg)
        loadingIndicatorView = findViewById(R.id.avi)

        //content.setText(this.message);
    }

    fun setHtmlText(htmlText: String) {
        content?.let {
            it.text = HtmlUtils.getTextHtml(htmlText)
            if (TextUtils.isEmpty(htmlText)) {
                it.visibility = View.GONE
            } else {
                if (it.visibility == View.VISIBLE) {
                    it.visibility = View.VISIBLE
                }
            }
        }

    }

    fun setText(message: String?) {
        content?.let {
            it.text = message
            if (TextUtils.isEmpty(message)) {
                it.visibility = View.GONE
            } else {
                if (it.visibility == View.VISIBLE) {
                    it.visibility = View.VISIBLE
                }
            }
        }

    }

    fun setText(resId: Int) {
        setText(context.resources.getString(resId))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun show() {
        loadingIndicatorView?.show()
        super.show()
    }

    override fun dismiss() {
        loadingIndicatorView?.hide()

        super.dismiss()
    }
}