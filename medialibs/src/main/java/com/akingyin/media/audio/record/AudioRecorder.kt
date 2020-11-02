/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.audio.record


import android.media.MediaRecorder
import android.os.storage.OnObbStateChangeListener
import androidx.annotation.IntDef
import androidx.annotation.WorkerThread
import com.akingyin.base.ext.logDebug

import java.io.File
import java.io.IOException


/**
 * @ Description:
 * @author king
 * @ Date 2020/9/29 11:22
 * @version V1.0
 */
class AudioRecorder private  constructor(){
    private var mState = STATE_IDLE
    private var mOnErrorListener: OnErrorListener? = null
    private var mSampleStart: Long = 0 // time at which latest record or play operation started

    private var mRecorder: MediaRecorder? = null
    private var mStarted = false

    fun setOnErrorListener(listener: OnErrorListener) {
        mOnErrorListener = listener
    }

    @Synchronized
    fun getMaxAmplitude(): Int {
        return if (mState != STATE_RECORDING) {
            0
        } else mRecorder?.maxAmplitude?:0
    }

    fun progress(): Int {
        return if (mState == STATE_RECORDING) {
            ((System.currentTimeMillis() - mSampleStart) / 1000).toInt()
        } else 0
    }

    /**
     * Directly start record, including prepare and start.
     *
     * MediaRecorder.AudioSource.MIC
     * MediaRecorder.OutputFormat.MPEG_4
     * MediaRecorder.AudioEncoder.AAC
     * 开始录音 先准备再开始
     */
    @WorkerThread
    @Synchronized
    fun startRecord(audioSource: Int, outputFormat: Int, audioEncoder: Int,
                    sampleRate: Int, bitRate: Int, outputFile: File): Boolean {
        stopRecord()
        mRecorder = MediaRecorder().apply {
            setAudioSource(audioSource)
            setOutputFormat(outputFormat)
            setAudioSamplingRate(sampleRate)
            setAudioEncodingBitRate(bitRate)
            setAudioEncoder(audioEncoder)
            setOutputFile(outputFile.absolutePath)
        }


        // Handle IOException
        try {
            mRecorder?.prepare()
        } catch (exception: IOException) {
            ( "startRecord fail, prepare fail: " + exception.message).logDebug(TAG)
            setError(OnObbStateChangeListener.ERROR_INTERNAL)
            mRecorder?.run {
                reset()
                release()
            }
            mRecorder = null
            return false
        } catch (exception: RuntimeException) {
            ( "startRecord fail, prepare fail: " + exception.message).logDebug(TAG)
            setError(OnObbStateChangeListener.ERROR_INTERNAL)
            mRecorder?.run {
                reset()
                release()
            }
            mRecorder = null
            return false
        }
        // Handle RuntimeException if the recording couldn't start
        try {
            mRecorder?.start()
            mStarted = true
        } catch (exception: RuntimeException) {
            ("startRecord fail, start fail: " + exception.message).logDebug(TAG)
            setError(OnObbStateChangeListener.ERROR_INTERNAL)
            mRecorder?.run {
                reset()
                release()
            }
            mRecorder = null
            mStarted = false
            return false
        }
        mSampleStart = System.currentTimeMillis()
        mState = STATE_RECORDING
        return true
    }

    /**
     * 录音准备
     * prepare for a new audio record.
     */
    @WorkerThread
    @Synchronized
    @JvmOverloads
    fun prepareRecord(audioSource: Int, outputFormat: Int, audioEncoder: Int,
                      sampleRate: Int = DEFAULT_SAMPLE_RATE, bitRate: Int = DEFAULT_BIT_RATE, outputFile: File): Boolean {
        stopRecord()
        mRecorder = MediaRecorder().apply {
            setAudioSource(audioSource)
            setOutputFormat(outputFormat)
            setAudioSamplingRate(sampleRate)
            setAudioEncodingBitRate(bitRate)
            setAudioEncoder(audioEncoder)
            setOutputFile(outputFile.absolutePath)

        }

        // Handle IOException
        try {
            mRecorder?.prepare()
        } catch (exception: IOException) {
            ("startRecord fail, prepare fail: " + exception.message).logDebug(TAG)
            setError(OnObbStateChangeListener.ERROR_INTERNAL)
            mRecorder?.run {
                reset()
                release()
            }
            mRecorder = null
            return false
        }
        mState = STATE_PREPARED
        return true
    }

