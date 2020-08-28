/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media
import android.Manifest
import android.os.Bundle
import com.akingyin.base.BaseNfcTagActivity
import com.akingyin.base.config.AppFileConfig
import com.akingyin.base.dialog.MaterialDialogUtil
import com.akingyin.base.utils.FileUtils
import com.akingyin.base.utils.StringUtils
import com.akingyin.media.engine.ImageEngine
import com.akingyin.media.model.LocalMediaData
import permissions.dispatcher.ktx.constructPermissionsRequest



/**
 * @ Description:
 * @author king
 * @ Date 2020/8/26 17:30
 * @version V1.0
 */
abstract class BaseMultimediaActivity<T : LocalMediaData> : BaseNfcTagActivity() {




    override fun initInjection() {

    }


    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }


    override fun startRequest() {

    }



    /**
     * 获取图片加载引擎
     */
    abstract fun getImageEngine(): ImageEngine



    /**
     * 创建数据
     */
    abstract fun onCreateMediaData(): T


    /**
     * 保存数据
     */
    abstract fun saveMediaData(mediaData: T)


    /**
     * 图片目录
     */
    open fun getImageDirPath() = AppFileConfig.APP_FILE_ROOT


    /**
     * 视频目录
     */
    open fun getVideoDirPath() = AppFileConfig.APP_FILE_ROOT

    /**
     * 音频目录
     */
    open fun getAudioDirPath() = AppFileConfig.APP_FILE_ROOT


    /**
     * 获取多媒体文件名
     */
    open fun getMediaFileName(@MediaMimeType.MediaType mediaType: Int): String {
        return when (mediaType) {
            MediaMimeType.ofImage() -> StringUtils.getUUID() + ".jpg"
            MediaMimeType.ofAudio() -> StringUtils.getUUID() + ".mp3"
            MediaMimeType.ofVideo() -> StringUtils.getUUID() + ".mp4"
            else -> StringUtils.getUUID()
        }
    }

    /**
     * 添加文本
     */
    open fun onAddOrModifyText(mediaData: T?, postion: Int = -1,callBack: (addMedia: Boolean, data:T,postion:Int) -> Unit) {

        MaterialDialogUtil.showEditDialog(this, "添加文本", mediaData?.mediaText ?: "") {
            mediaData?.let { mediaData ->
                mediaData.mediaText = it
                saveMediaData(mediaData)
                callBack(false,mediaData,postion)

            } ?: onCreateMediaData().apply {
                mediaType = MediaMimeType.ofText()
                mediaText = it
                saveMediaData(this)
                callBack(true,this,postion)
            }
        }
    }

    /**
     * 添加或修改多媒体数据
     */
    open  fun  onAddOrModifyMediaData(mediaData: T?, postion: Int = -1,@MediaMimeType.MediaType mediaType: Int = MediaMimeType.ofImage(),callBack: (addMedia: Boolean, data:T,postion:Int) -> Unit){

        fun   callBack (result: Boolean, localFilePath: String){
            if (result) {
                mediaData?.let {
                    it.mediaLocalPath = localFilePath
                    it.mediaOriginalPath = localFilePath
                    it.mediaServerPath = ""
                    saveMediaData(it)
                    callBack(false,it,postion)
                } ?: onCreateMediaData().apply {
                    this.mediaType = mediaType

                    mediaLocalPath = localFilePath
                    mediaOriginalPath = localFilePath
                    saveMediaData(this)
                    callBack(true,this,postion)
                }
            }
        }
        when(mediaType){
            MediaMimeType.ofImage() ->{
                openCameraTakePicture { result, localFilePath ->
                    callBack(result,localFilePath)
                }
            }
            MediaMimeType.ofVideo() ->{
                openCameraRecordVideo { result, localFilePath ->
                    callBack(result,localFilePath)
                }
            }
            MediaMimeType.ofAudio() ->{
                openRecordAudio { result, localFilePath ->
                    callBack(result,localFilePath)
                }
            }
        }
    }



    /**
     * 打开相机拍照
     */
    open fun openCameraTakePicture(imageName: String = getMediaFileName(MediaMimeType.ofImage()), imageDirPath: String = getImageDirPath(), callBack: (result: Boolean, localFilePath: String) -> Unit) = constructPermissionsRequest(Manifest.permission.CAMERA) {
        startMediaActivityForResult(imageName, imageDirPath,"", REQUEST_CAMERA_TAKE_PIC_CODE,callBack)
    }

    /**
     * 涂鸦
     */
    open fun  openImageTuya(mediaData: T, postion: Int = -1, callBack: (result: Boolean, localFilePath: String) -> Unit){
        if(mediaData.mediaType == MediaMimeType.ofImage()&& FileUtils.isFileExist(mediaData.mediaLocalPath)){
            startMediaActivityForResult(FileUtils.getFileName(mediaData.mediaLocalPath), FileUtils.getFolderName(mediaData.mediaLocalPath), getMediaFileName(MediaMimeType.ofImage()),REQUEST_CAMERA_TAKE_PIC_CODE,callBack)
        }else{
            showError("文件类型不正确或要地文件不存在，无法涂鸦！")
        }

    }
    /**
     * 打开相机录视频
     */
    open fun openCameraRecordVideo(videoName: String = getMediaFileName(MediaMimeType.ofVideo()), videoDirPath: String = getVideoDirPath(), callBack: (result: Boolean, localFilePath: String) -> Unit) = constructPermissionsRequest(Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO) {
        startMediaActivityForResult(videoName, videoDirPath,"", REQUEST_RECORD_VIDEO_CODE,callBack)
    }

    /**
     * 打开录音机录视频
     */
    open fun openRecordAudio(audioName: String = getMediaFileName(MediaMimeType.ofAudio()), audioDirPath: String = getAudioDirPath(), callBack: (result: Boolean, localFilePath: String) -> Unit) = constructPermissionsRequest(
            Manifest.permission.RECORD_AUDIO) {
         startMediaActivityForResult(audioName, audioDirPath,"", REQUEST_RECORD_AUDIO_CODE,callBack)

    }



    /**
     * 启动多媒体界面
     *采用 registerForActivityResult() 代替 startActivityForResult（），简化了数据回调的写法
     *  registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

    }.launch(Intent())
     */
    abstract fun startMediaActivityForResult(fileName: String, fileDirPath: String,tuyaReFileName:String="", request: Int,call:(result:Boolean,localPath:String)->Unit)


    companion object {
        /** 拍照  */
        const val REQUEST_CAMERA_TAKE_PIC_CODE = 1000

        /** 视频 */
        const val REQUEST_RECORD_VIDEO_CODE = 1001

        /**录音 */
        const val REQUEST_RECORD_AUDIO_CODE = 1002
    }

}