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
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import android.graphics.Color.*
import android.os.Bundle
import android.text.Layout

import android.view.MotionEvent
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.colorChooser
import com.akingyin.base.SimpleActivity
import com.akingyin.base.dialog.MaterialDialogUtil
import com.akingyin.base.ext.*
import com.akingyin.base.utils.FileUtils
import com.akingyin.media.R
import com.akingyin.media.camera.CameraBitmapUtil
import com.akingyin.media.camerax.ui.FLAGS_FULLSCREEN
import com.akingyin.media.camerax.ui.IMMERSIVE_FLAG_TIMEOUT
import com.akingyin.media.databinding.ActivityDoodleBinding
import com.akingyin.media.doodle.core.IDoodleShape
import com.akingyin.media.doodle.core.Sticker
import com.akingyin.media.doodle.shape.*
import com.qmuiteam.qmui.util.QMUIDisplayHelper
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
@Suppress("DEPRECATION")
class DoodleActivity:SimpleActivity() {

    /** 路经 */
    private  var  fileDir=""
    /** 旧图片名 */
    private  var  oldFileName=""
    /** 新图片名 */
    private  var  reFileName =""

    /** 默认画的类型 */
    private  var  defaultShape = CircleShape



    override fun initInjection() {

    }

    override fun useViewBind()=true

