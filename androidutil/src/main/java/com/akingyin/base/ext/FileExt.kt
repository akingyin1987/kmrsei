package com.akingyin.base.ext


import android.util.Base64

import java.io.*
import java.nio.channels.FileChannel
import java.security.MessageDigest


/**
 * @ Description:
 * @author king
 * @ Date 2019/4/29 15:21
 * @version V1.0
 */
fun File.copy(dest: File) {

    var fi: FileInputStream? = null
    var fo: FileOutputStream? = null
    var ic: FileChannel? = null
    var oc: FileChannel? = null
    try {
        if (!dest.exists()) {
            dest.createNewFile()
        }
        fi = FileInputStream(this)
        fo = FileOutputStream(dest)
        ic = fi.channel
        oc = fo.channel
        ic.transferTo(0, ic.size(), oc)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        fi?.close()
        fo?.close()
        ic?.close()
        oc?.close()
    }
}

fun File.move(dest: File) {
    copy(dest)
    delete()
}

fun File.copyDirectory(dest: File) {
    if (!dest.exists()) {
        dest.mkdirs()
    }
    val files = listFiles()
    files?.forEach {
        if (it.isFile) {
            it.copy(File("${dest.absolutePath}/${it.name}"))
        }
        if (it.isDirectory) {
            val dirSrc = File("$absolutePath/${it.name}")
            val dirDest = File("${dest.absolutePath}/${it.name}")
            dirSrc.copyDirectory(dirDest)
        }
    }
}

fun File.moveDirectory(dest: File) {
    copyDirectory(dest)
    deleteAll()
}

fun File.deleteAll() {
    if (isFile && exists()) {
        delete()
        return
    }
    if (isDirectory) {
        val files = listFiles()
        if (files == null || files.isEmpty()) {
            delete()
            return
        }
        files.forEach { it.deleteAll() }
        delete()
    }
}

fun File.md5(): String {
    if (!this.isFile) {
        return ""
    }
    return encryptFile(this, "MD5")
}

fun File.sha1(): String? {
    if (!this.isFile) {
        return null
    }
    return encryptFile(this, "SHA-1")
}

fun File.toBase64():String{
    if(!isFile){
        return ""
    }
   return FileInputStream(this).use { input->
        // 缓存流
         BufferedInputStream(input).use { bufInput->
             // 先把二进制流写入到ByteArrayOutputStream中

             ByteArrayOutputStream().use {
                 byteArray->
                 val bt = ByteArray(4096)
                 var len: Int
                 while (bufInput.read(bt).also { len = it } != -1) {
                     byteArray.write(bt, 0, len)
                 }
                  byteArray.flush()
                  Base64.encodeToString(byteArray.toByteArray(),Base64.DEFAULT)
             }

         }


    }

}

private fun encryptFile(file: File, type: String): String {
    val digest: MessageDigest = MessageDigest.getInstance(type)

     FileInputStream(file).use {input->
         val buffer = ByteArray(1024)

         var len: Int
         while (input.read(buffer).also { len = it } != -1) {
             digest.update(buffer, 0, len)
         }
     }

    return bytes2Hex(digest.digest())
}

fun File.toByteArray(): ByteArray {
  return  FileInputStream(this).use { input ->
      // 缓存流
      BufferedInputStream(input).use { bufInput ->
          // 先把二进制流写入到ByteArrayOutputStream中

          ByteArrayOutputStream().use { byteArray ->
              val bt = ByteArray(1024)
              var len: Int
              while (bufInput.read(bt).also { len = it } != -1) {
                  byteArray.write(bt, 0, len)
              }
              byteArray.flush()
              byteArray.toByteArray()
          }

      }
  }
}