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
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.SystemClock
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
import androidx.core.view.ViewCompat
import com.akingyin.base.ext.messageFormat
import com.akingyin.base.ext.no
import com.akingyin.base.ext.tryCatch
import com.akingyin.base.ext.yes
import com.akingyin.base.utils.CalculationUtil
import com.akingyin.media.R
import com.akingyin.media.camera.CameraBitmapUtil
import com.akingyin.media.doodle.core.BitmapStickerIcon
import com.akingyin.media.doodle.core.IDoodle
import com.akingyin.media.doodle.core.IDoodleShape
import com.akingyin.media.doodle.core.Sticker
import com.akingyin.media.doodle.event.DeleteIconEvent
import com.akingyin.media.doodle.event.FlipHorizontallyEvent
import com.akingyin.media.doodle.event.FlipVerticallyEvent
import com.akingyin.media.doodle.event.ZoomIconEvent
import com.blankj.utilcode.util.SizeUtils
import timber.log.Timber
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.*


/**
 * @ Description:
 * @author king
 * @ Date 2020/8/13 13:57
 * @version V1.0
 */
@SuppressLint("ViewConstructor")
class DoodleView @JvmOverloads constructor( context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr),IDoodle {




    /** 是否显示图标 */
    private var showIcons = false
    /** 是否显示边框 */
    private var showBorder = false

    /** 是不显示在最外层 */
    private var bringToFrontCurrentSticker = false

    /** 放大镜参数 */
    private var mPointPaint: Paint
    private var mPointFillPaint: Paint
    private var mMagnifierDrawable: ShapeDrawable? = null
    private var mDraggingPoint: Point = Point()
    private var mMagnifierCrossPaint: Paint
    private var mMagnifierPaint: Paint
    private val mScaleX = 1f
    private val mScaleY = 1f
    @IntDef(value = [ActionMode.NONE, ActionMode.DRAG, ActionMode.ZOOM_WITH_TWO_FINGER, ActionMode.ICON, ActionMode.CLICK,ActionMode.DRAW,ActionMode.CIRCLE])
    @Retention(AnnotationRetention.SOURCE)
     annotation class ActionMode {
        companion object {
            const val NONE = 0
            const val DRAG = 1
            const val ZOOM_WITH_TWO_FINGER = 2
            const val ICON = 3
            const val CLICK = 4
            /** 涂鸦画 */
            const val DRAW = 5
            /** 圆 */
            const val CIRCLE = 6
        }
    }




    /** 当前所有的图形 */
    private val stickers: MutableList<IDoodleShape> = ArrayList()
    /** 是否涂鸦保存过 */
    var  isDoodleAndSave = false

    /** 涂鸦是否发生过改变 */
    var  isDoodleChange = stickers.size>0
    /** 贴纸四个角的图标 */
    private val icons: MutableList<BitmapStickerIcon> = ArrayList(4)

    /** 边框画笔 */
    private val borderPaint = Paint()

    /** 贴图矩形区域 */
    private val stickerRect = RectF()

    /** 矩阵运算 */
    private val sizeMatrix = Matrix()
    private val downMatrix = Matrix()
    private val moveMatrix = Matrix()

    // 存储变量区
    private val bitmapPoints = FloatArray(8)
    private val bounds = FloatArray(8)
    private val point = FloatArray(2)
    private val currentCenterPoint = PointF()
    private val tmp = FloatArray(2)
    private var midPoint = PointF()

    // endregion 触发移动事件的最小距离
    private var touchSlop = 0

    /** 当前操作的角标 */
    private var currentIcon: BitmapStickerIcon? = null

    //the first point down position
    private var downX = 0f
    private var downY = 0f

    private var oldDistance = 0f
    private var oldRotation = 0f

    @ActionMode
     var currentMode = ActionMode.NONE

    /** 当前选中操作的图形(拖动、删除、点击等) */
     var handlingSticker: IDoodleShape? = null
    /** 当前正在涂鸦的图形 */
     var dragingDoodle:IDoodleShape? = null

    private var locked = false
    private var constrained = false

     var onStickerOperationListener: OnStickerOperationListener? = null

    private var lastClickTime: Long = 0
    private var minClickDelayTime = DEFAULT_MIN_CLICK_DELAY_TIME

    //当前画笔颜色
    var  mCurrentPenColor = Color.RED

