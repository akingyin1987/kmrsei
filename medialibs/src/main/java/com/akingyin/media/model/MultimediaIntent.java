/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.media.model;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import java.io.File;
import java.io.Serializable;

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

 * @ Author king
 * @ Date 2016/12/3 10:48
 * @ Version V1.0
 */

public class MultimediaIntent implements Serializable{

  private    String   saveDir;//路径目录

  private    String   OriginalName;//原始图片

  public String getOriginalName() {
    return OriginalName;
  }

  public void setOriginalName(String originalName) {
    OriginalName = originalName;
  }

  private    String   saveName;

  private    String   fileName;

  private    int   requestCode;

  public void setSaveDir(String saveDir) {
    this.saveDir = saveDir;
  }

  public int getRequestCode() {
    return requestCode;
  }

  public void setRequestCode(int requestCode) {
    this.requestCode = requestCode;
  }

  public void setSaveName(String saveName) {
    this.saveName = saveName;
  }

  public String getFileName() {
    return fileName;
  }

  public MultimediaIntent setFileName(String fileName) {
    this.fileName = fileName;
    return this;
  }

  public void setMultimediaEnum(MultimediaTypeEnum multimediaEnum) {
    this.multimediaEnum = multimediaEnum;
  }

  public ApiMultimediaCall getCall() {
    return call;
  }

  public void setCall(ApiMultimediaCall call) {
    this.call = call;
  }

  private MultimediaTypeEnum multimediaEnum;

  private   ApiMultimediaCall   call;

  public String getSaveDir() {
    return saveDir;
  }

  public String getSaveName() {
    return saveName;
  }

  public MultimediaTypeEnum getMultimediaEnum() {
    return multimediaEnum;
  }

 public static class Builder{
    private    String   saveDir;

    private    String   saveName;

    private    String   OriginalName;

    private    String    fileName;

    private Activity mContext;

    private MultimediaTypeEnum  multimediaEnum;

    private   ApiMultimediaCall   call;

    private    int   requestCode;

    public   Builder    SaveDir(@NonNull String  saveDir){
      this.saveDir = saveDir;
      return  this;
    }
    public  Builder    SaveName(@NonNull String  saveName){
      this.saveName = saveName;
      return  this;
    }

    public  Builder   originalName(String   OriginalName){
      this.OriginalName = OriginalName;
      return this;
    }

    public  Builder   CallBack(ApiMultimediaCall  call){
      this.call = call;
      return  this;
    }


   public   Builder   fileName(String  fileName){
     this.fileName = fileName;
     return  this;
   }

    public   Builder   MultimediaTypeEnum(MultimediaTypeEnum  multimediaEnum){
      this.multimediaEnum = multimediaEnum;
      return  this;
    }


    public   void  start(Activity  mContext){
      MultimediaIntent  intent  = new MultimediaIntent();
      intent.call = call;
      intent.multimediaEnum = multimediaEnum;
      intent.saveDir = saveDir;
      intent.saveName = saveName;
      Intent  toIntent = getMultimediaIntent(multimediaEnum,intent,mContext);
      mContext.startActivity(toIntent);
    }

    public   MultimediaIntent  startForResult(Activity  mContext,int  requestCode){
      MultimediaIntent  intent  = new MultimediaIntent();
      intent.call = call;
      intent.multimediaEnum = multimediaEnum;
      intent.saveDir = saveDir;
      intent.saveName = saveName;
      intent.OriginalName = OriginalName;
      intent.fileName = fileName;
      intent.requestCode = requestCode;
      Intent  toIntent = getMultimediaIntent(multimediaEnum,intent,mContext);
      mContext.startActivityForResult(toIntent,requestCode);

      return  intent;
    }

  }

  public static final String SAVE_DIR = "save_dir";
  public static final String SAVE_NAME="save_name";
   public  static Intent   getMultimediaIntent(MultimediaTypeEnum  multimediaEnum,MultimediaIntent  multimediaIntent,Context context){
     Intent   intent  = new Intent();

     int  sdkVersion = Build.VERSION.SDK_INT;
     File    file = new File(multimediaIntent.getSaveDir(),multimediaIntent.getSaveName());

     switch (multimediaEnum){
       case SysCamrea:
         intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
         if(sdkVersion>= Build.VERSION_CODES.N){
           ContentValues contentValues = new ContentValues(1);
           contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
           Uri uri =context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
           intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
         }else{
           intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
         }
         break;
       case SysVideo:
         intent.setAction( MediaStore.ACTION_VIDEO_CAPTURE);
         if(sdkVersion>= Build.VERSION_CODES.N){
           ContentValues contentValues = new ContentValues(1);
           contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
           Uri uri =context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,contentValues);
           intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
         }else{
           intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
         }
       //  intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, );            //设置拍摄的质量
         intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
         intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT,41943040);
         break;
       case SysAudio:
         intent.setAction( MediaStore.Audio.Media.RECORD_SOUND_ACTION);
         if(sdkVersion>= Build.VERSION_CODES.N){
           ContentValues contentValues = new ContentValues(1);
           contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
           Uri uri =context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,contentValues);
           intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
         }else{
           intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
         }

         break;
       case CustomCamera1:


         break;
       case CustomCamera2:
         intent.setAction("com.zlcdgroup.camera2");

         intent.setType("camera/*");
         intent.putExtra(SAVE_DIR,multimediaIntent.getSaveDir());
         intent.putExtra(SAVE_NAME,multimediaIntent.getSaveName());
         break;
       case TuTuCamera:
         intent.setAction("com.zlcdgroup.tucamera");
         intent.setType("camera/*");
         intent.putExtra(SAVE_DIR,multimediaIntent.getSaveDir());
         intent.putExtra(SAVE_NAME,multimediaIntent.getSaveName());

         break;

       case GoogleCamera:
         intent.setAction("com.zlcdgroup.googlecamera");
         intent.setType("camera/*");
         intent.putExtra(SAVE_DIR,multimediaIntent.getSaveDir());
         intent.putExtra(SAVE_NAME,multimediaIntent.getSaveName());
         break;
       case TuYa1:

         break;
       case TuYa2:

         break;
         default:
     }

     return   intent;
   }



}
