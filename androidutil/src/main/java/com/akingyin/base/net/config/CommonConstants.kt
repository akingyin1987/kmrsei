/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.base.net.config

/**
 * Created by Administrator on 2017/9/12.
 */
object CommonConstants {
    const val CACHE_SP_NAME = "sp_cache" //默认SharedPreferences缓存目录
    const val CACHE_DISK_DIR = "disk_cache" //默认磁盘缓存目录
    const val CACHE_HTTP_DIR = "http_cache" //默认HTTP缓存目录
    const val MAX_AGE_ONLINE = 60 //默认最大在线缓存时间（秒）
    const val MAX_AGE_OFFLINE = 24 * 60 * 60 //默认最大离线缓存时间（秒）
    const val COOKIE_PREFS = "Cookies_Prefs" //默认Cookie缓存目录
    const val DEFAULT_TIMEOUT = 60 //默认超时时间
    const val DEFAULT_MAX_IDLE_CONNECTIONS = 5 //默认空闲连接数
    const val DEFAULT_KEEP_ALIVE_DURATION: Long = 8 //默认心跳间隔时长
    const val CACHE_MAX_SIZE = 10 * 1024 * 1024 //默认最大缓存大小
            .toLong()
    var API_HOST = "http://114.215.108.130/PRDMSKFB/" //默认API主机地址
    var token = "" //token
    var imei = ""
    var refreshToken = "" //刷新 使用token
}