    init {
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        mPointPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPointPaint.color = -0xff0001
        mPointPaint.strokeWidth = 1f
        mPointPaint.style = Paint.Style.STROKE

        mPointFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPointFillPaint.color = Color.WHITE
        mPointFillPaint.style = Paint.Style.FILL
        mPointFillPaint.alpha = 175
        mMagnifierPaint = Paint(Paint.ANTI_ALIAS_FLAG)


        mMagnifierPaint.color = Color.WHITE
        mMagnifierPaint.style = Paint.Style.FILL

        mMagnifierCrossPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mMagnifierCrossPaint.color = Color.BLUE
        mMagnifierCrossPaint.style = Paint.Style.FILL
        mMagnifierCrossPaint.strokeWidth = SizeUtils.dp2px(MAGNIFIER_CROSS_LINE_WIDTH).toFloat()

        attrs?.run {
           context.theme.obtainStyledAttributes(this,R.styleable.StickerView,defStyleAttr,0).use {
               showIcons = it.getBoolean(R.styleable.StickerView_showIcons, false)
               showBorder = it.getBoolean(R.styleable.StickerView_showBorder, false)
               bringToFrontCurrentSticker = it.getBoolean(R.styleable.StickerView_bringToFrontCurrentSticker, false)
               borderPaint.isAntiAlias = true
               borderPaint.strokeWidth =5f
               borderPaint.color = it.getColor(R.styleable.StickerView_borderColor, Color.BLACK)
               borderPaint.alpha = it.getInteger(R.styleable.StickerView_borderAlpha, 128)

           }
        }
        if(null == attrs){
            borderPaint.isAntiAlias = true
            borderPaint.strokeWidth =5f
            borderPaint.color = Color.BLACK
            borderPaint.alpha = 128
        }
        configDefaultIcons()


    }




