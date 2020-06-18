/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.taskmanager

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @ Description:
 * @author king
 * @ Date 2020/6/17 11:49
 * @version V1.0
 */
class ThreadManage private constructor(){
    @Volatile
    private var singleton: ExecutorService? = null

    fun createPool(poolSize: Int): ExecutorService? {
        if (singleton == null) {
            synchronized(ThreadManage::class.java) {
                if (singleton == null) {
                    singleton = Executors.newFixedThreadPool(poolSize)
                }
            }
        }
        return singleton
    }


    fun shutdown() {
        singleton?.shutdown()
        singleton = null
    }

    fun  shutdownNow(){
        singleton?.shutdownNow()
        singleton = null
    }



}