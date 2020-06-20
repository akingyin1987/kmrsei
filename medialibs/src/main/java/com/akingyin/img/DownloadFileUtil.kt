/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.img

import com.akingyin.base.net.okhttp.OkHttpUtils
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * 文件下载
 * @ Description:
 * @author king
 * @ Date 2020/6/19 16:52
 * @version V1.0
 */
object DownloadFileUtil {


    /**
     * 通过协程下载文件
     */
    fun  coordinateDownloadFile(url:String,dir:String,fileName:String,callBack:(Boolean)->Unit):Job{
       return GlobalScope.launch(Main) {
            withContext(IO){
                asycDownloadFile(url,dir,fileName){
                    _, _ ->
                }
            }?.let {
               callBack(true)
            }?:callBack.invoke(false)
        }
    }

    fun   downloadRxFile(url:String,dir:String,fileName:String):Observable<File?>{
         return Observable.just(url).map {
            asycDownloadFile(url,dir,fileName){
                 _, _ ->
             }
        }
    }

    fun   asycDownloadFile(url:String,dir:String,fileName:String,onProgress:(progress:Long,total:Long)->Unit):File?{
        if(url.isEmpty() || dir.isEmpty() || fileName.isEmpty()){
            return null
        }
        File(dir).mkdirs()
        val response = OkHttpUtils.getInstance().newCall(Request.Builder().url(url).build()).execute()
        if(response.isSuccessful){
            var input: InputStream? = null
            var buf = ByteArray(2048)
            var len = 0
            var fos: FileOutputStream? = null
            try {
                response.body?.let {
                    responseBody ->
                    val total = responseBody.contentLength()
                    var current: Long = 0
                    input = responseBody.byteStream()
                    fos = FileOutputStream(File(dir,fileName))
                    while (input!!.read(buf).also { len = it } != -1) {
                        current += len.toLong()
                        fos?.write(buf, 0, len)
                        onProgress.invoke(current,total)

                    }
                    fos?.flush()
                }

            }catch (e:Exception){
                e.printStackTrace()
            }finally {
                input?.close()
                fos?.close()
            }
        }
        return File(dir,fileName)
    }
}