    /**
     * 设置放大镜参数
     */
    fun  setMagnifierBitmap(dis: Bitmap){
         val magnifierShader = BitmapShader(dis, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
         mMagnifierDrawable = ShapeDrawable(OvalShape()).apply {
             paint.shader = magnifierShader
         }

         val origW: Int = dis.width
         val origH: Int = dis.height
         mActWidth = (origW * mScaleX).roundToInt()
         mActHeight = (origH * mScaleY).roundToInt()
         mActLeft = (width - mActWidth) / 2
         mActTop = (height - mActHeight) / 2
     }

    /**
     * //实际显示图片的位置
     */
    private var mActWidth = 0
    /**
     * //实际显示图片的位置
     */
    private  var mActHeight:Int = 0
    /**
     * //实际显示图片的位置
     */
    private  var mActLeft:Int = 0
    /**
     * //实际显示图片的位置
     */
    private  var mActTop:Int = 0


    private  var  mCurrentX = 0
    private  var mCurrentY = 0
      private val mMagnifierMatrix = Matrix()
     private fun onDrawMagnifier(canvas: Canvas) {

         mMagnifierDrawable?.let {
             mDraggingPoint.run {
                 x = mCurrentX
                 y = mCurrentY
             }
             if(mDraggingPoint.x == 0 && mDraggingPoint.y == 0){
                 return
             }
             canvas.save()
             val draggingX: Float = getViewPointX(mDraggingPoint)
             val draggingY: Float = getViewPointY(mDraggingPoint)
             val radius = (width / 8).coerceAtMost(height / 8).toFloat()
             var cx = radius
             val lineOffset = SizeUtils.dp2px(MAGNIFIER_BORDER_WIDTH)

             val pointsDistance: Double = CalculationUtil.getPointsDistance(draggingX, draggingY, 0f, 0f)
             if (pointsDistance < radius * 2.5) {
                 it.setBounds((mActWidth/mScaleX).toInt() - radius.toInt() * 2 + lineOffset, lineOffset, (mActHeight/mScaleY).toInt() - lineOffset, radius.toInt() * 2 - lineOffset)
                 cx = (mActWidth/mScaleX).toInt()  - radius
                 //平移到绘制shader的起始位置
                 println("-----显示在右边---")
             }else{
                 it.setBounds(lineOffset, lineOffset, radius.toInt() * 2 - lineOffset, radius.toInt() * 2 - lineOffset)
                 //平移到绘制shader的起始位置

             }
             mMagnifierMatrix.setTranslate(radius - draggingX, radius - draggingY)
             canvas.drawCircle(cx, radius, radius, mMagnifierPaint)


             it.paint.shader.setLocalMatrix(mMagnifierMatrix)
             it.draw(canvas)

             //画放大镜十字线
             val crossLength: Float = SizeUtils.dp2px(MAGNIFIER_CROSS_LINE_LENGTH).toFloat()
             canvas.drawLine(cx, radius - crossLength, cx, radius + crossLength, mMagnifierCrossPaint)
             canvas.drawLine(cx - crossLength, radius, cx + crossLength, radius, mMagnifierCrossPaint)
             canvas.restore()
         }

    }
    private fun getViewPointX(point: Point): Float {
        return getViewPointX(point.x.toFloat())
    }

    private fun getViewPointX(x: Float): Float {
        //return x * mScaleX + mActLeft;
        return x * mScaleX
    }

    private fun getViewPointY(point: Point): Float {
        return getViewPointY(point.y.toFloat())
    }

    private fun getViewPointY(y: Float): Float {
        //	return y * mScaleY + mActTop;
        return y * mScaleY
    }



    fun setLocked(locked: Boolean): DoodleView {
        this.locked = locked
        invalidate()
        return this
    }
    /**
     * 设置默认的图标
     */
   private fun configDefaultIcons() {
         icons.clear()
         ContextCompat.getDrawable(context, R.drawable.sticker_ic_close_white_18dp)?.let {
             icons.add( BitmapStickerIcon(it, BitmapStickerIcon.Gravity.LEFT_TOP).apply {
                 iconEvent = DeleteIconEvent()
             })
        }

        ContextCompat.getDrawable(context,R.drawable.sticker_ic_scale_white_18dp)?.let {
            icons.add(BitmapStickerIcon(it,BitmapStickerIcon.Gravity.RIGHT_BOTOM).apply {
                iconEvent = ZoomIconEvent()
            })
        }

        ContextCompat.getDrawable(context, R.drawable.sticker_ic_flip_white_18dp)?.let {
            icons.add(BitmapStickerIcon(it,BitmapStickerIcon.Gravity.RIGHT_TOP).apply {
                iconEvent =FlipHorizontallyEvent()
            })
        }
        ContextCompat.getDrawable(context, R.drawable.sticker_ic_flip_white_18dp)?.let {
            icons.add(BitmapStickerIcon(it,BitmapStickerIcon.Gravity.LEFT_BOTTOM).apply {
                iconEvent = FlipVerticallyEvent()
            })
        }
    }


    /**
     * 交换两个顺序
     * Swaps sticker at layer [[oldPos]] with the one at layer [[newPos]].
     * Does nothing if either of the specified layers doesn't exist.
     */
    fun swapLayers(oldPos: Int, newPos: Int) {
        if (stickers.size >= oldPos && stickers.size >= newPos) {
            Collections.swap(stickers, oldPos, newPos)
            invalidate()
        }
    }

    /**
     * Sends sticker from layer [[oldPos]] to layer [[newPos]].
     * Does nothing if either of the specified layers doesn't exist.
     */
    fun sendToLayer(oldPos: Int, newPos: Int) {
        if (stickers.size >= oldPos && stickers.size >= newPos) {
            val s: IDoodleShape = stickers[oldPos]
            stickers.removeAt(oldPos)
            stickers.add(newPos, s)
            invalidate()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            stickerRect.left = left.toFloat()
            stickerRect.top = top.toFloat()
            stickerRect.right = right.toFloat()
            stickerRect.bottom = bottom.toFloat()
        }
    }





    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        canvas?.let {
            drawStickers(it)
            onDrawMagnifier(it)
        }



    }

