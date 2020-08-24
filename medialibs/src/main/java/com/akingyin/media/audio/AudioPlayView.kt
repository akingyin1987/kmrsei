package com.akingyin.media.audio

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.SeekBar
import com.akingyin.base.utils.FileUtils
import com.akingyin.media.R
import kotlinx.android.synthetic.main.dialog_audio_play_layout.view.*
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
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        View.inflate(context, R.layout.dialog_audio_play_layout, this)

    }

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
        dialog_audio_play.setOnClickListener {
            // 播放
            if (playerState == PAUSE) {
                startPlay()

                falgTime = SystemClock.elapsedRealtime()
                beginTime = falgTime - dialog_audio_bar.progress
                dialog_audio_nowTime.base = beginTime
                dialog_audio_nowTime.start()
            } else initPlayer()
        }
        dialog_audio_pause.setOnClickListener {
            pausePlay()
        }
        dialog_audio_bar.setOnSeekBarChangeListener(this)
        dialog_audio_name.text = FileUtils.getFileName(url)
    }


    fun  setPlayError(error:String){
        dialog_audio_name.text = error
    }

    class AudioHandler(dialog: AudioPlayView) : Handler() {
        private val week: WeakReference<AudioPlayView> by lazy {
            WeakReference<AudioPlayView>(dialog)
        }

        override fun handleMessage(msg: Message) {
            println("handleMessage->${week.get()?.mediaPlayer?.currentPosition}")
            week.get()?.dialog_audio_bar?.progress = week.get()?.mediaPlayer?.currentPosition ?: 0
        }
    }


    private fun initPlayer() {
        println("url=${url}")
        mediaPlayer=mediaPlayer?:MediaPlayer()
        if(url.isNotEmpty()){
            mediaPlayer?.setDataSource(url)
            mediaPlayer?.prepareAsync()
            mediaPlayer?.setOnPreparedListener { play ->
                println("资源加载成功--->${null == audioHandler}")
                dialog_audio_bar.max = play.duration
                audioHandler?.post(this)
                dialog_audio_countTime.text = secToTime(play.duration / 1000)
                // 设置运动时间
                falgTime = SystemClock.elapsedRealtime()
                pauseTime = 0
                startPlay()
                dialog_audio_nowTime.base = falgTime
                dialog_audio_nowTime.start()
            }
            mediaPlayer?.setOnCompletionListener {
                println("setOnCompletionListener")
                stopPlay()
                dialog_audio_bar.progress = 0

                dialog_audio_nowTime.base = SystemClock.elapsedRealtime()
                dialog_audio_nowTime.start()
                dialog_audio_nowTime.stop()
            }
        }

    }

    private  fun  pausePlay(){
        // 暂停
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            playerState = PAUSE

        }
        dialog_audio_nowTime.stop()
        pauseTime = SystemClock.elapsedRealtime()
        dialog_audio_play.visibility = View.VISIBLE
        dialog_audio_pause.visibility = View.GONE
        dialog_audio_bar.isEnabled = false
    }

    // 开始播放
    private fun startPlay() {
        mediaPlayer?.start()
        playerState = PLAY
        dialog_audio_play.visibility = View.GONE
        dialog_audio_pause.visibility = View.VISIBLE
        dialog_audio_bar.isEnabled = true
    }

    // 停止播放
    private fun stopPlay() {
        dialog_audio_play.visibility = View.VISIBLE
        dialog_audio_pause.visibility = View.GONE
        mediaPlayer?.release()
        mediaPlayer = null
        playerState = UNIT
        dialog_audio_bar.isEnabled = false
    }


    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser && mediaPlayer != null) {
            mediaPlayer?.seekTo(progress)
            falgTime = SystemClock.elapsedRealtime()
            beginTime = falgTime - seekBar!!.progress
            dialog_audio_nowTime.base = beginTime
            dialog_audio_nowTime.start()
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
        println("onDetachedFromWindow-->>${url}")
    }
}