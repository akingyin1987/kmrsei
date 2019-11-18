package com.akingyin.zxingcamera;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import java.util.List;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/11/18 15:17
 */
public class CameraDialogUtil {

  interface   CameraCallBack<T>{
       void   call(T   t);
  }

  public  static final  <T>   void   showSelectSingleDialog(Context context,String  title,
      final List<T> data ,
      final CameraCallBack<T>  callBack){
    final ArrayAdapter<T>
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, data);
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(title);

    builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        if(null != callBack){
          callBack.call(data.get(which));
        }
      }
    });
    builder.show();
  }
}
