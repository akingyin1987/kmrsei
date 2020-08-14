/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.doodle

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.Layout
import android.util.DisplayMetrics
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.akingyin.base.SimpleActivity
import com.akingyin.base.dialog.MaterialDialogUtil
import com.akingyin.base.ext.click
import com.akingyin.base.ext.goneAlphaAnimation
import com.akingyin.base.ext.visibleAlphaAnimation
import com.akingyin.base.ext.withIO
import com.akingyin.base.utils.FileUtils
import com.akingyin.media.R
import com.akingyin.media.camera.CameraBitmapUtil
import com.akingyin.media.databinding.ActivityDoodleBinding
import com.akingyin.media.doodle.core.IDoodleShape
import com.akingyin.media.doodle.core.Sticker
import com.akingyin.media.doodle.shape.TextDoodleShape
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.io.File

/**
 * @ Description:
 * @author king
 * @ Date 2020/8/14 11:48
 * @version V1.0
 */
class DoodleActivity:SimpleActivity() {

    private  var  filePath=""
    private  var  reFileName =""
    override fun initInjection() {

    }

    override fun useViewBind()=true

    lateinit var bindView:ActivityDoodleBinding


    override fun initViewBind() {
        bindView = ActivityDoodleBinding.inflate(layoutInflater)
         setContentView(bindView.root)
    }

    override fun getLayoutId()= R.layout.activity_doodle

    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    /**
     * 缩放比例（只针对图片稍比当前屏幕大）
     */
    var scale = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
          intent.run {
              filePath = getStringExtra(FILE_PATH)?:""
              reFileName = getStringExtra(FILE_RENAME)?:FileUtils.getFileName(filePath)
          }
          lifecycleScope.launch(Main){
              val dm = DisplayMetrics()
              withIO {
                  BitmapFactory.decodeFile(filePath)
              }.let {
                  windowManager.defaultDisplay.getMetrics(dm)
                  scale = CameraBitmapUtil.getBitmapScale(it, dm)
                  requestedOrientation = if (it.width > it.height) {
                      ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                  } else {
                      ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                  }
                  bindView.doodleImage.setImageBitmap(it)
                  bindView.doodleView.setMagnifierBitmap(it)
              }

          }
          bindView.doodleImage.setOnTouchListener { _, event ->
              println("这里会进来吗--->")
              when (event.action) {
                  MotionEvent.ACTION_DOWN -> {
                      bindView.bootomBar.root.goneAlphaAnimation()
                      bindView.titleBar.root.goneAlphaAnimation()
                  }
                  MotionEvent.ACTION_UP -> {
                      bindView.bootomBar.root.visibleAlphaAnimation()
                      bindView.titleBar.root.visibleAlphaAnimation()
                  }
              }
              false
          }
          bindView.doodleView.apply {
              //default icon layout

             setBackgroundColor(Color.WHITE)
             setLocked(false)
             setConstrained(true)
          }.onStickerOperationListener = object:DoodleView.OnStickerOperationListener{
              override fun onStickerAdded(sticker: IDoodleShape) {

              }

              override fun onStickerClicked(sticker: IDoodleShape) {

              }

              override fun onStickerDeleted(sticker: IDoodleShape) {

              }

              override fun onStickerDragFinished(sticker: IDoodleShape) {

              }

              override fun onStickerTouchedDown(sticker: IDoodleShape) {

              }

              override fun onStickerZoomFinished(sticker: IDoodleShape) {

              }

              override fun onStickerFlipped(sticker: IDoodleShape) {

              }

              override fun onStickerDoubleTapped(sticker: IDoodleShape) {

              }

              override fun onTouchDown() {
                 bindView.bootomBar.root.goneAlphaAnimation()
                 bindView.titleBar.root.goneAlphaAnimation()
              }

              override fun onTouchUp() {
                 bindView.bootomBar.root.visibleAlphaAnimation()
                 bindView.titleBar.root.visibleAlphaAnimation()
              }
          }
        bindView.bootomBar.btnPenText.click {
            addTextSticker()
        }
        bindView.bootomBar.doodleBtnFinish.click { 
            bindView.doodleView.saveDoodleBitmap(File(filePath)){
                result, error ->  
            }
        }
    }


    private  fun   addTextSticker(){
        MaterialDialogUtil.showEditDialog(this,"添加文本",""){
           bindView.doodleView.addShape( TextDoodleShape(this).apply {
               ContextCompat.getDrawable(this@DoodleActivity,R.drawable.sticker_transparent_background)?.let {
                   setDrawable(it)
               }
               setText(it)
               setTextColor(Color.BLACK)
               setTextAlign(Layout.Alignment.ALIGN_CENTER)
               resizeText()
           },Sticker.Position.CENTER)
        }
    }


    override fun startRequest() {

    }
    companion object{
        const val FILE_PATH="filePath"
        const val FILE_RENAME="rename"
    }
}