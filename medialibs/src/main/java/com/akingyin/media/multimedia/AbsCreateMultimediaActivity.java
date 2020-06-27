/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.media.multimedia;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import com.akingyin.media.callback.AppCallBack3;
import com.akingyin.media.model.MultimediaTypeEnum;
import com.akingyin.media.model.OperationStateEnum;
import java.io.File;

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
 * @ Description:                                          #
 * @author king
 * @ Date 2016/12/6 12:05
 * @ Version V1.0
 */

public abstract class AbsCreateMultimediaActivity extends AppCompatActivity {

  public static OperationStateEnum sOperationStateEnum;
  public static MultimediaTypeEnum sMultimediaTypeEnum;
  public    static   String   localPath="";

  public    static AppCallBack3<File> callBack;

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    //if(null != sOperationStateEnum && OperationStateEnum.NULL != sOperationStateEnum){
    //  try {
    //    if(sOperationStateEnum == OperationStateEnum.AddAudio ){
    //
    //      if(null != data ){
    //        String  path = getAudioFilePathFromUri(data.getData());
    //        if(!TextUtils.equals(path,localPath)){
    //          FileUtils.copyFile(path,localPath);
    //        }
    //      }
    //    }
    //    final   File  file = new File(localPath);
    //    if(file.exists()){
    //      if(  sMultimediaTypeEnum == MultimediaTypeEnum.SysCamrea
    //          && sOperationStateEnum == OperationStateEnum.AddImage){
    //      Disposable disposable = Observable.just(file).map(new Function<File, Boolean>() {
    //
    //          @Override public Boolean apply(File file) throws Exception {
    //            return CameraBitmapUtil.SysCameraZipImage(localPath,getNowTime());
    //          }
    //        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
    //            .subscribe(new Consumer<Boolean>() {
    //              @Override public void accept(Boolean aBoolean) throws Exception {
    //                if(null != callBack){
    //                  if(aBoolean){
    //                    callBack.call(file);
    //                  }else{
    //                    callBack.onError(new Exception("内存不足"),"压缩图片失败，内存不足");
    //                  }
    //
    //                  sMultimediaTypeEnum = null;
    //                }
    //              }
    //            }, new Consumer<Throwable>() {
    //              @Override public void accept(Throwable throwable) throws Exception {
    //                if(null != callBack){
    //                  callBack.onError(throwable,"压缩图片失败，内存不足");
    //
    //                  sMultimediaTypeEnum = null;
    //                }
    //              }
    //            });
    //      }else{
    //        if(null != callBack){
    //          callBack.call(file);
    //
    //          sMultimediaTypeEnum = null;
    //        }
    //      }
    //
    //
    //    }else{
    //      if(null != callBack){
    //
    //        callBack.onError(new Exception("拍照取消或失败"),(null == sOperationStateEnum?"当前":sOperationStateEnum.getName())+"操作已取消或失败");
    //
    //        sMultimediaTypeEnum = null;
    //      }
    //    }
    //  }catch (Exception e){
    //    e.printStackTrace();
    //
    //    sMultimediaTypeEnum = null;
    //    localPath = null;
    //
    //  }
    //}
  }

  @Override
  public void onBackPressed() {

    super.onBackPressed();
  }

  private String getAudioFilePathFromUri(Uri uri) {
    Cursor cursor = getContentResolver()
        .query(uri, null, null, null, null);
    cursor.moveToFirst();
    int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
    return cursor.getString(index);
  }


  protected  abstract    long    getNowTime();
}
