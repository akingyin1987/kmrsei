package com.akingyin.bmap;

import android.graphics.Point;
import com.akingyin.map.model.IMarkerModel;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import java.util.ArrayList;
import java.util.List;

/**
 * 聚合点
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/12/24 18:21
 */
public class Cluster {

  private LatLng mLatLng;
  private List<IMarkerModel> mClusterItems;
  private Marker mMarker;
  private Point mPoint;


  Cluster( LatLng latLng) {

    mLatLng = latLng;
    mClusterItems = new ArrayList<>();
  }

  public Cluster(Point point,LatLng latLng) {
    mLatLng = latLng;
    mPoint = point;
    mClusterItems = new ArrayList<>();
  }

  void addClusterItem(IMarkerModel clusterItem) {
    mClusterItems.add(clusterItem);
  }

  int getClusterCount() {
    return mClusterItems.size();
  }



  LatLng getCenterLatLng() {
    return mLatLng;
  }

  Point  getCenterPoint(){
    return  mPoint;
  }

  void setMarker(Marker marker) {
    mMarker = marker;
  }

  Marker getMarker() {
    return mMarker;
  }

  List<IMarkerModel> getClusterItems() {
    return mClusterItems;
  }
}
