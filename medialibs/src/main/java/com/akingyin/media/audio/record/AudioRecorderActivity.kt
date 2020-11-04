/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.audio.record

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import cafe.adriel.androidaudiorecorder.Util
import cafe.adriel.androidaudiorecorder.VisualizerHandler
import cafe.adriel.androidaudiorecorder.model.AudioChannel
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate
import cafe.adriel.androidaudiorecorder.model.AudioSource
import com.akingyin.base.SimpleActivity
import com.akingyin.media.R
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import omrecorder.AudioChunk
import omrecorder.OmRecorder
import omrecorder.PullTransport
import omrecorder.Recorder
import java.io.File
import java.util.*


/**
 * @ Description:
 * @author king
 * @ Date 2020/11/4 16:48
 * @version V1.0
 */
class AudioRecorderActivity : SimpleActivity() , PullTransport.OnAudioChunkPulledListener, MediaPlayer.OnCompletionListener {
    private var filePath: String=""
    private var source: AudioSource? = null
    private var channel: AudioChannel? = null
    private var sampleRate: AudioSampleRate? = null
    private var color = 0
    private var autoStart = false
    private var keepDisplayOn = false


    private var player: MediaPlayer? = null
    private var recorder: Recorder? = null
    private var visualizerHandler: VisualizerHandler? = null

    private var timer: Timer? = null
    private var saveMenuItem: MenuItem? = null
    private var recorderSecondsElapsed = 0
    private var playerSecondsElapsed = 0
    private var isRecording = false


    lateinit var contentLayout: RelativeLayout
    lateinit var visualizerView: GLAudioVisualizationView
    lateinit var statusView: TextView
    lateinit var timerView: TextView
    lateinit var restartView: ImageButton
    lateinit var recordView: ImageButton
    lateinit var playView: ImageButton
    override fun initInjection() {

    }

    override fun getLayoutId()= R.layout.aar_activity_audio_recorder


