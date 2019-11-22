/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.img.multimedia;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.akingyin.img.callback.AppCallBack;

/**
 * * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG              #
 * #
 *
 * @ Description:   多媒体制作帮助类                                       #
 * @ Author king
 * @ Date 2016/12/3 14:57
 * @ Version V1.0
 */

public class MultimediaHelper {






  /**
   * 文字编辑对话框
   * @param context
   * @param message
   * @param cb
   */
  public   static    void    showEditDialog(Context  context,@Nullable String  message, final AppCallBack<String> cb){
    new MaterialDialog.Builder(context).title("文字编辑")
        .inputType(InputType.TYPE_NULL)
        .inputRange(2,200)
        .input("请输入", TextUtils.isEmpty(message)?"":message, false, new MaterialDialog.InputCallback() {
      @Override public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
        dialog.dismiss();
        if(null != cb){
          cb.call(input.toString());
        }
      }
    }).show();
  }

  /**
   * 确认对话框
   * @param context
   * @param title
   * @param content
   * @param cb
   */
  public  static   void   showConfigDialog(Context  context,@NonNull String title,@NonNull String  content,
      final AppCallBack<String>  cb){
    new MaterialDialog.Builder(context).title(title)
         .positiveText("确定")
         .negativeText("取消")
        .content(content).onPositive(new MaterialDialog.SingleButtonCallback() {
      @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        if(null != cb){
          cb.call(null);
        }
      }
    }).show();
  }

}
