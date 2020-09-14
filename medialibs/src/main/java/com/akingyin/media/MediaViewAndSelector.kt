/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media

import android.app.Activity
import androidx.fragment.app.Fragment
import com.akingyin.media.model.ImageTextList
import com.akingyin.media.model.ImageTextTypeList
import java.lang.ref.WeakReference

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/14 15:51
 * @version V1.0
 */
class MediaViewAndSelector  private constructor(activity:Activity?, fragment: Fragment?){
    private var mActivity: WeakReference<Activity>? = null
    private var mFragment: WeakReference<Fragment>? = null

    /** 是否支持下载 */
    private  var   supportDownload = false

    /** 是否支持选择 */
    private  var   supportCheck = false

    /** 选择最大数量 */
    private  var   selectMaxLen = 9

    /** 选择类型 */
    private  var   selectMediaType = MediaMimeType.ofAll()


    init {
        activity?.let {
            mActivity = WeakReference(it)
        }
        fragment?.let {
            mFragment = WeakReference(it)
        }
    }



    class  BuildConfig{
        /** 是否支持下载 */
        var   supportDownload = false


        /** 是否支持选择 */
        var   supportCheck = false

        /** 选择最大数量 */
        var   selectMaxLen = 9

        /** 选择类型 */
        var   selectMediaType = MediaMimeType.ofAll()



        fun build(activity: Activity?=null,fragment: Fragment?=null):MediaViewAndSelector{
         return MediaViewAndSelector(activity,fragment).also {
             it.supportCheck = supportCheck
             it.supportDownload = supportDownload
             it.selectMaxLen = selectMaxLen
             it.selectMediaType = selectMediaType

         }
        }


    }






}