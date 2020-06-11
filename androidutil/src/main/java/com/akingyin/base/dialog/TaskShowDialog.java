package com.akingyin.base.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatDialog;
import com.akingyin.base.R;
import com.akingyin.base.rx.RxUtil;
import com.akingyin.base.utils.HtmlUtils;
import com.qmuiteam.qmui.skin.QMUISkinHelper;
import com.qmuiteam.qmui.skin.QMUISkinValueBuilder;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.QMUILoadingView;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialogView;
import com.qmuiteam.qmui.widget.textview.QMUISpanTouchFixTextView;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/12/29.
 */

public class TaskShowDialog {


  private AppCompatDialog loadingDialog;

  private boolean isLoading() {
    return null != loadingDialog && loadingDialog.isShowing();
  }

  private DialogCallBack<Boolean> callBack;

  public DialogCallBack<Boolean> getCallBack() {
    return callBack;
  }

  public TaskShowDialog setCallBack(DialogCallBack<Boolean> callBack) {
    this.callBack = callBack;
    return this;
  }

  private Disposable mDisposable;
  private long currentTime;
  private String msg;
  private TextView tipView;


  public   void   setTipWord(String  message){
    tipView.setText(HtmlUtils.getTextHtml(message));
  }

  public void showLoadDialog(Context context, String message) {

    this.msg = message;
    try {
      if (isLoading()) {
        return;
      }
      if (null == loadingDialog) {
        loadingDialog = new AppCompatDialog(context, R.style.MyDialogStyle);

        Context dialogContext = loadingDialog.getContext();
        QMUITipDialogView dialogView = new QMUITipDialogView(dialogContext);
        dialogView.setOrientation(LinearLayout.HORIZONTAL);
        QMUISkinValueBuilder builder = QMUISkinValueBuilder.acquire();

        QMUILoadingView loadingView = new QMUILoadingView(dialogContext);
        loadingView.setColor(QMUIResHelper.getAttrColor(dialogContext,
            R.attr.qmui_skin_support_tip_dialog_loading_color));

        loadingView.setSize(
            QMUIResHelper.getAttrDimen(dialogContext, R.attr.qmui_tip_dialog_loading_size));
        builder.tintColor(R.attr.qmui_skin_support_tip_dialog_loading_color);
        QMUISkinHelper.setSkinValue(loadingView, builder);
        dialogView.addView(loadingView, onCreateIconOrLoadingLayoutParams(dialogContext));

        tipView = new QMUISpanTouchFixTextView(context);
        tipView.setEllipsize(TextUtils.TruncateAt.END);
        tipView.setGravity(Gravity.CENTER);
        tipView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
            QMUIResHelper.getAttrDimen(context, R.attr.qmui_tip_dialog_text_size));
        tipView.setTextColor(QMUIResHelper.getAttrColor(context,
            R.attr.qmui_skin_support_tip_dialog_text_color));

        builder.clear();
        builder.textColor(R.attr.qmui_skin_support_tip_dialog_text_color);
        QMUISkinHelper.setSkinValue(tipView, builder);

        dialogView.addView(tipView, onCreateTextLayoutParams(dialogContext));
        builder.release();
        loadingDialog.setContentView(dialogView);
      }

      currentTime = System.currentTimeMillis();
      if (!TextUtils.isEmpty(message)) {
        tipView.setText(MessageFormat.format("{0} ,耗时({1}s)", message,
            (System.currentTimeMillis() - currentTime) / 1000));
      } else {
        tipView.setText(
            String.format(Locale.getDefault(),"处理中...耗时(%ds)", (System.currentTimeMillis() - currentTime) / 1000));
      }
      loadingDialog.setCancelable(true);
      loadingDialog.setCanceledOnTouchOutside(false);
      loadingDialog.show();

      loadingDialog.setOnCancelListener(dialog ->

      {
        if(null != callBack){
          callBack.call(true);
        }

        if (null != mDisposable && !mDisposable.isDisposed()) {
          mDisposable.dispose();
        }
      });
      loadingDialog.setOnDismissListener(dialog ->

      {
        if (null != mDisposable && !mDisposable.isDisposed()) {
          mDisposable.dispose();
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }

    mDisposable = Observable.interval(1, 1, TimeUnit.SECONDS)
        .compose(RxUtil.<Long>IO_Main())
        .subscribe(new Consumer<Long>() {
          @Override public void accept(Long aLong) throws Exception {
            if (!TextUtils.isEmpty(msg)) {
              setTipWord(
                  msg + " ,耗时(" + ((System.currentTimeMillis() - currentTime) / 1000) + "s" + ")");
            } else {
              setTipWord(
                  "处理中...耗时(" + (System.currentTimeMillis() - currentTime) / 1000 + "s" + ")");
            }
          }
        }, new Consumer<Throwable>() {
          @Override public void accept(Throwable throwable) throws Exception {
             throwable.printStackTrace();
          }
        });
  }



  private LinearLayout.LayoutParams onCreateIconOrLoadingLayoutParams(Context context) {
    LinearLayout.LayoutParams lp =  new LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    lp.leftMargin = QMUIResHelper.getAttrDimen(context, R.attr.qmui_tip_dialog_text_margin_top);
    lp.rightMargin =   QMUIResHelper.getAttrDimen(context, R.attr.qmui_tip_dialog_text_margin_top);
    lp.gravity = Gravity.CENTER;
    return lp;

  }

  private LinearLayout.LayoutParams onCreateTextLayoutParams(Context context) {
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    lp.leftMargin = QMUIResHelper.getAttrDimen(context, R.attr.qmui_tip_dialog_text_margin_top);
    lp.rightMargin =   QMUIResHelper.getAttrDimen(context, R.attr.qmui_tip_dialog_text_margin_top);
    lp.gravity = Gravity.CENTER;
    return lp;
  }

  public void hideDialog() {

    if (null != mDisposable && !mDisposable.isDisposed()) {
      mDisposable.dispose();
    }
    if (isLoading()) {
      loadingDialog.dismiss();
    }
    loadingDialog = null;
  }

  public void flushDialog(String message, int progress) {
    this.msg = message;
    if (isLoading()) {
      if (!TextUtils.isEmpty(msg)) {
        setTipWord(
            msg + " .耗时(" + ((System.currentTimeMillis() - currentTime) / 1000) + "s" + ")");
      } else {
        setTipWord(
            "处理中...耗时(" + (System.currentTimeMillis() - currentTime) / 1000 + "s" + ")");
      }
    }
  }
}
