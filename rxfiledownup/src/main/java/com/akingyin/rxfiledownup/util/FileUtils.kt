/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.rxfiledownup.util

import com.akingyin.rxfiledownup.record.Task
import java.io.File
import java.io.RandomAccessFile
import java.nio.channels.FileChannel

/**
 * @ Description:
 * @author king
 * @ Date 2020/12/10 11:36
 * @version V1.0
 */

fun File.shadow(): File {
    val shadowPath = "$canonicalPath.download"
    return File(shadowPath)
}

fun File.tmp(): File {
    val tmpPath = "$canonicalPath.tmp"
    return File(tmpPath)
}

fun File.recreate(length: Long = 0L, block: () -> Unit = {}) {
    delete()
    val created = createNewFile()
    if (created) {
        setLength(length)
        block()
    } else {
        throw IllegalStateException("File create failed!")
    }
}

fun File.setLength(length: Long = 0L) {
    RandomAccessFile(this, "rw").setLength(length)
}

fun File.channel(): FileChannel {
    return RandomAccessFile(this, "rw").channel
}

fun File.clear() {
    val shadow = shadow()
    val tmp = tmp()
    shadow.delete()
    tmp.delete()
    delete()
}



fun File.getBlock(offset: Long, blockSize: Int): ByteArray {
    val result = ByteArray(blockSize)

   return RandomAccessFile(this,"r").use {
            it.seek(offset)
       when (val readSize = it.read(result)) {
           -1 -> {
               return@use ByteArray(blockSize)
           }
           blockSize -> {
               return@use result
           }
           else -> {
               val tmpByte = ByteArray(readSize)
               System.arraycopy(result, 0, tmpByte, 0, readSize)
               tmpByte
           }
       }

        }




}

//internal fun Task.getDir(): File {
//    return File(savePath)
//}
//
//internal fun Task.getFile(): File {
//    return File(savePath, saveName)
//}