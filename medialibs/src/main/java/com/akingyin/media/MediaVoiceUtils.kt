/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool

/**
 * 提示音播放
 * @ Description:
 * @author king
 * @ Date 2020/7/14 16:46
 * @version V1.0
 */
class MediaVoiceUtils private constructor(){

    private var soundPool: SoundPool? = null

    /**
     * 创建某个声音对应的音频ID
     */
    private var soundID = 0

    private fun  initPool(context:Context){
       if(null == soundPool){
           soundPool = SoundPool.Builder().setMaxStreams(1)
                   .setAudioAttributes(AudioAttributes.Builder().
                   setLegacyStreamType(AudioManager.STREAM_ALARM).build())
                   .build()
           soundID = soundPool!!.load(context.applicationContext, R.raw.picture_music, 1)
       }
    }

    /**
     * 播放音频
     */
    fun play() {
        soundPool?.run {
            play(soundID, 0.1f, 0.5f, 0, 1, 1f)
        }

    }

    /**
     * 释放资源
     */
    fun releaseSoundPool() {
        try {
            soundPool?.release()
            soundPool = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object{
        private val mediaVoiceUtils:MediaVoiceUtils by lazy {
            MediaVoiceUtils()
        }



        @JvmStatic
        fun getMediaVoiceInstance(context: Context):MediaVoiceUtils{
            mediaVoiceUtils.initPool(context)
            return  mediaVoiceUtils
        }
    }

}