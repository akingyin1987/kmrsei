package com.akingyin.bmap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import androidx.collection.LruCache;
import com.akingyin.map.R;
import com.akingyin.map.base.AbstractMapMarkersActivity;
import com.akingyin.map.model.IMarkerModel;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 用于展示聚合的overlay
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/12/24 18:25
 */
public class ClusterOverlay implements BaiduMap.OnMapStatusChangeListener,
    BaiduMap.OnMarkerClickListener{
  private List<IMarkerModel> mPoints;
  private BaiduMap bdMap;
  private List<Cluster> mClusters;
  private int mClusterSize;
  private Projection mProjection;
  private Context mContext;
  private ExecutorService executor;
  private float level = 0;
  private ClusterClickListener mClusterClickListener;
  private ClusterRender mClusterRender;
  private LatLngBounds bounds;
  private LatLng centerLatLng;

  private LruCache<Integer, BitmapDescriptor> mLruCache;
  private   boolean   seeAll;

  public boolean isSeeAll() {
    return seeAll;
  }

  public void setSeeAll(boolean seeAll) {
    this.seeAll = seeAll;
  }

  public   int      getTotal(){
    return mPoints.size();
  }

  public   Cluster    getCluster(int  postion){
    if(postion >=0 && postion<mClusters.size()){
      return mClusters.get(postion);
    }
    return  null;
  }

  private AbstractMapMarkersActivity.MyManager manager;
  private Handler handler = new Handler() {
    public void handleMessage(Message message) {

      System.out.println("加载到MAP");
      addClusterToMap();

    }
  };

  /**
   * @param bdMap
   * @param clusterSize 聚合范围的大小
   * @param context
   */
  public ClusterOverlay(BaiduMap bdMap, AbstractMapMarkersActivity.MyManager manager, int clusterSize, Context context) {
    this(bdMap, null, manager, clusterSize, context);

  }

  /**
   * @param bdMap
   * @param clusterItems 聚合元素
   * @param clusterSize
   * @param context
   */
  public ClusterOverlay(BaiduMap bdMap, List<IMarkerModel> clusterItems, AbstractMapMarkersActivity.MyManager manager,
      int clusterSize, Context context) {
    mLruCache = new LruCache<Integer, BitmapDescriptor>(80) {
      @Override
      protected void entryRemoved(boolean evicted, Integer key, BitmapDescriptor oldValue, BitmapDescriptor newValue) {
        oldValue.getBitmap().recycle();
      }
    };
    if (clusterItems != null) {
      mPoints = new ArrayList<>();
      mPoints.addAll(clusterItems);
    } else {
      mPoints = new ArrayList<>();
    }
    this.manager = manager;
    mContext = context;
    mClusters = new ArrayList<>();
    this.bdMap = bdMap;

    this.bdMap.setOnMapStatusChangeListener(this);
    this.bdMap.setOnMarkerClickListener(this);
    mProjection = this.bdMap.getProjection();
    mClusterSize = clusterSize;
    executor = Executors.newFixedThreadPool(2);
    bounds = bdMap.getMapStatus().bound;

    assignClusters();

  }

  /**
   * 设置聚合点的点击事件
   *
   * @param clusterClickListener
   */
  public void setOnClusterClickListener(
      ClusterClickListener clusterClickListener) {
    mClusterClickListener = clusterClickListener;
  }



  /**
   * 添加一个聚合点
   *
   * @param item
   */
  public void addClusterItem(IMarkerModel item) {
    mPoints.add(item);
    assignSingleCluster(item);

  }

  public  void  addAllClusterItems(List<IMarkerModel>  list){
    mPoints.clear();
    tempPoints.clear();

    mPoints.addAll(list);
    System.out.println("共==" + mPoints.size());

  }

  /**
   * 设置聚合元素的渲染样式，不设置则默认为气泡加数字形式进行渲染
   *
   * @param render
   */
  public void setClusterRenderer(ClusterRender render) {
    mClusterRender = render;
  }

  /**
   * 将聚合元素添加至地图上
   */
  private synchronized void addClusterToMap() {
    executor.submit(new Runnable() {
      @Override
      public void run() {
        manager.removeAll();
        manager.removeFromMap();
        int index = 0;
        for (Cluster cluster : mClusters) {
          addSingleClusterToMap(cluster, index);
          index++;
        }

        manager.addToMap();
        if(isSeeAll()){
          manager.zoomToSpan();
        }

      }
    });


    //manager.zoomToSpan();
  }

  /**
   * 将单个聚合元素添加至地图显示
   *
   * @param cluster
   */
  private void addSingleClusterToMap(Cluster cluster,int index) {
    LatLng latlng = cluster.getCenterLatLng();
    //        TextOptions   textOptions = new TextOptions();
    //       textOptions.position(latlng);
    //        textOptions.fontSize(50);
    //        textOptions.bgColor( Color.argb(159, 210, 154, 6));
    //        textOptions.text(String.valueOf(cluster.getClusterCount()));

    MarkerOptions markerOptions = new MarkerOptions();

    markerOptions.anchor(0.5f, 0.5f)
        .icon(getBitmapDes(cluster.getClusterCount())).position(latlng);
    Bundle bundle = new Bundle();
    bundle.putInt("index",index);
    markerOptions.extraInfo(bundle);
    manager.addOverlay(markerOptions);

    cluster.setOverlayOptions(markerOptions);
    //cluster.setMarker(marker);
  }

  /**
   * 获取每个聚合点的绘制样式
   */


  /**
   * 获取每个聚合点的绘制样式
   */
  private BitmapDescriptor getBitmapDes(int num) {
    BitmapDescriptor bitmapDescriptor = mLruCache.get(num);
    if (bitmapDescriptor == null) {
      TextView textView = new TextView(mContext);
      if (num > 1) {
        String tile = String.valueOf(num);
        textView.setText(tile);
      }
      textView.setGravity(Gravity.CENTER);
      textView.setTextColor(Color.BLACK);
      textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
      if (mClusterRender != null && mClusterRender.getDrawAble(num) != null) {
        textView.setBackgroundDrawable(mClusterRender.getDrawAble(num));
      } else {
        textView.setBackgroundResource(R.drawable.defaultcluster);
      }
      bitmapDescriptor = BitmapDescriptorFactory.fromView(textView);
      mLruCache.put(num, bitmapDescriptor);

    }
    return bitmapDescriptor;
  }
  /**
   * 更新已加入地图聚合点的样式
   */
  private void updateCluster(Cluster cluster) {


  }

  /**
   * 对点进行聚合
   */
  List<IMarkerModel>   tempPoints = new ArrayList<>();
  public void assignClusters() {
    tempPoints.clear();

    for (Cluster cluster : mClusters) {
      cluster.onDestory();
    }
    mClusters.clear();
    executor.submit(new Runnable() {
      @Override
      public void run() {
        try {
          if(!seeAll){
            for (ClusterItem clusterItem : mPoints) {
              LatLng latlng = clusterItem.getPosition();

              if (bounds.contains(latlng)) {
                tempPoints.add(clusterItem);
              }
            }
          }else{
            tempPoints.addAll(mPoints);
          }

          int  size = tempPoints.size();
          for(ClusterItem  clusterItem : tempPoints){

            LatLng latlng = clusterItem.getPosition();
            Point point = mProjection.toScreenLocation(latlng);
            if(size >300 ||level < 17){

              Cluster cluster = getCluster(point);
              if (cluster != null) {
                cluster.addClusterItem(clusterItem);
              } else {
                cluster = new Cluster(point, latlng);
                mClusters.add(cluster);
                cluster.addClusterItem(clusterItem);
              }
            }else{
              Cluster  cluster = new Cluster(point,latlng);
              cluster.addClusterItem(clusterItem);
              mClusters.add(cluster);
            }
          }
          handler.sendEmptyMessage(0);
        } catch (Exception e) {
          e.printStackTrace();
        }


      }
    });

  }

  /**
   * 在已有的聚合基础上，对添加的单个元素进行聚合
   *
   * @param clusterItem
   */
  private void assignSingleCluster(IMarkerModel clusterItem) {
    LatLng latlng = new LatLng(clusterItem.getLat(),clusterItem.getLng());
    Point point = mProjection.toScreenLocation(latlng);
    Cluster cluster = getCluster(point);
    if (cluster != null) {
      cluster.addClusterItem(clusterItem);
      updateCluster(cluster);
    } else {
      cluster = new Cluster(point, latlng);
      mClusters.add(cluster);
      cluster.addClusterItem(clusterItem);
      addSingleClusterToMap(cluster,mClusters.size()-1);
    }
  }

  /**
   * 根据一个点获取是否可以依附的聚合点，没有则返回null
   *
   * @param point
   * @return
   */
  private Cluster getCluster(Point point) {
    for (Cluster cluster : mClusters) {
      Point poi = cluster.getCenterPoint();
      double distance = getDistanceBetweenTwoPoints(point.x, point.y,
          poi.x, poi.y);
      if (distance < mClusterSize) {
        return cluster;
      }
    }
    return null;
  }

  /**
   * 两点的距离
   *
   * @param x1
   * @param y1
   * @param x2
   * @param y2
   * @return
   */
  private double getDistanceBetweenTwoPoints(double x1, double y1, double x2,
      double y2) {
    double distance = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2)
        * (y1 - y2));
    return distance;
  }

  public double getDistanceBetweenTwoPoints(LatLng l1, LatLng l2) {
    return DistanceUtil.getDistance(l1, l2);
  }


  //点击事件
  @Override
  public boolean onMarkerClick(Marker arg0) {
    if (mClusterClickListener == null) {
      return false;
    }
    for (Cluster cluster : mClusters) {
      if (arg0.equals(cluster.getMarker())) {
        mClusterClickListener.onClick(arg0, cluster.getClusterItems());
        return false;
      }
    }
    return false;
  }

  @Override
  public void onMapStatusChangeStart(MapStatus mapStatus) {

  }

  @Override
  public void onMapStatusChange(MapStatus mapStatus) {

  }

  @Override
  public void onMapStatusChangeFinish(MapStatus mapStatus) {
    //放大缩小完成后对聚合点进行重新计算
    float leveltemp = mapStatus.zoom;
    if (leveltemp != level) {
      bounds = mapStatus.bound;
      if(!seeAll){
        assignClusters();
      }
      level = leveltemp;
      centerLatLng = mapStatus.bound.getCenter();
    } else {
      bounds = mapStatus.bound;
      //当缩放等级不变的时候，看中心点是否发生变化
      if (null == centerLatLng) {
        if(!seeAll){
          assignClusters();
        }
      } else if (centerLatLng.latitude != bounds.getCenter().latitude ||
          centerLatLng.longitude != bounds.getCenter().longitude) {
        if(!seeAll){
          assignClusters();
        }
      }
      centerLatLng = bounds.getCenter();
    }
    setSeeAll(false);
  }



}
