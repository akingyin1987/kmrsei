package com.akingyin.img;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import java.io.File;

/**
 * 将图片加载放在统一的接口中，方便更换图片框架
 * @author king
 * @version V1.0
 * @ Description:
 *
 *
 * @ Date 2017/12/5 11:32
 */
public class ImageLoadUtil {





  public   static RequestOptions getImgRequestOptions(@DrawableRes int  resPlaceholder,@DrawableRes int  resError){
    return new RequestOptions()
            .centerCrop()
            .placeholder(resPlaceholder)
            .error(resError)

            .priority(Priority.HIGH);


  }

   public   static RequestOptions getImgRequestOptions(){
      return new RequestOptions()
           .fitCenter()
           .placeholder(R.drawable.big_img_error)
           .error(R.drawable.icon_img_load_error)

           .priority(Priority.HIGH);


   }

  public   static RequestOptions getFullRequestOptions(){
    return new RequestOptions()
        .fitCenter()
        .placeholder(R.drawable.big_img_error)
        .error(R.drawable.icon_img_load_error)

        .priority(Priority.HIGH);


  }


  public  static    void   NotificationGallery(Context   context,Uri uri){
    //通知图库更新
    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
    context.sendBroadcast(intent);
  }
  /**
   * 图片加载框架初始化
   */
  public    static    void    loadImageConfig(Context context,String  basePath,int  size){


  }



  /**
   * 清除SD图片加载缓存
   */
  public    static    void    cleanLoadImageSdCache(Context context){

    Glide.get(context).clearDiskCache();
  }



  /**
   * 清除内存缓存，只可在主线程中执行
   * @param context
   */
  @UiThread
  public    static    void   cleanMemoryCache(Context context){
    Glide.get(context).clearMemory();

  }

  /**
   * 加载本地图片
   * @param path
   * @param context
   * @param imageView
   */
  public  static  <T extends ImageView>    void     loadImageLocalFile(String  path, Context  context, T imageView){
    Glide.with(context).load("file://"+path)
        .apply(getImgRequestOptions())

        .into(imageView);
  }

  public  static  <T extends ImageView>    void     loadImage(String  path, Context  context, T imageView){

    Glide.with(context).load(path)
        .apply(getImgRequestOptions())

        .into(imageView);
  }
  public  static  <T extends ImageView>    void     loadImageLocalFile(File file, Context  context, T imageView){
    Glide.with(context).load(file).into(imageView);
  }

  /**
   * 加载图片
   * @param url
   * @param error
   * @param placeholder
   * @param t
   * @param <T>
   */
  public  static  <T extends  ImageView>   void   loadImage(@NonNull Context  context,@Nullable String  url,@DrawableRes int  error,@DrawableRes int placeholder,T  t){
    Glide.with(context).load(url).apply(getImgRequestOptions(placeholder,error)).into(t);
  }

  /**
   * 加载服务器图片
   * @param url
   * @param context
   * @param imageView
   */
  public  static  <T extends ImageView>    void     loadImageServerFile(String   url,Context  context,T imageView){

    Glide.with(context).load(url)
        .apply(getImgRequestOptions())
        .into(imageView);
  }

  /**
   * 无占位符
   * @param url
   * @param context
   * @param imageView
   */
  public  static <T extends ImageView>   void   loadImageServerFileNoPlaceHolder(String   url,Context  context,T imageView){
    Glide.with(context).load(url)
       // .error(R.drawable.big_img_error)

        // .placeholder(R.drawable.big_img_error)
        .transition(new DrawableTransitionOptions().crossFade(300))
        .into(imageView);
  }

  /**
   * 加载本地资源文件
   * @param res
   * @param context
   * @param imageView
   */
  public   static   void     loadImageRes(@DrawableRes int   res,Context context,ImageView imageView){
    Glide.with(context).load(res)
      .apply(getImgRequestOptions())
     .into(imageView);
  }
}
