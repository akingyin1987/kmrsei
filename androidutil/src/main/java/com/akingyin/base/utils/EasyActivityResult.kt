/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import java.util.*

/**
 * @ Description:
 * @author king
 * @ Date 2020/8/6 11:44
 * @version V1.0
 */
object EasyActivityResult {

    // 缓存容器, 临时保存进行启动的activity和与之对应的callback实例。用于在接收回传数据时
    private val container = mutableMapOf<String, MutableMap<Int, (resultCode:Int, data: Intent?) -> Unit>>()
    private val codeGenerator = Random()// 用于进行requestCode自动生成的生成器
    private var lastTime = 0L

    @JvmStatic
    fun  getRandomRequestCode() = codeGenerator.nextInt(0x0000FFFF)

    /**
     * 使用 **context.startActivityForResult(intent, requestCode)** 进行页面启动。并绑定callback
     */
    @JvmStatic
    fun startActivity(tag: String,context: Context, intent:Intent, callback:((resultCode:Int, data:Intent?) -> Unit)?) {
        startActivity(tag,context, intent, callback, null)
    }

    @JvmStatic
    fun  onActivityResultCall(tag: String,requestCode: Int,callback:((resultCode:Int, data:Intent?) -> Unit)){
        // 保存回调缓存
        if (container.containsKey(tag)) {
            container[tag]?.put(requestCode, callback)
        } else {
            container[tag] = mutableMapOf(Pair(requestCode, callback))
        }
        println("EasyActivityResult->$tag")
    }

    /**
     * 使用 **context.startActivityForResult(intent, requestCode, options)** 进行页面启动。并绑定callback
     */
    @JvmStatic
    fun startActivity(tag:String,context:Context, intent:Intent, callback:((resultCode:Int, data:Intent?) -> Unit)?, options: Bundle?) {
        val current = System.currentTimeMillis()
        val last = lastTime
        println("EasyActivityResult->$tag")
        lastTime = current
        // 防暴击：两次启动间隔必须大于1秒。
        if (current - last < 1000) {
            return
        }

        if (context !is Activity || callback == null) {
            context.startActivity(intent)
        } else {
            // 自动生成有效的requestCode进行使用。
            val requestCode = codeGenerator.nextInt(0x0000FFFF)
            if (options == null ) {
                context.startActivityForResult(intent, requestCode)
            } else {
                context.startActivityForResult(intent, requestCode, options)
            }

            // 保存回调缓存
            if (container.containsKey(tag)) {
                container[tag]?.put(requestCode, callback)
            } else {
                container[tag] = mutableMapOf(Pair(requestCode, callback))
            }
        }
    }

    @JvmStatic
    fun dispatch(tag:String, requestCode:Int, resultCode:Int, data: Intent?){
        if (!container.containsKey(tag)) {
            return
        }

        // 从缓存中取出与此activity绑定的、与requestCode相匹配的回调。进行回调通知
        container[tag]?.remove(requestCode)?.invoke(resultCode, data)

        // 清理无用的缓存条目。避免内存泄漏
        releaseInvalidItems()
    }
    @JvmStatic
    fun  onRemoveActivityResult(tag: String){
        container.remove(tag)
    }

    private fun releaseInvalidItems() {
        val keys = container.keys
        val iterator = keys.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (container[next]?.isEmpty() == true) {
                iterator.remove()
            }
        }
    }
}