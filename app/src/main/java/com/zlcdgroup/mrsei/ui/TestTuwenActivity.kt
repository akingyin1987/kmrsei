package com.zlcdgroup.mrsei.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder
import cafe.adriel.androidaudiorecorder.model.AudioChannel
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate
import cafe.adriel.androidaudiorecorder.model.AudioSource
import com.akingyin.base.ext.appServerTime
import com.akingyin.base.ext.startActivityForResult

import com.akingyin.base.utils.FileUtils
import com.akingyin.base.utils.StringUtils
import com.akingyin.media.callback.AppCallBack3
import com.akingyin.media.model.MultimediaEnum
import com.akingyin.media.model.ValMessage
import com.akingyin.media.multimedia.AbsCreateMultimediaActivity
import com.akingyin.media.multimedia.BaseMultimediaActivity
import com.akingyin.media.multimedia.MultimediaHelper

import com.zlcdgroup.mrsei.data.db.dao.ImageTextEntityDao
import com.zlcdgroup.mrsei.data.db.help.DbCore
import com.zlcdgroup.mrsei.data.entity.ImageTextEntity
import java.io.File

/**
 * @ Description:
 * @author king
 * @ Date 2019/11/22 15:07
 * @version V1.0
 */
class TestTuwenActivity : BaseMultimediaActivity<ImageTextEntity>() {
      val  imageTextEntityDao:ImageTextEntityDao by lazy {
          DbCore.getDaoSession().imageTextEntityDao
      }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // imageTextEntityDao = DbCore.getDaoSession().imageTextEntityDao
    }

    override fun getStringTitle()="图文测试"




    override fun createObject(): ImageTextEntity {
        return  ImageTextEntity()
    }


    override fun getPasteData(): MutableList<ImageTextEntity> {
        return  imageTextEntityDao.queryBuilder().orderDesc(ImageTextEntityDao.Properties.BySort).list()
    }

    override fun saveData(t: ImageTextEntity) {
        imageTextEntityDao.save(t)
    }

    override fun saveCopyData(t: ImageTextEntity) {
        imageTextEntityDao.save(t)
    }

    override fun delectData(t: ImageTextEntity) {
        imageTextEntityDao.delete(t)
    }

    override fun goToPasteEditActivity() {

    }



    override fun getNowTime() = appServerTime

    override fun defaultAddMultimedia(): MultimediaEnum? {
        return null
    }

    override fun onDataChange(time: Long) {
    }

    override fun goToSettingActivity() {
    }

    override fun initDatas(datas: MutableList<ImageTextEntity>) {
        datas.addAll(imageTextEntityDao.queryBuilder().orderAsc(ImageTextEntityDao.Properties.BySort).list())
    }

    override fun onAddText(text: String?, callBack3: AppCallBack3<String>) {
        MultimediaHelper.showEditDialog(this,text){
            callBack3.call(text)
        }
    }

    var   callBack : AppCallBack3<File>? = null
    override fun onTakePicture(filePath: String, callBack3: AppCallBack3<File>) {
        this.callBack = callBack3
        AbsCreateMultimediaActivity.localPath = filePath
        startActivityForResult<SimpleCameraActivity>(bundle = arrayOf("imgLocalPath" to filePath,"cameraViewInfo" to "cameraViewInfo","cameraViewType" to "cameraViewType"),requestCode = 100)
    }

    override fun onImageTuya(originalImg: String?, currentImg: String, newImg: String, callBack3: AppCallBack3<File>) {
    }

    override fun onSoundRecording(filePath: String, callBack3: AppCallBack3<File>) {
        this.callBack = callBack3
        AbsCreateMultimediaActivity.localPath = filePath

        AndroidAudioRecorder.with(this)
                // Required
                .setFilePath(filePath)
                .setRequestCode(101)
                // Optional
                .setSource(AudioSource.MIC)
                .setChannel(AudioChannel.STEREO)
                .setSampleRate(AudioSampleRate.HZ_48000)
                .setAutoStart(true)
                .setKeepDisplayOn(true)

                // Start recording
                .record()

    }

    override fun onVideoRecording(filePath: String, callBack3: AppCallBack3<File>) {
    }

    override fun getDirPath(multimediaEnum: MultimediaEnum): String {
      var  dir = getExternalFilesDir(null)!!.absolutePath
      dir =  when(multimediaEnum){
          MultimediaEnum.IMAGE -> dir+File.separator+"imgs"
          MultimediaEnum.Audio -> dir+File.separator+"audios"
          MultimediaEnum.Video -> dir+File.separator+"videos"
          else  ->  dir
      }

      return  dir
    }

    override fun getPastePath(multimediaEnum: MultimediaEnum): String {
        return getExternalFilesDir(null)!!.absolutePath+File.separator+"caches"
    }

    override fun getFileName(multimediaEnum: MultimediaEnum): String {
        return when(multimediaEnum){
              MultimediaEnum.IMAGE -> StringUtils.getUUID()+".jpg"
              MultimediaEnum.Audio -> StringUtils.getUUID()+".mp3"
              MultimediaEnum.Video -> StringUtils.getUUID()+".mp4"
              else  ->  StringUtils.getUUID()
          }
    }

    override fun onBackValData(datas: MutableList<ImageTextEntity>): ValMessage {
        return  ValMessage(true,"")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100 && resultCode == Activity.RESULT_OK){
           if(FileUtils.isFileExist(AbsCreateMultimediaActivity.localPath)){
               callBack?.call(File(AbsCreateMultimediaActivity.localPath))
           }else{
               callBack?.onError(null,"拍照失败")
           }
        }else if(requestCode == 101 && resultCode == Activity.RESULT_OK){
            if(FileUtils.isFileExist(AbsCreateMultimediaActivity.localPath)){
                callBack?.call(File(AbsCreateMultimediaActivity.localPath))
            }else{
                callBack?.onError(null,"录音失败")
            }
        }
    }

    override fun haveAddText() = true

    override fun haveAddImage() = true

    override fun haveAddAudio() = true
}