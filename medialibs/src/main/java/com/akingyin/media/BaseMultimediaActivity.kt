/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.akingyin.base.SimpleActivity
import com.akingyin.base.ext.withIO
import com.akingyin.media.adapter.MediaGridListAdapter
import com.akingyin.media.engine.ImageEngine
import com.akingyin.media.model.LocalMediaData


/**
 * @ Description:
 * @author king
 * @ Date 2020/8/26 17:30
 * @version V1.0
 */
abstract class BaseMultimediaActivity<T : LocalMediaData> : SimpleActivity() {

     lateinit var  mediaGridListAdapter : MediaGridListAdapter<T>

    private  var  title = "图文制作"

    /**
     * 基础数据
     */
    private  var  datas: MutableList<T>  = mutableListOf()



    override fun initInjection() {

    }



    override fun initializationData(savedInstanceState: Bundle?) {
        mediaGridListAdapter = MediaGridListAdapter(getImageEngine(),supportGridAdapterDelMedia(),supportGridAdapterCheck(),supportGridAdapterCamera())
        lifecycleScope.launchWhenCreated {
            showLoading()
           withIO {
               onLoadMediaData()
           }.let {
               datas.addAll(it)

               hideLoadDialog()
           }
        }
    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }




    override fun startRequest() {

    }

    /**
     * 加载基础数据
     */
    abstract suspend fun  onLoadMediaData():MutableList<T>

    /**
     * 获取图片加载引擎
     */
    abstract fun getImageEngine():ImageEngine


    /**
     * 适配器是否支持删除
     */
    open  fun  supportGridAdapterDelMedia() = false

    /**
     * 适配器是否支持拍照
     */
    open fun  supportGridAdapterCamera() = false

    /**
     * 适配器是否支持选中
     */
    open fun  supportGridAdapterCheck() = false

}