    lateinit var bindView:ActivityDoodleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setResult(Activity.RESULT_CANCELED)
    }

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
    var  srcBitmap:Bitmap?=null
    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
          intent.run {
              fileDir = getStringExtra(FILE_DIR)?:""
              oldFileName = getStringExtra(FILE_OLDNAME)?:""
              reFileName = getStringExtra(FILE_RENAME)?:oldFileName
              defaultShape = getIntExtra(SHAPE_TYPE,defaultShape)
          }
          lifecycleScope.launch(Main){
              val dm = QMUIDisplayHelper.getDisplayMetrics(this@DoodleActivity)

              withIO {
                  BitmapFactory.decodeFile(fileDir+File.separator+oldFileName).let {
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
                  srcBitmap = it
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



                      override fun onStickerFlipped(sticker: IDoodleShape) {

                      }


                      override fun onIntercept(event: MotionEvent): Boolean {
                          return bindView.bootomBar.btnHollCircle.isSelected||
                                  bindView.bootomBar.btnPenMosaic.isSelected||
                                  bindView.bootomBar.btnArrow.isSelected||
                                  bindView.bootomBar.btnLine.isSelected||
                                  bindView.bootomBar.btnPenBrokenArrow.isSelected
                      }

                      override fun onAddShape() {
                          println("onAddShape")
                          if(bindView.bootomBar.btnHollCircle.isSelected){
                              println("添加--->圆")
                              drawCircle()
                          }
                          if(bindView.bootomBar.btnPenMosaic.isSelected){
                              println("添加--->马赛克")
                              drawMosaic(srcBitmap)
                          }
                          if(bindView.bootomBar.btnArrow.isSelected){
                              println("添加--->箭头")
                              drawArrow()
                          }
                          if(bindView.bootomBar.btnLine.isSelected){
                              println("添加--->直线")
                              drawLine()
                          }
                          if(bindView.bootomBar.btnPenBrokenArrow.isSelected){
                              println("添加--->直线and箭头")
                              drawLineArrow()
                          }
                      }

                      override fun onTouchDown(select: Boolean) {
                          bindView.bootomBar.root.goneAlphaAnimation()
                          bindView.titleBar.root.goneAlphaAnimation()
                          println("onTouchDown=$select")
                      }

                      override fun onTouchUp() {
                          bindView.bootomBar.root.visibleAlphaAnimation()
                          bindView.titleBar.root.visibleAlphaAnimation()
                      }
                  }
                  when(defaultShape){
                      CircleShape->drawCircle()
                      ArrowShape -> drawArrow()
                      LineArrowShape->drawLineArrow()
                      LineShape ->drawLine()
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

        bindView.bootomBar.btnArrow.click {
            it.toggleView{
                select ->
                if(select){
                    drawArrow()
                }
            }
        }

        bindView.bootomBar.btnLine.click {
            it.toggleView{
                select ->
                if(select){
                    drawLine()
                }
            }
        }
        //马赛克
        bindView.bootomBar.btnPenMosaic.click {
            it.toggleView{
                select ->
                if(select){
                    drawMosaic(srcBitmap)
                }
            }
        }

        bindView.bootomBar.btnPenBrokenArrow.click {
            it.toggleView{
                select ->
                if(select){
                    drawLineArrow()
                }
            }
        }
        bindView.bootomBar.btnSetColorContainer.click {
            selectDrawColor(bindView.doodleView.handlingSticker?.getPenColor()?:RED){
                bindView.bootomBar.btnSetColor.setBackgroundColor(it)
                doodleView.mCurrentPenColor = it
                doodleView.handlingSticker?.setDoodlePenColor(it)
                doodleView.postInvalidate()
            }

        }
        bindView.titleBar.doodleBtnBack.click {
            onBackPressed()
        }

        bindView.titleBar.doodleBtnUndo.click {
            doodleView.removeLast()
        }
        bindView.titleBar.doodleBtnRotate.click {
            MaterialDialogUtil.showConfigDialog(this,message = "确定要清除所有涂鸦操作！"){
                if(it){
                    doodleView.removeAllDoodeShape()
                }
            }
        }
        bindView.bootomBar.doodleBtnFinish.click { 
           doodleView.saveDoodleBitmap(File(fileDir+File.separator+reFileName),1/scale){
                result, error ->
               if(result){
                   showTips("保存成功")
               }else{
                   showError(error)
               }
            }
        }
    }

    /**
     * 选择画笔颜色
     */
    private  fun   selectDrawColor(@ColorInt color: Int,call:(color:Int) ->Unit){
        val colors = intArrayOf(BLACK,DKGRAY,GRAY,LTGRAY,WHITE,RED, GREEN, BLUE,YELLOW,CYAN,MAGENTA)
        MaterialDialog(this).show {
            title(text = "选择画笔颜色")
            colorChooser(colors = colors,initialSelection = color){
                _, color ->
                call.invoke(color)
            }
        }
    }

    /**
     * 画折线箭头
     */
    private  fun   drawLineArrow(){
        bindView.bootomBar.btnPenBrokenArrow.setSelectToggle(true,bindView.bootomBar.btnPenMosaic,
                bindView.bootomBar.btnArrow,bindView.bootomBar.btnHollCircle,
                bindView.bootomBar.btnLine,bindView.bootomBar.btnPenText)
        doodleView.currentMode = DoodleView.ActionMode.DRAW
        doodleView.dragingDoodle = LineArrowDoodleShape(doodleView.mCurrentPenColor)
    }

    private  fun   drawCircle(){
        bindView.bootomBar.btnHollCircle.setSelectToggle(true,bindView.bootomBar.btnPenMosaic,
                bindView.bootomBar.btnArrow,bindView.bootomBar.btnPenBrokenArrow,
                bindView.bootomBar.btnLine,bindView.bootomBar.btnPenText)
        doodleView.currentMode = DoodleView.ActionMode.DRAW
        doodleView.dragingDoodle = CircleDoodleShape(this,doodleView.mCurrentPenColor)
    }

    private  fun   drawLine(){
        bindView.bootomBar.btnLine.setSelectToggle(true,bindView.bootomBar.btnPenMosaic,
                bindView.bootomBar.btnHollCircle,bindView.bootomBar.btnPenBrokenArrow,
                bindView.bootomBar.btnArrow,bindView.bootomBar.btnPenText)
        doodleView.currentMode = DoodleView.ActionMode.DRAW
        doodleView.dragingDoodle = LineDoodleShape(doodleView.mCurrentPenColor)
    }

    private  fun   drawArrow(){
        bindView.bootomBar.btnArrow.setSelectToggle(true,bindView.bootomBar.btnPenMosaic,
                bindView.bootomBar.btnHollCircle,bindView.bootomBar.btnPenBrokenArrow,
                bindView.bootomBar.btnLine,bindView.bootomBar.btnPenText)
        doodleView.currentMode = DoodleView.ActionMode.DRAW
        doodleView.dragingDoodle = ArrowDoodleShape(doodleView.mCurrentPenColor)
    }

    private  fun  drawMosaic(bitmap: Bitmap?){
        bindView.bootomBar.btnPenMosaic.setSelectToggle(true,bindView.bootomBar.btnHollCircle,
                bindView.bootomBar.btnArrow,bindView.bootomBar.btnPenBrokenArrow,
                bindView.bootomBar.btnLine,bindView.bootomBar.btnPenText)
        doodleView.currentMode = DoodleView.ActionMode.DRAW
        bitmap?.let {
            doodleView.dragingDoodle = MosaicDoodleShape(srcBitmap = bitmap)
        }

    }


    private  fun   addTextSticker(){
        MaterialDialogUtil.showEditDialog(this,"添加文本",""){
          doodleView.addShape( TextDoodleShape(this).apply {
               ContextCompat.getDrawable(this@DoodleActivity,R.drawable.sticker_transparent_background)?.let {
                   setDrawable(it)
               }
               setText(it)
               setTextColor(doodleView.mCurrentPenColor)
               setTextAlign(Layout.Alignment.ALIGN_CENTER)
               resizeText()
           },Sticker.Position.CENTER)
        }
    }

    override fun startRequest() {

    }

    override fun onBackPressed() {
        if(doodleView.isDoodleChange && !doodleView.isDoodleAndSave){
            MaterialDialogUtil.showConfigDialog(this,message = "当前涂鸦已发生改变是否放弃！"){
                if(it){
                    super.onBackPressed()
                }
            }
            return
        }
        if(FileUtils.isFileExist(fileDir+File.separator+reFileName)){
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra("tuyaLocalPath",fileDir+File.separator+reFileName)
            })
        }
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        bindView.doodleView.postDelayed({
            window.decorView.systemUiVisibility = FLAGS_FULLSCREEN
        }, IMMERSIVE_FLAG_TIMEOUT)
    }

    override fun onDestroy() {
        super.onDestroy()
        srcBitmap?.recycle()
    }

    companion object{
        const val FILE_DIR="fileDir"
        const val FILE_OLDNAME="oldFileName"
        const val FILE_RENAME="reFileName"
        const val SHAPE_TYPE ="shape_type"

        const val ArrowShape = 1
        const val CircleShape =2
        const val LineArrowShape =3
        const val LineShape =4
        const val TextShape =5

    }
}