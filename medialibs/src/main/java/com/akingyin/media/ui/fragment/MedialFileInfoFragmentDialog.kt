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
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.akingyin.base.ext.*
import com.akingyin.base.utils.DateUtil
import com.akingyin.base.utils.FileUtils
import com.akingyin.base.utils.HtmlUtils
import com.akingyin.base.utils.StringUtils
import com.akingyin.media.MediaUtils
import com.akingyin.media.databinding.FragmentMedialFileInfoBinding
import com.akingyin.media.engine.ImageEngine
import com.akingyin.media.engine.LocationEngine
import com.akingyin.util.MediaFileUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import me.gujun.android.taggroup.TagGroup
import java.nio.charset.Charset
import kotlin.properties.Delegates


/**
 * @ Description:
 * @author king
 * @ Date 2020/8/7 15:05
 * @version V1.0
 */

@Suppress("DEPRECATION")
class MedialFileInfoFragmentDialog( var locationEngine : LocationEngine?=null, var  imageEngine: ImageEngine?=null ):BottomSheetDialogFragment() {


    /** 修改图片定位权限 */
    private   var   authEditLocation = false

    /** 编辑标签权限*/
    private   var   authEditTag = false
    lateinit var  bindView: FragmentMedialFileInfoBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       bindView = FragmentMedialFileInfoBinding.inflate(inflater, container, false)
        return  bindView.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val screenHeight = getScreenHeight(requireActivity())
        val statusBarHeight = getStatusBarHeight(requireContext())
        val dialogHeight = screenHeight - statusBarHeight

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                if (dialogHeight == 0) ViewGroup.LayoutParams.MATCH_PARENT else dialogHeight)
        setPeekHeight(dialogHeight)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEventAndData()
        initView()
    }

    private fun setPeekHeight(peekHeight: Int) {
        if (peekHeight <= 0) {
            return
        }
        val bottomSheetBehavior = getBottomSheetBehavior()
        bottomSheetBehavior?.peekHeight = peekHeight
    }

    private fun setMaxHeight(maxHeight: Int) {
        if (maxHeight <= 0) {
            return
        }
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, maxHeight)
        dialog?.window?.setGravity(Gravity.BOTTOM)
    }

    private fun getBottomSheetBehavior(): BottomSheetBehavior<View>? {
        val view: View? = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        return view?.let { BottomSheetBehavior.from(view) }
    }

    private   var   filePath :String by Delegates.notNull()
     fun initEventAndData() {
        filePath = arguments?.getString("filePath", "") ?:""
        authEditLocation =arguments?.getBoolean("authEditLocation")?:false
        authEditTag = arguments?.getBoolean("authEditTag")?:false
    }

    @SuppressLint("RestrictedApi")
     fun initView() {
        lifecycleScope.launchWhenCreated {
            tryCatch({
                when {
                    MediaFileUtil.isImageFileType(filePath) -> {
                        val exifInterface = ExifInterface(filePath)
                        bindView.tvDatetime.text = "拍摄时间：{0}".messageFormat(DateUtil.millis2String(exifInterface.dateTime))
                        bindView.tvFilename.text = HtmlUtils.getTextHtml("文件名：{0}<br>  分辨率：{1}X{2}".messageFormat(FileUtils.getFileName(filePath),
                                exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)
                                    ?: "", exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH)
                            ?: ""))
                        bindView.tvFilesize.text = "文件大小：{0}".messageFormat(com.blankj.utilcode.util.FileUtils.getSize(filePath))
                        bindView.tvLocalpath.text = "本地路径：{0}".messageFormat(filePath)
                        exifInterface.latLong?.let {
                            bindView.ivAddLoc.gone()
                            bindView.ivRemoveLoc.visiable()
                            val locType = exifInterface.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD)
                                ?: "bd09ll"

                            bindView.tvFileloc.text = asyncInBackground {
                                locationEngine?.getLocationAddr(it[0], it[1], locType) ?: "未知"
                            }.await()

                            imageEngine?.customLoadImage(requireContext(), asyncInBackground {
                                locationEngine?.getLocationImageUrl(it[0], it[1], locType, filePath)
                                    ?: ""
                            }.await(), bindView.ivLocimg)
                        }
                        if (null == exifInterface.latLong) {
                            bindView.ivAddLoc.visiable()
                            bindView.tvFileloc.text = "无位置信息"
                            if (authEditLocation) {
                                bindView.tvFileloc.visiable()
                                bindView.ivLocimg.setImageURI(null)
                            } else {
                                bindView.ivLocimg.gone()
                                bindView.tvFileloc.gone()
                            }
                            bindView.ivRemoveLoc.gone()
                        }

                        if (authEditLocation) {
                            bindView.ivLocimg.click {
                                exifInterface.latLong?.let {
                                    locationEngine?.getNewLocation("bd09ll", it[0], it[1]) { lat, lng, _ ->
                                        exifInterface.setLatLong(lat, lng)
                                        exifInterface.setAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD, "bd09ll")
                                        exifInterface.saveAttributes()
                                        initView()
                                    }
                                }
                            }
                            bindView.ivRemoveLoc.click {
                                exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, "")
                                exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "")
                                exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, "")
                                exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "")
                                exifInterface.setAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD, "")
                                exifInterface.saveAttributes()
                                initView()
                            }
                            bindView.ivAddLoc.click {
                                locationEngine?.getNewLocation("bd09ll") { lat, lng, _ ->
                                    println("获取到最新的位置$lat,$lng")
                                    exifInterface.setLatLong(lat, lng)
                                    exifInterface.setAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD, "bd09ll")
                                    exifInterface.saveAttributes()
                                    initView()
                                }
                            }
                        }

                        if (!authEditLocation) {
                            bindView.ivAddLoc.gone()
                            bindView.ivRemoveLoc.gone()
                        }
                        val tags = String( exifInterface.getAttributeBytes(ExifInterface.TAG_USER_COMMENT)?: byteArrayOf(), Charset.forName("utf-8"))
                       // val tags = exifInterface.getAttribute(ExifInterface.TAG_USER_COMMENT) ?: ""

                        if (authEditTag) {
                            bindView.tagGroup.gone()
                            bindView.tagGroupEdit.visiable()
                            bindView.tagGroupEdit.setTags(tags.split("@").filter {
                                it.isNotEmpty()
                            })

                            bindView.tagGroupEdit.setOnTagChangeListener(object : TagGroup.OnTagChangeListener {
                                override fun onDelete(tagGroup: TagGroup?, tag: String?) {
                                    val newTags = bindView.tagGroupEdit.tags.joinToString("@") {
                                        it.trim()
                                    }
                                    exifInterface.setAttribute(ExifInterface.TAG_USER_COMMENT, newTags)
                                    exifInterface.saveAttributes()
                                }

                                override fun onAppend(tagGroup: TagGroup?, tag: String?) {
                                    val newTags = bindView.tagGroupEdit.tags.joinToString("@") {
                                        it.trim()
                                    }
                                    exifInterface.setAttribute(ExifInterface.TAG_USER_COMMENT, newTags)
                                    exifInterface.saveAttributes()
                                }
                            })
                        } else {
                            bindView.tagGroup.setTags(tags.split("@").filter {
                                it.isNotEmpty()
                            })
                            bindView.tagGroup.visiable()
                            bindView.tagGroupEdit.gone()
                        }


                    }
                    MediaFileUtil.isAudioFileType(filePath) -> {
                        MediaUtils.extractDuration(requireContext(), filePath).run {
                            bindView.tvDatetime.text = "时长：{0}".messageFormat(DateUtil.formatDurationTime(this))
                        }
                        bindView.tvFilename.text = "文件名：{0}".messageFormat(FileUtils.getFileName(filePath))
                        bindView.tvFilesize.text = "文件大小：{0}".messageFormat(StringUtils.FormetFileSize(FileUtils.getFileSize(filePath)))
                        bindView.tvLocalpath.text = "本地路径：{0}".messageFormat(filePath)
                    }
                    MediaFileUtil.isVideoFileType(filePath) -> {
                        MediaUtils.extractDuration(requireContext(), filePath).run {
                            bindView.tvDatetime.text = "时长：{0}".messageFormat(DateUtil.formatDurationTime(this))
                        }
                        bindView.tvFilename.text = "文件名：{0}".messageFormat(FileUtils.getFileName(filePath))
                        bindView.tvFilesize.text = "文件大小：{0}".messageFormat(StringUtils.FormetFileSize(FileUtils.getFileSize(filePath)))
                        bindView.tvLocalpath.text = "本地路径：{0}".messageFormat(filePath)
                    }
                }
            })

        }

    }

    override fun onDestroyView() {
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent(KEY_MEDIAL_FILE_INFO_DIALOG_ACTION))
        super.onDestroyView()

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }

    private fun getScreenHeight(activity: Activity): Int {
        val displaymetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displaymetrics)
        return displaymetrics.heightPixels
    }

    private fun getStatusBarHeight(context: Context): Int {
        var statusBarHeight = 0
        val res: Resources = context.resources
        val resourceId: Int = res.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight
    }
    companion object{
        const val KEY_MEDIAL_FILE_INFO_DIALOG_ACTION="medial.file.dialog.action"
        fun newInstance(filePath: String, authEditLocation: Boolean = false, authEditTag: Boolean = false, locationEngine : LocationEngine?=null,  imageEngine: ImageEngine?=null): MedialFileInfoFragmentDialog {
            return MedialFileInfoFragmentDialog(locationEngine,imageEngine).apply {
                arguments = Bundle().apply {
                    putString("filePath", filePath)
                    putBoolean("authEditLocation", authEditLocation)
                    putBoolean("authEditTag", authEditTag)
                }

            }
        }
    }
}