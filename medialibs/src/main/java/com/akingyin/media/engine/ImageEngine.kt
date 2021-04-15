/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.engine

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.akingyin.media.R
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/14 11:09
 * @version V1.0
 */

interface ImageEngine {
    /**
     * Loading image
     * 加载图片
     * @param context
     * @param url
     * @param imageView
     */
    fun loadImage(context: Context, url: String, imageView: ImageView ,callBack:((result:Boolean)->Unit)?=null)


    /**
     * 自定义加载
     */
    fun customLoadImage(context: Context, url: String, imageView: ImageView, @DrawableRes placeholder:Int= R.drawable.ic_image_loading_layer
                        ,@DrawableRes errorResourceId:Int =R.drawable.ic_img_loading_error,overrideWidth:Int=0,overrideHight:Int=0, callBack:((result:Boolean)->Unit)?=null)


    /**
     * Loading image
     *加载网络图片适配长图方案
     * 注意：此方法只有加载网络图片才会回调
     * @param context
     * @param url
     * @param imageView
     */
    fun loadImage(context: Context, url: String, imageView: ImageView, longImageView: SubsamplingScaleImageView?, callback: OnImageCompleteCallback?)


    /**
     * Load album catalog pictures
     *加载相册目录
     * @param context
     * @param url
     * @param imageView
     */
    fun loadFolderImage(context: Context, url: String, imageView: ImageView)

    /**
     * Load GIF image
     *加载gif
     * @param context
     * @param url
     * @param imageView
     */
    fun loadAsGifImage(context: Context, url: String, imageView: ImageView)

    /**
     * Load picture list picture
     *加载图片列表图片
     * @param context
     * @param url
     * @param imageView
     */
    fun loadGridImage(context: Context, url: String, imageView: ImageView,callBack:((result:Boolean)->Unit)?=null)

    /**
     * 清除某个view 缓存
     */
    fun  clearCacheByImageView(imageView: ImageView)


    /**
     * 清楚内存
     * @param context Context
     */
    fun  clearMemory(context: Context)

    /**
     *
     * @param context Context
     * @param path String
     */
    fun  clearDiskCacheByPath(context: Context,path:String)


    /**
     * 清除硬盘缓存
     * @param context Context
     */
    fun  clearDiskCache(context: Context)

    interface  OnImageCompleteCallback{
        /**
         * Start loading
         */
        fun onShowLoading()

        /**
         * Stop loading
         */
        fun onHideLoading()
    }

}