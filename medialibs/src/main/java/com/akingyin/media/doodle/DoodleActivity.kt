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
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.akingyin.base.SimpleActivity
import com.akingyin.base.dialog.MaterialDialogUtil
import com.akingyin.base.ext.*
import com.akingyin.base.utils.FileUtils
import com.akingyin.media.R
import com.akingyin.media.camera.CameraBitmapUtil
import com.akingyin.media.databinding.ActivityDoodleBinding
import com.akingyin.media.doodle.core.IDoodleShape
import com.akingyin.media.doodle.core.Sticker
import com.akingyin.media.doodle.shape.CircleDoodleShap
import com.akingyin.media.doodle.shape.TextDoodleShape
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.io.File
import kotlin.properties.Delegates

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
    var  doodleView:DoodleView by Delegates.notNull()
    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
          intent.run {
              filePath = getStringExtra(FILE_PATH)?:""
              reFileName = getStringExtra(FILE_RENAME)?:FileUtils.getFileName(filePath)
          }
          lifecycleScope.launch(Main){
              val dm = DisplayMetrics()
              windowManager.defaultDisplay.getMetrics(dm)
              withIO {
                  BitmapFactory.decodeFile(filePath).let {
                      scale = CameraBitmapUtil.getBitmapScale(it, dm)
                      if(scale>0){
                          CameraBitmapUtil.bitmapScale(it,scale)
                      }else{
                          it
                      }
                  }
              }.let {
                  println("scale=$scale")
                  requestedOrientation = if (it.width > it.height) {
                      ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                  } else {
                      ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                  }
                  doodleView = bindView.doodleView
                  bindView.doodleImage.setImageBitmap(it)
                  doodleView.setMagnifierBitmap(it)
                  doodleView.apply {

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
                          if(bindView.bootomBar.btnHollCircle.isSelected){
                              drawCircle()
                          }
                      }

                      override fun onTouchUp() {
                          bindView.bootomBar.root.visibleAlphaAnimation()
                          bindView.titleBar.root.visibleAlphaAnimation()
                      }
                  }
              }

          }


        bindView.bootomBar.btnPenText.click {

            addTextSticker()
        }
        //画圆
        bindView.bootomBar.btnHollCircle.click {
           it.toggleView{
               select ->
               if(select){
                   drawCircle()
               }
           }


        }
        bindView.bootomBar.doodleBtnFinish.click { 
           doodleView.saveDoodleBitmap(File(filePath),scale){
                result, error ->
               if(result){
                   showTips("保存成功")
               }else{
                   showError(error)
               }
            }
        }
    }

    private  fun   drawCircle(){
        bindView.bootomBar.btnHollCircle.setSelectToggle(true,bindView.bootomBar.btnPenMosaic,
                bindView.bootomBar.btnArrow,bindView.bootomBar.btnPenBrokenArrow,
                bindView.bootomBar.btnLine,bindView.bootomBar.btnPenText)
        doodleView.currentMode = DoodleView.ActionMode.DRAW
        doodleView.dragingDoodle = CircleDoodleShap(this)
    }


    private  fun   addTextSticker(){
        MaterialDialogUtil.showEditDialog(this,"添加文本",""){
          doodleView.addShape( TextDoodleShape(this).apply {
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