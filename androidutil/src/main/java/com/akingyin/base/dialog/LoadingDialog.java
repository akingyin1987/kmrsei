package com.akingyin.base.dialog;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatDialog;
import com.akingyin.base.R;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/12/7 13:38
 */
public class LoadingDialog extends AppCompatDialog {

  private TextView content;

  public LoadingDialog(Context context) {
    super(context, R.style.MyDialogStyle);
    init();
  }

  public LoadingDialog(Context context, String message) {
    super(context, R.style.MyDialogStyle);
    init();

  }

  public LoadingDialog(Context context, int theme, String message) {
    super(context, R.style.MyDialogStyle);
    init();
  }


  AVLoadingIndicatorView  loadingIndicatorView =null;
  public void init() {
    setContentView(R.layout.custom_loading_view);
    setCanceledOnTouchOutside(true);
    content = findViewById(R.id.tips_msg);
    loadingIndicatorView = findViewById(R.id.avi);
    if(null != loadingIndicatorView){
      loadingIndicatorView.show();
    }
    //content.setText(this.message);
  }

  public  void   setHtmlText(String htmlText){
    content.setText(Html.fromHtml(htmlText));
    if(TextUtils.isEmpty(htmlText)){
      content.setVisibility(View.GONE);
    }else{
      if(content.getVisibility() == View.VISIBLE){
        content.setVisibility(View.VISIBLE);
      }
    }
  }
  public void setText(String message) {
    content.setText(message);
    if(TextUtils.isEmpty(message)){
      content.setVisibility(View.GONE);
    }else{
      if(content.getVisibility() == View.VISIBLE){
        content.setVisibility(View.VISIBLE);
      }
    }
  }

  public void setText(int resId) {
    setText(getContext().getResources().getString(resId));
  }

  @Override public void dismiss() {
    if(null != loadingIndicatorView){
      loadingIndicatorView.hide();
    }
    super.dismiss();
  }
}
