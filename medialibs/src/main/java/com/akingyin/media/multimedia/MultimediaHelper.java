/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.media.multimedia;

import android.content.Context;
import android.text.InputType;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.input.DialogInputExtKt;
import com.akingyin.media.callback.AppCallBack;

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
   MaterialDialog  dialog =  new  MaterialDialog(context,MaterialDialog.getDEFAULT_BEHAVIOR());
   dialog.setTitle("文本编辑");
   dialog = DialogInputExtKt.input(dialog, "请输入", null, message, null, InputType.TYPE_CLASS_TEXT, 100, false,
        false,(materialDialog, text)->{
            cb.call(text.toString());
           return  null;
        });
    dialog.show();


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
    MaterialDialog  dialog = new MaterialDialog(context,MaterialDialog.getDEFAULT_BEHAVIOR());
    dialog.title(null,title);
    dialog.message(null,content,null);
    dialog.positiveButton(null, "确定", materialDialog -> {
      cb.call(null);
      return null;
    });
    dialog.negativeButton(null, "取消", materialDialog -> null);
    dialog.show();

  }

}
