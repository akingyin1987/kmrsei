/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.rxfiledownup.upload


import com.akingyin.base.net.Result
import com.akingyin.base.net.exception.ApiException
import com.akingyin.base.utils.FileUtils
import com.akingyin.rxfiledownup.manager.Failed
import com.akingyin.rxfiledownup.manager.Normal
import com.akingyin.rxfiledownup.manager.Paused
import com.akingyin.rxfiledownup.manager.Status
import com.akingyin.rxfiledownup.util.getBlock
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request

import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


/**
 * @ Description:
 * @author king
 * @ Date 2020/12/10 11:57
 * @version V1.0
 */
class UploadTask(var id: String = "", var filePath: String = "",
                 var url: String = "", var chunck: Int = 0,
                 var okHttpClient: OkHttpClient) {
    private val FILE_MODE = "rwd"

    private var uploadStatus:Status = Normal()

    private var chuncks:Int =0

    //流块

    private val position = 0



     fun run():Result<String>{
      return  try {
          val blockLength = 1024 * 1024
          val file =  File(filePath)
          chuncks = if ((file.length() % blockLength).toInt() == 0) { // 算出总块数
              file.length().toInt() / blockLength
          } else {
              file.length().toInt() / blockLength + 1
          }
          while (chunck< chuncks && (uploadStatus !is  Paused || uploadStatus !is Failed)){
              //上传当前块文件
              val mBlock: ByteArray = file.getBlock((chunck) * blockLength.toLong(), blockLength)
              val builder = MultipartBody.Builder().apply {
                  addFormDataPart("file", FileUtils.getFileName(filePath), mBlock.toRequestBody(MEDIA_TYPE_File))
                  addFormDataPart("chunks", chuncks.toString())
                  addFormDataPart("chunk", chunck.toString())
              }
              val response =  okHttpClient.newCall(Request.Builder().url(url).post(builder.build()).build()).execute()
              if(response.isSuccessful){
                 // val ret = response.body?.string()?:""

                  chunck++
              }else{
                  return Result.Error(ApiException(msg="上传文件失败"))
              }

          }
          Result.Success("", 0L)
        }catch (e: Exception){
            e.printStackTrace()
          Result.Error(ApiException.handleException(e))
        }
    }
    private val MEDIA_TYPE_File: MediaType = ("application/octet-stream").toMediaType()
}