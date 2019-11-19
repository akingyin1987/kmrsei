/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.map.base;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.akingyin.map.R;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;


/**
 * 基础地图
 * @author king
 * @version V1.0
 * @ Description:
 *
 * Company:重庆中陆承大科技有限公司
 * @ Date 2017/11/24 12:41
 */
public abstract class BaseMapActivity  extends AppCompatActivity {
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    /** 地图模式（正常，跟随，罗盘） */
    protected ImageView location_icon;
    private MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

    private ImageButton zoom_in,zoom_out;
    /** 交通 */
    private ImageButton  road_condition;

    /** 地图类型（普通2d,普通3d,卫星） */
    private ImageButton  map_layers;

    /** 全景 */
    protected ImageButton  map_street;
    /** 定位数据 */
    private MyLocationData locdata = null;
    private MyLocationConfiguration locConfig = null;


    protected ViewSwitcher  vs_seeall;
    protected ImageView     iv_seeall;

    /** 显示当前位置 */
    protected  ViewSwitcher  vs_showloc;
    protected  ImageView   iv_showloc;

    /** 地图类型 */
    private View maplayer;
    protected   MapLoadingDialog    mLoadingDialog;

    protected ProgressBar  location_progress;

    protected BDAbstractLocationListener mLocationListener;

   // protected BDLocationListener   mLocationListener;

    /** 位置提醒 */
    //private BDNotifyListener    mBDNotifyListener;


    public MapView getmMapView() {
        return mMapView;
    }


    public BaiduMap getmBaiduMap() {
        return mBaiduMap;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View   view  =  onCreateView(LayoutInflater.from(this));
        setContentView(view);
        initView(savedInstanceState);

        initLoc();
        initialization();


    }


