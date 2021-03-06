package com.akingyin.media.audio

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.SeekBar
import com.akingyin.base.utils.FileUtils
import com.akingyin.media.databinding.DialogAudioPlayLayoutBinding

import java.lang.ref.WeakReference

/**
 * 音频播放控件
 * @ Description:
 * @author king
 * @ Date 2019/12/16 13:45
 * @version V1.0
 */
class AudioPlayView : RelativeLayout, SeekBar.OnSeekBarChangeListener, Runnable {


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    var  viewBinding : DialogAudioPlayLayoutBinding = DialogAudioPlayLayoutBinding.inflate(LayoutInflater.from(context),this,true)


    /** 音频路径 */
    var url: String = ""
        set(value)  {
            field = value
            initView()
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


    private fun initView() {
        audioHandler = audioHandler ?:AudioHandler(this)
        viewBinding.dialogAudioPlay.setOnClickListener {
            // 播放
            if (playerState == PAUSE) {
                startPlay()

                falgTime = SystemClock.elapsedRealtime()
                beginTime = falgTime - viewBinding.dialogAudioBar.progress
                viewBinding.dialogAudioNowTime.base = beginTime
                viewBinding.dialogAudioNowTime.start()
            } else initPlayer()
        }
        viewBinding.dialogAudioPause.setOnClickListener {
            pausePlay()
        }
        viewBinding.dialogAudioBar.setOnSeekBarChangeListener(this)
        viewBinding.dialogAudioName.text = FileUtils.getFileName(url)
    }


    fun  setPlayError(error:String){
        viewBinding.dialogAudioName.text = error
    }

    @Suppress("DEPRECATION")
    class AudioHandler(dialog: AudioPlayView) : Handler() {
        private val week: WeakReference<AudioPlayView> by lazy {
            WeakReference<AudioPlayView>(dialog)
        }

        override fun handleMessage(msg: Message) {

            week.get()?.viewBinding?.dialogAudioBar?.progress = week.get()?.mediaPlayer?.currentPosition ?: 0
        }
    }


    private fun initPlayer() {

        mediaPlayer=mediaPlayer?:MediaPlayer()
        if(url.isNotEmpty()){
            try {
                mediaPlayer?.reset()
                mediaPlayer?.setDataSource(url)
                mediaPlayer?.prepareAsync()
            }catch (e:Exception){
                e.printStackTrace()
            }

            mediaPlayer?.setOnPreparedListener { play ->

                viewBinding.dialogAudioBar.max = play.duration
                audioHandler?.post(this)
                viewBinding.dialogAudioCountTime.text = secToTime(play.duration / 1000)
                // 设置运动时间
                falgTime = SystemClock.elapsedRealtime()
                pauseTime = 0
                startPlay()
                viewBinding.dialogAudioNowTime.base = falgTime
                viewBinding.dialogAudioNowTime.start()
            }
            mediaPlayer?.setOnCompletionListener {

                stopPlay()
                viewBinding.dialogAudioBar.progress = 0

                viewBinding.dialogAudioNowTime.base = SystemClock.elapsedRealtime()
                viewBinding.dialogAudioNowTime.start()
                viewBinding.dialogAudioNowTime.stop()
            }
        }

    }

    private  fun  pausePlay(){
        // 暂停
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            playerState = PAUSE

        }
        viewBinding.dialogAudioNowTime.stop()
        pauseTime = SystemClock.elapsedRealtime()
        viewBinding.dialogAudioPlay.visibility = View.VISIBLE
        viewBinding.dialogAudioPause.visibility = View.GONE
        viewBinding.dialogAudioBar.isEnabled = false
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


    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser && mediaPlayer != null) {
            mediaPlayer?.seekTo(progress)
            falgTime = SystemClock.elapsedRealtime()
            beginTime = falgTime - seekBar!!.progress
            viewBinding.dialogAudioNowTime.base = beginTime
            viewBinding.dialogAudioNowTime.start()
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    override fun run() {

        // 获得歌曲现在播放位置并设置成播放进度条的值
        if(mediaPlayer?.isPlaying == true){

            audioHandler?.sendEmptyMessage(0)
            // 每次延迟100毫秒再启动线程
            audioHandler?.postDelayed(this, 100)
        }


    }


    /** int类型转时分秒格式 */
    private fun secToTime(time: Int): String {
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

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if(!hasWindowFocus){
            pausePlay()
        }

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mediaPlayer?.isPlaying == true) mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        playerState = UNIT
        audioHandler?.removeCallbacks(this)
        audioHandler?.removeCallbacksAndMessages(null)
        audioHandler = null

    }
}