    override fun initializationData(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            filePath = savedInstanceState.getString(EXTRA_FILE_PATH)?:""
            source = savedInstanceState.getSerializable(EXTRA_SOURCE) as AudioSource
            channel = savedInstanceState.getSerializable(EXTRA_CHANNEL) as AudioChannel
            sampleRate = savedInstanceState.getSerializable(EXTRA_SAMPLE_RATE) as AudioSampleRate
            color = savedInstanceState.getInt(EXTRA_COLOR)
            autoStart = savedInstanceState.getBoolean(EXTRA_AUTO_START)
            keepDisplayOn = savedInstanceState.getBoolean(EXTRA_KEEP_DISPLAY_ON)
        } else {
            filePath = intent.getStringExtra(EXTRA_FILE_PATH)?:""
            source = intent.getSerializableExtra(EXTRA_SOURCE) as AudioSource?
            channel = intent.getSerializableExtra(EXTRA_CHANNEL) as AudioChannel?
            sampleRate = intent.getSerializableExtra(EXTRA_SAMPLE_RATE) as AudioSampleRate?
            color = intent.getIntExtra(EXTRA_COLOR, Color.BLACK)
            autoStart = intent.getBooleanExtra(EXTRA_AUTO_START, false)
            keepDisplayOn = intent.getBooleanExtra(EXTRA_KEEP_DISPLAY_ON, false)
        }
        if(keepDisplayOn){
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
            it.elevation = 0F
            it.setBackgroundDrawable(
                    ColorDrawable(Util.getDarkerColor(color)))
            it.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.aar_ic_clear))
        }
        visualizerView = GLAudioVisualizationView.Builder(this)
                .setLayersCount(1)
                .setWavesCount(6)
                .setWavesHeight(R.dimen.aar_wave_height)
                .setWavesFooterHeight(R.dimen.aar_footer_height)
                .setBubblesPerLayer(20)
                .setBubblesSize(R.dimen.aar_bubble_size)
                .setBubblesRandomizeSize(true)
                .setBackgroundColor(Util.getDarkerColor(color))
                .setLayerColors(intArrayOf(color))
                .build()

        contentLayout = findViewById(R.id.content)
        statusView = findViewById(R.id.status)
        timerView = findViewById(R.id.timer)
        restartView = findViewById(R.id.restart)
        recordView = findViewById(R.id.record)
        playView = findViewById(R.id.play)

        contentLayout.setBackgroundColor(Util.getDarkerColor(color))
        contentLayout.addView(visualizerView, 0)
        restartView.visibility = View.INVISIBLE
        playView.visibility = View.INVISIBLE

        if (Util.isBrightColor(color)) {
            ContextCompat.getDrawable(this, R.drawable.aar_ic_clear)?.colorFilter = BlendModeColorFilter(Color.BLACK, BlendMode.SRC_ATOP)
            ContextCompat.getDrawable(this, R.drawable.aar_ic_check)?.colorFilter = BlendModeColorFilter(Color.BLACK, BlendMode.SRC_ATOP)
            statusView.setTextColor(Color.BLACK)
            timerView.setTextColor(Color.BLACK)
            restartView.setColorFilter(Color.BLACK)
            recordView.setColorFilter(Color.BLACK)
            playView.setColorFilter(Color.BLACK)
        }
    }

    override fun onSaveInstanceData(outState: Bundle?) {
        outState?.putString(EXTRA_FILE_PATH, filePath)
        outState?.putInt(EXTRA_COLOR, color)
    }
   override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.aar_audio_recorder, menu)
        saveMenuItem = menu?.findItem(R.id.action_save)
        saveMenuItem?.icon = ContextCompat.getDrawable(this, R.drawable.aar_ic_check)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        if (i == android.R.id.home) {
            finish()
        } else if (i == R.id.action_save) {
            selectAudio()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun initView() {

    }

    override fun startRequest() {

    }
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (autoStart && !isRecording) {
            toggleRecording(null)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            visualizerView.onResume()
        } catch (e: Exception) {
        }
    }

    override fun onPause() {
        restartRecording(null)
        try {
            visualizerView.onPause()
        } catch (e: Exception) {
        }
        super.onPause()
    }

    override fun onDestroy() {
        restartRecording(null)
        setResult(RESULT_CANCELED)
        try {
            visualizerView.release()
        } catch (e: Exception) {
        }
        super.onDestroy()
    }

    override fun onAudioChunkPulled(audioChunk: AudioChunk?) {
        audioChunk?.let {
            val amplitude = if (isRecording) it.maxAmplitude().toFloat() else 0f
            visualizerHandler?.onDataReceived(amplitude)
        }

    }

    override fun onCompletion(mp: MediaPlayer?) {
        stopPlaying()
    }

    private fun selectAudio() {
        stopRecording()
        setResult(RESULT_OK)
        finish()
    }

    fun toggleRecording(v: View?) {
        stopPlaying()
        Util.wait(100) {
            if (isRecording) {
                pauseRecording()
            } else {
                resumeRecording()
            }
        }
    }

    fun togglePlaying(v: View?) {
        pauseRecording()
        Util.wait(100) {
            if (isPlaying()) {
                stopPlaying()
            } else {
                startPlaying()
            }
        }
    }

    fun restartRecording(v: View?) {
        if (isRecording) {
            stopRecording()
        } else if (isPlaying()) {
            stopPlaying()
        } else {
            visualizerHandler = VisualizerHandler().apply {
                visualizerView.linkTo(this)
                visualizerView.release()
                stop()
            }

        }
        saveMenuItem?.isVisible = false
        statusView.visibility = View.INVISIBLE
        restartView.visibility = View.INVISIBLE
        playView.visibility = View.INVISIBLE
        recordView.setImageResource(R.drawable.aar_ic_rec)
        timerView.text = "00:00:00"
        recorderSecondsElapsed = 0
        playerSecondsElapsed = 0
    }

    private fun resumeRecording() {
        isRecording = true
        saveMenuItem?.isVisible = false
        statusView.setText(R.string.aar_recording)
        statusView.visibility = View.VISIBLE
        restartView.visibility = View.INVISIBLE
        playView.visibility = View.INVISIBLE
        recordView.setImageResource(R.drawable.aar_ic_pause)
        playView.setImageResource(R.drawable.aar_ic_play)
        visualizerHandler = VisualizerHandler().apply {
            visualizerView.linkTo(this)
        }

        if (recorder == null) {
            timerView.text = "00:00:00"

            recorder = OmRecorder.wav(
                    PullTransport.Default(Util.getMic(source, channel, sampleRate), this),
                    File(filePath))
        }
        recorder?.resumeRecording()
        startTimer()
    }

    private fun pauseRecording() {
        isRecording = false
        if (!isFinishing) {
            if (null != saveMenuItem) {
                saveMenuItem!!.isVisible = true
            }
        }
        statusView.setText(R.string.aar_paused)
        statusView.visibility = View.VISIBLE
        restartView.visibility = View.VISIBLE
        playView.visibility = View.VISIBLE
        recordView.setImageResource(R.drawable.aar_ic_rec)
        playView.setImageResource(R.drawable.aar_ic_play)
        visualizerView.release()
        visualizerHandler?.stop()
        recorder?.pauseRecording()
        stopTimer()
    }

    private fun stopRecording() {
        visualizerView.release()
        visualizerHandler?.stop()
        recorderSecondsElapsed = 0
        recorder?.stopRecording()
        if (recorder != null) {
            recorder = null
        }
        stopTimer()
    }

    private fun startPlaying() {
        try {
            stopRecording()
            player = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
                start()
                visualizerView.linkTo(DbmHandler.Factory.newVisualizerHandler(this@AudioRecorderActivity, this))
                visualizerView.post { setOnCompletionListener(this@AudioRecorderActivity) }
            }
            timerView.text = "00:00:00"
            statusView.setText(R.string.aar_playing)
            statusView.visibility = View.VISIBLE
            playView.setImageResource(R.drawable.aar_ic_stop)
            playerSecondsElapsed = 0
            startTimer()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun stopPlaying() {
        statusView.text = ""
        statusView.visibility = View.INVISIBLE
        playView.setImageResource(R.drawable.aar_ic_play)
        visualizerView.release()
        visualizerHandler?.stop()
        player?.run {
            try {
                stop()
                reset()
            } catch (e: Exception) {
            }
        }

        stopTimer()
    }

    private fun isPlaying(): Boolean {
        return try {
            player != null && player?.isPlaying!! && !isRecording
        } catch (e: Exception) {
            false
        }
    }

    private fun startTimer() {
        stopTimer()
        timer = Timer().apply {
            scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                updateTimer()
            }
        }, 0, 1000)
        }

    }

    private fun stopTimer() {
        timer?.cancel()
        timer?.purge()
        if (timer != null) {

            timer = null
        }
    }

    private fun updateTimer() {
        runOnUiThread {
            if (isRecording) {
                recorderSecondsElapsed++
                timerView.text = Util.formatSeconds(recorderSecondsElapsed)
            } else if (isPlaying()) {
                playerSecondsElapsed++
                timerView.text = Util.formatSeconds(playerSecondsElapsed)
            }
        }
    }

    companion object{
         const val EXTRA_FILE_PATH = "filePath"
         const val EXTRA_COLOR = "color"
         const val EXTRA_SOURCE = "source"
         const val EXTRA_CHANNEL = "channel"
         const val EXTRA_SAMPLE_RATE = "sampleRate"
         const val EXTRA_AUTO_START = "autoStart"
         const val EXTRA_KEEP_DISPLAY_ON = "keepDisplayOn"
    }
}