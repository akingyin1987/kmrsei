package com.akingyin.media

import android.content.Context
import android.util.Log
import com.akingyin.media.glide.progress.ProgressManager.getGlideOkHttpClient
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import java.io.InputStream

/**
 * @ Description:
 * @author king
 * @ Date 2017/8/28 16:47
 * @ Version V1.0
 */
@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        println("AppGlideModule->applyOptions")
        if (BuildConfig.DEBUG) {
            builder.setLogLevel(Log.DEBUG)
        }
        val memoryCacheSizeBytes = 1024 * 1024 * 20
        builder.setMemoryCache(LruResourceCache(memoryCacheSizeBytes.toLong()))
        val bitmapPoolSizeBytes = 1024 * 1024 * 30
        builder.setBitmapPool(LruBitmapPool(bitmapPoolSizeBytes.toLong()))
        val diskCacheSizeBytes = 1024 * 1024 * 100
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, diskCacheSizeBytes.toLong()))
    }

    override fun registerComponents(context: Context, glide: Glide,
                                    registry: Registry) {
        super.registerComponents(context, glide, registry)
        println("registerComponents ->")
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(getGlideOkHttpClient()))
    }
}