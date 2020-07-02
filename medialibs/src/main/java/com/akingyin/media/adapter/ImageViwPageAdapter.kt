/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.media.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import cn.jzvd.JzvdStd
import com.akingyin.audio.AudioPlayDialog.Companion.getInstance
import com.akingyin.audio.AudioPlayView
import com.akingyin.audio.AudioUtil.getMediaDuration
import com.akingyin.base.utils.FileUtils
import com.akingyin.base.utils.HtmlUtils
import com.akingyin.media.ImageLoadUtil
import com.akingyin.media.R
import com.akingyin.media.model.ImageTextModel
import com.github.chrisbanes.photoview.PhotoView
import java.io.File
import java.text.MessageFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 *
 * @author king
 * @date 2016/10/10
 */
class ImageViwPageAdapter : BasePageAdapter<ImageTextModel> {
    private val inflater: LayoutInflater
    var fragmentManager: FragmentManager? = null

    constructor(context: Context?) : super(context) {
        inflater = LayoutInflater.from(context)
    }

    constructor(datas: List<ImageTextModel?>?, context: Context?) : super(datas, context) {
        inflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, imageTextModel: ImageTextModel, convertView: View, container: ViewGroup): View {
        val rootview = inflater.inflate(R.layout.item_image_video, null)
        val holder = ViewHolder(rootview, context)
        println("----------------getView---------------")
        holder.bind(imageTextModel, position + 1)
        return rootview
    }