    public   void   initView(Bundle savedInstanceState ){
        mMapView = (MapView) findViewById(R.id.map_content);
        mBaiduMap = mMapView.getMap();
        vs_showloc = (ViewSwitcher)findViewById(R.id.vs_showloc);
        iv_showloc = (ImageView)findViewById(R.id.iv_showloc);
        iv_seeall = (ImageView)findViewById(R.id.iv_seeall);
        vs_seeall = (ViewSwitcher)findViewById(R.id.vs_seeall);
        location_icon = (ImageView)findViewById(R.id.location_icon);
        location_progress = (ProgressBar)findViewById(R.id.location_progress);
        zoom_out = (ImageButton)findViewById(R.id.zoom_out);
        zoom_in = (ImageButton)findViewById(R.id.zoom_in);
        road_condition = (ImageButton)findViewById(R.id.road_condition);
        map_layers = (ImageButton)findViewById(R.id.map_layers);
        map_street = (ImageButton)findViewById(R.id.map_street);
        iv_showloc.setTag("0");
        iv_showloc.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if(iv_showloc.getTag().equals("1")){
                   showViewInfo();
                }else{
                   hideViewInfo();
                }
            }
        });
        baseInitialization(savedInstanceState);
        initialization();
    }

    protected   LocationClient  mLocClient;
    protected   void  initLoc(){

        mLocClient = new LocationClient(this);

        mLocationListener = new BDAbstractLocationListener() {
            @Override public void onReceiveLocation(BDLocation bdLocation) {
                onBdReceiveLocation(bdLocation);
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

    /**
     * 添加位置提醒
     * @param latLng  坐标
     * @param d   范围
     */
    protected   void   addNotifyLoc(LatLng  latLng, float  d ){
        //if(null != mBDNotifyListener){
        //    mBDNotifyListener.SetNotifyLocation(latLng.latitude,latLng.longitude,d,mLocClient.getLocOption().getCoorType());
        //}
    }



    private void baseInitialization(Bundle  bundle){

        mMapView.showZoomControls(false);

        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开始交通地图
        mBaiduMap.setTrafficEnabled(false);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        locConfig = new MyLocationConfiguration(mCurrentMode, true, getLocationBitmap());

        mBaiduMap.setMyLocationConfiguration(locConfig);
        if(null != bundle){
            double  lat = bundle.getDouble("lat",0);
            double  lng = bundle.getDouble("lng",0);
            LatLng latLng = null;
            if(lat >0 && lng >0 ){
                latLng = new LatLng(lat,lng);
                MapStatus mMapStatus = new MapStatus.Builder().zoom(15).target(latLng).build();
                MapStatusUpdate statusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);

                mBaiduMap.animateMapStatus(statusUpdate);
            }
        }

        location_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mCurrentMode) {
                    case NORMAL:
                        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                        location_icon.setImageResource(R.drawable.main_icon_follow);
                        break;
                    case COMPASS:
                        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                        location_icon.setImageResource(R.drawable.main_icon_location);
                        break;
                    case FOLLOWING:
                        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
                        location_icon.setImageResource(R.drawable.main_icon_compass);
                        break;
                    default:
                        break;
                }
                mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(mCurrentMode, true, getLocationBitmap()));
            }
        });

        road_condition.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mBaiduMap.isTrafficEnabled()) {
                    road_condition.setImageResource(R.drawable.main_icon_roadcondition_off);
                    mBaiduMap.setTrafficEnabled(false);
                } else {
                    road_condition.setImageResource(R.drawable.main_icon_roadcondition_on);
                    mBaiduMap.setTrafficEnabled(true);
                }

            }
        });
        map_layers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showMapLayerDialog(v,10,-5);
            }
        });

        maplayer = LayoutInflater.from(this).inflate(R.layout.map_layer,null);
        layer_selector = (RadioGroup)maplayer.findViewById(R.id.layer_selector);
        layer_satellite = (RadioButton)maplayer.findViewById(R.id.layer_satellite);
        layer_2d = (RadioButton)maplayer.findViewById(R.id.layer_2d);
        layer_3d = (RadioButton)maplayer.findViewById(R.id.layer_3d);
        layer_2d.setChecked(true);
        layer_selector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.layer_satellite) {
                    //卫星
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                } else if (checkedId == R.id.layer_2d) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    MapStatus ms = new MapStatus.Builder(mBaiduMap.getMapStatus()).overlook(0).build();
                    MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
                    mBaiduMap.animateMapStatus(u);
                } else if (checkedId == R.id.layer_3d) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    MapStatus ms = new MapStatus.Builder(mBaiduMap.getMapStatus()).overlook(-45).build();
                    MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
                    mBaiduMap.animateMapStatus(u);
                }
            }
        });
        zoom_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float supportmax = mBaiduMap.getMaxZoomLevel();
                float localzoom = mBaiduMap.getMapStatus().zoom;

                if (localzoom == supportmax) {
                    zoom_in.setEnabled(false);
                    Toast.makeText(BaseMapActivity.this, "已到支持最大级别", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!zoom_out.isEnabled()) {
                    zoom_out.setEnabled(true);
                }
                MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(localzoom + 0.5f);
                mBaiduMap.animateMapStatus(u);
            }
        });
        zoom_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float supportmin = mBaiduMap.getMinZoomLevel();
                float localzoom = mBaiduMap.getMapStatus().zoom;
                if (localzoom == supportmin) {
                    zoom_out.setEnabled(false);
                    Toast.makeText(BaseMapActivity.this, "已到支持最小级别", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!zoom_in.isEnabled()){
                    zoom_in.setEnabled(true);
                }
                MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(localzoom - 0.5f);
                mBaiduMap.animateMapStatus(u);
            }
        });

        map_street.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             MyLocationData  locationData =  mBaiduMap.getLocationData();
             if(null == locationData){
                showToast("当前没有位置信息无法查看");
                return;
            }
                Intent intent = new Intent(BaseMapActivity.this,BaiduPanoramaActivity.class);
                intent.putExtra("lat",locationData.latitude);
                intent.putExtra("lng",locationData.longitude);
                startActivity(intent);

            }
        });
    }


    protected   void    showViewInfo(){
        iv_showloc.setImageResource(R.drawable.ic_visibility_black_24dp);
        iv_showloc.setTag("0");
        if(null != getmBaiduMap()){
            getmBaiduMap().setMyLocationEnabled(true);
        }
    }

    protected    void   hideViewInfo(){
        if(null != getmBaiduMap()){
            getmBaiduMap().setMyLocationEnabled(false);
        }
        iv_showloc.setImageResource(R.drawable.ic_visibility_off_black_24dp);
        iv_showloc.setTag("1");
    }


    private PopupWindow mPopupWindow;
    private RadioGroup  layer_selector;
    private RadioButton  layer_satellite,layer_2d,layer_3d;
    public   void    showMapLayerDialog(View v, int xoff, int yoff){

        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(maplayer, RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

        }
        if (mPopupWindow.isShowing()) {
            mPopupWindow.setAnimationStyle(R.anim.layer_pop_out);

            mPopupWindow.dismiss();
        } else {
            mPopupWindow.setAnimationStyle(R.anim.layer_pop_in);
            mPopupWindow.showAsDropDown(v, xoff, yoff);

        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        hidLoadialog();
        if(null != mBaiduMap){
            //关闭定位层
            mBaiduMap.setMyLocationEnabled(false);
        }
        if(null != mMapView){
            mMapView.onDestroy();
            mMapView = null;
        }
        //if(null != mBDNotifyListener){
        //    mLocClient.removeNotifyEvent(mBDNotifyListener);
        //}

        if(null != mLocClient && mLocClient.isStarted()){
            mLocClient.stop();
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        if(null != mMapView){
            mMapView.onPause();
        }
        if(null != mLocClient && mLocClient.isStarted()){
            mLocClient.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null != mMapView){
            mMapView.onResume();
        }
        if(null != mLocClient && !mLocClient.isStarted()){
            mLocClient.start();
        }
    }
    protected    boolean isFirstLoc = true;




    public void onBdReceiveLocation(BDLocation location) {
        // map view 销毁后不在处理新接收的位置
        if (location == null || mMapView == null) {
            return;
        }
        initMyLocationData(location);
        if (isFirstLoc) {
            System.out.println("-----------isFirstLoc------------");
            isFirstLoc = false;
            LatLng ll = new LatLng(location.getLatitude(),
                location.getLongitude());
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(u);
        }
        onLocation(location);


    }

    protected   void    initMyLocationData(BDLocation  location){
        MyLocationData locData = new MyLocationData.Builder()
            .accuracy(location.getRadius())
            // 此处设置开发者获取到的方向信息，顺时针0-360
            .direction(location.getDirection()).latitude(location.getLatitude())
            .longitude(location.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);
    }


    public   void   showLoadDialog(){
        runOnUiThread(new Runnable() {
            @Override public void run() {
                if(null == mLoadingDialog ){
                    mLoadingDialog = new MapLoadingDialog(BaseMapActivity.this);
                }

                if(!mLoadingDialog.isShowing()){
                    mLoadingDialog.show();
                }
            }
        });

    }


    public   void    hidLoadialog(){
       runOnUiThread(new Runnable() {
           @Override public void run() {
               if(null  != mLoadingDialog && mLoadingDialog.isShowing()){
                   mLoadingDialog.dismiss();
               }
           }
       });
    }

    public   void    showToast(final String  msg){
        runOnUiThread(new Runnable() {
            @Override public void run() {
                Toast.makeText(BaseMapActivity.this,msg,Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** 初始化 */
    public   abstract   void    initialization();

    /**
     * 初如化VIEW
     * @param inflater
     * @return
     */
    public  abstract    View    onCreateView(LayoutInflater inflater);

    /**
     * 返回百度定位数据
     * @param bdLocation
     */
    protected   abstract   void   onLocation(BDLocation   bdLocation);

    /**
     * 获取定位图标  空则使用默认
     * @return
     */
    protected  abstract BitmapDescriptor   getLocationBitmap();

    /**
     * 获取位置提醒信息
     * @param bdLocation
     * @param d
     */
    protected  abstract void   onBdNotify(BDLocation  bdLocation,float  d);
}
