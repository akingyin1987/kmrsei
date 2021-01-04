/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.rxfiledownup.upload

import androidx.test.platform.app.InstrumentationRegistry
import com.akingyin.base.utils.RandomUtil
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import java.io.File

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2021/1/4 11:23
 */
class UploadTaskTest {

    lateinit var okHttpClient: OkHttpClient
    lateinit var file:File


    @Before
    fun setUp() {
        okHttpClient = OkHttpClient()
        val context  = InstrumentationRegistry.getInstrumentation().context
        file = File(context.getExternalFilesDir(null),"1.jpg")

    }



    @After
    fun tearDown() {
    }

    @Test
    fun run() {
        var  uploadTask = UploadTask(RandomUtil.randomUUID,filePath = file.absolutePath,okHttpClient=okHttpClient)
        uploadTask.run()
    }
}