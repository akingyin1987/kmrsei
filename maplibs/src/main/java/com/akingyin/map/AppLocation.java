package com.akingyin.map;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * @ Description:
 * @ Author king
 * @ Date 2017/8/24 14:55
 * @ Version V1.0
 */

public class AppLocation  {
  private   static  volatile AppLocation ourInstance = new AppLocation();

  private   static  volatile LocationClient     bdLocationClient;


  public LocationClientOption   getOption(){
    LocationClientOption option = new LocationClientOption();
    option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
    //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

    option.setCoorType("bd09ll");
    //可选，默认gcj02，设置返回的定位结果坐标系

    int span=2000;
    option.setScanSpan(span);
    //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

    option.setIsNeedAddress(true);
    //可选，设置是否需要地址信息，默认不需要

    option.setOpenGps(true);
    //可选，默认false,设置是否使用gps

    option.setLocationNotify(true);
    //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

    option.setIsNeedLocationDescribe(true);
    //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

    option.setIsNeedLocationPoiList(true);
    //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

    option.setIgnoreKillProcess(false);
    //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

   // option.setIgnoreCacheException(false);
    //可选，默认false，设置是否收集CRASH信息，默认收集

    option.setEnableSimulateGps(false);
    //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

   // option.setWifiValidTime(5*60*1000);
    //可选，7.2版本新增能力，如果您设置了这个接口，首次启动定位时，会先判断当前WiFi是否超出有效期，超出有效期的话，会先重新扫描WiFi，然后再定位
    return  option;
  }

  public static AppLocation getInstance() {
      if(null == ourInstance){
        synchronized (AppLocation.class){
           ourInstance = new AppLocation();
        }
      }

    return ourInstance;
  }


  private    AppLocListion     mAppLocListion;

  public   synchronized   void    startLocation(Context  context,AppLocListion  appLocListion){
    this.mAppLocListion = appLocListion;
    System.out.println("开始定位"+(null == bdLocationClient));
    if(null == bdLocationClient){
      bdLocationClient = new LocationClient(context.getApplicationContext());
      bdLocationClient.setLocOption(getOption());
    }
    //bdLocationClient.registerLocationListener(mLocationListener);
    if(!bdLocationClient.isStarted()){
       bdLocationClient.start();
    }
  }

  public  synchronized    void   stopLocation(){
    System.out.println("stopLocation");
    try {
      if(null != bdLocationClient && bdLocationClient.isStarted()){
        System.out.println("关阀");
      //  bdLocationClient.unRegisterLocationListener(mLocationListener);
       // bdLocationClient.stop();

      }
    }catch (Exception e){
      System.out.println("errrrr");
      e.printStackTrace();
    }catch (Error e){
      e.printStackTrace();
    }

  }


  public synchronized void    onDestory(){
    stopLocation();

  }

  private AppLocation() {
  }

  //private   BDAbstractLocationListener   mLocationListener  = new BDAbstractLocationListener() {
  //  @Override public void onReceiveLocation(BDLocation bdLocation) {
  //    if(null != mAppLocListion){
  //      mAppLocListion.call(bdLocation);
  //    }
  //  }
  //};


 public interface    AppLocListion{
      void   call(BDLocation bdLocation);
    }
}
