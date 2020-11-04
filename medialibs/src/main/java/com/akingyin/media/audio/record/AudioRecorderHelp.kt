/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.audio.record

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

import androidx.fragment.app.Fragment
import cafe.adriel.androidaudiorecorder.model.AudioChannel
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate
import cafe.adriel.androidaudiorecorder.model.AudioSource
import com.akingyin.base.config.AppFileConfig

/**
 * @ Description:
 * @author king
 * @ Date 2020/11/4 16:38
 * @version V1.0
 */
class AudioRecorderHelp  private constructor(fragment: Fragment?=null,activity: Activity?=null){

    protected val EXTRA_FILE_PATH = "filePath"
    protected val EXTRA_COLOR = "color"
    protected val EXTRA_SOURCE = "source"
    protected val EXTRA_CHANNEL = "channel"
    protected val EXTRA_SAMPLE_RATE = "sampleRate"
    protected val EXTRA_AUTO_START = "autoStart"
    protected val EXTRA_KEEP_DISPLAY_ON = "keepDisplayOn"

    private var activity: AppCompatActivity? = null
    private var fragment: Fragment? = null

    private var filePath: String = AppFileConfig.APP_FILE_ROOT + "/recorded_audio.wav"
    private var source: AudioSource = AudioSource.MIC
    private var channel: AudioChannel = AudioChannel.STEREO
    private var sampleRate: AudioSampleRate = AudioSampleRate.HZ_44100
    private var color: Int = Color.parseColor("#546E7A")
    private var requestCode = 0
    private var autoStart = false
    private var keepDisplayOn = false


    fun with(activity: Activity): AudioRecorderHelp {
        return AudioRecorderHelp(activity = activity)
    }

    fun with(fragment: Fragment): AudioRecorderHelp? {
        return AudioRecorderHelp(fragment)
    }

    fun setFilePath(filePath: String): AudioRecorderHelp {
        this.filePath = filePath
        return this
    }

    fun setColor(color: Int): AudioRecorderHelp {
        this.color = color
        return this
    }

    fun setRequestCode(requestCode: Int): AudioRecorderHelp {
        this.requestCode = requestCode
        return this
    }

    fun setSource(source: AudioSource): AudioRecorderHelp {
        this.source = source
        return this
    }

    fun setChannel(channel: AudioChannel): AudioRecorderHelp {
        this.channel = channel
        return this
    }

    fun setSampleRate(sampleRate: AudioSampleRate): AudioRecorderHelp {
        this.sampleRate = sampleRate
        return this
    }

    fun setAutoStart(autoStart: Boolean): AudioRecorderHelp {
        this.autoStart = autoStart
        return this
    }

    fun setKeepDisplayOn(keepDisplayOn: Boolean): AudioRecorderHelp {
        this.keepDisplayOn = keepDisplayOn
        return this
    }

    fun record(callBack:(result: ActivityResult)->Unit) {
        activity?.let {
            val intent = Intent(it, AudioRecorderActivity::class.java)
            intent.putExtra(EXTRA_FILE_PATH, filePath)
            intent.putExtra(EXTRA_COLOR, color)
            intent.putExtra(EXTRA_SOURCE, source)
            intent.putExtra(EXTRA_CHANNEL, channel)
            intent.putExtra(EXTRA_SAMPLE_RATE, sampleRate)
            intent.putExtra(EXTRA_AUTO_START, autoStart)
            intent.putExtra(EXTRA_KEEP_DISPLAY_ON, keepDisplayOn)
            it.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result->
                callBack.invoke(result)
            }.launch(intent)
        }

    }

    fun recordFromFragment(callBack:(result: ActivityResult)->Unit) {
        fragment?.let {
            val intent = Intent(it.requireActivity(), AudioRecorderActivity::class.java)
            intent.putExtra(EXTRA_FILE_PATH, filePath)
            intent.putExtra(EXTRA_COLOR, color)
            intent.putExtra(EXTRA_SOURCE, source)
            intent.putExtra(EXTRA_CHANNEL, channel)
            intent.putExtra(EXTRA_SAMPLE_RATE, sampleRate)
            intent.putExtra(EXTRA_AUTO_START, autoStart)
            intent.putExtra(EXTRA_KEEP_DISPLAY_ON, keepDisplayOn)
            it.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result->
                callBack.invoke(result)
            }.launch(intent)

        }

    }

}