    /**
     * 事件拦截 true = 拦截，不会往子view 传递 直接会调用 onTouchEvent 事件处理
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {

        if (locked){
            mCurrentX = 0
            mCurrentY = 0
            return  super.onInterceptTouchEvent(ev)
        }
        mCurrentX = ev.x.toInt()
        mCurrentY = ev.y.toInt()
        when (ev.action) {

            MotionEvent.ACTION_DOWN -> {
                downX = ev.x
                downY = ev.y
                val intercept = findCurrentIconTouched() != null || findHandlingSticker() != null||null != dragingDoodle||currentMode != ActionMode.NONE ||onStickerOperationListener?.onIntercept(ev)?:false
                if(!intercept){
                    postInvalidate()
                }
                Timber.tag(TAG).d("onInterceptTouchEvent={0}".messageFormat(intercept))
                return intercept
            }


        }
        return super.onInterceptTouchEvent(ev)
    }

    private var mDownEventTimestamp: Long = 0
    private fun delta(): Long {
        return System.currentTimeMillis() - mDownEventTimestamp
    }
    /**
     * 已经完整的处理了该事件，不希望其它回调方法再次处理 时返回true
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (locked) {
            return super.onTouchEvent(event)
        }
        mCurrentX = event.x.toInt()
        mCurrentY = event.y.toInt()
        when (event.action) {

            //当屏幕检测到第一个触点按下之后就会触发到这个事件
            MotionEvent.ACTION_DOWN -> {
                 onStickerOperationListener?.onTouchDown()
                mDownEventTimestamp = System.currentTimeMillis()
                if (!onTouchDown(event)) {
                    println("返回什么--->")
                    mCurrentY = 0
                    mCurrentX = 0
                    onStickerOperationListener?.onTouchUp()
                    return false
                }
            }

            //当屏幕上已经有触点处于按下的状态的时候，再有新的触点被按下时触发
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDistance = calculateDistance(event)
                oldRotation = calculateRotation(event)
                midPoint = calculateMidPoint(event)
                handlingSticker?.let {
                    if(isInStickerArea(it,event.getX(1), event.getY(1)) && null == findCurrentIconTouched()){
                        currentMode = ActionMode.ZOOM_WITH_TWO_FINGER
                    }
                }

            }
            //当触点在屏幕上移动时触发，触点在屏幕上停留也是会触发的，主要是由于它的灵敏度很高，而我们的手指又不可能完全静止
            MotionEvent.ACTION_MOVE -> {
                handleCurrentMode(event)
                invalidate()
            }

            //当最后一个触点松开时被触发。
            MotionEvent.ACTION_UP -> {
                println("onAction-up")
                onTouchUp(event)
                mCurrentY = 0
                mCurrentX = 0
                onStickerOperationListener?.onTouchUp()
            }

            //当屏幕上有多个点被按住，松开其中一个点时触发（即非最后一个点被放开时）触发
            MotionEvent.ACTION_POINTER_UP -> {
                println("onAction-ACTION_POINTER_UP")
                handlingSticker?.let {
                    if(currentMode == ActionMode.ZOOM_WITH_TWO_FINGER){
                      //  onStickerOperationListener?.onStickerZoomFinished(it)
                    }
                }
                currentMode = ActionMode.NONE
            }
        }
        return true
    }
    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        stickers.forEach {
            transformSticker(it)
        }
    }

    /**
     * Sticker's drawable will be too bigger or smaller
     * This method is to transform it to fit
     * step 1：let the center of the sticker image is coincident with the center of the View.
     * step 2：Calculate the zoom and zoom
     */
    private fun transformSticker( sticker: IDoodleShape) {

        sizeMatrix.reset()
        val width = width.toFloat()
        val height = height.toFloat()
        val stickerWidth = sticker.getWidth().toFloat()
        val stickerHeight = sticker.getHeight().toFloat()
        //step 1
        val offsetX = (width - stickerWidth) / 2
        val offsetY = (height - stickerHeight) / 2
        sizeMatrix.postTranslate(offsetX, offsetY)

        //step 2
        val scaleFactor: Float
        scaleFactor = if (width < height) {
            width / stickerWidth
        } else {
            height / stickerHeight
        }
        sizeMatrix.postScale(scaleFactor / 2f, scaleFactor / 2f, width / 2f, height / 2f)
        sticker.matrix.reset()
        sticker.setMatrix(sizeMatrix)
        invalidate()
    }

    private fun onTouchUp( event: MotionEvent) {

        val currentTime = SystemClock.uptimeMillis()
        when(currentMode){
            ActionMode.ICON ->{
                currentIcon?.let {
                    if(null != handlingSticker){
                        it.onActionUp(this,event)
                    }
                }
            }
            ActionMode.DRAW->{
                Timber.tag(TAG).d("onTouchUp dragingDoodle")
                dragingDoodle?.let {
                    it.setEndLocation(event.x,event.y)
                    if(it.isDrawShapeComplete() ){
                        onCreateSticketShape()
                    }
                }

            }
            ActionMode.DRAG->{
                handlingSticker?.let {
                    if(abs(event.x - downX) < touchSlop && abs(event.y - downY) < touchSlop){
                        currentMode = ActionMode.CLICK
                        onStickerOperationListener?.onStickerClicked(it)
//                        if (currentTime - lastClickTime < minClickDelayTime) {
//                            onStickerOperationListener?.onStickerDoubleTapped(it)
//                        }
                    }
                    onStickerOperationListener?.onStickerDragFinished(it)
                }
            }
        }
        currentMode = ActionMode.NONE
        lastClickTime = currentTime
    }