    private inner class ViewHolder(view: View, private val context: Context) {
        val pv_img: PhotoView
        val tv_text: TextView
        val tv_pagenum: TextView
        val card_pic_num: TextView
        val iv_marker_video: ImageView
        val videoplayer: JzvdStd
        val iv_audio_markder: ImageView
        val tv_audio_time: TextView
        val mAudioPlayView: AudioPlayView
        fun bind(imageTextModel: ImageTextModel, postion: Int) {
            if (TextUtils.isEmpty(imageTextModel.title)) {
                card_pic_num.text = MessageFormat.format("{0}", count)
            } else {
                card_pic_num.text = HtmlUtils.getTextHtml("<font color = 'red'>" + imageTextModel.title + "</font>" + count)
            }
            videoplayer.visibility = View.GONE
            iv_marker_video.visibility = View.GONE
            mAudioPlayView.visibility = View.GONE
            tv_pagenum.text = MessageFormat.format("{0}/{1}", postion, count)
            if (!TextUtils.isEmpty(imageTextModel.text)) {
                pv_img.visibility = View.GONE
                tv_text.visibility = View.VISIBLE
                tv_text.text = imageTextModel.text
            } else {
                tv_text.visibility = View.GONE
                pv_img.visibility = View.VISIBLE
                if (null == pv_img.scaleType) {
                    pv_img.scaleType = ImageView.ScaleType.CENTER
                }
                if (imageTextModel.haveNetServer) {
                    try {
                        if (FileUtils.isFileExist(imageTextModel.localPath)) {
                            if (imageTextModel.multimediaType == 2) {
                                pv_img.visibility = View.GONE
                                iv_marker_video.visibility = View.VISIBLE
                                videoplayer.visibility = View.VISIBLE
                                ImageLoadUtil.loadImageLocalFile(imageTextModel.localPath, context, videoplayer.posterImageView)
                                videoplayer.setUp(imageTextModel.localPath, "视频")
                            } else if (imageTextModel.multimediaType == 1) {
                                ImageLoadUtil.loadImageLocalFile(imageTextModel.localPath, context, pv_img)
                            } else if (imageTextModel.multimediaType == 3) {
                                iv_marker_video.visibility = View.GONE
                                videoplayer.visibility = View.GONE
                                pv_img.visibility = View.GONE
                                mAudioPlayView.visibility = View.VISIBLE
                                mAudioPlayView.url = imageTextModel.localPath
                                // iv_audio_markder.setVisibility(View.VISIBLE);
                                // setAudioDuration(tv_audio_time,imageTextModel.localPath);
                            }
                        } else {
                            if (imageTextModel.multimediaType == 1) {
                                ImageLoadUtil.loadImageServerFile(imageTextModel.serverPath, context, pv_img)
                            } else if (imageTextModel.multimediaType == 2) {
                                pv_img.visibility = View.GONE
                                iv_marker_video.visibility = View.VISIBLE
                                videoplayer.visibility = View.VISIBLE
                                ImageLoadUtil.loadImageServerFile(imageTextModel.serverPath, context, videoplayer.posterImageView)
                                videoplayer.setUp(imageTextModel.serverPath, "视频")
                            } else if (imageTextModel.multimediaType == 3) {
                                iv_marker_video.visibility = View.GONE
                                videoplayer.visibility = View.GONE
                                pv_img.visibility = View.GONE
                                mAudioPlayView.visibility = View.VISIBLE
                                mAudioPlayView.url = imageTextModel.serverPath
                                // iv_audio_markder.setVisibility(View.VISIBLE);
                                // setAudioDuration(tv_audio_time,imageTextModel.localPath);
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        if (imageTextModel.multimediaType == 1) {
                            ImageLoadUtil.loadImageServerFile(imageTextModel.serverPath, context, pv_img)
                        } else if (imageTextModel.multimediaType == 2) {
                            pv_img.visibility = View.GONE
                            iv_marker_video.visibility = View.VISIBLE
                            videoplayer.visibility = View.VISIBLE
                            ImageLoadUtil.loadImageServerFile(imageTextModel.serverPath, context, videoplayer.posterImageView)
                            videoplayer.setUp(imageTextModel.serverPath, "视频")
                        } else if (imageTextModel.multimediaType == 3) {
                            iv_marker_video.visibility = View.GONE
                            videoplayer.visibility = View.GONE
                            pv_img.visibility = View.GONE
                            iv_audio_markder.visibility = View.VISIBLE
                            setAudioDuration(tv_audio_time, imageTextModel.localPath)
                        }
                    }
                } else {
                    if (imageTextModel.multimediaType == 1) {
                        ImageLoadUtil.loadImageLocalFile(imageTextModel.localPath, context, pv_img)
                    } else if (imageTextModel.multimediaType == 2) {
                        iv_marker_video.visibility = View.VISIBLE
                        videoplayer.visibility = View.VISIBLE
                        pv_img.visibility = View.GONE
                        ImageLoadUtil.loadImageLocalFile(imageTextModel.localPath, context, videoplayer.posterImageView)
                        videoplayer.setUp(imageTextModel.localPath, "视频")
                    } else if (imageTextModel.multimediaType == 3) {
                        iv_marker_video.visibility = View.GONE
                        videoplayer.visibility = View.GONE
                        pv_img.visibility = View.GONE
                        iv_audio_markder.visibility = View.VISIBLE
                        setAudioDuration(tv_audio_time, imageTextModel.localPath)
                    }
                }
            }
            iv_audio_markder.setOnClickListener {
                if (FileUtils.isFileExist(imageTextModel.localPath)) {
                    try {
                        getInstance(imageTextModel.localPath).show(fragmentManager!!, "audio")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    try {
                        getInstance(imageTextModel.serverPath).show(fragmentManager!!, "audio")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            iv_marker_video.visibility = View.GONE
            iv_marker_video.setOnClickListener {
                try {
                    if (FileUtils.isFileExist(imageTextModel.localPath)) {
                        playVideo(context, imageTextModel.localPath)
                    } else {
                        if (imageTextModel.haveNetServer) {
                            playVideo(context, imageTextModel.serverPath)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        init {
            mAudioPlayView = view.findViewById(R.id.audio_player)
            pv_img = view.findViewById<View>(R.id.pv_img) as PhotoView
            tv_text = view.findViewById<View>(R.id.tv_text) as TextView
            tv_pagenum = view.findViewById<View>(R.id.tv_pagenum) as TextView
            card_pic_num = view.findViewById<View>(R.id.card_pic_num) as TextView
            iv_marker_video = view.findViewById<View>(R.id.iv_marker_video) as ImageView
            videoplayer = view.findViewById(R.id.videoplayer)
            iv_audio_markder = view.findViewById(R.id.iv_audio_markder)
            tv_audio_time = view.findViewById(R.id.tv_audio_time)
        }
    }

    fun setAudioDuration(textView: TextView, localPath: String?) {
        if (TextUtils.isEmpty(localPath)) {
            textView.visibility = View.GONE
        } else {
            val itemDuration = getMediaDuration(localPath)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration.toLong())
            val seconds = (TimeUnit.MILLISECONDS.toSeconds(itemDuration.toLong())
                    - TimeUnit.MINUTES.toSeconds(minutes))
            textView.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    }

    fun playVideo(context: Context, path: String) {
        if (path.startsWith("http")) {
            val extension = MimeTypeMap.getFileExtensionFromUrl(path)
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            val mediaIntent = Intent(Intent.ACTION_VIEW)
            mediaIntent.setDataAndType(Uri.parse(path), mimeType)
            context.startActivity(mediaIntent)
        } else {
            val intent = Intent(Intent.ACTION_VIEW)
            val file = File(path)
            var uri: Uri? = null
            uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(context, "com.zlcdgroup.caims.provider", file)
            } else {
                Uri.fromFile(file)
            }
            intent.setDataAndType(uri, "video/*")
            context.startActivity(intent)
        }
    }
}