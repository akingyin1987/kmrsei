/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.map;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import androidx.appcompat.widget.Toolbar;
import com.akingyin.map.base.BaiduPanoramaActivity;
import com.akingyin.map.base.BaseMapActivity;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * 路径规划
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2017/11/29 17:51
 */

public class MapPathPlanActivity  extends BaseMapActivity implements OnGetRoutePlanResultListener {

  Toolbar mToolbar;

  SegmentedGroup    mSegmentedGroup;

  private   RoutePlanSearch   mRoutePlanSearch;

  RouteLine route = null;
  OverlayManager routeOverlay = null;
  PlanNode startNode = null;
  PlanNode endNode = null;
  String   city="重庆";

   //目的地
   private   String    destination;
  @Override public void initialization() {
    mToolbar = (Toolbar)findViewById(R.id.toolbar);
    setToolBar(mToolbar,"路径规划");
    double  lat = getIntent().getDoubleExtra("lat",0);
    double  lng = getIntent().getDoubleExtra("lng",0);
    destination = getIntent().getStringExtra("destination");
    endNode = PlanNode.withLocation(new LatLng(lat,lng));
    map_street.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        if(null == endNode){
          showToast("当前没有位置信息无法查看");
          return;
        }
        Intent intent = new Intent(MapPathPlanActivity.this, BaiduPanoramaActivity.class);
        intent.putExtra("lat",endNode.getLocation().latitude);
        intent.putExtra("lng",endNode.getLocation().longitude);
        intent.putExtra("addr",TextUtils.isEmpty(destination)?"":destination);

        startActivity(intent);

      }
    });
    mSegmentedGroup = (SegmentedGroup)findViewById(R.id.sb_group);
    mRoutePlanSearch = RoutePlanSearch.newInstance();
    mRoutePlanSearch.setOnGetRoutePlanResultListener(this);
    mSegmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(RadioGroup group, int checkedId) {


         serarchPath(checkedId);
      }
    });
    iv_seeall.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(null != routeOverlay){

          routeOverlay.zoomToSpan();
        }
      }
    });
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

  protected   void   setEnable(boolean  enable){
    mSegmentedGroup.setEnabled(enable);
  }
  private   void    serarchPath(int   checkedId){
    if(null == getmBaiduMap() || null == getmMapView()){
      return;
    }
    MyLocationData   locationData = getmBaiduMap().getLocationData();

    if(null == locationData){

      showToast("请出现当前位置后再进行路径规划");
      return;
    }

    showLoadDialog();
    mLoadingDialog.setCancelable(false);
    startNode =PlanNode.withLocation(new LatLng(locationData.latitude,locationData.longitude));


    if (routeOverlay != null) {
      routeOverlay.removeFromMap();
    }
    if(checkedId == R.id.rb_driving){
      mRoutePlanSearch.drivingSearch(new DrivingRoutePlanOption().from(startNode).to(endNode));
    }else if(checkedId == R.id.rb_transport){
      mRoutePlanSearch.transitSearch(new TransitRoutePlanOption().from(startNode).to(endNode).city(city));
    }else if(checkedId == R.id.rb_walking){
      mRoutePlanSearch.walkingSearch(new WalkingRoutePlanOption().from(startNode).to(endNode));

    }
  }

  @Override public View onCreateView(LayoutInflater inflater) {
    return inflater.inflate(R.layout.activity_map_path_plan,null);
  }


  boolean    isfristLoad = true;
  @Override protected void onLocation(BDLocation bdLocation) {
     if(!TextUtils.isEmpty(bdLocation.getCity())){
       city = bdLocation.getCity();
     }
    if(isfristLoad){
      System.out.println("------------------------");
      serarchPath(R.id.rb_driving);
      isfristLoad = false;
    }

  }

  @Override protected BitmapDescriptor getLocationBitmap() {
    return null;
  }

  @Override protected void onBdNotify(BDLocation bdLocation, float d) {

  }

  @Override public void onGetWalkingRouteResult(WalkingRouteResult result) {
    hidLoadialog();
    if (null == getmMapView()) {
      return;
    }
    if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
      showToast("抱歉，未找到结果");
    }
    if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
      // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
      // result.getSuggestAddrInfo()
      return;
    }

    if (result.error == SearchResult.ERRORNO.NO_ERROR) {
      if (routeOverlay != null) {
        routeOverlay.removeFromMap();
      }
      route = result.getRouteLines().get(0);
      WalkingRouteOverlay overlay = new WalkingRouteOverlay(getmBaiduMap());
      getmBaiduMap().setOnMarkerClickListener(overlay);
      routeOverlay = overlay;
      overlay.setData(result.getRouteLines().get(0));
      overlay.addToMap();
      overlay.zoomToSpan();
    }
  }

  @Override public void onGetTransitRouteResult(TransitRouteResult result) {
   hidLoadialog();
    if (null == getmMapView()) {
      return;
    }
    if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
       showToast("抱歉，未找到结果");
    }
    if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
      // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
      // result.getSuggestAddrInfo()
      return;
    }
    if (result.error == SearchResult.ERRORNO.NO_ERROR) {
      if (routeOverlay != null) {
        routeOverlay.removeFromMap();
      }
      route = result.getRouteLines().get(0);
      TransitRouteOverlay overlay = new TransitRouteOverlay(getmBaiduMap());
      getmBaiduMap().setOnMarkerClickListener(overlay);
      routeOverlay = overlay;
      overlay.setData(result.getRouteLines().get(0));
      overlay.addToMap();
      overlay.zoomToSpan();
    }
  }

  @Override public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

  }

  @Override public void onGetDrivingRouteResult(DrivingRouteResult result) {
   hidLoadialog();
    if (null == getmMapView()) {
      return;
    }

    if (routeOverlay != null) {
      routeOverlay.removeFromMap();
    }
    if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
      showToast("抱歉，未找到结果");
    }
    if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
      // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
      // result.getSuggestAddrInfo()
      return;
    }
    if (result.error == SearchResult.ERRORNO.NO_ERROR) {

      route = result.getRouteLines().get(0);
      DrivingRouteOverlay overlay = new DrivingRouteOverlay(getmBaiduMap());
      routeOverlay = overlay;
      getmBaiduMap().setOnMarkerClickListener(overlay);
      overlay.setData(result.getRouteLines().get(0));
      overlay.addToMap();
      overlay.zoomToSpan();
    }
  }

  @Override public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

  }

  @Override public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {

    getMenuInflater().inflate(R.menu.menu_map_path,menu);


    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
     openBaiduMap(item.getItemId());
    return true;
  }


  private    void    openBaiduMap(int  itemId){
    if(null == getmBaiduMap() || null == getmMapView()){
      return;
    }
    MyLocationData  locationData = getmBaiduMap().getLocationData();
    if(null == locationData){
      showToast("请出现当前位置后再进行导航");
      return;
    }
    NaviParaOption para = new NaviParaOption()
        .startPoint(new LatLng(locationData.latitude,locationData.longitude))
        .startName("当前位置")
        .endPoint(endNode.getLocation())
        .endName(TextUtils.isEmpty(destination)?"终点":destination);


    try {
      boolean  result = false;
      if(itemId == R.id.item_driving){
       result = BaiduMapNavigation.openBaiduMapNavi(para,this);
      }else if(itemId == R.id.item_walking){
       result = BaiduMapNavigation.openBaiduMapWalkNavi(para,this);
      }else if(itemId == R.id.item_ar){
         result = BaiduMapNavigation.openBaiduMapWalkNaviAR(para,this);
      }else if(itemId == R.id.item_transport){
        RouteParaOption para2 = new RouteParaOption()
            .startPoint(para.getStartPoint())
            .endPoint(para.getEndPoint())
            .busStrategyType(RouteParaOption.EBusStrategyType.bus_recommend_way);
       result =  BaiduMapRoutePlan.openBaiduMapTransitRoute(para2, this);
      }
      if(!result){
        showToast("调用百度地图出错，请检查是否安装最新版本");
      }
    }catch (Exception e){
      e.printStackTrace();
      showToast("调用百度地图出错，请检查是否安装最新版本");
    }

  }

  @Override public void onDestroy() {
    super.onDestroy();
    try {
      BaiduMapNavigation.finish(this);
      BaiduMapRoutePlan.finish(this);
    }catch (Exception e){
      e.printStackTrace();
    }
    if(null != mRoutePlanSearch){
      mRoutePlanSearch.destroy();
    }

  }
}
