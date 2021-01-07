/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.base.net.okhttp


import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

import java.util.*

/**
 * Created by zlcd on 2015/12/29.
 */
object OkHttpUtils {

    private val MEDIA_TYPE_JPG: MediaType = "application/octet-stream".toMediaType()
    private val MEDIA_TYPE_TEXT: MediaType = "text/plain".toMediaType()

    /**
     * 创建文件类
     *
     * @param file
     * @return
     */
    fun onCreateFileBody(file: File): RequestBody {

        return  file.asRequestBody(MEDIA_TYPE_JPG)
    }

    /**
     * 非文件类
     *
     * @param value
     * @return
     */
    fun onCreateTextBody(value: String): RequestBody {
       return value.toRequestBody(MEDIA_TYPE_TEXT)

    }

    /**
     * 创建文件
     * @param file
     * @return
     */
    fun createMultiFilepart(name:String,fileName:String, file: String): MultipartBody {
        val builder = MultipartBody.Builder()
        builder.addFormDataPart(name, fileName, onCreateFileBody(File(file)))
        return builder.build()
    }

    /**
     * 创建文件并带参数
     * @param key
     * @param value
     * @param file
     * @param params
     * @return
     */
    fun createMultiFilepart(name:String,fileName:String, file: String, params: Map<String, String>?): MultipartBody {
        val builder = MultipartBody.Builder()
        builder.addFormDataPart(name, fileName, onCreateFileBody(File(file)))
        if (null != params && params.isNotEmpty()) {
            for (s in params.keys) {
                params[s]?.let { builder.addFormDataPart(s, it) }
            }
        }
        return builder.build()
    }


    /**
     * 创建多文件上传
     * @param files
     * @param filepart
     * @param params
     * @return
     */
    fun createMultipartBodyFileParts(files: List<String>, filepart: String, params: Map<String, String>?): List<MultipartBody.Part> {
        val parts: MutableList<MultipartBody.Part> = LinkedList()
        for (path in files) {
            try {
                val file = File(path)
                parts.add(MultipartBody.Part.createFormData(filepart,file.name,onCreateFileBody(file)))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        params?.let {params->
            for (keys in params.keys) {
                params[keys]?.let { MultipartBody.Part.createFormData(keys, it) }?.let { parts.add(it) }
            }
        }

        return parts
    }



    /**
     * 下载文件
     * @param okHttpClient
     * @param fileUrl
     * @param destFileDir
     * @param callBack
     */
    fun downLoadFile(okHttpClient: OkHttpClient, fileUrl: String, destFileDir: String, success:()->Unit,failure:(error:String)->Unit,progress:(total:Long, current:Long)->Unit) {
        val file = File(destFileDir, fileUrl)
        if (file.exists()) {
            return
        }
        val request = Request.Builder().url(fileUrl).build()
        val call = okHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
               failure.invoke("出错了,${e.message}")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {



                try {
                    if(response.isSuccessful){
                        response.body?.let {body->
                            val total = body.contentLength()
                            var current: Long = 0
                            body.byteStream().use {input->
                                val buf = ByteArray(2048)
                                var len: Int
                                FileOutputStream(file).use {fos->
                                    while (input.read(buf).also { len = it  }!= -1){
                                        current+=len.toLong()
                                        fos.write(buf,0,len)
                                        progress.invoke(total,current)
                                    }
                                    fos.flush()
                                }

                            }
                          success.invoke()
                        }?:failure.invoke("文件内容为空")

                    }else{
                        failure.invoke("下载文件失败,错误码：${response.code}")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                   failure.invoke("出错了,${e.message}")
                }
            }
        })
    }


}