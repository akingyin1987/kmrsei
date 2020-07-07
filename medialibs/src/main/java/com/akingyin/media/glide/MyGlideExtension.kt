/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.glide

import androidx.annotation.NonNull
import com.akingyin.media.R
import com.bumptech.glide.annotation.GlideExtension
import com.bumptech.glide.annotation.GlideOption
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.BaseRequestOptions


/**
 * @ Description:
 * @author king
 * @ Date 2020/7/7 11:24
 * @version V1.0
 */
@GlideExtension
object MyGlideExtension {

    /**
     * 默认占位图
     */
    private val ICON_DEFAULT_PLACEHOLDER: Int by lazy {
        R.drawable.ic_image_loading_layer
    }

    @NonNull
    @GlideOption
    @JvmStatic
    fun applyDefaultImage(options: BaseRequestOptions<*>): BaseRequestOptions<*>? {
        return options
                .fitCenter()
                .placeholder(ICON_DEFAULT_PLACEHOLDER)
                .error(R.drawable.ic_image_loading_error)
                .format(DecodeFormat.PREFER_RGB_565)

    }

}