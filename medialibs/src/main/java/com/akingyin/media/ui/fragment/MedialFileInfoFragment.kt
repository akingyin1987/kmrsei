/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import com.akingyin.base.SimpleFragment
import com.akingyin.base.ext.*
import com.akingyin.base.utils.DateUtil
import com.akingyin.base.utils.FileUtils
import com.akingyin.base.utils.HtmlUtils

import com.akingyin.media.R
import com.akingyin.media.databinding.FragmentMedialFileInfoBinding
import com.akingyin.media.engine.ImageEngine
import com.akingyin.media.engine.LocationEngine
import com.akingyin.util.MediaFileUtil

import kotlin.properties.Delegates


/**
 * 文件详情
 * @ Description:
 * @author king
 * @ Date 2020/8/7 11:13
 * @version V1.0
 */
open class MedialFileInfoFragment :SimpleFragment(){

    var  imageEngine:ImageEngine?=null
    var locationEngine : LocationEngine?=null
    /** 修改图片定位权限 */
    private   var   authEditLocation = false

    /** 编辑标签权限*/
    private   var   authEditTag = false
    override fun injection() {

    }

    override fun getLayoutId()= R.layout.fragment_medial_file_info

    override fun useViewBind()=true

    lateinit var  bindView: FragmentMedialFileInfoBinding
    override fun initViewBind(inflater: LayoutInflater, container: ViewGroup?): View? {
       bindView = FragmentMedialFileInfoBinding.inflate(inflater,container,false)
       return  bindView.root
    }

    private   var   filePath :String by Delegates.notNull()
    override fun initEventAndData() {
        filePath = arguments?.getString("filePath","") ?:""
        authEditLocation =arguments?.getBoolean("authEditLocation")?:false
        authEditTag = arguments?.getBoolean("authEditTag")?:false
    }

    @SuppressLint("RestrictedApi")
    override fun initView() {
        lifecycleScope.launchWhenResumed {
            tryCatch({
                when{
                    MediaFileUtil.isImageFileType(filePath)->{
                        val exifInterface = ExifInterface(filePath)
                        bindView.tvDatetime.text ="拍摄时间：{0}".messageFormat(DateUtil.millis2String(exifInterface.dateTime))
                        bindView.tvFilename.text = HtmlUtils.getTextHtml("文件名：{0}<br>  分辨率：{1}X{2}".messageFormat(FileUtils.getFileName(filePath),
                                exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)?:"",exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH)?:""))
                        bindView.tvFilesize.text = "文件大小：{0}".messageFormat(com.blankj.utilcode.util.FileUtils.getSize(filePath))
                        bindView.tvLocalpath.text="本地路径：{0}".messageFormat(filePath)
                        exifInterface.latLong?.let {
                            bindView.ivAddLoc.gone()
                            bindView.ivRemoveLoc.visiable()
                            val locType = exifInterface.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD)?:"bd09ll"
                            bindView.tvFileloc.text= withIO {
                                locationEngine?.getLocationAddr(it[0],it[1],locType)?:"未知"
                            }

                            imageEngine?.loadImage(requireContext(), withIO {
                                locationEngine?.getLocationImageUrl(it[0],it[1],locType,filePath)?:""
                            },bindView.ivLocimg)
                        }
                        if(null == exifInterface.latLong){
                            bindView.ivAddLoc.visiable()
                            if(authEditLocation){
                                bindView.tvFileloc.visiable()
                            }else{
                                bindView.tvFileloc.gone()
                            }
                            bindView.ivRemoveLoc.gone()
                        }
                        if(!authEditLocation){
                            bindView.ivAddLoc.gone()
                            bindView.ivRemoveLoc.gone()
                        }
                        val tags =  exifInterface.getAttribute(ExifInterface.TAG_USER_COMMENT)?:""
                        if(authEditTag){
                            bindView.tagGroup.gone()
                            bindView.tagGroupEdit.visiable()
                            bindView.tagGroupEdit.setTags(tags.split("@"))
                            bindView.tagGroupEdit.setOnTagClickListener {
                                val newTags =  bindView.tagGroupEdit.tags.joinToString("@")
                                exifInterface.setAttribute(ExifInterface.TAG_USER_COMMENT,newTags)
                                exifInterface.saveAttributes()
                            }
                        }else{
                            bindView.tagGroup.setTags(tags.split("@"))
                            bindView.tagGroup.visiable()
                            bindView.tagGroupEdit.gone()
                        }


                    }
                    MediaFileUtil.isAudioFileType(filePath)->{

                    }
                    MediaFileUtil.isVideoFileType(filePath)->{

                    }
                }
            })

        }

    }

    override fun lazyLoad() {

    }

    companion object{
        fun newInstance(filePath:String,authEditLocation:Boolean = false,authEditTag:Boolean = false): MedialFileInfoFragment {
            return MedialFileInfoFragment().apply {
                arguments = Bundle().apply {

                    putString("filePath", filePath)
                    putBoolean("authEditLocation",authEditLocation)
                    putBoolean("authEditTag",authEditTag)

                }
            }
        }
    }
}