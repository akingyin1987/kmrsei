/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.adapter

import android.widget.ImageView
import android.widget.TextView
import com.akingyin.base.ext.click
import com.akingyin.base.ext.gone
import com.akingyin.base.ext.visiable
import com.akingyin.base.mvvm.SingleLiveEvent
import com.akingyin.base.utils.DateUtil
import com.akingyin.base.utils.FileUtils
import com.akingyin.media.MediaConfig
import com.akingyin.media.MediaUtils
import com.akingyin.media.R
import com.akingyin.media.engine.ImageEngine
import com.akingyin.media.model.LocalMediaData
import com.akingyin.media.widget.CheckView
import com.chad.library.adapter.base.BaseProviderMultiAdapter
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder


/**
 * 多媒体适配器
 * @ Description:
 * @author king
 * @ Date 2020/7/15 11:22
 * @version V1.0
 */


class MediaGridListAdapter<T : LocalMediaData>(imageEngine: ImageEngine, supportDel: Boolean = false,
                                               supportChecked: Boolean = false, supportCamera: Boolean = false) : BaseProviderMultiAdapter<T>() {
    /** 选中或被选中消息 */
    var checkLiveEvent: SingleLiveEvent<Int> = SingleLiveEvent()

    /** 删除 */
    var delectLiveEvent: SingleLiveEvent<Int> = SingleLiveEvent()

    init {
        addItemProvider(MediaGridItemProvider(imageEngine, supportDel, supportChecked, checkLiveEvent, delectLiveEvent))
        if (supportCamera) {
            addItemProvider(CameraItemProvide())
        }
    }

    /**
     * 返回 item 类型
     * @param data List<T>
     * @param position Int
     * @return Int
     */
    override fun getItemType(data: List<T>, position: Int): Int {
        return data[position].itemType
    }

    class CameraItemProvide<T : LocalMediaData> : BaseItemProvider<T>() {
        override val itemViewType: Int
            get() = 0
        override val layoutId: Int
            get() = R.layout.item_multimedia_camera

        override fun convert(helper: BaseViewHolder, item: T) {

        }
    }

    class MediaGridItemProvider<T : LocalMediaData>(var imageEngine: ImageEngine, var supportDel: Boolean = false,
                                                    var supportChecked: Boolean = false, var checkLiveEvent: SingleLiveEvent<Int>
                                                    , var delectLiveEvent: SingleLiveEvent<Int>) : BaseItemProvider<T>() {
        override val itemViewType: Int
            get() = 1
        override val layoutId: Int
            get() = R.layout.item_multimedia_grid

        override fun convert(helper: BaseViewHolder, item: T) {
            with(helper) {
                setText(R.id.tv_num, item.mediaSort.toString())
                val ivImage = getView<ImageView>(R.id.iv_image)
                val tvText = getView<TextView>(R.id.tv_text)
                val ivDel = getView<ImageView>(R.id.iv_del)
                val tv_isGif = getView<TextView>(R.id.tv_isGif)
                tv_isGif.gone()
                val tv_duration = getView<TextView>(R.id.tv_duration)
                tv_duration.gone()
                val check_view = getView<CheckView>(R.id.check_view)
                if (supportChecked) {
                    check_view.visiable()
                    check_view.setCountable(true)
                    check_view.setChecked(item.mediaChecked)
                    check_view.click {
                        it.setChecked(!item.mediaChecked)
                        item.mediaChecked = !item.mediaChecked
                        checkLiveEvent.value = bindingAdapterPosition

                    }
                } else {
                    check_view.gone()
                }

                if (supportDel) {
                    ivDel.visiable()
                    ivDel.click {
                        item.mediaDelect = true
                        delectLiveEvent.value = bindingAdapterPosition
                    }
                } else {
                    ivDel.gone()
                }

                when (item.mediaType) {
                    MediaConfig.TYPE_TEXT -> {
                        tvText.visiable()
                        ivImage.gone()
                        setText(R.id.tv_text, item.mediaText)
                    }
                    else -> {
                        tvText.gone()
                        ivImage.visiable()
                        when (item.mediaType) {
                            MediaConfig.TYPE_IMAGE -> {
                                tv_duration.gone()
                                bindImageAndVideo(item, ivImage, tv_isGif, imageEngine)
                            }
                            MediaConfig.TYPE_AUDIO -> {
                                ivImage.setImageResource(R.drawable.picture_audio_placeholder)
                                bindVideoAudioDuration(item, tv_duration)

                            }
                            else -> {
                                bindImageAndVideo(item, ivImage, tv_isGif, imageEngine)
                                bindVideoAudioDuration(item, tv_duration)
                            }
                        }
                    }
                }


            }
        }

        private fun bindVideoAudioDuration(item: T, tv_duration: TextView) {
            tv_duration.visiable()
            when {
                FileUtils.isFileExist(item.mediaLocalPath) -> {
                    tv_duration.visiable()
                    if (item.mediaDuration == 0L) {
                        item.mediaDuration = MediaUtils.extractDuration(context, item.mediaLocalPath)
                    }
                    tv_duration.text = if (item.mediaDuration == 0L) {
                        "未知"
                    } else {
                        DateUtil.formatDurationTime(item.mediaDuration)
                    }
                }
                else -> {
                    tv_duration.text = "未知"
                }
            }
        }

        private fun bindImageAndVideo(data: T, imageView: ImageView, textView: TextView, imageEngine: ImageEngine) {
            if (FileUtils.isFileExist(data.mediaLocalPath)) {
                //如果本地文件存在
                imageEngine.loadGridImage(context, data.mediaLocalPath, imageView)
                textView.gone()
            } else {
                //采用网络获取
                imageEngine.loadGridImage(context, data.mediaServerPath, imageView) {
                    if (it) {
                        textView.visiable()
                        textView.text = "在线"
                    } else {
                        textView.gone()
                    }
                }
            }
        }
    }


}