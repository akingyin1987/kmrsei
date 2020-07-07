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
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.akingyin.media.GlideApp
import com.akingyin.media.GlideRequest
import com.akingyin.media.glide.progress.ProgressManager
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import java.lang.ref.WeakReference


/**
 * @ Description:
 * @author king
 * @ Date 2020/7/6 16:54
 * @version V1.0
 */
class GlideImageLoader private constructor(imageView: ImageView) {

    private val ANDROID_RESOURCE = "android.resource://"
    private val FILE = "file://"
    private val SEPARATOR = "/"

    private var url: String=""
    private var imageViewWeakReference: WeakReference<ImageView>? = null
    private var glideRequest: GlideRequest<Drawable>? = null

    init {
        imageViewWeakReference = WeakReference(imageView)
        glideRequest = getContext()?.let {
            GlideApp.with(it).asDrawable()
        }
    }


    companion object{
        fun create(imageView: ImageView): GlideImageLoader {
            return GlideImageLoader(imageView)
        }
    }






    fun getImageView(): ImageView? {
        return  imageViewWeakReference?.get()
    }

    fun getContext(): Context? {
        return  getImageView()?.context
    }



    fun getGlideRequest(): GlideRequest<*> {

        if (glideRequest == null) {
          glideRequest =  getContext()?.let {
               GlideApp.with(it).asDrawable()
           }

        }
        return glideRequest!!
    }

    protected fun resId2Uri(@DrawableRes resId: Int): Uri {
        return Uri.parse(ANDROID_RESOURCE + getContext()?.packageName.toString() + SEPARATOR + resId)
    }

    fun load(@DrawableRes resId: Int, @DrawableRes placeholder: Int, @NonNull transformation: Transformation<Bitmap?>?): GlideImageLoader {
        return loadImage(resId2Uri(resId), placeholder, transformation)
    }

    private fun loadImage(obj: Any?): GlideRequest<Drawable>? {
        if (obj is String) {
            url = obj
        }
        return glideRequest!!.load(obj)
    }


    fun loadImage(obj: Any?, @DrawableRes placeholder: Int, transformation: Transformation<Bitmap?>?): GlideImageLoader {
        glideRequest = loadImage(obj)
        if (placeholder != 0) {
            glideRequest = glideRequest?.placeholder(placeholder)
        }
        if (transformation != null) {
            glideRequest = glideRequest?.transform(transformation)
        }
        getImageView()?.let {
            glideRequest?.into(GlideImageViewTarget(it,url))
        }

        return this
    }

    fun listener(obj: Any?, onProgressListener: OnProgressListener?): GlideImageLoader {
        if (obj is String) {
            url = obj
        }
        ProgressManager.addListener(url, onProgressListener)

        return this
    }

    private class GlideImageViewTarget  constructor(view: ImageView,var uri:String) : DrawableImageViewTarget(view) {


        override fun onLoadFailed(@Nullable errorDrawable: Drawable?) {
            println("onLoadFailed->>")
             ProgressManager.getProgressListener(uri)?.let {
                 it.onProgress(true,100,0,0)
                 ProgressManager.removeListener(uri)
             }

            super.onLoadFailed(errorDrawable)
        }

        override fun onResourceReady(@NonNull resource: Drawable, @Nullable transition: Transition<in Drawable?>?) {
            println("onResourceReady->>")

           ProgressManager.getProgressListener(uri)?.let {
               println("onResourceReady->资源加载完毕")
               it.onProgress(true,100,0,0)
               ProgressManager.removeListener(uri)
           }

            super.onResourceReady(resource, transition)
        }

    }
}