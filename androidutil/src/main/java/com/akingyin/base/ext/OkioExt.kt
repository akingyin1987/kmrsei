/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.ext

import okio.BufferedSource
import okio.buffer
import okio.sink
import java.io.File
import java.io.OutputStream

/**
 * @ Description:
 * @author king
 * @ Date 2021/3/12 16:36
 * @version V1.0
 */

fun  BufferedSource.saveTo(stream: OutputStream){
    use { input ->
        stream.sink().buffer().use {
            it.writeAll(input)
            it.flush()
        }
    }

}

fun BufferedSource.saveTo(file: File){
    try {
        file.parentFile?.mkdirs()
        saveTo(file.outputStream())
    }catch (e:Exception){
        e.printStackTrace()
    }
}