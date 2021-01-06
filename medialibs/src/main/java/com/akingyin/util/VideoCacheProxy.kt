package com.akingyin.util

import android.content.Context
import com.danikula.videocache.HttpProxyCacheServer


class VideoCacheProxy private constructor(){
    private var proxy: HttpProxyCacheServer? = null


    companion object{
        @Volatile
        private var instance: VideoCacheProxy? = null


        fun getInstance(): VideoCacheProxy {
            return instance ?: synchronized(this) {
                instance ?: VideoCacheProxy().also { instance = it }
            }
        }

        fun getProxy(context: Context):HttpProxyCacheServer{
            return getInstance().proxy?: synchronized(this){
                HttpProxyCacheServer.Builder(context)
                    .maxCacheSize(512 * 1024 * 1024).build()
                    .also {
                    getInstance().proxy = it
                }
            }
        }
    }

}