/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.zlcdgroup.baidumaplib.base.BaseMapActivity;
import java.text.MessageFormat;

/**
 * 基于百度地图的坐标拾取
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2017/11/24 13:33
 */

public class MapCoordinatePickupActivity  extends BaseMapActivity implements View.OnClickListener {

   private   double   currentLat,currentLng;

   private   double   oldCurrentLat,oldCurrentLng;

  private boolean onlysee = false;

  private boolean draggable = true;

  private boolean  geoAddr = true;

  RelativeLayout  adjust_layout;
  LinearLayout   latlng_step;
  TextView   tv_lalnginfo;
  Toolbar   mToolbar;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    currentLng = oldCurrentLng = getIntent().getDoubleExtra("lng",0);
    currentLat = oldCurrentLat = getIntent().getDoubleExtra("lat",0);
    super.onCreate(savedInstanceState);
  }

  public int tag = 10;
   /** 反转获取到的地址 */
   private   String    addr;
  @Override public void initialization() {
    System.out.println("getTheme--->>>"+getTheme());

     onlysee = getIntent().getBooleanExtra("onlysee",onlysee);
     draggable = getIntent().getBooleanExtra("draggable",draggable);
     geoAddr = getIntent().getBooleanExtra("geoAddr",geoAddr);
     adjust_layout = (RelativeLayout) findViewById(R.id.adjust_layout);
     latlng_step = (LinearLayout)findViewById(R.id.latlng_step);
     tv_lalnginfo = (TextView)findViewById(R.id.tv_lalnginfo);
     RadioGroup   radioGroup = (RadioGroup)findViewById(R.id.rg_step);
     findViewById(R.id.ib_center).setOnClickListener(this);
     findViewById(R.id.ib_down).setOnClickListener(this);
     findViewById(R.id.ib_left).setOnClickListener(this);
     findViewById(R.id.ib_right).setOnClickListener(this);
     findViewById(R.id.ib_up).setOnClickListener(this);
    if(onlysee){
      adjust_layout.setVisibility(View.GONE);
      latlng_step.setVisibility(View.GONE);
    }
    mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark);
    if (currentLat > 0 && currentLng > 0) {
      tv_lalnginfo.setVisibility(View.VISIBLE);
      tv_lalnginfo.setText(MessageFormat.format("lat:{0,number,#.######}\r\nlng:{1,number,#.######}", currentLat, currentLng));
      locOverlay = new MarkerOptions().icon(mCurrentMarker).position(new LatLng(currentLat, currentLng)).draggable(false);

      LatLng ll = new LatLng(currentLat, currentLng);
      MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, getmBaiduMap().getMaxZoomLevel());
      if(null != loctionMarker){
        loctionMarker.remove();
      }
      loctionMarker = (Marker) getmBaiduMap().addOverlay(locOverlay);
      getmBaiduMap().animateMapStatus(u);
    }
    mToolbar = (Toolbar)findViewById(R.id.toolbar);
    setToolBar(mToolbar,"坐标拾取");
    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId == R.id.rb_step_one){
            tag = 1;
        }else if(checkedId == R.id.rb_step_tow){
            tag = 5;
        }else if(checkedId == R.id.rb_step_three){
            tag = 10;
        }else if(checkedId == R.id.rb_step_fore){
            tag = 30;
        }else if(checkedId == R.id.rb_step_five){
            tag = 100;
        }
      }
    });

    getmBaiduMap().setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
      @Override public void onMarkerDrag(Marker marker) {
        tv_lalnginfo.setText(MessageFormat.format("lat:{0,number,#.######}\r\nlng:{1,number,#.######}", marker.getPosition().latitude, marker.getPosition().longitude));

      }

      @Override public void onMarkerDragEnd(Marker marker) {
        if (null != loctionMarker) {
          loctionMarker.remove();
        }
        LatLng ll = marker.getPosition();
        currentLat = ll.latitude;
        currentLng = ll.longitude;
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, getmBaiduMap().getMaxZoomLevel());
        locOverlay = new MarkerOptions().draggable(draggable).zIndex(9).perspective(true).icon(mCurrentMarker).position(ll);
        loctionMarker = (Marker) getmBaiduMap().addOverlay(locOverlay);
        getmBaiduMap().animateMapStatus(u);
        tv_lalnginfo.setText(MessageFormat.format("lat:{0,number,#.######}\r\nlng:{1,number,#.######}", currentLat, currentLng));
      }

      @Override public void onMarkerDragStart(Marker marker) {

      }
    });

    location_icon.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        errorLoc=0;
        if(null != mLocClient && !mLocClient.isStarted()){
          mLocClient.start();
        }
         location_icon.setVisibility(View.GONE);
        showLoadDialog();
      }
    });
  }

  @Override public View onCreateView(LayoutInflater inflater) {
    return inflater.inflate(R.layout.activity_map_coordinate_pickup,null);
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

  @Override protected BitmapDescriptor getLocationBitmap() {
    return null;
  }

  @Override protected void onBdNotify(BDLocation bdLocation, float d) {

  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    if(!onlysee){
      getMenuInflater().inflate(R.menu.menu_save,menu);
    }


    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId() == R.id.item_save){
      if(currentLng >0 && currentLat >0){
        Intent   intent = new Intent();
        intent.putExtra("lat",currentLat);
        intent.putExtra("lng",currentLng);
        setResult(RESULT_OK,intent);
        finish();
        return true;

      }
      showToast("当前没成功获取到定位信息，无法保存");
    }
    return super.onOptionsItemSelected(item);
  }

  @Override protected void initLoc() {
    mLocClient = new LocationClient(this);
    //mLocationListener = new BDLocationListener() {
    //  @Override public void onReceiveLocation(BDLocation bdLocation) {
    //    onMapReceiveLocation(bdLocation);
    //  }
    //
    //
    //};
    mLocationListener = new BDAbstractLocationListener() {
      @Override public void onReceiveLocation(BDLocation bdLocation) {
        onMapReceiveLocation(bdLocation);
      }
    };
    mLocClient.registerLocationListener(mLocationListener);
    LocationClientOption option = new LocationClientOption();
    option.setOpenGps(true);
    option.setCoorType("bd09ll");

    option.setScanSpan(5000);
    option.setIsNeedAddress(true);
    option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
    option.setProdName("watersys");
    option.SetIgnoreCacheException(true);
    mLocClient.setLocOption(option);
    mLocClient.start();

  }


  int   errorLoc = 0;
   public void onMapReceiveLocation(BDLocation location) {
    // map view 销毁后不在处理新接收的位置
    if (location == null || getmMapView() == null) {
      return;
    }
    MyLocationData locData = new MyLocationData.Builder()
        .accuracy(location.getRadius())
        // 此处设置开发者获取到的方向信息，顺时针0-360
        .direction(location.getDirection()).latitude(location.getLatitude())
        .longitude(location.getLongitude()).build();
    getmBaiduMap().setMyLocationData(locData);
    if (isFirstLoc) {
      isFirstLoc = false;
      LatLng ll = new LatLng(location.getLatitude(),
          location.getLongitude());
      MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,19.0F);
      getmBaiduMap().animateMapStatus(u);
      if(currentLng==0){
        currentLat = location.getLatitude();
        currentLng = location.getLongitude();
        updateLatLng(0,0);
      }
      mLocClient.stop();
      return;
    }
    if(location.getLocType() == BDLocation.TypeGpsLocation ||
        location.getLocType() == BDLocation.TypeNetWorkLocation){

      currentLat = location.getLatitude();
      currentLng = location.getLongitude();
      updateLatLng(0,0);

      tv_lalnginfo.setText(
          MessageFormat.format("lat:{0,number,#.######}\r\nlng:{1,number,#.######}\r\n精度：{2}\r\n卫星数:{3}", currentLat, currentLng,
              location.getRadius(),
              location.getSatelliteNumber() < 0 ? 0 : location.getSatelliteNumber()));
      mLocClient.stop();
      hidLoadialog();
      location_icon.setVisibility(View.VISIBLE);
    }else{
      errorLoc++;
      if(errorLoc >=4){
        location_icon.setVisibility(View.VISIBLE);
        hidLoadialog();
        mLocClient.stop();
        showToast("获取定位信息失败，请检查GPS是否正常，位置环境是否相对空旷");
      }
    }



  }

  @Override protected void onLocation(BDLocation bdLocation) {

  }

  public Marker loctionMarker;
  public BitmapDescriptor mCurrentMarker;

  public OverlayOptions locOverlay;
  public void updateLatLng(int lat_offset, int lng_offset) {
    int lat = (int) (currentLat * 1E6 + lat_offset);
    int lng = (int) (currentLng * 1E6 + lng_offset);
    currentLat = lat * 1.0 / 1E6;
    currentLng = lng * 1.0 / 1E6;
    if (null != loctionMarker) {

      loctionMarker.remove();
    }
    LatLng ll = new LatLng(currentLat, currentLng);
    tv_lalnginfo.setText(MessageFormat.format("lat:{0,number,#.######}\r\nlng:{1,number,#.######}", currentLat, currentLng));
    locOverlay = new MarkerOptions().draggable(draggable).perspective(true).icon(mCurrentMarker).position(ll);
    loctionMarker = (Marker) getmBaiduMap().addOverlay(locOverlay);

  }


  @Override
  public void onClick(View v) {
    int i = v.getId();
    if (i == R.id.ib_left) {
      if (currentLat <= 0 || currentLng <= 0) {
        showToast("没有当前位置，请点定位获取当然位置");
        return;
      }
      updateLatLng(0, -tag);
    } else if (i == R.id.ib_center) {
      if (currentLat <= 0 || currentLng <= 0) {
        showToast("没有当前位置，请点定位获取当然位置");
        return;
      }
      if (null != loctionMarker) {
        loctionMarker.remove();
      }
      LatLng ll = new LatLng(currentLat, currentLng);
      MapStatusUpdate u =
          MapStatusUpdateFactory.newLatLngZoom(ll, getmBaiduMap().getMaxZoomLevel());
      locOverlay = new MarkerOptions().draggable(draggable)
          .zIndex(9)
          .perspective(true)
          .icon(mCurrentMarker)
          .position(ll);
      loctionMarker = (Marker) getmBaiduMap().addOverlay(locOverlay);
      getmBaiduMap().animateMapStatus(u);
    } else if (i == R.id.ib_down) {
      if (currentLat <= 0 || currentLng <= 0) {
        showToast("没有当前位置，请点定位获取当然位置");
        return;
      }
      updateLatLng(-tag, 0);
    } else if (i == R.id.ib_right) {
      if (currentLat <= 0 || currentLng <= 0) {
        showToast("没有当前位置，请点定位获取当然位置");
        return;
      }
      updateLatLng(0, tag);
    } else if (i == R.id.ib_up) {
      if (currentLat <= 0 || currentLng <= 0) {
        showToast("没有当前位置，请点定位获取当然位置");
        return;
      }
      updateLatLng(tag, 0);
    }

  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (null != mCurrentMarker) {
      mCurrentMarker.recycle();
      mCurrentMarker = null;
    }
  }

  public static class Builder{
    private   double   lat;

    private   double   lng;

    private boolean onlysee = false;

    private boolean draggable = true;

    private boolean  geoAddr = true;//是否反转地址

    public   Builder  Lat(double  lat){
      this.lat = lat;
      return  this;
    }

    public  Builder Lng(double  lng){
      this.lng = lng;
      return  this;
    }

    public   Builder isOnlysee(Boolean  onlysee){
      this.onlysee = onlysee;
      return this;
    }

    public   Builder isDraggable(Boolean  draggable){
      this.draggable = draggable;
      return  this;
    }

    public   Builder isGeoAddr(Boolean geoAddr){
      this.geoAddr = geoAddr;
      return  this;
    }

    public Intent   build(Context  context){
      Intent   intent = new Intent(context,MapCoordinatePickupActivity.class);
      if(lat>0){
        intent.putExtra("lat",lat);
        intent.putExtra("lng",lng);

      }
      intent.putExtra("onlysee",onlysee);
      intent.putExtra("draggable",draggable);
      intent.putExtra("geoAddr",geoAddr);
      return  intent;

    }

  }
}
