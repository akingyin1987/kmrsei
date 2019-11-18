package com.akingyin.zxingcamera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.OrientationEventListener;
import android.view.Surface;
import java.lang.ref.WeakReference;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/7/19 10:42
 */
public class SensorCameraHelp  implements SensorEventListener {

  private WeakReference<Context>  mContextWeakReference;

  private int mLastOrientation = 0;

  private  int rotation = 90;
  private   OrientationChangeListener   mOrientationChangeListener;

  public OrientationChangeListener getOrientationChangeListener() {
    return mOrientationChangeListener;
  }

  public void setOrientationChangeListener(OrientationChangeListener orientationChangeListener) {
    mOrientationChangeListener = orientationChangeListener;
  }

  private OrientationEventListener  orientationEventListener = null;

  public SensorCameraHelp(Context  context) {
    mContextWeakReference = new WeakReference<>(context);
    if(context  instanceof Activity){
      int  def = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
      switch (def){
        case Surface.ROTATION_0:
            rotation = 0;
          break;
        case Surface.ROTATION_90:
           rotation =90;
          break;
        case Surface.ROTATION_180:
           rotation = 180;
          break;
        case Surface.ROTATION_270:
           rotation = 270;
          break;

          default:
      }
    }
    orientationEventListener = new OrientationEventListener(context) {
      @Override public void onOrientationChanged(int orientation) {
        if(orientation == ORIENTATION_UNKNOWN){
          return;
        }
        int diff = Math.abs(orientation - mLastOrientation);
        if( diff > 180 ) {
          diff = 360 - diff;
        }
        if( diff > 60 ) {
          int    orientation2 = (orientation + 45) / 90 * 90;
          orientation2 %= 360;
          if( orientation2 != mLastOrientation ) {
            mLastOrientation = orientation2;
            if(null != mOrientationChangeListener){
              int  relative_orientation = (mLastOrientation+rotation)%360;
              int  ui_rotation = (360 - relative_orientation) % 360;
              mOrientationChangeListener.onChange(relative_orientation,ui_rotation);
            }

          }
        }
      }
    };
  }

  @Override public void onSensorChanged(SensorEvent event) {

  }

  @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }


  public   void   onResume(){
    orientationEventListener.enable();
  }

  public  void   onPause(){
    orientationEventListener.disable();

  }



  interface OrientationChangeListener {
    void onChange(int relativeRotation, int uiRotation);
  }
}
