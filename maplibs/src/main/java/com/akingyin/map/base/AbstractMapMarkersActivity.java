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
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.akingyin.map.MapPathPlanActivity;
import com.akingyin.map.R;
import com.akingyin.map.adapter.MarkderInfoPagerAdapter;
import com.akingyin.map.model.IMarkerModel;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.utils.DistanceUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 地图显示坐标信息
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2017/11/25 11:45
 */

public  abstract class AbstractMapMarkersActivity  extends  BaseMapActivity implements ILoadImage,IOperationListen{
  public BitmapDescriptor pathRead = BitmapDescriptorFactory.fromAsset("icon_road_red_arrow.png");
  public  BitmapDescriptor pathGreen = BitmapDescriptorFactory.fromAsset("icon_road_green_arrow.png");
  protected BitmapDescriptor readBitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark);
  protected BitmapDescriptor startBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_start);
  protected BitmapDescriptor endBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_end);
  protected BitmapDescriptor   personDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.person);
  private List<IMarkerModel>    dataQueue  = new LinkedList<>();

  protected   ExecutorService singleThreadPool = null;
  protected AtomicBoolean     isMapLoaded  = new AtomicBoolean(false);

  private ViewPager viewpager;
  private MarkderInfoPagerAdapter mInfoPagerAdapter;

  protected   MyManager   mManager;
  protected   MyManager    overlayManager;
  protected   OtherManager    mOtherManager;
  protected   Marker mCurrentMarker = null;
  protected Animation  mShowAction,mHiddenAction;
  protected Toolbar mToolbar;


  private PopupWindow mPopupBottonWindow;
  private View   popView;
  private ImageView  closeButton;

  public     IMarkerModel   getIMarkerModel(int  postion){
     if(postion>=0 && postion< dataQueue.size()){
      return dataQueue.get(postion);
     }
    return  null;
  }

  @Override public void initialization() {
    singleThreadPool = new  ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
      @Override public Thread newThread(@NonNull Runnable r) {
        return new Thread(r);
      }
    });


    mShowAction = AnimationUtils.loadAnimation(this, R.anim.layer_pop_in);
    mHiddenAction = AnimationUtils.loadAnimation(this, R.anim.layer_pop_out);
    popView = LayoutInflater.from(this).inflate(R.layout.item_openmap_viewpager,null);
    viewpager = (ViewPager)popView.findViewById(R.id.viewpager);
    closeButton = (ImageView)popView.findViewById(R.id.close);
    closeButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(null != mPopupBottonWindow && mPopupBottonWindow.isShowing()){
          mPopupBottonWindow.dismiss();
        }
      }
    });
    mToolbar = (Toolbar)findViewById(R.id.toolbar);
    setToolBar(mToolbar,getTitleInfo());
    vs_seeall.setVisibility(View.VISIBLE);
    iv_seeall.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(null != mManager){
          mManager.zoomToSpan();
        }
      }
    });
    mInfoPagerAdapter = new MarkderInfoPagerAdapter(this,new ArrayList<IMarkerModel>());

    mInfoPagerAdapter.setILoadImage(this);
    mInfoPagerAdapter.setIOperationListen(this);
    viewpager.setAdapter(mInfoPagerAdapter);
    getmBaiduMap().setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
      @Override public void onMapLoaded() {

         onRefreshMarkders();
      }
    });
    getmBaiduMap().setOnMapClickListener(new BaiduMap.OnMapClickListener() {
      @Override public void onMapClick(LatLng latLng) {
        onBaiduMapClick(latLng);
      }

      @Override public void onMapPoiClick(MapPoi mapPoi) {
        if(null != mapPoi){
          onBaiduMapClick(mapPoi.getPosition());
        }
      }
    });

  }

  protected   void    onBaiduMapClick(LatLng   latLng){}

  public   void   hiddenPopWindow(){
    if(null != mPopupBottonWindow && mPopupBottonWindow.isShowing()){
      mPopupBottonWindow.dismiss();
    }

  }


  protected   AtomicBoolean    isloadingToMarkders = new AtomicBoolean(false);


  protected    void     onRefreshMarkersBefore(){

  }

  protected    void   onRefreshMarkersAfter(){

  }
  /**
   * 刷新当前markders
   */
  protected   void    onRefreshMarkders(){
    if(!isloadingToMarkders.get()){
      onRefreshMarkersBefore();
      isloadingToMarkders.getAndSet(true);
      singleThreadPool.execute(new Runnable() {
        @Override public void run() {
          try {
            dataQueue.clear();
            List<IMarkerModel>  iMarkerModels = onLoadMarkerDatas();
            dataQueue.addAll(iMarkerModels);
            toMapMarkers();
            isMapLoaded.getAndSet(true);
            if(showPathPlan() && displayInOrder()){
              loadMarkerPath(iMarkerModels,null);
            }else{
              if(showPathPlan()){
                lastbdBDLocation = null;
                firstLoadPath = true;
              }
            }
          }catch (Exception e){
            e.printStackTrace();
          }finally {
            isloadingToMarkders.getAndSet(false);
            onRefreshMarkersAfter();
          }

        }
      });
    }

  }


  private    BDLocation    lastbdBDLocation;
  @Override
  protected void onLocation(final BDLocation bdLocation) {

     //当前要显示路径 且不用内置的顺序
     if(showPathPlan() && !displayInOrder()){

       if(null == lastbdBDLocation || DistanceUtil.getDistance(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()),new LatLng(lastbdBDLocation.getLatitude(),lastbdBDLocation.getLongitude()))>getMinDisFlushPath()){
         if(isMapLoaded.get() && !isloadingToMarkders.get()){
           lastbdBDLocation = bdLocation;
           temps.clear();
           temps.addAll(dataQueue);

           singleThreadPool.execute(new Runnable() {
             @Override public void run() {

               loadMarkerPath(temps,new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()));
             }
           });
         }

       }

     }
  }

  /** 所有路线点集合 */
  List<LatLng> points = new LinkedList<>();
  List<LatLng> points2 = new LinkedList<>();
  List<Integer> indexs = new ArrayList<>();

  /** 构造纹理队列 */
  List<BitmapDescriptor> customList = new ArrayList<>();

  private   Polyline   polylineMarker = null;
  private Marker  personMarder;

  private    boolean  firstLoadPath = true;
  protected    void   loadMarkerPath(List<IMarkerModel>   iMarkerModels,LatLng  currentLatlng){
    if(isMapLoaded.get() && !isloadingToMarkders.get()){
      try {
        filterMakerToPath(temps);
        isMapLoaded.getAndSet(false);
        List<IMarkerModel> iMarkerModels1  = new ArrayList<>();
        Iterator<IMarkerModel>   iterator = iMarkerModels.iterator();
        while (iterator.hasNext()){
          IMarkerModel  iMarkerModel = iterator.next();
          if(!iMarkerModel.isComplete()){
            iMarkerModels1.add(iMarkerModel);
          }
        }
        if(displayInOrder() || null == currentLatlng){
          Collections.sort(iMarkerModels1, new Comparator<IMarkerModel>() {
            @Override public int compare(IMarkerModel o1, IMarkerModel o2) {
              if(o1.getAppointSort() > o2.getAppointSort()){
                return 1;
              }else if(o1.getAppointSort() < o2.getAppointSort()){
                return  -1;
              }
              return 0;
            }
          });
        }else{
          iMarkerModels1 = PathPlanUtil.getOptimalPathPlan(iMarkerModels1,currentLatlng);
        }
        points.clear();
        indexs.clear();
        if(iMarkerModels1.size() >0){
          Integer  startSort = null;
          int  index = 0;

          for (IMarkerModel iMarkerModel : iMarkerModels1) {

            if(!iMarkerModel.isComplete() && null  == startSort){
              startSort = index;
            }

            points.add(new LatLng(iMarkerModel.getLat(),iMarkerModel.getLng()));
            index++;

          }
          //处理经纬度，将排序后的计算距离小于2米的直接去掉一个
          Iterator<LatLng> iterator2 = points.listIterator();

          LatLng  latLng =null;
          while(iterator2.hasNext()){
            LatLng  temp = iterator2.next();
            if(null != latLng){
              if(DistanceUtil.getDistance(latLng,temp)<2){
                iterator2.remove();
              }
            }
            latLng = temp;
          }

          indexs.clear();
          for (int i = 0; i < points.size(); i++) {
            indexs.add(1);
          }
          if(null != polylineMarker){
            polylineMarker.remove();
          }
          if(null != personMarder){
            personMarder.remove();
            personMarder = null;
          }

          if(customList.size() == 0){
            customList.add(pathGreen);
            customList.add(pathRead);
          }
          if(points.size()<=2){
            return;
          }
          PolylineOptions polylineOptionBg = new PolylineOptions();
          // 折线线宽， 默认为 5， 单位：像素
          polylineOptionBg.width(20);
          // 折线是否虚线
          polylineOptionBg.dottedLine(true);
          //  polylineOptionBg.color(0xAAFF0000); // 折线颜色
          // 折线坐标点列表:[2,10000]，且不能包含null
          polylineOptionBg.points(points);
          // 纹理宽、高是否保持原比例渲染
          polylineOptionBg.keepScale(true);
          polylineOptionBg.textureIndex(indexs);
          polylineOptionBg.customTextureList(customList);

          polylineMarker = (Polyline) getmBaiduMap().addOverlay(polylineOptionBg);

          //绘制终点与启点
          List<LatLng> pointStartEnd = new ArrayList<>(2);
          pointStartEnd.add(points.get(null != startSort && startSort<points.size()?startSort:0));
          pointStartEnd.add(points.get(points.size() - 1));

          OverlayOptions  persontempMarker = new MarkerOptions()
              .position(pointStartEnd.get(0)).animateType(
                  MarkerOptions.MarkerAnimateType.grow).icon(personDescriptor).draggable(false);
          personMarder = (Marker) getmBaiduMap().addOverlay(persontempMarker);
          Bundle bundle =  new Bundle();

          OverlayOptions startmarker = new MarkerOptions()
              .position(pointStartEnd.get(0)).animateType(
                  MarkerOptions.MarkerAnimateType.drop).icon(startBitmap).draggable(false);

          OverlayOptions endmarker = new MarkerOptions()
              .position(pointStartEnd.get(1)).extraInfo(bundle).animateType(
                  MarkerOptions.MarkerAnimateType.drop).icon(endBitmap).draggable(false);
          if(null == overlayManager){
            overlayManager = new MyManager(getmBaiduMap(),new LinkedList<OverlayOptions>());
          }


          overlayManager.cleanOverlay();
          overlayManager.addOverlay(startmarker);
          overlayManager.addOverlay(endmarker);
          overlayManager.addToMap();
          mManager.setNewPathDatas(iMarkerModels1);

          MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(points.get(0));
          getmBaiduMap().animateMapStatus(u);
          if(firstLoadPath){

            showListPathMarker(iMarkerModels1,0);

          }
          firstLoadPath = false;
        }else {
          if(null != overlayManager){
            overlayManager.removeFromMap();
            overlayManager.cleanOverlay();
          }
        }

      }catch (Exception e){
        e.printStackTrace();
      }finally {
        isMapLoaded.getAndSet(true);
      }
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
    if(null != mPopupBottonWindow && mPopupBottonWindow.isShowing()){
      mPopupBottonWindow.dismiss();
      return;
    }
    super.onBackPressed();
  }

  private  List<IMarkerModel>  temps = new LinkedList<>();
  protected   AtomicBoolean   flushMarker  =  new AtomicBoolean(false);
  public   synchronized    void   toMapMarkers(){
    if(!flushMarker.get()){
      try {
        flushMarker.set(true);
        temps.clear();
        temps.addAll(dataQueue);
        filterAddMakerToMap(temps);
        try {
          if(null != mManager){
            mManager.removeAll();
            mManager.removeFromMap();
          }
          if(null != mOtherManager){
            mOtherManager.removeAll();
            mOtherManager.removeFromMap();
          }
          if(null != polylineMarker){
            polylineMarker.remove();
          }
          if(null != personMarder){
            personMarder.remove();
          }
          if(null != overlayManager){
            overlayManager.removeAll();
            overlayManager.cleanOverlay();
            overlayManager.removeFromMap();
          }

        }catch (Exception e){
          e.printStackTrace();
        }
        List<OverlayOptions>   overlayOptions = new LinkedList<>();
        int   index = 0;
        for (IMarkerModel iMarkerModel : temps) {
          LatLng point = new LatLng(iMarkerModel.getLat(), iMarkerModel.getLng());
          Bundle bundle = new Bundle();
          bundle.putInt("index", index);
          MarkerOptions  option = new MarkerOptions().animateType(getMarkerAnimateByMarker(iMarkerModel))
              .position(point)

              .draggable(isDragMarker(iMarkerModel))
              .icon(getMarkerBitmapDescriptor(iMarkerModel))
              .title("index=" + index).extraInfo(bundle);
          overlayOptions.add(option);
          index++;

        }
        if(null == mOtherManager){
          mOtherManager = new OtherManager(getmBaiduMap());
          getmBaiduMap().setOnMarkerClickListener(mOtherManager);
        }
        addOtherOverlay(mOtherManager);
        if(null == mManager){
          mManager = new MyManager(getmBaiduMap(), overlayOptions);

          getmBaiduMap().setOnMarkerClickListener(mManager);
        }else{
          mManager.cleanOverlay();
          mManager.addOverlays(overlayOptions);
        }

        mManager.setNewDatas(temps);
        mManager.addToMap();
        mManager.zoomToSpan();
      }catch (Exception e){
        e.printStackTrace();
      }finally {
        flushMarker.set(false);
      }
    }


  }


  protected MarkerOptions.MarkerAnimateType  getMarkerAnimateByMarker(IMarkerModel  iMarkerModel){
    return MarkerOptions.MarkerAnimateType.none;
  }

  /**
   * 添加其它遮盖物
   */
  protected    void     addOtherOverlay(OtherManager  overlayManager){

  }

  /**
   * 初始化popup
   */
  protected   void   initPopupWindow(){
    if (null == mPopupBottonWindow) {

      mPopupBottonWindow = new PopupWindow(this);
      mPopupBottonWindow.setContentView(popView);
      mPopupBottonWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
      mPopupBottonWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
      mPopupBottonWindow.setFocusable(true);
      mPopupBottonWindow.setBackgroundDrawable(new BitmapDrawable());
      mPopupBottonWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
          if (null != mCurrentMarker) {
            int index = mCurrentMarker.getExtraInfo().getInt("index");
            if(index>=0 && index<mManager.getIMarkerModels().size()){
              IMarkerModel  iMarkerModel1 = mManager.getIMarkerModels().get(index);
              mCurrentMarker.setIcon(getMarkerBitmapDescriptor(iMarkerModel1));
            }

          }

        }
      });
    }



  }

  private ViewPager.OnPageChangeListener  mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override public void onPageSelected(int position) {
      IMarkerModel  testMarkerModel = mInfoPagerAdapter.getIMarkerModel(position);
      Marker   tempMarker = mManager.getMarker(testMarkerModel);
      if(null !=tempMarker) {
        if(null != mCurrentMarker){
          IMarkerModel  temp = mManager.getIMarkerModels().get(mCurrentMarker.getExtraInfo().getInt("index"));
          mCurrentMarker.setIcon(getMarkerBitmapDescriptor(temp));
        }
        mCurrentMarker = tempMarker;
        mCurrentMarker.setIcon(readBitmap);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(new LatLng(testMarkerModel.getLat(),testMarkerModel.getLng()));
        getmBaiduMap().animateMapStatus(u);
      }


    }

    @Override public void onPageScrollStateChanged(int state) {

    }
  };

  Handler   mainHandle = new Handler(Looper.getMainLooper());
  /**
   * 显示当前路径所有Marker
   * @param iMarkerModels
   */
  protected   void   showListPathMarker(final List<IMarkerModel>  iMarkerModels , final int   postion){
    mainHandle.post(new Runnable() {
      @Override public void run() {
        initPopupWindow();
        if(mPopupBottonWindow.isShowing()){
          mPopupBottonWindow.dismiss();
        }
        List<IMarkerModel>  showImarkers = new ArrayList<>();
        int  index = 0;
        for (IMarkerModel iMarkerModel : iMarkerModels) {
          index++;
          if(null != iMarkerModel.getMarkes() && iMarkerModel.getMarkes().size()>0){
            iMarkerModel.setSortInfo("详情   "+iMarkerModels.size()+"-"+index+"("+(iMarkerModel.getMarkes().size()+1)+"-1)");
            showImarkers.add(iMarkerModel);

            for (int i = 0; i < iMarkerModel.getMarkes().size(); i++) {
              iMarkerModel.getMarkes().get(i).setSortInfo("详情   "+iMarkerModels.size()+"-"+index+"("+(iMarkerModel.getMarkes().size()+1)+"-"+(i+2)+")");
              showImarkers.add(iMarkerModel.getMarkes().get(i));
            }

          }else{
            iMarkerModel.setSortInfo("详情   "+iMarkerModels.size()+"-"+index);
            showImarkers.add(iMarkerModel);
          }
        }

        mInfoPagerAdapter = new MarkderInfoPagerAdapter(AbstractMapMarkersActivity.this,showImarkers);
        mInfoPagerAdapter.setIOperationListen(AbstractMapMarkersActivity.this);
        mInfoPagerAdapter.setPathMarker(true);
        mInfoPagerAdapter.setILoadImage(AbstractMapMarkersActivity.this);
        viewpager.removeOnPageChangeListener(mOnPageChangeListener);
        viewpager.addOnPageChangeListener(mOnPageChangeListener);
        viewpager.setAdapter(mInfoPagerAdapter);
        viewpager.startAnimation(mShowAction);
        mPopupBottonWindow.setOutsideTouchable(false);
        mPopupBottonWindow.setFocusable(false);
        viewpager.setCurrentItem(postion);
        mCurrentMarker = mManager.getMarker(iMarkerModels.get(postion));
        if(null != mCurrentMarker){
          mCurrentMarker.setIcon(readBitmap);
        }
        if (mPopupBottonWindow.isShowing()) {
          mPopupBottonWindow.dismiss();
        } else {
          mPopupBottonWindow.showAtLocation(closeButton, Gravity.BOTTOM, 0, 0);
        }
      }
    });

  }


  public    void    setMarkerSelected(IMarkerModel  iMarkerModel){
    if(null != mCurrentMarker){
      IMarkerModel  temp = mManager.getIMarkerModels().get(mCurrentMarker.getExtraInfo().getInt("index"));
      mCurrentMarker.setIcon(getMarkerBitmapDescriptor(temp));
    }
    mCurrentMarker = mManager.getMarker(iMarkerModel);
    if(null != mCurrentMarker){
      mCurrentMarker.setIcon(readBitmap);
      MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(new LatLng(iMarkerModel.getLat(),iMarkerModel.getLng()));
      getmBaiduMap().animateMapStatus(u);
    }
  }

  protected   void   showMarkerInfo(Marker  marker){

    try {
      initPopupWindow();
      if(null == marker || null == marker.getExtraInfo() || !marker.getExtraInfo().containsKey("index")){
        return;
      }
      if(null != mPopupBottonWindow && mPopupBottonWindow.isShowing()){
        mPopupBottonWindow.dismiss();
      }
      int  index = marker.getExtraInfo().getInt("index");

      IMarkerModel   iMarkerModel = mManager.getIMarkerModels().get(index);
      MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(new LatLng(iMarkerModel.getLat(),iMarkerModel.getLng()));
      getmBaiduMap().animateMapStatus(u);

      int   postion = mManager.containsMarkerInPath(iMarkerModel);
      if(postion>=0){
        showListPathMarker(mManager.getPathIMarkerModels(),postion);
        return;
      }
      List<IMarkerModel>  iMarkerModels = new LinkedList<>();
      iMarkerModels.add(iMarkerModel);
      if(null != iMarkerModel.getMarkes()){
        iMarkerModels.addAll(iMarkerModel.getMarkes());
      }
      mInfoPagerAdapter = new MarkderInfoPagerAdapter(this,iMarkerModels);
      mInfoPagerAdapter.setIOperationListen(this);
      mInfoPagerAdapter.setILoadImage(this);
      viewpager.setAdapter(mInfoPagerAdapter);
      viewpager.startAnimation(mShowAction);
      viewpager.setCurrentItem(0);
      mCurrentMarker = marker;
      mCurrentMarker.setIcon(readBitmap);
      if (mPopupBottonWindow.isShowing()) {
        mPopupBottonWindow.dismiss();
      } else {
        mPopupBottonWindow.showAtLocation(closeButton, Gravity.BOTTOM, 0, 0);
      }
    }catch (Exception  e){
      e.printStackTrace();
    }catch (Error  e){
      e.printStackTrace();
    }

  }

  /**
   *
   * @return
   */
  protected   abstract   String   getTitleInfo();

  /**
   * 加载当前markers数据
   * @return
   */
  protected abstract   List<IMarkerModel>   onLoadMarkerDatas();

  /**
   * 获取当前marker图标
   * @param iMarkerModel
   * @return
   */
  protected abstract BitmapDescriptor  getMarkerBitmapDescriptor(IMarkerModel iMarkerModel);

  /**
   * 当前是否可以拖动
   * @param iMarkerModel
   * @return
   */
  protected   boolean     isDragMarker(IMarkerModel iMarkerModel){
    return  false;
  }
  /**
   * 加载到地图上
   * 过滤当前的markers
   * @param iMarkerModels
   */
  protected abstract   void     filterAddMakerToMap(List<IMarkerModel>  iMarkerModels);

  /**
   * 过滤路径
   * 过滤当前的markers
   * @param iMarkerModels
   */
  protected abstract   void     filterMakerToPath(List<IMarkerModel>  iMarkerModels);

  /**
   * 是否安指定顺序显示
   * @return
   */
  protected abstract   boolean   displayInOrder();

  /**
   * 是否显示基于marker点的路径规划
   * @return
   */
  protected abstract   boolean    showPathPlan();

  /**
   * 最短距离刷新路径规划
   * @return
   */
  protected abstract   int   getMinDisFlushPath();

  @Override public void onOtherOperation(int postion, IMarkerModel iMarkerModel, View view) {

  }

  @Override public void onPathPlan(int postion, IMarkerModel iMarkerModel) {
    Intent   intent  = new Intent(this, MapPathPlanActivity.class);
    intent.putExtra("lat",iMarkerModel.getLat());
    intent.putExtra("lng",iMarkerModel.getLng());
    startActivity(intent);
  }

  /**
   * 其它marker点击事件
   * @param marker
   * @return
   */
  protected   boolean   onClickOtherMarker(Marker   marker,Integer  integer){
    return   false;
  }

  public    class    OtherManager extends  OverlayManager{

    private List<OverlayOptions> overlays = new ArrayList<>();


    public OtherManager(BaiduMap baiduMap) {
      super(baiduMap);
    }
    public synchronized void addOverlay(OverlayOptions option) {
      overlays.add(option);
    }
    public synchronized void addOverlays(List<OverlayOptions> options) {
      overlays.addAll(options);
    }
    public void removeOverlay(OverlayOptions option) {
      overlays.remove(option);

    }

    public void removeAll() {
      overlays.clear();
    }
    @Override public List<OverlayOptions> getOverlayOptions() {
      return overlays;
    }

    @Override public boolean onMarkerClick(Marker marker) {
      Integer    index = null;
      if(null != marker.getExtraInfo() && marker.getExtraInfo().containsKey("index")){
          index = marker.getExtraInfo().getInt("index");
      }

      return onClickOtherMarker(marker,index);
    }

    @Override public boolean onPolylineClick(Polyline polyline) {
      return false;
    }
  }

  public    final   static   String    OTHER_MARKER_KEY="other_marker_key";
  public class  MyManager extends OverlayManager{
    private  List<IMarkerModel>  mIMarkerModels = new LinkedList<>();

    private  List<IMarkerModel>  pathIMarkerModels = new LinkedList<>();


    private    int   containsMarkerInPath(IMarkerModel   iMarkerModel){

      return   pathIMarkerModels.indexOf(iMarkerModel);
    }

    public List<IMarkerModel> getPathIMarkerModels() {

      return pathIMarkerModels;
    }

    public List<IMarkerModel> getIMarkerModels() {
      return mIMarkerModels;
    }

    public void setNewDatas(List<IMarkerModel> datas) {
      mIMarkerModels.clear();
      mIMarkerModels.addAll(datas);
    }

    public   Marker   getMarker(IMarkerModel  iMarkerModel){
      for (int i = 0; i < mIMarkerModels.size(); i++) {
        if(mIMarkerModels.get(i) == iMarkerModel){
          return (Marker) getOverLay(i);
        }
      }
      return null;
    }

    public  void   setNewPathDatas(List<IMarkerModel>  datas){
      pathIMarkerModels.clear();
      pathIMarkerModels.addAll(datas);
    }

    public   void   cleanOverlay(){
      mIMarkerModels.clear();
      pathIMarkerModels.clear();
      overlays.clear();
    }

    private List<OverlayOptions> overlays = new ArrayList<>();
    public MyManager(BaiduMap baiduMap,List<OverlayOptions>  overlays) {
      super(baiduMap);
      this.overlays = overlays;
    }
    public synchronized void addOverlay(OverlayOptions option) {
      overlays.add(option);
    }
    public synchronized void addOverlays(List<OverlayOptions> options) {
      overlays.addAll(options);
    }
    public void removeOverlay(OverlayOptions option) {
      overlays.remove(option);
    }

    public void removeAll() {
      overlays.clear();
    }
    @Override public List<OverlayOptions> getOverlayOptions() {
      return overlays;
    }

    @Override public boolean onMarkerClick(Marker marker) {
      System.out.println("onMarkerClick");

      if(null != marker && null != marker.getExtraInfo() && marker.getExtraInfo().containsKey(OTHER_MARKER_KEY)){
        onClickOtherMarker(marker,marker.getExtraInfo().getInt("index"));
      }else{
        showMarkerInfo(marker);
      }

      return true;
    }

    @Override public boolean onPolylineClick(Polyline polyline) {
      return false;
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    try {
      hiddenPopWindow();
    }catch (Exception e){
      e.printStackTrace();
    }
    try {
      if(null != singleThreadPool){
        singleThreadPool.shutdown();
      }
    }catch (Exception e){
      e.printStackTrace();
    }
    if(null != pathGreen){
      pathGreen.recycle();
    }
    if(null != readBitmap){
      readBitmap.recycle();
    }
    if(null != pathRead){
      pathRead.recycle();
    }

    if(null != startBitmap){
      startBitmap.recycle();
    }
    if(null != endBitmap){
      endBitmap.recycle();
    }
    if(null != personDescriptor){
      personDescriptor.recycle();
    }
  }

  @Override public void initView(TextView left, TextView center, TextView right, int postion,
      IMarkerModel iMarkerModel, View... views) {

  }

  @Override protected void showViewInfo() {
    super.showViewInfo();
    if(null != getmBaiduMap() && null != getmBaiduMap().getLocationData()){
      LatLng   latLng = new LatLng(getmBaiduMap().getLocationData().latitude,getmBaiduMap().getLocationData().longitude);
      loadMarkerPath(mManager.getPathIMarkerModels(),latLng);
    }

  }

  @Override protected void hideViewInfo() {
    super.hideViewInfo();
    if(null != overlayManager){
      overlayManager.cleanOverlay();
      overlayManager.removeAll();
      overlayManager.removeFromMap();
    }
    if(null != personMarder){
      personMarder.remove();
    }
    if(null != polylineMarker){
      polylineMarker.remove();
    }
  }

  @Override protected void onBdNotify(BDLocation bdLocation, float d) {

  }
}
