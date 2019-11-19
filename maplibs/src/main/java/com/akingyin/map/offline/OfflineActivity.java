/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.map.offline;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.akingyin.map.OnBdListion;
import com.akingyin.map.R;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * @author king
 * @version V1.0
 * @ Description:
 *
 *
 * @ Date 2017/12/28 10:23
 */
public class OfflineActivity extends AppCompatActivity implements MKOfflineMapListener {


    private MKOfflineMap mOffline = null;

    OfflineAdapter mOfflineAdapter;

    RecyclerView recycler;
    public LocationClient mLocationClient;
    protected    String    city="重庆";

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayout());
    System.out.println("onCreate=====baidumap==offline===");
    mLocationClient = new LocationClient(this);
    LocationClientOption option = new LocationClientOption();
    option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
    //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

    option.setCoorType("bd09ll");
    //可选，默认gcj02，设置返回的定位结果坐标系

    int span=3000;
    option.setScanSpan(span);
    //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

    option.setIsNeedAddress(true);
    //可选，设置是否需要地址信息，默认不需要

    option.setOpenGps(false);
    //可选，默认false,设置是否使用gps

    option.setLocationNotify(true);
    //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

    option.setIsNeedLocationDescribe(true);
    //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

    option.setIsNeedLocationPoiList(true);
    //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到


    mLocationClient.setLocOption(option);

    mLocationClient.registerLocationListener(mLocationListener);
    setResult(RESULT_OK);
     SharedPreferences  sharedPreferences = getSharedPreferences("location_pre",Context.MODE_PRIVATE);
     city = sharedPreferences.getString("city_key",city);
    initEventAndData();

  }

  public   void   initData(){
        List<MKOLUpdateElement> mkolUpdateElements = mOffline.getAllUpdateInfo();
        if(null == mkolUpdateElements){
            mkolUpdateElements = new LinkedList<>();
        }
        mOfflineAdapter.setUpdateElements(mkolUpdateElements);
        mOfflineAdapter.notifyDataSetChanged();
    }

    private   void    addOrUpdate(MKOLSearchRecord mkolSearchRecord){
      for(MKOLUpdateElement  mkolUpdateElement : mOfflineAdapter.getUpdateElements()){
          if(mkolSearchRecord.cityID == mkolUpdateElement.cityID){
              return;
          }
      }
      List<MKOLUpdateElement>   mkolUpdateElements = new LinkedList<>();
       MKOLUpdateElement  mkolUpdateElement = new MKOLUpdateElement();
       mkolUpdateElement.cityID = mkolSearchRecord.cityID;
       mkolUpdateElement.cityName = mkolSearchRecord.cityName;
       mkolUpdateElements.add(mkolUpdateElement);
        mOfflineAdapter.setUpdateElements(mkolUpdateElements);
        mOfflineAdapter.notifyDataSetChanged();
    }

    public String formatDataSize(int size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format(Locale.getDefault(),"%dK", size / 1024);
        } else {
            ret = String.format(Locale.getDefault(),"%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }
    @Override
    public void onGetOfflineMapState(int type, int state) {
        System.out.println("onGetOfflineMapState"+type+":"+state);
        switch (type) {

            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
                MKOLUpdateElement update = mOffline.getUpdateInfo(state);
                // 处理下载进度更新提示
                if (update != null) {
                    List<MKOLUpdateElement>  mkolUpdateElements = mOfflineAdapter.getUpdateElements();
                  for(int i=0;i<mOfflineAdapter.getItemCount();i++){
                      MKOLUpdateElement  mkolUpdateElement = mkolUpdateElements.get(i);
                      if(update.cityID == mkolUpdateElement.cityID){
                          mkolUpdateElements.set(i,update);
                          ItemOffineHolder itemOffineHolder = mOfflineAdapter.getOffineHolder(i);
                          if(itemOffineHolder.getMKOLUpdateElement().cityID == update.cityID){
                              itemOffineHolder.bind(update);
                          }
                          return;
                      }
                  }
                }
            }
            break;
            case MKOfflineMap.TYPE_NEW_OFFLINE:
                // 有新离线地图安装
                Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
                break;
            case MKOfflineMap.TYPE_VER_UPDATE:
                // 版本更新提示
                // MKOLUpdateElement e = mOffline.getUpdateInfo(state);

                break;
            default:
                break;
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if(null != mLocationClient && mLocationClient.isStarted()){
          mLocationClient.stop();
        }
        mOffline.destroy();
    }
     int   errorCount=0;
     //public BDLocationListener   mLocationListener = new BDLocationListener() {
     //  @Override public void onReceiveLocation(BDLocation bdLocation) {
     //    System.out.println("null == bdLocation" + bdLocation.getLocType() + ":" + bdLocation.getCity());
     //    if (!TextUtils.isEmpty(bdLocation.getCity())) {
     //      if (!TextUtils.equals(city, bdLocation.getCity())) {
     //        city = bdLocation.getCity();
     //        SharedPreferences sharedPreferences = getSharedPreferences("location_pre", Context.MODE_PRIVATE);
     //        sharedPreferences.edit().putString("city_key", city).apply();
     //      }
     //      List<MKOLSearchRecord> mkolSearchRecords = mOffline.searchCity(bdLocation.getCity());
     //      if (mkolSearchRecords.size() > 0) {
     //        addOrUpdate(mkolSearchRecords.get(0));
     //      }
     //      mLocationClient.stop();
     //    } else {
     //      if (errorCount == 4) {
     //        errorCount = 0;
     //        Toast.makeText(OfflineActivity.this, "未获取到当前城市,请确认网络是否正常", Toast.LENGTH_SHORT).show();
     //        mLocationClient.stop();
     //        if (!TextUtils.isEmpty(city)) {
     //          List<MKOLSearchRecord> mkolSearchRecords = mOffline.searchCity(city);
     //          if (mkolSearchRecords.size() > 0) {
     //            addOrUpdate(mkolSearchRecords.get(0));
     //          }
     //        }
     //      }
     //      errorCount++;
     //    }
     //  }
     //
     //
     //};
     public BDAbstractLocationListener mLocationListener = new BDAbstractLocationListener() {
       @Override public void onReceiveLocation(BDLocation bdLocation) {
         System.out.println("null == bdLocation"+bdLocation.getLocType()+":"+bdLocation.getCity());
         if(!TextUtils.isEmpty(bdLocation.getCity())){
           if(!TextUtils.equals(city,bdLocation.getCity())){
             city = bdLocation.getCity();
             SharedPreferences  sharedPreferences = getSharedPreferences("location_pre",Context.MODE_PRIVATE);
              sharedPreferences.edit().putString("city_key",city).apply();
           }
           List<MKOLSearchRecord> mkolSearchRecords = mOffline.searchCity(bdLocation.getCity());
           if(mkolSearchRecords.size()>0){
             addOrUpdate(mkolSearchRecords.get(0));

           }
           mLocationClient.stop();
         }else{
           if(errorCount == 4){
             errorCount=0;
             Toast.makeText(OfflineActivity.this,"未获取到当前城市,请确认网络是否正常",Toast.LENGTH_SHORT).show();
             mLocationClient.stop();
             if(!TextUtils.isEmpty(city)){
               List<MKOLSearchRecord> mkolSearchRecords = mOffline.searchCity(city);
               if(mkolSearchRecords.size()>0){
                 addOrUpdate(mkolSearchRecords.get(0));

               }
             }

           }
           errorCount++;

         }
       }
     };

     protected int getLayout() {

        return R.layout.activity_baidumap_offline;
    }

     protected void initEventAndData() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setToolBar(toolbar,"离线地图");
        recycler = (RecyclerView)findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setItemAnimator(new DefaultItemAnimator());
        mOfflineAdapter= new OfflineAdapter(this);
        mOfflineAdapter.setOnBdListion(new OnBdListion() {
            @Override public void call(MKOLUpdateElement mkolUpdateElement, int status) {
                System.out.println("----------"+status);
                if(status == 0){
                    mOffline.remove(mkolUpdateElement.cityID);
                    initData();
                }else if(status == 1){
                    mOffline.start(mkolUpdateElement.cityID);
                }else if(status == 2){
                    mOffline.pause(mkolUpdateElement.cityID);
                }
            }
        });

        findViewById(R.id.fab_loc).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                getLocation();
            }
        });
        recycler.setAdapter(mOfflineAdapter);
        mOffline = new MKOfflineMap();
        mOffline.init(this);

        initData();
        getLocation();
    }


    private   void    getLocation(){
      if(null != mLocationClient && !mLocationClient.isStarted()){
        mLocationClient.start();
      }
    }

  protected void setToolBar(Toolbar toolbar, String title) {
    toolbar.setTitle(title);
    setSupportActionBar(toolbar);
    if(null != getSupportActionBar()){
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      toolbar.setNavigationOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          onBackPressed();
        }
      });
    }

  }

  @Override public void onBackPressed() {
    setResult(RESULT_OK);
    super.onBackPressed();
  }
}