    /**
     * After prepared, start record now.
     * 准备后才可调用 此方法
     */
    @WorkerThread
    @Synchronized
    fun startRecord(): Boolean {
        if (mRecorder == null || mState != STATE_PREPARED) {
            setError(ERROR_NOT_PREPARED)
            return false
        }
        // Handle RuntimeException if the recording couldn't start
        try {
            mRecorder?.start()
            mStarted = true
        } catch (exception: RuntimeException) {
            ("startRecord fail, start fail: " + exception.message).logDebug(TAG)
            setError(OnObbStateChangeListener.ERROR_INTERNAL)
            mRecorder?.run {
                reset()
                release()
            }
            mRecorder = null
            mStarted = false
            return false
        }
        mSampleStart = System.currentTimeMillis()
        mState = STATE_RECORDING
        return true
    }

    /**
     * stop record, and save audio file.
     *
     * @return record audio length in seconds, -1 if not a successful record.
     */
    @WorkerThread
    @Synchronized
    fun stopRecord(): Int {
        if (mRecorder == null) {
            mState = STATE_IDLE
            return -1
        }
        var length = -1
        when (mState) {
            STATE_RECORDING -> {
                try {
                    // seems to be a bug in Android's AAC based audio encoders
                    // ref: http://stackoverflow.com/a/24092524/3077508
                    Thread.sleep(STOP_AUDIO_RECORD_DELAY_MILLIS.toLong())
                    mRecorder?.stop()
                    mStarted = false
                    length = ((System.currentTimeMillis() - mSampleStart) / 1000).toInt()
                } catch (e: RuntimeException) {
                    ("stopRecord fail, stop fail(no audio data recorded): " +
                            e.message).logDebug(TAG)
                } catch (e: InterruptedException) {
                    ("stopRecord fail, stop fail(InterruptedException): " + e.message).logDebug(TAG)
                }
                try {
                    mRecorder?.reset()
                } catch (e: RuntimeException) {
                    ("stopRecord fail, reset fail " + e.message).logDebug(TAG)
                }
                mRecorder?.release()
                mRecorder = null
                mState = STATE_IDLE
            }
            STATE_PREPARED, STATE_IDLE -> {
                try {
                    mRecorder?.reset()
                } catch (e: RuntimeException) {
                    ("stopRecord fail, reset fail " + e.message).logDebug(TAG)
                }
                mRecorder?.release()
                mRecorder = null
                mState = STATE_IDLE
            }
            else -> {
                try {
                    mRecorder?.reset()
                } catch (e: RuntimeException) {
                    ( "stopRecord fail, reset fail " + e.message).logDebug(TAG)
                }
                mRecorder?.release()
                mRecorder = null
                mState = STATE_IDLE
            }
        }
        return length
    }

    private fun setError(error: Int) {
        mOnErrorListener?.onError(error)
    }

    @IntDef(value = [ERROR_SDCARD_ACCESS, ERROR_INTERNAL, ERROR_NOT_PREPARED])
    @Retention(AnnotationRetention.SOURCE)
    annotation class Error
    interface OnErrorListener {
        @WorkerThread
        fun onError(@Error error: Int)
    }

    companion object{
        const val DEFAULT_SAMPLE_RATE = 44100
        const val DEFAULT_BIT_RATE = 44100
        const val ERROR_SDCARD_ACCESS = 1
        const val ERROR_INTERNAL = 2
        const val ERROR_NOT_PREPARED = 3

        private const val TAG = "AudioRecorder"
        private const val STOP_AUDIO_RECORD_DELAY_MILLIS = 300
        private const val STATE_IDLE = 0
        private const val STATE_PREPARED = 1
        private const val STATE_RECORDING = 2

        private  val  instance:AudioRecorder by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){
            AudioRecorder()
        }

        @JvmStatic
        @Synchronized
        fun  getAudioRecorder():AudioRecorder{
            return  instance
        }
    }

}