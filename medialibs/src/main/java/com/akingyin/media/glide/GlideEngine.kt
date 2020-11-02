/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.glide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.akingyin.media.GlideApp
import com.akingyin.media.MediaUtils
import com.akingyin.media.R
import com.akingyin.media.engine.ImageEngine
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.target.Target
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.ImageViewState
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/14 11:41
 * @version V1.0
 */
class GlideEngine :ImageEngine{

    /**
     * Loading image
     * 加载图片
     * @param context
     * @param url
     * @param imageView
     */
    override fun loadImage(context: Context, url: String, imageView: ImageView, callBack: ((result: Boolean) -> Unit)?) {
        GlideApp.with(context).apply {
            if(null == callBack){
                load(url).fitCenter()
                        .applyDefaultImage()
                        .into(imageView)
            }else{
                asBitmap().load(url)
                        .fitCenter().applyDefaultImage()
                        .listener(object : RequestListener<Bitmap>{
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                                callBack.invoke(false)
                                return false
                            }

                            override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                callBack.invoke(true)
                                return false
                            }
                        }).into(imageView)
            }
        }


    }

    override fun customLoadImage(context: Context, url: String, imageView: ImageView, placeholder: Int, errorResourceId: Int, overrideWidth: Int, overrideHight: Int, callBack: ((result: Boolean) -> Unit)?) {
        GlideApp.with(context).apply {
            if(null == callBack){
                load(url).apply{
                    if(overrideHight >0 && overrideWidth>0){
                        override(overrideWidth,overrideHight)
                    }
                }.apply(RequestOptions().placeholder(placeholder).error(errorResourceId))
                        .into(imageView)

            }else{
                asBitmap().load(url).apply {
                    if(overrideHight >0 && overrideWidth>0){
                        override(overrideWidth,overrideHight)
                    }
                }
                        .apply(RequestOptions().placeholder(placeholder).error(errorResourceId))
                        .listener(object : RequestListener<Bitmap>{
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                                callBack.invoke(false)
                                return false
                            }

                            override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                callBack.invoke(true)
                                return false
                            }
                        }).into(imageView)
            }
        }
    }

    /**
     * Loading image
     *加载网络图片适配长图方案
     * 注意：此方法只有加载网络图片才会回调
     * @param context
     * @param url
     * @param imageView
     */
    override fun loadImage(context: Context, url: String, imageView: ImageView, longImageView: SubsamplingScaleImageView?, callback: ImageEngine.OnImageCompleteCallback?) {
       GlideApp.with(context)
               .asBitmap().load(url)
               .applyDefaultImage()
               .into(object :ImageViewTarget<Bitmap>(imageView){

                   override fun onLoadStarted(placeholder: Drawable?) {
                       super.onLoadStarted(placeholder)
                       callback?.onShowLoading()
                   }

                   override fun onLoadFailed(errorDrawable: Drawable?) {
                       super.onLoadFailed(errorDrawable)
                       callback?.onHideLoading()
                   }

                   override fun setResource(resource: Bitmap?) {
                       callback?.onHideLoading()
                       resource?.let {
                           val eqLongImage: Boolean = MediaUtils.isLongImg(resource.width,
                                   resource.height)
                           longImageView?.visibility = if (eqLongImage) View.VISIBLE else View.GONE
                           imageView.visibility = if (eqLongImage) View.GONE else View.VISIBLE
                           if (eqLongImage) {
                               // 加载长图
                               longImageView?.apply {
                                   isQuickScaleEnabled = true
                                   isZoomEnabled = true
                                   isPanEnabled = true
                                  setDoubleTapZoomDuration(100)
                                  setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
                                   setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
                                  setImage(ImageSource.bitmap(resource),
                                           ImageViewState(0f, PointF(0f, 0f), 0))
                               }?:imageView.setImageBitmap(resource)

                           } else {
                               // 普通图片
                               imageView.setImageBitmap(resource)
                           }
                       }
                   }
               })
    }

    /**
     * Load album catalog pictures
     *加载相册目录
     * @param context
     * @param url
     * @param imageView
     */
    override fun loadFolderImage(context: Context, url: String, imageView: ImageView) {

        GlideApp.with(context)
                .asBitmap()
                .load(url)
                .override(180, 180)
                .centerCrop()
                .sizeMultiplier(0.5f)
                .apply(RequestOptions().placeholder(R.drawable.picture_image_placeholder))
                .into(object : BitmapImageViewTarget(imageView) {
                    override fun setResource(resource: Bitmap?) {
                        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.resources, resource)
                        circularBitmapDrawable.cornerRadius = 8f
                        imageView.setImageDrawable(circularBitmapDrawable)
                    }
                })
    }

    /**
     * Load GIF image
     *加载gif
     * @param context
     * @param url
     * @param imageView
     */
    override fun loadAsGifImage(context: Context, url: String, imageView: ImageView) {
        GlideApp.with(context)
                .asGif()
                .load(url)
                .into(imageView)
    }

    /**
     * Load picture list picture
     *加载图片列表图片
     * @param context
     * @param url
     * @param imageView
     */
    override fun loadGridImage(context: Context, url: String, imageView: ImageView,callBack: ((result: Boolean) -> Unit)?) {
        GlideApp.with(context).run {
            if(null == callBack){
                load(url)
                        .override(200, 200)
                        .centerCrop()
                        .applyDefaultImage()
                        .into(imageView)
            }else{
                asBitmap().load(url)
                        .override(200, 200)
                        .centerCrop()
                        .applyDefaultImage()
                        .listener(object : RequestListener<Bitmap>{
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                                callBack.invoke(false)
                                return false
                            }

                            override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                callBack.invoke(true)
                                return false
                            }
                        })
                        .into(imageView)
            }
        }

    }

    override fun clearCacheByImageView(imageView: ImageView) {
       GlideApp.with(imageView).clear(imageView)
    }

    companion object {

        @JvmStatic
        private val   instance : GlideEngine by lazy {
           GlideEngine()
        }

        @JvmStatic
        fun  getGlideEngineInstance() : GlideEngine{
            return  instance
        }
    }
}