    /**
     * 处理移动事件
     */
    private fun handleCurrentMode( event: MotionEvent) {
        when (currentMode) {
            ActionMode.NONE, ActionMode.CLICK -> {
                println("click")
            }
            ActionMode.DRAW ->{
                Timber.tag(TAG).d("onTouchDown handleCurrentMode")
                dragingDoodle?.setMoveLocation(event.x,event.y)
            }
            ActionMode.DRAG -> {
                handlingSticker?.let {
                    moveMatrix.set(downMatrix)
                    moveMatrix.postTranslate(event.x - downX, event.y - downY)
                    it.setMatrix(moveMatrix)
                    if (constrained) {
                        constrainSticker(it)
                    }
                }

            }
           ActionMode.ZOOM_WITH_TWO_FINGER -> {
               handlingSticker?.let {
                   val newDistance = calculateDistance(event)
                   val newRotation = calculateRotation(event)
                   moveMatrix.set(downMatrix)
                   moveMatrix.postScale(newDistance / oldDistance, newDistance / oldDistance, midPoint.x,
                           midPoint.y)
                   moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y)
                   it.setMatrix(moveMatrix)
               }

           }
            ActionMode.ICON -> if (handlingSticker != null && currentIcon != null) {
                currentIcon?.onActionMove(this, event)
            }
        }
    }
    private fun constrainSticker(sticker: IDoodleShape) {
        var moveX = 0f
        var moveY = 0f
        val width = width
        val height = height
        sticker.getMappedCenterPoint(currentCenterPoint, point, tmp)
        if (currentCenterPoint.x < 0) {
            moveX = -currentCenterPoint.x
        }
        if (currentCenterPoint.x > width) {
            moveX = width - currentCenterPoint.x
        }
        if (currentCenterPoint.y < 0) {
            moveY = -currentCenterPoint.y
        }
        if (currentCenterPoint.y > height) {
            moveY = height - currentCenterPoint.y
        }
        sticker.matrix.postTranslate(moveX, moveY)
    }

    /**
     * 处理按下事件
     * @param event MotionEvent received from [)][.onTouchEvent]
     */
    private fun onTouchDown( event: MotionEvent): Boolean {
        if(currentMode == ActionMode.NONE){
            currentMode = ActionMode.DRAG
        }
        downX = event.x
        downY = event.y
        //计算当前贴图的中心坐标，否则设置为0，0
        midPoint = calculateMidPoint()
        oldDistance = calculateDistance(midPoint.x, midPoint.y, downX, downY)
        oldRotation = calculateRotation(midPoint.x, midPoint.y, downX, downY)
        currentIcon = findCurrentIconTouched()
        if (currentIcon != null) {
            currentMode = ActionMode.ICON
            currentIcon?.onActionDown(this, event)
        } else {
            handlingSticker = findHandlingSticker()
        }
        handlingSticker?.let {
            downMatrix.set(it.matrix)
            if (bringToFrontCurrentSticker) {
                stickers.remove(it)
                stickers.add(it)
            }
            //  onStickerOperationListener?.onStickerTouchedDown(it)
        }

        if(null == currentIcon && null == handlingSticker && null == dragingDoodle){
            //可以添加图形
            onStickerOperationListener?.onAddShape()
        }
        if(null != dragingDoodle){

            Timber.tag(TAG).d("onTouchDown dragingDoodle")
            currentMode = ActionMode.DRAW
            dragingDoodle?.setStartLocation(downX,downY)
        }

        if (currentIcon == null && handlingSticker == null && null == dragingDoodle) {

            return false
        }
        invalidate()
        return true
    }

    /**
     * 计算中心坐标
     */
    private fun calculateMidPoint(event: MotionEvent?=null): PointF {
      return when (event) {
          null -> {
              handlingSticker?.let {
                  it.getMappedCenterPoint(midPoint,point,tmp)
                  midPoint
              }?:midPoint.apply {
                  set(0f,0f)
              }
          }
          else -> {
              if(event.pointerCount < 2){
                  midPoint.apply {
                      set(0f,0f)
                  }
              }else{
                  midPoint.apply {
                      x = (event.getX(0) + event.getX(1)) / 2
                      y= (event.getY(0) + event.getY(1)) / 2
                  }
              }
          }
      }


    }

    private fun findCurrentIconTouched(): BitmapStickerIcon? {
        for (icon in icons) {
            val x: Float = icon.x - downX
            val y: Float = icon.y - downY
            val distancePow2 = x * x + y * y
            if (distancePow2 <= (icon.iconRadius + icon.iconRadius).pow(2)) {
                return icon
            }
        }
        return null
    }

    /**
     * find the touched Sticker
     */

    private fun findHandlingSticker(): IDoodleShape? {
        for (i in stickers.indices.reversed()) {
            if (isInStickerArea(stickers[i], downX, downY)) {
                println("选中已画的图---->")
                return stickers[i]
            }
        }
        return null
    }
    private fun isInStickerArea( sticker: IDoodleShape, downX: Float, downY: Float): Boolean {
        if(!sticker.supportOtherHandle()){
            return false
        }
        tmp[0] = downX
        tmp[1] = downY
        return sticker.contains(tmp)
    }
    /**
     * 画贴图
     */
    private fun drawStickers(canvas: Canvas) {
        for (i in stickers.indices) {
            val sticker: Sticker = stickers[i]
            sticker.draw(canvas)
        }
        dragingDoodle?.drawHelpers(canvas,this)
        if (handlingSticker != null && !locked && (showBorder || showIcons)) {
            getStickerPoints(handlingSticker, bitmapPoints)
            val x1 = bitmapPoints[0]
            val y1 = bitmapPoints[1]
            val x2 = bitmapPoints[2]
            val y2 = bitmapPoints[3]
            val x3 = bitmapPoints[4]
            val y3 = bitmapPoints[5]
            val x4 = bitmapPoints[6]
            val y4 = bitmapPoints[7]
            if (showBorder) {
                canvas.drawLine(x1, y1, x2, y2, borderPaint)
                canvas.drawLine(x1, y1, x3, y3, borderPaint)
                canvas.drawLine(x2, y2, x4, y4, borderPaint)
                canvas.drawLine(x4, y4, x3, y3, borderPaint)
            }

            //draw icons
            if (showIcons) {
                val rotation: Float = calculateRotation(x4, y4, x3, y3)
                for (i in icons.indices) {
                    val icon = icons[i]
                    when (icon.position) {
                        BitmapStickerIcon.Gravity.LEFT_TOP -> configIconMatrix(icon, x1, y1, rotation)
                        BitmapStickerIcon.Gravity.RIGHT_TOP -> configIconMatrix(icon, x2, y2, rotation)
                        BitmapStickerIcon.Gravity.LEFT_BOTTOM -> configIconMatrix(icon, x3, y3, rotation)
                        BitmapStickerIcon.Gravity.RIGHT_BOTOM -> configIconMatrix(icon, x4, y4, rotation)
                    }
                    icon.draw(canvas, borderPaint)
                }
            }
        }
    }


    /**
     *获取贴图坐标
     */
    private fun getStickerPoints( sticker: Sticker?, dst: FloatArray) {
        sticker?.let {
            it.getBoundPoints(bounds)
            it.getMappedPoints(dst, bounds)
        }?:Arrays.fill(dst,0F)


    }


    private fun configIconMatrix( icon: BitmapStickerIcon, x: Float, y: Float,
                                   rotation: Float) {
        icon.run {
            this.x = x
            this.y = y
            this.matrix.reset()
            this.matrix.postRotate(rotation,icon.getWidth()/2F,icon.getHeight()/2F)
            this.matrix.postTranslate(x - icon.getWidth() / 2, y - icon.getHeight() / 2)
        }

    }

    /**
     * calculate rotation in line with two fingers and x-axis
     */
    private fun calculateRotation( event: MotionEvent?): Float {
        return if (event == null || event.pointerCount < 2) {
            0f
        } else calculateRotation(event.getX(0), event.getY(0), event.getX(1), event.getY(1))
    }

    private fun calculateRotation(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val x = x1 - x2.toDouble()
        val y = y1 - y2.toDouble()
        val radians = atan2(y, x)
        return Math.toDegrees(radians).toFloat()
    }

    /**
     * calculate Distance in two fingers
     */
    private fun calculateDistance( event: MotionEvent?): Float {
        return if (event == null || event.pointerCount < 2) {
            0f
        } else calculateDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1))
    }

    private fun calculateDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val x = x1 - x2.toDouble()
        val y = y1 - y2.toDouble()
        return sqrt(x * x + y * y).toFloat()
    }




    fun setConstrained(constrained: Boolean): DoodleView{
        this.constrained = constrained
        postInvalidate()
        return this
    }



    override fun flipCurrentSticker(direction: Int) {
        handlingSticker?.let {
            flip(it,direction)
        }

    }
    private fun flip( sticker: IDoodleShape, @IDoodle.Flip direction: Int) {
        sticker.getCenterPoint(midPoint)
        if (direction and FLIP_HORIZONTALLY > 0) {
            sticker.matrix.preScale(-1f, 1f, midPoint.x, midPoint.y)
            sticker.setFlippedHorizontally(!sticker.isFlippedHorizontally())
        }
        if (direction and FLIP_VERTICALLY > 0) {
            sticker.matrix.preScale(1f, -1f, midPoint.x, midPoint.y)
            sticker.setFlippedVertically(!sticker.isFlippedVertically())
        }
        onStickerOperationListener?.onStickerFlipped(sticker)

        invalidate()
    }


    override fun zoomAndRotateCurrentSticker(event: MotionEvent) {
      handlingSticker?.let {
          zoomAndRotateSticker(it,event)
      }
    }

    private fun zoomAndRotateSticker( sticker: IDoodleShape,  event: MotionEvent) {
        val newDistance = calculateDistance(midPoint.x, midPoint.y, event.x, event.y)
        val newRotation = calculateRotation(midPoint.x, midPoint.y, event.x, event.y)
        moveMatrix.set(downMatrix)
        moveMatrix.postScale(newDistance / oldDistance, newDistance / oldDistance, midPoint.x,
                midPoint.y)
        moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y)
        sticker.setMatrix(moveMatrix)

    }

    override fun removeCurrentDoodeShape() {
        handlingSticker?.let {
            remove(it)
        }

    }

    fun   removeLast(){
        if(stickers.size>0){
            remove(stickers[stickers.size-1])
        }

    }

    override fun remove(iDoodleShape: IDoodleShape): Boolean {
        return if (stickers.contains(iDoodleShape)) {
            stickers.remove(iDoodleShape)
            onStickerOperationListener?.onStickerDeleted(iDoodleShape)

            if (handlingSticker == iDoodleShape) {
                handlingSticker = null
            }
            invalidate()
            true
        } else {
            Timber.d( "remove: the sticker is not in this StickerView")
            false
        }
    }

    override fun removeAllDoodeShape() {
        stickers.clear()
        handlingSticker?.release()
        handlingSticker = null
        invalidate()
    }

    override fun addShape(iDoodleShape: IDoodleShape, postion: Int) {
        if (ViewCompat.isLaidOut(this)) {
            addShapeImmediately(iDoodleShape, postion)
        } else {
            post {
                addShapeImmediately(iDoodleShape, postion)
            }
        }

    }


    private fun addShapeImmediately(iDoodleShape: IDoodleShape, @Sticker.Position position: Int) {
        println("addShapeImmediately")
        setShapePosition(iDoodleShape, position)
        if(null == iDoodleShape.getTranslateOffset()){
            iDoodleShape.getDrawable()?.let {

                val widthScaleFactor: Float  = width.toFloat() / it.intrinsicWidth
                val heightScaleFactor = height.toFloat() / it.intrinsicHeight
                val scaleFactor: Float = if (widthScaleFactor > heightScaleFactor) heightScaleFactor else widthScaleFactor
                iDoodleShape.matrix.postScale(scaleFactor / 2, scaleFactor / 2, width / 2f, height / 2f)
            }
        }
        stickers.add(iDoodleShape)

        if(iDoodleShape.supportOtherHandle()){
            handlingSticker = iDoodleShape
        }

        onStickerOperationListener?.onStickerAdded(iDoodleShape)
        invalidate()
    }

    private fun setShapePosition(iDoodleShape: IDoodleShape, @Sticker.Position position: Int) {


        if(null != iDoodleShape.getTranslateOffset()){
            iDoodleShape.getTranslateOffset()?.run {
                if(x>0 && y>0){
                    println("getTranslateOffset=${toString()}")
                    iDoodleShape.matrix.postTranslate(x,y)
                }

            }
           return
        }
        val width = width.toFloat()
        val height = height.toFloat()
        var offsetX = width - iDoodleShape.getWidth()
        var offsetY = height - iDoodleShape.getHeight()
        when {
            position and Sticker.Position.TOP > 0 -> {
                offsetY /= 4f
            }
            position and Sticker.Position.BOTTOM > 0 -> {
                offsetY *= 3f / 4f
            }
            else -> {
                offsetY /= 2f
            }
        }
        when {
            position and Sticker.Position.LEFT > 0 -> {
                offsetX /= 4f
            }
            position and Sticker.Position.RIGHT > 0 -> {
                offsetX *= 3f / 4f
            }
            else -> {
                offsetX /= 2f
            }
        }
        iDoodleShape.matrix.postTranslate(offsetX, offsetY)
    }

    override fun replace(iDoodleShape: IDoodleShape, needStayState: Boolean): Boolean {
     return   handlingSticker?.let {
            val width = width.toFloat()
            val height = height.toFloat()
            if (needStayState) {
                iDoodleShape.setMatrix(it.matrix)
                iDoodleShape.setFlippedVertically(it.isFlippedVertically())
                iDoodleShape.setFlippedHorizontally(it.isFlippedHorizontally())
            } else {
                it.matrix.reset()
                // reset scale, angle, and put it in center
                val offsetX = (width - it.getWidth()) / 2f
                val offsetY = (height - it.getHeight()) / 2f
                iDoodleShape.matrix.postTranslate(offsetX, offsetY)

                val scaleFactor: Float = it.getDrawable()?.let {
                     drawable->
                    if (width < height) {
                        width / drawable.intrinsicWidth
                    } else {
                        height / drawable.intrinsicHeight
                    }
                }?:0F
                iDoodleShape.matrix.postScale(scaleFactor / 2f, scaleFactor / 2f, width / 2f, height / 2f)
            }
            val index = stickers.indexOf(it)
            stickers[index] = iDoodleShape
            handlingSticker = iDoodleShape

            invalidate()
            true
        }?:false

    }

    override fun onCreateSticketShape() {
       dragingDoodle?.let {
           if(it.qualifiedShape()){
               it.resetDrawable()
               addShape(it)
           }
           currentMode = ActionMode.NONE
       }
        dragingDoodle = null
    }

    override fun saveDoodleBitmap(file: File,scale:Float, call: (result: Boolean, error: String?) -> Unit) {
        tryCatch({
            createBitmap(scale)?.let {

                CameraBitmapUtil.saveBitmapToPath(it,file.absolutePath).yes {
                    if(!isDoodleAndSave){
                        isDoodleAndSave = stickers.size>0
                    }
                    call(true,null)
                }.no {
                    call(false,"保存失败")
                }
            }?:call(false,"保存失败")
        },{
            call.invoke(false,it.message)
        })


    }


    @Throws(OutOfMemoryError::class)
    fun createBitmap(scale:Float = 0f): Bitmap? {
        handlingSticker = null
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        if(scale>0){
           return CameraBitmapUtil.bitmapScale(bitmap,scale)
        }
        return bitmap
    }
    companion object{
        const val DEFAULT_MIN_CLICK_DELAY_TIME = 200

        const val FLIP_HORIZONTALLY = 1
        const val FLIP_VERTICALLY = 1 shl 1


         const val MAGNIFIER_CROSS_LINE_WIDTH = 0.8f //dp，放大镜十字宽度

         const val MAGNIFIER_CROSS_LINE_LENGTH = 3f //dp， 放大镜十字长度

         const val MAGNIFIER_BORDER_WIDTH = 1f //dp，放大镜边框宽度

        //放大镜的半径
         const val RADIUS = 100

        //放大倍数
         const val FACTOR = 2

         const val TAG ="doodleView"



    }
    interface OnStickerOperationListener {
        fun onStickerAdded( sticker: IDoodleShape)
        fun onStickerClicked(sticker: IDoodleShape)
        fun onStickerDeleted(sticker: IDoodleShape)
        fun onStickerDragFinished(sticker: IDoodleShape)
       // fun onStickerTouchedDown(sticker: IDoodleShape)
      //  fun onStickerZoomFinished(sticker: IDoodleShape)
        fun onStickerFlipped(sticker: IDoodleShape)
      //  fun onStickerDoubleTapped(sticker: IDoodleShape)
        fun onTouchDown( select :Boolean = false)
        fun onTouchUp()
        fun onIntercept(event: MotionEvent):Boolean
        //可以添加图形
        fun onAddShape()
    }
}