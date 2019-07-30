package com.zlcdgroup.mrsei.utils

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.OrientationEventListener
import android.view.Surface
import kotlin.math.abs
import kotlin.properties.Delegates


/**
 * @ Description:
 * @author king
 * @ Date 2019/7/17 11:08
 * @version V1.0
 */
class  SensorHelp(context: Context)  :SensorEventListener{

    private val TAG = "SensorUtil"
    private var mOrientationChangeListener: OrientationChangeListener? = null



    private var mSensorManager: SensorManager by  Delegates.notNull()

    private var mGravitySensor: Sensor? = null// 重力加速感应器
    private var mMagneticSensor: Sensor? = null// 地磁传感器
    private var mOrientationSensor:Sensor? = null//方向传感 器

    private var mAccelerometerValues = FloatArray(3)

    private var mMagneticFieldValues = FloatArray(3)

    private var mLastOrientation: Float = 0.toFloat()

    private  var rotation :Int = 90

    private  var  orientationEventListener : OrientationEventListener  by Delegates.notNull()

    init {
         if(context  is Activity){

             rotation = context.windowManager.defaultDisplay.rotation
             when(rotation){
                 Surface.ROTATION_0 -> rotation =0
                 Surface.ROTATION_90 -> rotation =90
                 Surface.ROTATION_180 -> rotation =180
                 Surface.ROTATION_270-> rotation =270
             }
         }

         orientationEventListener  = object :OrientationEventListener(context){
             override fun onOrientationChanged(orientation: Int) {
                 if(orientation == ORIENTATION_UNKNOWN){
                     return
                 }
                 var diff = abs(mLastOrientation - orientation)
                 if(diff == 0F){
                     return
                 }
                 if( diff > 180 ) {
                     diff = 360 - diff
                 }
                 if( diff > 60 ) {
                     var   orientation2 = (orientation + 45) / 90 * 90
                     orientation2 %= 360
                     if( orientation2 != mLastOrientation.toInt() ) {
                         mLastOrientation = orientation2.toFloat()

                          mOrientationChangeListener?.apply {
                              val relative_orientation = (mLastOrientation+rotation)%360
                              val ui_rotation = (360 - relative_orientation) % 360
                              onChange(ui_rotation)
                          }
                     }
                 }
             }
         }
         mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
         mSensorManager.getSensorList(Sensor.TYPE_ALL)?.let {
             it.forEach { sensor ->

                 println(sensor.name)
             }
         }
        mSensorManager.let {
            mGravitySensor = it.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            mMagneticSensor = it.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        }
        println("null ==${null == mMagneticSensor}")

    }



    fun   onRegister(){
        orientationEventListener.enable()
        mOrientationSensor?.let {
            mSensorManager.registerListener(this,it,SensorManager.SENSOR_DELAY_UI)
        }
        mGravitySensor?.let {
            mSensorManager.registerListener(this,it,SensorManager.SENSOR_DELAY_UI)
        }

        mMagneticSensor?.let {
            mSensorManager.registerListener(this,it,SensorManager.SENSOR_DELAY_UI)

        }

    }


    fun  onUnregister(){
        orientationEventListener.disable()
        mGravitySensor?.let {
            mSensorManager.unregisterListener(this,it)
        }

        mMagneticSensor?.let {
            mSensorManager.unregisterListener(this,it)

        }
        mOrientationSensor?.let {
            mSensorManager.unregisterListener(this,it)
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {

        if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {



        } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {


          // calculateOrientation(event)
        }

    }


    interface OrientationChangeListener {
        fun onChange(value: Float)
    }

    fun setOrientationChangeListener(mOrientationChangeListener: OrientationChangeListener) {
        this.mOrientationChangeListener = mOrientationChangeListener
    }

    var   lastTime :Long = 0
    fun calculateOrientation(sensorEvent: SensorEvent){
        if(sensorEvent.sensor.type == Sensor.TYPE_ACCELEROMETER){
            var  nowTime = System.currentTimeMillis()
            if(nowTime - lastTime<400){
                return
            }
            lastTime = nowTime
            println("x=${sensorEvent.values[0]}  y=${sensorEvent.values[1]}   z=${sensorEvent.values[2]}")
            if(sensorEvent.values[0] < 4 && sensorEvent.values[0] > -4){
                //UP
                if(sensorEvent.values[1] > 0){
                   mOrientationChangeListener?.apply { onChange(0F) }
                }else if(sensorEvent.values[1] < 0){
                   // UP SIDE DOWN
                    mOrientationChangeListener?.apply { onChange(180F) }
                }
            }else if(sensorEvent.values[1] < 4 && sensorEvent.values[1] > -4){
                if (sensorEvent.values[0] > 0) {
                    // LEFT
                    mOrientationChangeListener?.apply { onChange(90F) }
                } else if (sensorEvent.values[0] < 0) {
                    // RIGHT
                    mOrientationChangeListener?.apply { onChange(270F) }

                }
            }
        }
    }

//    /**
//     * 计算方向
//     */
//    fun  calculateOrientation(){
//       val values = FloatArray(3)
//       val R = FloatArray(9)
//        SensorManager.getRotationMatrix(R, null, mAccelerometerValues, mMagneticFieldValues)
//        SensorManager.getOrientation(R, values)
//        println("values=${Arrays.toString(values)}")
//        values[0] = Math.toDegrees(values[0].toDouble()).toFloat()
////        if(abs(mLastOrientation - values[0]) <= 0.1){
////
////            return
////        }
//
//        values[0].let {  value->
//           when{
//               value>=-5 && value<5 ->{
//                   println("正北")
//               }
//               value >=5 && value<85->{
//                   println("东北")
//               }
//               value >= 85 && value<95->{
//                   println("正东")
//               }
//               value>=95 && value<175->{
//                   println("东南")
//               }
//               value in 175.0..180.0 ->{
//                   println("正南")
//               }
//               value in -180.0 ..-175.0 ->{
//                   println("正南")
//               }
//               value >-175 && value<-95 ->{
//                   println("西南")
//               }
//               value>=-95 && value<-85 ->{
//                   println("正西")
//               }
//               value>= -85 && value<-5 ->{
//                   println("西北")
//               }
//
//
//
//           }
//
//
//        }
//        mOrientationChangeListener?.apply {
//            onChange(values[0])
//        }
//        mLastOrientation = values[0]
//    }


}