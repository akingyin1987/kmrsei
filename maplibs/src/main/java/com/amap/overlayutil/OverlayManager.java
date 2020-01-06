package com.amap.overlayutil;

import android.text.TextUtils;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/**
 * 基于地图点的管理
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/12/31 17:15
 */
public abstract class OverlayManager implements AMap.OnMarkerClickListener,
    AMap.OnPolylineClickListener  {


  AMap mBaiduMap = null;
  private List<MarkerOptions> mOverlayOptionList = null;

  List<Marker> mOverlayList = null;

  /**
   * 通过一个BaiduMap 对象构造
   *
   * @param baiduMap
   */
  public OverlayManager(AMap  baiduMap) {

    mBaiduMap = baiduMap;
    if (mOverlayOptionList == null) {
      mOverlayOptionList =  new ArrayList<>();
    }
    if (mOverlayList == null) {
      mOverlayList = new ArrayList<>();
    }
  }

  /**
   * 覆写此方法设置要管理的Overlay列表
   *
   * @return 管理的Overlay列表
   */
  public abstract List<MarkerOptions> getOverlayOptions();

  /**
   * 将所有Overlay 添加到地图上
   */
  public final void addToMap() {
    if (mBaiduMap == null) {
      return;
    }

    removeFromMap();
    List<MarkerOptions> overlayOptions = getOverlayOptions();
    if (overlayOptions != null) {

      mOverlayOptionList.addAll(overlayOptions);

    }

    for (MarkerOptions option : mOverlayOptionList) {

      mOverlayList.add(mBaiduMap.addMarker(option));
    }
  }

  /**
   * 将所有Overlay 从 地图上消除
   */
  public final   void removeFromMap() {
    if (mBaiduMap == null) {
      return;
    }
    for (Marker marker : mOverlayList) {
      marker.remove();
      marker.destroy();
    }
    mOverlayOptionList.clear();
    mOverlayList.clear();

  }


  public Marker getMarker(int  index){

    for (Marker overlay : mOverlayList) {
       String  jsonStr =  overlay.getSnippet();
       if(!TextUtils.isEmpty(jsonStr)){
         try {
           JSONObject  jsonObject =  new JSONObject(jsonStr);
           if(index == jsonObject.getInt("index")){
             return  overlay;
           }

         }catch (Exception e){
           e.printStackTrace();
         }

       }
    }
    return  null;
  }



  /**
   * 缩放地图，使所有Overlay都在合适的视野内
   * <p>
   * 注： 该方法只对Marker类型的overlay有效
   * </p>
   *
   */
  public void zoomToSpan() {
    if (mBaiduMap == null) {
      return;
    }
    if (mOverlayList.size() > 0) {

      LatLngBounds.Builder builder = new LatLngBounds.Builder();
      for (Marker overlay : mOverlayList) {
        // polyline 中的点可能太多，只按marker 缩放
        builder.include(overlay.getPosition());
      }

      mBaiduMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),0));

    }
  }

}
