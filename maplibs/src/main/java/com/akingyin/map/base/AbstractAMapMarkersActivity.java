package com.akingyin.map.base;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupWindow;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;
import com.akingyin.map.MapPathPlanActivity;
import com.akingyin.map.R;
import com.akingyin.map.adapter.MarkderInfoPagerAdapter;
import com.akingyin.map.adapter.MarkerInfoRecyclerAdapter;
import com.akingyin.map.model.IMarkerModel;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.overlayutil.OverlayManager;
import com.baidu.mapapi.utils.CoordinateConverter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONObject;

/**
 * 基于高德地图 坐标点的显示
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2020/1/6 11:09
 */
public abstract class AbstractAMapMarkersActivity extends BaseAMapActivity implements ILoadImage,IOperationListen{

  public BitmapDescriptor pathRead = BitmapDescriptorFactory.fromAsset("icon_road_red_arrow.png");
  public  BitmapDescriptor pathGreen = BitmapDescriptorFactory.fromAsset("icon_road_green_arrow.png");
  protected BitmapDescriptor readBitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark);
  protected BitmapDescriptor startBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_start);
  protected BitmapDescriptor endBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_end);
  protected BitmapDescriptor   personDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.person);
  private List<IMarkerModel> dataQueue  = new LinkedList<>();

  protected ExecutorService singleThreadPool = null;
  protected AtomicBoolean isMapLoaded  = new AtomicBoolean(false);

  private ViewPager viewpager;
  private MarkderInfoPagerAdapter mInfoPagerAdapter;

  private ViewPager2 mRecyclerView;
  private MarkerInfoRecyclerAdapter<IMarkerModel> mMarkerInfoRecyclerAdapter;

  /** 当前显示操作marker */
  protected MyManager mManager;

  /** 路径 */
  protected MyManager overlayManager;
  /** 其它操作类marker */
  protected OtherManager mOtherManager;
  protected Marker mCurrentMarker = null;
  protected   List<BitmapDescriptor>   lastClickMarkerIcon = null;
  protected Animation mShowAction,mHiddenAction;
  protected Toolbar mToolbar;


  private PopupWindow mPopupBottonWindow;
  private View popView;
  private ImageView closeButton;

  public     IMarkerModel   getIMarkerModel(int  postion){

    if(postion>=0 && postion< dataQueue.size()){
      return dataQueue.get(postion);
    }
    return  null;
  }

  @Override public void initialization() {
    singleThreadPool =  Executors.newFixedThreadPool(1);
    mShowAction = AnimationUtils.loadAnimation(this, R.anim.layer_pop_in);
    mHiddenAction = AnimationUtils.loadAnimation(this, R.anim.layer_pop_out);
    popView = LayoutInflater.from(this).inflate(R.layout.item_openmap_recyclerview,null);
    mRecyclerView = popView.findViewById(R.id.map_recycler);
    closeButton = popView.findViewById(R.id.close);
    closeButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(null != mPopupBottonWindow && mPopupBottonWindow.isShowing()){
          mPopupBottonWindow.dismiss();
        }
      }
    });
    mToolbar = findViewById(R.id.toolbar);
    setToolBar(mToolbar,getTitleInfo());
    vs_seeall.setVisibility(View.VISIBLE);
    iv_seeall.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(null != mManager){
          mManager.zoomToSpan();
        }
      }
    });
    if(null != mRecyclerView){
      mRecyclerView.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
      mMarkerInfoRecyclerAdapter = new MarkerInfoRecyclerAdapter<>();
      mMarkerInfoRecyclerAdapter.setILoadImage(this);
      mMarkerInfoRecyclerAdapter.setIOperationListen(this);
      mRecyclerView.setAdapter(mMarkerInfoRecyclerAdapter);
      mRecyclerView.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
          super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override public void onPageSelected(int position) {
          super.onPageSelected(position);
          onViewPageSelected(position);
        }

        @Override public void onPageScrollStateChanged(int state) {
          super.onPageScrollStateChanged(state);
        }
      });

    }

    if(null != viewpager){
      mInfoPagerAdapter = new MarkderInfoPagerAdapter(this,new ArrayList<IMarkerModel>());

      mInfoPagerAdapter.setILoadImage(this);
      mInfoPagerAdapter.setIOperationListen(this);
      viewpager.setAdapter(mInfoPagerAdapter);
    }
    getAMap().setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
      @Override public void onMapLoaded() {
        onRefreshMarkders();
      }
    });

    getAMap().setOnMapClickListener(new AMap.OnMapClickListener() {
      @Override public void onMapClick(LatLng latLng) {
        onAMapClick(latLng);
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


  private    IMarkerModel  getMarkerDataByAdapter(int postion){
    if(null != mRecyclerView){
      return  mMarkerInfoRecyclerAdapter.getItem(postion);
    }
    if(null != viewpager){
      return  mInfoPagerAdapter.getIMarkerModel(postion);
    }
    return  null;
  }
  private   void   onViewPageSelected(int position){
    IMarkerModel  testMarkerModel = getMarkerDataByAdapter(position);
    Marker tempMarker = mManager.getMarker(testMarkerModel);
    if(null !=tempMarker) {
      if(null != mCurrentMarker ){
         if(null != lastClickMarkerIcon && lastClickMarkerIcon.size()>0){
           mCurrentMarker.setIcon(lastClickMarkerIcon.get(0));
         }else{
            Integer  index = getMarkerIndex(mCurrentMarker);
            if(null != index){
              IMarkerModel  temp = mManager.getIMarkerModels().get(index);
              mCurrentMarker.setIcon(getMarkerBitmapDescriptor(temp));
            }

         }

      }
      mCurrentMarker = tempMarker;
      lastClickMarkerIcon = mCurrentMarker.getIcons();
      mCurrentMarker.setIcon(readBitmap);
      getAMap().moveCamera(CameraUpdateFactory.changeLatLng(tempMarker.getPosition()));

    }

  }

  public   Integer   getMarkerIndex(Marker  marker){
    try {
      String  jsonStr = marker.getSnippet();
      if(!TextUtils.isEmpty(jsonStr)){
        JSONObject  jsonObject = new JSONObject(jsonStr);
        if(!jsonObject.isNull("index")){
          return  jsonObject.getInt("index");
        }

      }
    }catch (Exception e){
      e.printStackTrace();
    }
    return  null;
  }


  protected   void    onAMapClick(LatLng latLng){}

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


  private Location lastbdBDLocation;

  @Override
  protected void onLocation( Location bdLocation) {
    System.out.println("onlocation-->");
    //当前要显示路径 且不用内置的顺序
    if(showPathPlan() && !displayInOrder()){

      if(null == lastbdBDLocation || AMapUtils.calculateLineDistance(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()),new LatLng(lastbdBDLocation.getLatitude(),lastbdBDLocation.getLongitude()))>getMinDisFlushPath()){
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

  private Polyline polylineMarker = null;
  private Marker personMarder;

  private    boolean  firstLoadPath = true;
  protected    void   loadMarkerPath(List<IMarkerModel>   iMarkerModels, LatLng currentLatlng){
    if(isMapLoaded.get() && !isloadingToMarkders.get()){
      try {
        filterMakerToPath(temps);
        isMapLoaded.getAndSet(false);
        List<IMarkerModel> iMarkerModels1  = new ArrayList<>();
        Iterator<IMarkerModel> iterator = iMarkerModels.iterator();
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
           CoordinateConverter  coordinateConverter = new CoordinateConverter();
           coordinateConverter.coord(new com.baidu.mapapi.model.LatLng(currentLatlng.latitude,currentLatlng.longitude));
           coordinateConverter.from(CoordinateConverter.CoordType.COMMON);
           iMarkerModels1 = PathPlanUtil.getOptimalPathPlan(iMarkerModels1,coordinateConverter.convert());
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

          LatLng latLng =null;
          while(iterator2.hasNext()){
           LatLng temp = iterator2.next();
            if(null != latLng){
              if(AMapUtils.calculateLineDistance(latLng,temp)<2){
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
          polylineOptionBg.setDottedLine(true);
          //  polylineOptionBg.color(0xAAFF0000); // 折线颜色
          // 折线坐标点列表:[2,10000]，且不能包含null
          polylineOptionBg.addAll(points);
          // 纹理宽、高是否保持原比例渲染
          polylineOptionBg.setUseTexture(true);

          polylineOptionBg.setCustomTextureIndex(indexs);
          polylineOptionBg.setCustomTextureList(customList);


          polylineMarker = (Polyline) getAMap().addPolyline(polylineOptionBg);

          //绘制终点与启点
          List<LatLng> pointStartEnd = new ArrayList<>(2);
          pointStartEnd.add(points.get(null != startSort && startSort<points.size()?startSort:0));
          pointStartEnd.add(points.get(points.size() - 1));

          MarkerOptions persontempMarker = new MarkerOptions()
              .position(pointStartEnd.get(0)).icon(personDescriptor).draggable(false);
          personMarder =  getAMap().addMarker(persontempMarker);

          JSONObject  jsonObject = new JSONObject();
          jsonObject.put(AbstractBMapMarkersActivity.OTHER_MARKER_KEY,"yes");
          jsonObject.put("index",0);
          MarkerOptions startmarker = new MarkerOptions()
              .position(pointStartEnd.get(0)).icon(startBitmap).snippet(jsonObject.toString()).draggable(false);

          jsonObject.put("index",1);
          MarkerOptions endmarker = new MarkerOptions()
              .position(pointStartEnd.get(1)).icon(endBitmap).snippet(jsonObject.toString()).draggable(false);
          if(null == overlayManager){
            overlayManager = new MyManager(getAMap(),new LinkedList<MarkerOptions>());
          }


          overlayManager.cleanOverlay();
          overlayManager.addOverlay(startmarker);
          overlayManager.addOverlay(endmarker);
          overlayManager.addToMap();
          mManager.setNewPathDatas(iMarkerModels1);
          getAMap().moveCamera(CameraUpdateFactory.changeLatLng(points.get(0)));
          if(firstLoadPath){

            showListPathMarker(iMarkerModels1,null,0);

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
        List<MarkerOptions>   overlayOptions = new LinkedList<>();
        int   index = 0;
        for (IMarkerModel iMarkerModel : temps) {
          LatLng point = new LatLng(iMarkerModel.getLat(), iMarkerModel.getLng());
          JSONObject  jsonObject = new JSONObject();
          jsonObject.put("index",index);
         MarkerOptions option = new MarkerOptions()
              .position(point)
              .draggable(isDragMarker(iMarkerModel))
              .icon(getMarkerBitmapDescriptor(iMarkerModel))
              .snippet(jsonObject.toString());
          overlayOptions.add(option);
          index++;

        }
        if(null == mOtherManager){
          mOtherManager = new OtherManager(getAMap());
          getAMap().setOnMarkerClickListener(mOtherManager);
        }
        addOtherOverlay(mOtherManager);
        if(null == mManager){
          mManager = new MyManager(getAMap(), overlayOptions);

          getAMap().setOnMarkerClickListener(mManager);
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

  /**
   * 添加其它遮盖物
   */
  protected    void     addOtherOverlay(OtherManager overlayManager){

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
      mPopupBottonWindow.setBackgroundDrawable(new BitmapDrawable((Resources) null,(Bitmap) null));
      mPopupBottonWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
          if (null != mCurrentMarker) {
            if(null != lastClickMarkerIcon&& lastClickMarkerIcon.size()>0){
              mCurrentMarker.setIcon(lastClickMarkerIcon.get(0));
            }else{
              Integer index = getMarkerIndex(mCurrentMarker);
              if(null != index && index>=0 && index<mManager.getIMarkerModels().size()){
                IMarkerModel  iMarkerModel1 = mManager.getIMarkerModels().get(index);
                mCurrentMarker.setIcon(getMarkerBitmapDescriptor(iMarkerModel1));
              }
            }
          }

        }
      });
    }
  }



  Handler mainHandle = new Handler(Looper.getMainLooper());
  /**
   * 显示当前路径所有Marker
   * @param iMarkerModels
   */
  protected   void   showListPathMarker(final List<IMarkerModel>  iMarkerModels ,final Marker  marker, final int   postion){
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
        if(null != viewpager){
          mInfoPagerAdapter = new MarkderInfoPagerAdapter(AbstractAMapMarkersActivity.this,showImarkers);
          mInfoPagerAdapter.setIOperationListen(AbstractAMapMarkersActivity.this);
          mInfoPagerAdapter.setPathMarker(showPathPlan());
          mInfoPagerAdapter.setILoadImage(AbstractAMapMarkersActivity.this);
          viewpager.removeOnPageChangeListener(mOnPageChangeListener);
          viewpager.addOnPageChangeListener(mOnPageChangeListener);
          viewpager.setAdapter(mInfoPagerAdapter);
          viewpager.startAnimation(mShowAction);
          viewpager.setCurrentItem(postion);
        }

        if(null != mRecyclerView){
          mMarkerInfoRecyclerAdapter.setPathMarker(showPathPlan());
          mMarkerInfoRecyclerAdapter.setNewData(iMarkerModels);
          mRecyclerView.setCurrentItem(postion);
          mRecyclerView.startAnimation(mShowAction);
        }

        if(showPathPlan()){
          mPopupBottonWindow.setOutsideTouchable(false);
          mPopupBottonWindow.setFocusable(false);
        }else{
          mPopupBottonWindow.setOutsideTouchable(true);
          mPopupBottonWindow.setFocusable(true);
        }

        if(null == marker){
          mCurrentMarker = mManager.getMarker(iMarkerModels.get(postion));
        } else{
          mCurrentMarker = marker;
        }
        System.out.println("on marker");
        if(null != mCurrentMarker){
          System.out.println("改变图标->>>>");
          lastClickMarkerIcon = mCurrentMarker.getIcons();
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






  private ViewPager.OnPageChangeListener  mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override public void onPageSelected(int position) {

      onViewPageSelected(position);
    }

    @Override public void onPageScrollStateChanged(int state) {

    }
  };



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
  protected abstract BitmapDescriptor getMarkerBitmapDescriptor(@NonNull IMarkerModel iMarkerModel);

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
   * @param
   */
  protected abstract   void     filterAddMakerToMap(@NonNull List<IMarkerModel>  iMarkerModels);

  /**
   * 过滤路径
   * 过滤当前的markers
   * @param iMarkerModels
   */
  protected abstract   void     filterMakerToPath(@NonNull List<IMarkerModel>  iMarkerModels);

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
    Intent intent  = new Intent(this, MapPathPlanActivity.class);
    intent.putExtra("lat",iMarkerModel.getLat());
    intent.putExtra("lng",iMarkerModel.getLng());
    startActivity(intent);
  }

  /**
   * 其它marker点击事件
   * @param marker
   * @return
   */
  protected   boolean   onClickOtherMarker(Marker marker,Integer  integer){
    return   false;
  }


  @Override public void onBackPressed() {
    if(null != mPopupBottonWindow && mPopupBottonWindow.isShowing()){
      mPopupBottonWindow.dismiss();
      return;
    }
    super.onBackPressed();
  }


  public class  MyManager extends OverlayManager {
    private  List<IMarkerModel>  mIMarkerModels = new LinkedList<>();

    private  List<IMarkerModel>  pathIMarkerModels = new LinkedList<>();

    private    int   containsMarkerInPath(IMarkerModel   iMarkerModel){

      return   pathIMarkerModels.indexOf(iMarkerModel);
    }

    private    int   containsMarkerInAll(IMarkerModel   iMarkerModel){

      return   mIMarkerModels.indexOf(iMarkerModel);
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

    public Marker getMarker(IMarkerModel  iMarkerModel){
      for (int i = 0; i < mIMarkerModels.size(); i++) {
        if(mIMarkerModels.get(i) == iMarkerModel){
          return getMarker(i);
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

    private List<MarkerOptions> overlays = new ArrayList<>();
    public MyManager(AMap baiduMap,List<MarkerOptions>  overlays) {
      super(baiduMap);
      this.overlays = overlays;
    }
    public synchronized void addOverlay(MarkerOptions option) {
      overlays.add(option);
    }
    public synchronized void addOverlays(List<MarkerOptions> options) {
      overlays.addAll(options);
    }
    public void removeOverlay(MarkerOptions option) {
      overlays.remove(option);
    }

    public void removeAll() {
      overlays.clear();
    }
    @Override public List<MarkerOptions> getOverlayOptions() {
      return overlays;
    }

    @Override public boolean onMarkerClick(Marker marker) {

       try {
         if(null != marker){
           String  jsonStr = marker.getSnippet();
           JSONObject  jsonObject = new JSONObject(jsonStr);
           if(jsonObject.isNull(AbstractBMapMarkersActivity.OTHER_MARKER_KEY)){
              showMarkerInfo(marker);
           }else{
             onClickOtherMarker(marker,jsonObject.getInt("index"));
           }
         }


       }catch (Exception e){
         e.printStackTrace();
       }


      return true;
    }

    @Override public void onPolylineClick(Polyline polyline) {

    }
  }


  public    class    OtherManager extends OverlayManager {

    private List<MarkerOptions> overlays = new ArrayList<>();


    public OtherManager(AMap baiduMap) {
      super(baiduMap);
    }
    public synchronized void addOverlay(MarkerOptions option) {
      overlays.add(option);
    }
    public synchronized void addOverlays(List<MarkerOptions> options) {
      overlays.addAll(options);
    }
    public void removeOverlay(MarkerOptions option) {
      overlays.remove(option);

    }

    public void removeAll() {
      overlays.clear();
    }
    @Override public List<MarkerOptions> getOverlayOptions() {
      return overlays;
    }

    @Override public boolean onMarkerClick(Marker marker) {
      Integer    index = getMarkerIndex(marker);


      return onClickOtherMarker(marker,index);
    }

    @Override public void onPolylineClick(Polyline polyline) {

    }
  }
  public   void   hiddenPopWindow(){
    if(null != mPopupBottonWindow && mPopupBottonWindow.isShowing()){
      mPopupBottonWindow.dismiss();
    }

  }


  protected   void   showMarkerInfo(Marker marker){

    try {
      initPopupWindow();
      if(null == marker || TextUtils.isEmpty(marker.getSnippet())){
        return;
      }
      JSONObject   jsonObject = new JSONObject(marker.getSnippet());
      if(jsonObject.isNull("index")){
        return;
      }
      if(null != mPopupBottonWindow && mPopupBottonWindow.isShowing()){
        mPopupBottonWindow.dismiss();
      }
      int  index = jsonObject.getInt("index");
      System.out.println("showMarker="+index);
      IMarkerModel   iMarkerModel = mManager.getIMarkerModels().get(index);
      getAMap().moveCamera(CameraUpdateFactory.changeLatLng(marker.getPosition()));
      int   postion = mManager.containsMarkerInPath(iMarkerModel);
      if(postion>=0){
        System.out.println("showPath->>");
        showListPathMarker(mManager.getPathIMarkerModels(),marker,postion);
        return;
      }
      postion = mManager.containsMarkerInAll(iMarkerModel);
      if(postion>=0){
        System.out.println("showList->>");
        showListPathMarker(mManager.getIMarkerModels(),marker,postion);
        return;
      }
      List<IMarkerModel>  iMarkerModels = new LinkedList<>();
      iMarkerModels.add(iMarkerModel);
      if(null != iMarkerModel.getMarkes()){
        iMarkerModels.addAll(iMarkerModel.getMarkes());
      }
      if(null != viewpager){
        mInfoPagerAdapter = new MarkderInfoPagerAdapter(this,iMarkerModels);
        mInfoPagerAdapter.setIOperationListen(this);
        mInfoPagerAdapter.setILoadImage(this);
        viewpager.setAdapter(mInfoPagerAdapter);
        viewpager.startAnimation(mShowAction);
        viewpager.setCurrentItem(0);
      }
      if(null != mRecyclerView){
        mRecyclerView.startAnimation(mShowAction);
        mMarkerInfoRecyclerAdapter.setNewData(iMarkerModels);
        mRecyclerView.setCurrentItem(0);
      }

      lastClickMarkerIcon = marker.getIcons();
      mCurrentMarker = marker;
      mCurrentMarker.setIcon(readBitmap);
      if (mPopupBottonWindow.isShowing()) {
        mPopupBottonWindow.dismiss();
      } else {
        mPopupBottonWindow.showAtLocation(closeButton, Gravity.BOTTOM, 0, 0);
      }
    }catch (Exception | Error e){
      e.printStackTrace();
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

}
