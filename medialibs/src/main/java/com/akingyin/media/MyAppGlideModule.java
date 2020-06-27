package com.akingyin.media;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;

/**
 * @ Description:
 * @author king
 * @ Date 2017/8/28 16:47
 * @ Version V1.0
 */

@GlideModule
public final class MyAppGlideModule  extends AppGlideModule {
  @Override public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {

    if(BuildConfig.DEBUG){
      builder.setLogLevel(Log.DEBUG);
    }

    int memoryCacheSizeBytes = 1024 * 1024 * 20;
    builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));

    int bitmapPoolSizeBytes = 1024 * 1024 * 30;
    builder.setBitmapPool(new LruBitmapPool(bitmapPoolSizeBytes));

    int diskCacheSizeBytes = 1024*1024*100;
    builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskCacheSizeBytes));
  }
}
