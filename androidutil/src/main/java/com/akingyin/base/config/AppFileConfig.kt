/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.config

import android.content.Context

import java.io.File

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/7 18:17
 * @version V1.0
 */
object AppFileConfig {

    /**
     * 应用文件根目录
     */
    var APP_FILE_ROOT = ""

    fun getAppFileRoot(context: Context,appName:String?=""): File {
      return if(appName.isNullOrEmpty()){
          File(context.getExternalFilesDir(null)
                  ?.absoluteFile.toString() )
      }else{
          File(context.getExternalFilesDir(null)
                  ?.absoluteFile.toString() +File.separator+ appName+File.separator)
      }
    }
}