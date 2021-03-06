package com.akingyin.media.audio

import android.app.Dialog

import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.SeekBar
import com.akingyin.media.R
import com.akingyin.media.databinding.DialogAudioPlayLayoutBinding
import com.qmuiteam.qmui.util.QMUIDisplayHelper

import java.lang.ref.WeakReference

/**
 * @ Description:
 * @author king
 * @ Date 2019/7/26 11:45
 * @version V1.0
 */
class PlayAudioDialog : AudioManagerDialog(), SeekBar.OnSeekBarChangeListener, Runnable  {

    companion object {

        fun getInstance(filePath: String) = PlayAudioDialog().apply {
            arguments = Bundle().apply { putString("filePath", filePath) }
        }


    }

    private val UNIT = -1
    private val PLAY = 0
    private val PAUSE = 1

    private var playerState = UNIT

    // 静态类部类防泄漏
    private var audioHandler: AudioHandler? = null
    private var mediaPlayer: MediaPlayer? = null

    private var beginTime: Long = 0
    private var falgTime: Long = 0
    private var pauseTime: Long = 0

    override fun getContentView() = R.layout.dialog_audio_play_layout

    lateinit var  viewBinding:DialogAudioPlayLayoutBinding
    override fun useViewBind()=true

    override fun initViewBind(inflater: LayoutInflater, container: ViewGroup?): View {
        viewBinding = DialogAudioPlayLayoutBinding.inflate(inflater,container,false)
        return  viewBinding.root
    }

    override fun createDialog(savedInstanceState: Bundle?) = Dialog(requireContext(), R.style.Common_Dialog).apply {
        window?.setGravity(Gravity.CENTER)
    }

    override fun init(savedInstanceState: Bundle?) {
        audioHandler = AudioHandler(this)
        initPlayer()
        viewBinding.dialogAudioPlay.setOnClickListener { // 播放
            if (playerState == PAUSE) {
                startPlay()

                falgTime = SystemClock.elapsedRealtime()
                beginTime = falgTime - viewBinding.dialogAudioBar.progress
                viewBinding.dialogAudioNowTime.base = beginTime
                viewBinding.dialogAudioNowTime.start()
            } else initPlayer()
        }
        viewBinding.dialogAudioPause.setOnClickListener { // 暂停
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                playerState = PAUSE

                viewBinding.dialogAudioNowTime.stop()
                pauseTime = SystemClock.elapsedRealtime()

                viewBinding.dialogAudioPlay.visibility = View.VISIBLE
                viewBinding.dialogAudioPause.visibility = View.GONE
                viewBinding.dialogAudioBar.isEnabled = false
            }
        }
        viewBinding.dialogAudioBar.setOnSeekBarChangeListener(this)
        viewBinding.dialogAudioName.text = arguments?.getString("filePath")?.let {
            it.substring(it.lastIndexOf("/") + 1, it.length)
        }
    }

    override fun onStart() {
        super.onStart()
        setNeedWH()
    }
    fun setNeedWH() {
        val width = getDisplay()[0] * 0.88f
        dialog?.window?.setLayout(width.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    /**
     * 获取屏幕的宽，高
     */
    fun getDisplay() :IntArray{


        val intArray = IntArray(2)
        val displayMetrics = QMUIDisplayHelper.getDisplayMetrics(requireContext())


        intArray[0] = displayMetrics.widthPixels
        intArray[1] = displayMetrics.heightPixels
        return  intArray
    }

    private fun initPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(arguments?.getString("filePath"))
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener { play ->
            viewBinding.dialogAudioBar.max = play.duration
            audioHandler?.post(this)
            viewBinding.dialogAudioCountTime.text = secToTime(play.duration / 1000)

            // 设置运动时间
            falgTime = SystemClock.elapsedRealtime()
            pauseTime = 0
            viewBinding.dialogAudioNowTime.base = falgTime
            viewBinding.dialogAudioNowTime.start()

            startPlay()
        }
        mediaPlayer?.setOnCompletionListener {
            stopPlay()
            viewBinding.dialogAudioBar.progress = 0

            viewBinding.dialogAudioNowTime.base = SystemClock.elapsedRealtime()
            viewBinding.dialogAudioNowTime.start()
            viewBinding.dialogAudioNowTime.stop()
        }
    }

    // 开始播放
    private fun startPlay() {
        mediaPlayer?.start()
        playerState = PLAY
        viewBinding.dialogAudioPlay.visibility = View.GONE
        viewBinding.dialogAudioPause.visibility = View.VISIBLE
        viewBinding.dialogAudioBar.isEnabled = true
    }

    // 停止播放
    private fun stopPlay() {
        viewBinding.dialogAudioPlay.visibility = View.VISIBLE
        viewBinding.dialogAudioPause.visibility = View.GONE
        mediaPlayer?.release()
        mediaPlayer = null
        playerState = UNIT
        viewBinding.dialogAudioBar.isEnabled = false
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser && mediaPlayer != null) {
            mediaPlayer?.seekTo(progress)
            falgTime = SystemClock.elapsedRealtime()
            beginTime = falgTime - seekBar.progress
            viewBinding.dialogAudioNowTime.base = beginTime
            viewBinding.dialogAudioNowTime.start()
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
    override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

    // 提前释放资源
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // 释放资源
        if (mediaPlayer?.isPlaying == true) mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        playerState = UNIT
        audioHandler?.removeCallbacks(this)
        audioHandler?.removeCallbacksAndMessages(null)
        audioHandler = null
    }

    override fun run() {
        // 获得歌曲现在播放位置并设置成播放进度条的值
        if (mediaPlayer != null) {
            audioHandler?.sendEmptyMessage(0)
            // 每次延迟100毫秒再启动线程
            audioHandler?.postDelayed(this, 100)
        }
    }

    @Suppress("DEPRECATION")
    class AudioHandler(dialog: PlayAudioDialog) : Handler() {
        private val week: WeakReference<PlayAudioDialog> by lazy {
            WeakReference(dialog)
        }

        override fun handleMessage(msg: Message) {
            week.get()?.viewBinding?.dialogAudioBar?.progress = week.get()?.mediaPlayer?.currentPosition ?: 0
        }
    }
    /** int类型转时分秒格式 */
    fun secToTime(time: Int): String {
        val timeStr: String?
        val hour: Int
        var minute: Int
        val second: Int
        if (time <= 0) return "00:00"
        else {
            minute = time / 60
            if (minute < 60) {
                second = time % 60
                timeStr = unitFormat(minute) + ":" + unitFormat(second)
            } else {
                hour = minute / 60
                if (hour > 99) return "99:59:59"
                minute %= 60
                second = time - hour * 3600 - minute * 60
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second)
            }
        }
        return timeStr
    }
    private fun unitFormat(i: Int) = if (i in 0..9) "0$i" else "$i"
}