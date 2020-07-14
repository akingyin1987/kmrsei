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
import com.akingyin.media.GlideApp
import com.akingyin.media.engine.CacheResourcesEngine

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/14 13:43
 * @version V1.0
 */
object GlideCacheEngine : CacheResourcesEngine {

    /**
     * Get the cache path
     * 获取缓存地址
     * @param context
     * @param url
     */
    override fun onCachePath(context: Context, url: String): String? {
        try {
            return GlideApp.with(context).downloadOnly().load(url).submit().get().absolutePath
        }catch (e:Exception){
            e.printStackTrace()
        }
        return ""
    }
}