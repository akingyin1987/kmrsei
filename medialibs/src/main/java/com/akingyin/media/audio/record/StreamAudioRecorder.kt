/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.audio.record

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.NonNull
import androidx.annotation.WorkerThread
import com.akingyin.base.ext.logDebug
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.experimental.and


/**
 * @ Description:
 * @author king
 * @ Date 2020/9/29 11:19
 * @version V1.0
 */
class StreamAudioRecorder private constructor(){
   companion object{
       const val DEFAULT_SAMPLE_RATE = 44100
       const val DEFAULT_BUFFER_SIZE = 2048

       private  val  instance:StreamAudioRecorder by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){
           StreamAudioRecorder()
       }

       @JvmStatic
       @Synchronized
       fun  getStreamAudioRecorder():StreamAudioRecorder{
           return  instance
       }
   }

    private val TAG = "StreamAudioRecorder"

    private var mIsRecording: AtomicBoolean = AtomicBoolean(false)
    private var mExecutorService: ExecutorService? = null


    @Synchronized
    fun start(@NonNull audioDataCallback: AudioDataCallback): Boolean {
        return start(DEFAULT_SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, DEFAULT_BUFFER_SIZE, audioDataCallback)
    }

    /**
     * AudioFormat.CHANNEL_IN_MONO
     * AudioFormat.ENCODING_PCM_16BIT
     */
    @Synchronized
    fun start(sampleRate: Int, channelConfig: Int, audioFormat: Int,
              bufferSize: Int, @NonNull audioDataCallback: AudioDataCallback): Boolean {
        stop()
        mExecutorService = Executors.newSingleThreadExecutor()
        if (mIsRecording.compareAndSet(false, true)) {
            mExecutorService?.execute(
                    AudioRecordRunnable(sampleRate, channelConfig, audioFormat, bufferSize,
                            audioDataCallback))
            return true
        }
        return false
    }

    @Synchronized
    fun stop() {
        mIsRecording.compareAndSet(true, false)
        if (mExecutorService != null) {
            mExecutorService?.shutdown()
            mExecutorService = null
        }
    }

    /**
     * Although Android frameworks jni implementation are the same for ENCODING_PCM_16BIT and
     * ENCODING_PCM_8BIT, the Java doc declared that the buffer type should be the corresponding
     * type, so we use different ways.
     */
    interface AudioDataCallback {
        @WorkerThread
        fun onAudioData(data: ByteArray?, size: Int)
        fun onError()
    }



    inner class AudioRecordRunnable internal constructor(sampleRate: Int, channelConfig: Int, private val mAudioFormat: Int, byteBufferSize: Int,
                                                         @NonNull audioDataCallback: AudioDataCallback) : Runnable {
        private val mAudioRecord: AudioRecord
        private val mAudioDataCallback: AudioDataCallback
        private val mByteBuffer: ByteArray
        private val mShortBuffer: ShortArray
        private val mByteBufferSize: Int
        private val mShortBufferSize: Int
        override fun run() {
            if (mAudioRecord.state == AudioRecord.STATE_INITIALIZED) {
                try {
                    mAudioRecord.startRecording()
                } catch (e: IllegalStateException) {
                   ("startRecording fail: " + e.message).logDebug()
                    mAudioDataCallback.onError()
                    return
                }
                while (mIsRecording.get()) {
                    var ret: Int
                    if (mAudioFormat == AudioFormat.ENCODING_PCM_16BIT) {
                        ret = mAudioRecord.read(mShortBuffer, 0, mShortBufferSize)
                        if (ret > 0) {
                            mAudioDataCallback.onAudioData(
                                    short2byte(mShortBuffer, ret, mByteBuffer), ret * 2)
                        } else {
                            onError(ret)
                            break
                        }
                    } else {
                        ret = mAudioRecord.read(mByteBuffer, 0, mByteBufferSize)
                        if (ret > 0) {
                            mAudioDataCallback.onAudioData(mByteBuffer, ret)
                        } else {
                            onError(ret)
                            break
                        }
                    }
                }
            }
            mAudioRecord.release()
        }

        private fun short2byte(sData: ShortArray, size: Int, bData: ByteArray): ByteArray {
            if (size > sData.size || size * 2 > bData.size) {
                ( "short2byte: too long short data array").logDebug(TAG)
            }
            for (i in 0 until size) {
                bData[i * 2] = ((sData[i] and 0x00FF).toByte())
                bData[i * 2 + 1] = (sData[i].toInt().shr(8).toByte())
            }
            return bData
        }

        private fun onError(errorCode: Int) {
            if (errorCode == AudioRecord.ERROR_INVALID_OPERATION) {
                ( "record fail: ERROR_INVALID_OPERATION").logDebug(TAG)
                mAudioDataCallback.onError()
            } else if (errorCode == AudioRecord.ERROR_BAD_VALUE) {
                ( "record fail: ERROR_BAD_VALUE").logDebug(TAG)
                mAudioDataCallback.onError()
            }
        }

        init {

            val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, mAudioFormat)
            mByteBufferSize = byteBufferSize
            mShortBufferSize = mByteBufferSize / 2
            mByteBuffer = ByteArray(mByteBufferSize)
            mShortBuffer = ShortArray(mShortBufferSize)
            mAudioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig,
                    mAudioFormat, minBufferSize.coerceAtLeast(byteBufferSize))
            mAudioDataCallback = audioDataCallback
        }
    }
}