/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

//package com.akingyin.media.widget
//
//import android.content.Context
//import android.graphics.Point
//import android.util.AttributeSet
//import android.view.Surface
//import android.view.View
//import android.view.ViewGroup
//import android.view.Window
//import android.widget.ImageView
//import android.widget.SeekBar
//import com.akingyin.media.R
//import com.bumptech.glide.Glide
//import com.bumptech.glide.request.RequestOptions
//import com.shuyu.gsyvideoplayer.utils.CommonUtil
//import com.shuyu.gsyvideoplayer.utils.Debuger
//import com.shuyu.gsyvideoplayer.utils.GSYVideoType
//import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
//import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
//import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
//
//
///**
// * @ Description:
// * @author king
// * @ Date 2020/7/6 15:45
// * @version V1.0
// */
//class SampleCoverVideo  : StandardGSYVideoPlayer {
//    constructor(context: Context?, fullFlag: Boolean?) : super(context, fullFlag)
//    constructor(context: Context?) : super(context)
//    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
//
//    lateinit var mCoverImage: ImageView
//
//    var mCoverOriginUrl: String=""
//
//    var mCoverOriginId = 0
//
//    var mDefaultRes = 0
//
//    override fun init(context: Context?) {
//        super.init(context)
//        mCoverImage = findViewById(R.id.thumbImage)
//        if (mThumbImageViewLayout != null &&
//                (mCurrentState == -1 || mCurrentState == GSYVideoView.CURRENT_STATE_NORMAL || mCurrentState == GSYVideoView.CURRENT_STATE_ERROR)) {
//            mThumbImageViewLayout.visibility = View.VISIBLE
//        }
//    }
//
//    override fun getLayoutId(): Int {
//        return R.layout.video_layout_cover
//    }
//
//    fun loadCoverImage(url: String, res: Int) {
//        mCoverOriginUrl = url
//        mDefaultRes = res
//        Glide.with(context.applicationContext)
//                .setDefaultRequestOptions(
//                        RequestOptions()
//                                .frame(1000000)
//                                .centerCrop()
//                                .error(res)
//                                .placeholder(res))
//                .load(url)
//                .into(mCoverImage)
//    }
//
//    fun loadCoverImageBy(id: Int, res: Int) {
//        mCoverOriginId = id
//        mDefaultRes = res
//        mCoverImage.setImageResource(id)
//    }
//
//    override fun startWindowFullscreen(context: Context?, actionBar: Boolean, statusBar: Boolean): GSYBaseVideoPlayer? {
//        val gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar)
//        val sampleCoverVideo = gsyBaseVideoPlayer as SampleCoverVideo
//        sampleCoverVideo.loadCoverImage(mCoverOriginUrl, mDefaultRes)
//        return gsyBaseVideoPlayer
//    }
//
//
//    override fun showSmallVideo(size: Point?, actionBar: Boolean, statusBar: Boolean): GSYBaseVideoPlayer? {
//        //下面这里替换成你自己的强制转化
//        val sampleCoverVideo = super.showSmallVideo(size, actionBar, statusBar) as SampleCoverVideo
//        sampleCoverVideo.mStartButton.visibility = View.GONE
//        sampleCoverVideo.mStartButton = null
//        return sampleCoverVideo
//    }
//
//    override fun cloneParams(from: GSYBaseVideoPlayer, to: GSYBaseVideoPlayer) {
//        super.cloneParams(from, to)
//        val sf = from as SampleCoverVideo
//        val st = to as SampleCoverVideo
//        st.mShowFullAnimation = sf.mShowFullAnimation
//    }
//
//
//    /**
//     * 退出window层播放全屏效果
//     */
//    override fun clearFullscreenLayout() {
//        if (!mFullAnimEnd) {
//            return
//        }
//        mIfCurrentIsFullscreen = false
//        var delay = 0
//        if (mOrientationUtils != null) {
//            delay = mOrientationUtils.backToProtVideo()
//            mOrientationUtils.isEnable = false
//            if (mOrientationUtils != null) {
//                mOrientationUtils.releaseListener()
//                mOrientationUtils = null
//            }
//        }
//        if (!mShowFullAnimation) {
//            delay = 0
//        }
//
//        val vp: ViewGroup = CommonUtil.scanForActivity(context).findViewById(Window.ID_ANDROID_CONTENT)
//        val oldF = vp.findViewById<View>(fullId)
//        if (oldF != null) {
//            //此处fix bug#265，推出全屏的时候，虚拟按键问题
//            val gsyVideoPlayer = oldF as SampleCoverVideo
//            gsyVideoPlayer.mIfCurrentIsFullscreen = false
//        }
//        if (delay == 0) {
//            backToNormal()
//        } else {
//            postDelayed({ backToNormal() }, delay.toLong())
//        }
//    }
//
//
//    /******************* 下方两个重载方法，在播放开始前不屏蔽封面，不需要可屏蔽  */
//    override fun onSurfaceUpdated(surface: Surface?) {
//        super.onSurfaceUpdated(surface)
//        if (mThumbImageViewLayout != null && mThumbImageViewLayout.visibility == View.VISIBLE) {
//            mThumbImageViewLayout.visibility = View.INVISIBLE
//        }
//    }
//
//    override fun setViewShowState(view: View, visibility: Int) {
//        if (view === mThumbImageViewLayout && visibility != View.VISIBLE) {
//            return
//        }
//        super.setViewShowState(view, visibility)
//    }
//
//    override fun onSurfaceAvailable(surface: Surface?) {
//        super.onSurfaceAvailable(surface)
//        if (GSYVideoType.getRenderType() != GSYVideoType.TEXTURE) {
//            if (mThumbImageViewLayout != null && mThumbImageViewLayout.visibility == View.VISIBLE) {
//                mThumbImageViewLayout.visibility = View.INVISIBLE
//            }
//        }
//    }
//
//    /******************* 下方重载方法，在播放开始不显示底部进度和按键，不需要可屏蔽  */
//    protected var byStartedClick = false
//
//    override fun onClickUiToggle() {
//        if (mIfCurrentIsFullscreen && mLockCurScreen && mNeedLockFull) {
//            setViewShowState(mLockScreen, View.VISIBLE)
//            return
//        }
//        byStartedClick = true
//        super.onClickUiToggle()
//    }
//
//    override fun changeUiToNormal() {
//        super.changeUiToNormal()
//        byStartedClick = false
//    }
//
//    override fun changeUiToPreparingShow() {
//        super.changeUiToPreparingShow()
//        Debuger.printfLog("Sample changeUiToPreparingShow")
//        setViewShowState(mBottomContainer, View.INVISIBLE)
//        setViewShowState(mStartButton, View.INVISIBLE)
//    }
//
//    override fun changeUiToPlayingBufferingShow() {
//        super.changeUiToPlayingBufferingShow()
//        Debuger.printfLog("Sample changeUiToPlayingBufferingShow")
//        if (!byStartedClick) {
//            setViewShowState(mBottomContainer, View.INVISIBLE)
//            setViewShowState(mStartButton, View.INVISIBLE)
//        }
//    }
//
//    override fun changeUiToPlayingShow() {
//        super.changeUiToPlayingShow()
//        Debuger.printfLog("Sample changeUiToPlayingShow")
//        if (!byStartedClick) {
//            setViewShowState(mBottomContainer, View.INVISIBLE)
//            setViewShowState(mStartButton, View.INVISIBLE)
//        }
//    }
//
//    override fun startAfterPrepared() {
//        super.startAfterPrepared()
//        Debuger.printfLog("Sample startAfterPrepared")
//        setViewShowState(mBottomContainer, View.INVISIBLE)
//        setViewShowState(mStartButton, View.INVISIBLE)
//        setViewShowState(mBottomProgressBar, View.VISIBLE)
//    }
//
//    override fun onStartTrackingTouch(seekBar: SeekBar?) {
//        byStartedClick = true
//        super.onStartTrackingTouch(seekBar)
//    }
//}