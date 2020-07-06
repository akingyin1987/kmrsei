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
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import com.akingyin.media.glide.transformation.CircleTransformation
import com.akingyin.media.glide.transformation.RadiusTransformation
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView


/**
 * @ Description:
 * @author king
 * @ Date 2020/7/6 17:18
 * @version V1.0
 */
class GlideImageView :PhotoView{
    private var enableState = false
    private var pressedAlpha = 0.4f
    private var unableAlpha = 0.3f
     private var imageLoader: GlideImageLoader = GlideImageLoader.create(this)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    fun getImageLoader(): GlideImageLoader {

        return imageLoader
    }

    fun apply(options: RequestOptions): GlideImageView? {
        imageLoader.getGlideRequest()?.apply(options)
        return this
    }

    fun centerCrop(): GlideImageView? {
        getImageLoader().getGlideRequest()!!.centerCrop()
        return this
    }

    fun fitCenter(): GlideImageView? {
        getImageLoader().getGlideRequest()!!.fitCenter()
        return this
    }

    fun diskCacheStrategy(@NonNull strategy: DiskCacheStrategy?): GlideImageView? {
        getImageLoader().getGlideRequest()!!.diskCacheStrategy(strategy!!)
        return this
    }

    fun placeholder(@DrawableRes resId: Int): GlideImageView? {
        getImageLoader().getGlideRequest()!!.placeholder(resId)
        return this
    }

    fun error(@DrawableRes resId: Int): GlideImageView? {
        getImageLoader().getGlideRequest()!!.error(resId)
        return this
    }

    fun fallback(@DrawableRes resId: Int): GlideImageView? {
        getImageLoader().getGlideRequest()!!.fallback(resId)
        return this
    }

    fun dontAnimate(): GlideImageView? {
        getImageLoader().getGlideRequest()!!.dontTransform()
        return this
    }

    fun dontTransform(): GlideImageView? {
        getImageLoader().getGlideRequest()!!.dontTransform()
        return this
    }

    fun load(url: String?) {
        load(url, 0)
    }

    fun load(url: String?, @DrawableRes placeholder: Int) {
        load(url, placeholder, 0)
    }

    fun load(url: String?, @DrawableRes placeholder: Int, radius: Int) {
        load(url, placeholder, radius, null)
    }

    fun load(url: String?, @DrawableRes placeholder: Int, onProgressListener: OnProgressListener?) {
        load(url, placeholder, null, onProgressListener)
    }

    fun load(url: String?, @DrawableRes placeholder: Int, radius: Int, onProgressListener: OnProgressListener?) {
        load(url, placeholder, RadiusTransformation(context, radius), onProgressListener)
    }

    fun load(obj: Any?, @DrawableRes placeholder: Int, transformation: Transformation<Bitmap?>?) {
        getImageLoader().loadImage(obj, placeholder, transformation)
    }

    fun load(obj: Any?, @DrawableRes placeholder: Int, transformation: Transformation<Bitmap?>?, onProgressListener: OnProgressListener?) {
        getImageLoader().listener(obj, onProgressListener)!!.loadImage(obj, placeholder, transformation)
    }

    fun loadCircle(url: String?) {
        loadCircle(url, 0)
    }

    fun loadCircle(url: String?, @DrawableRes placeholder: Int) {
        loadCircle(url, placeholder, null)
    }

    fun loadCircle(url: String?, @DrawableRes placeholder: Int, onProgressListener: OnProgressListener?) {
        load(url, placeholder, CircleTransformation(), onProgressListener)
    }

    fun loadDrawable(@DrawableRes resId: Int) {
        loadDrawable(resId, 0)
    }

    fun loadDrawable(@DrawableRes resId: Int, @DrawableRes placeholder: Int) {
        loadDrawable(resId, placeholder, null)
    }

    fun loadDrawable(@DrawableRes resId: Int, @DrawableRes placeholder: Int, @NonNull transformation: Transformation<Bitmap?>?) {
        getImageLoader().load(resId, placeholder, transformation)
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (enableState) {
            alpha = if (isPressed) {
                pressedAlpha
            } else if (!isEnabled) {
                unableAlpha
            } else {
                1.0f
            }
        }
    }

    fun enableState(enableState: Boolean): GlideImageView? {
        this.enableState = enableState
        return this
    }

    fun pressedAlpha(pressedAlpha: Float): GlideImageView? {
        this.pressedAlpha = pressedAlpha
        return this
    }

    fun unableAlpha(unableAlpha: Float): GlideImageView? {
        this.unableAlpha = unableAlpha
        return this
    }
}