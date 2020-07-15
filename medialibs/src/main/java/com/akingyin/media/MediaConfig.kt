/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media

import android.view.View
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/14 11:58
 * @version V1.0
 */
object MediaConfig {

    const val APPLY_STORAGE_PERMISSIONS_CODE = 1
    const val APPLY_CAMERA_PERMISSIONS_CODE = 2
    const val APPLY_AUDIO_PERMISSIONS_CODE = 3
    const val APPLY_RECORD_AUDIO_PERMISSIONS_CODE = 4
    const val APPLY_CAMERA_STORAGE_PERMISSIONS_CODE = 5

    const val EXTRA_MEDIA_KEY = "mediaKey"
    const val EXTRA_MEDIA_PATH = "mediaPath"
    const val EXTRA_AUDIO_PATH = "audioPath"
    const val EXTRA_VIDEO_PATH = "videoPath"
    const val EXTRA_PREVIEW_VIDEO = "isExternalPreviewVideo"
    const val EXTRA_PREVIEW_DELETE_POSITION = "position"
    const val EXTRA_FC_TAG = "picture"
    const val EXTRA_RESULT_SELECTION = "extra_result_media"
    const val EXTRA_PREVIEW_SELECT_LIST = "previewSelectList"
    const val EXTRA_SELECT_LIST = "selectList"
    const val EXTRA_COMPLETE_SELECTED = "isCompleteOrSelected"
    const val EXTRA_CHANGE_SELECTED_DATA = "isChangeSelectedData"
    const val EXTRA_CHANGE_ORIGINAL = "isOriginal"
    const val EXTRA_POSITION = "position"
    const val EXTRA_OLD_CURRENT_LIST_SIZE = "oldCurrentListSize"
    const val EXTRA_DIRECTORY_PATH = "directory_path"
    const val EXTRA_BOTTOM_PREVIEW = "bottom_preview"
    const val EXTRA_CONFIG = "PictureSelectorConfig"
    const val EXTRA_SHOW_CAMERA = "isShowCamera"
    const val EXTRA_IS_CURRENT_DIRECTORY = "currentDirectory"
    const val EXTRA_BUCKET_ID = "bucket_id"
    const val EXTRA_PAGE = "page"
    const val EXTRA_DATA_COUNT = "count"
    const val CAMERA_FACING = "android.intent.extras.CAMERA_FACING"

    const val EXTRA_ALL_FOLDER_SIZE = "all_folder_size"
    const val EXTRA_QUICK_CAPTURE = "android.intent.extra.quickCapture"

    const val MAX_PAGE_SIZE = 60

    const val MIN_PAGE_SIZE = 10

    const val LOADED = 0

    const val NORMAL = -1

    const val CAMERA_BEFORE = 1



    /** 所有类型 */
    const val TYPE_ALL = 0
    /** 图片类型 */
    const val TYPE_IMAGE = 1

    /** 视频类型 */
    const val TYPE_VIDEO = 2

    /** 声音文件类型 */
    const val TYPE_AUDIO = 3

    /** 文本类型 */
    const val TYPE_TEXT = 4

    const val MAX_COMPRESS_SIZE = 100

    const val TYPE_CAMERA = 1
    const val TYPE_PICTURE = 2

    const val SINGLE = 1
    const val MULTIPLE = 2

    const val PREVIEW_VIDEO_CODE = 166
    const val CHOOSE_REQUEST = 188
    const val REQUEST_CAMERA = 909
}