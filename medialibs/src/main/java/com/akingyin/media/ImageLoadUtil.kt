package com.akingyin.media

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.UiThread
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import java.io.File

/**
 * 将图片加载放在统一的接口中，方便更换图片框架
 * @author king
 * @version V1.0
 * @ Description:
 *
 *
 * @ Date 2017/12/5 11:32
 */
object ImageLoadUtil {
    fun getImgRequestOptions(@DrawableRes resPlaceholder: Int, @DrawableRes resError: Int): RequestOptions {
        return RequestOptions()
                .fitCenter()
                .placeholder(resPlaceholder)
                .error(resError)
                .priority(Priority.HIGH)
    }

    val imgRequestOptions: RequestOptions
        get() = RequestOptions()
                .fitCenter()
                .placeholder(R.drawable.big_img_error)
                .error(R.drawable.icon_img_load_error)
                .priority(Priority.HIGH)

    val fullRequestOptions: RequestOptions
        get() = RequestOptions()
                .fitCenter()
                .placeholder(R.drawable.big_img_error)
                .error(R.drawable.icon_img_load_error)
                .priority(Priority.HIGH)


    //通知图库更新

    fun  scanGallery(context: Context, paths:Array<out String>,vararg mimeTypes:String= arrayOf("image/jpeg"),callBack: (path:String,uri:Uri)->Unit){
        MediaScannerConnection.scanFile(context,paths,mimeTypes){
            path, uri ->
               callBack.invoke(path, uri)
        }
    }



    /**
     * 图片加载框架初始化
     */
    fun loadImageConfig(context: Context, basePath: String, size: Int) {}

    /**
     * 清除SD图片加载缓存
     */
    fun cleanLoadImageSdCache(context: Context) {
        Glide.get(context).clearDiskCache()
    }

    /**
     * 清除内存缓存，只可在主线程中执行
     * @param context
     */
    @UiThread
    fun cleanMemoryCache(context: Context) {
        Glide.get(context).clearMemory()
    }

    /**
     * 加载本地图片
     * @param path
     * @param context
     * @param imageView
     */
    @JvmStatic
    fun <T : ImageView> loadImageLocalFile(path: String, context: Context, imageView: T) {
        Glide.with(context).load("file://$path")
                .apply(imgRequestOptions)
                .into(imageView)
    }

    @JvmStatic
    fun <T : ImageView> loadImage(path: String?, context: Context, imageView: T) {
       GlideApp.with(context)
               .load(path)
               .applyDefaultImage()
               .into(imageView)
    }

    fun <T : ImageView> loadImageLocalFile(file: File?, context: Context, imageView: T) {
        Glide.with(context).load(file).into(imageView)
    }

    /**
     * 加载图片
     * @param url
     * @param error
     * @param placeholder
     * @param t
     * @param <T>
    </T> */
    fun <T : ImageView> loadImage(context: Context, url: String?, @DrawableRes error: Int, @DrawableRes placeholder: Int, t: T) {
        Glide.with(context).load(url).apply(getImgRequestOptions(placeholder, error)).into(t)
    }

    /**
     * 加载服务器图片
     * @param url
     * @param context
     * @param imageView
     */
    @JvmStatic
    fun <T : ImageView> loadImageServerFile(url: String?, context: Context, imageView: T) {
        Glide.with(context).load(url)
                .apply(imgRequestOptions)
                .into(imageView)
    }

    /**
     * 无占位符
     * @param url
     * @param context
     * @param imageView
     */
    @JvmStatic
    fun <T : ImageView> loadImageServerFileNoPlaceHolder(url: String?, context: Context, imageView: T) {
        Glide.with(context).load(url) // .error(R.drawable.big_img_error)
                // .placeholder(R.drawable.big_img_error)
                .transition(DrawableTransitionOptions().crossFade(300))
                .into(imageView)
    }

    /**
     * 加载本地资源文件
     * @param res
     * @param context
     * @param imageView
     */
    fun loadImageRes(@DrawableRes res: Int, context: Context, imageView: ImageView) {
        Glide.with(context).load(res)
                .apply(imgRequestOptions)
                .into(imageView)
    }
}