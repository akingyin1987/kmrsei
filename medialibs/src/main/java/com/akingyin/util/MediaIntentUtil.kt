/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.util

import android.app.Activity
import android.content.Intent
import com.akingyin.media.model.ImageTextModel

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/8 11:17
 * @version V1.0
 */
object MediaIntentUtil {
    /** 多媒体文件选择及下载 code */
    const val ACTIVITY_REQUEST_MEDIA_SELECT_DOWNLOAD_CODE = 1000

    fun getMediaSelectAndDownloadItems(requestCode: Int, resultCode: Int, data: Intent?, call: (ImageTextModel) -> Unit) {
        if (requestCode == ACTIVITY_REQUEST_MEDIA_SELECT_DOWNLOAD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.getParcelableArrayListExtra<ImageTextModel>("result")?.forEach {
                    call(it)
                }

            }
        }


    }
}