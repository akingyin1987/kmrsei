package com.baidu.mapapi.overlayutil;

import android.os.Bundle;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnPolylineClickListener;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import java.util.ArrayList;
import java.util.List;

import static com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;

/**
 * 该类提供一个能够显示和管理多个Overlay的基类
 * <p>
 * 复写{@link #getOverlayOptions()} 设置欲显示和管理的Overlay列表
 * </p>
 * <p>
 * 通过
 * {@link com.baidu.mapapi.map.BaiduMap#setOnMarkerClickListener(com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener)}
 * 将覆盖物点击事件传递给OverlayManager后，OverlayManager才能响应点击事件。
 * <p>
 * 复写{@link #onMarkerClick(com.baidu.mapapi.map.Marker)} 处理Marker点击事件
 * </p>
 */
public abstract class OverlayManager implements OnMarkerClickListener, OnPolylineClickListener {

    BaiduMap mBaiduMap = null;
    private List<OverlayOptions> mOverlayOptionList = null;

    public List<Overlay> mOverlayList = null;

    /**
     * 通过一个BaiduMap 对象构造
     * 
     * @param baiduMap
     */
    public OverlayManager(BaiduMap baiduMap) {
        mBaiduMap = baiduMap;

        if (mOverlayOptionList == null) {
            mOverlayOptionList = new ArrayList<OverlayOptions>();
        }
        if (mOverlayList == null) {
            mOverlayList = new ArrayList<Overlay>();
        }
    }

    /**
     * 覆写此方法设置要管理的Overlay列表
     * 
     * @return 管理的Overlay列表
     */
    public abstract List<OverlayOptions> getOverlayOptions();


    public    Marker   getMarker(int  index){
        for (Overlay overlay : mOverlayList) {
            Bundle  bundle =  overlay.getExtraInfo();
            if(overlay instanceof  Marker && null != bundle && bundle.containsKey("index")){
                if(index == bundle.getInt("index")){
                    return (Marker) overlay;
                }
            }
        }
        return  null;
    }

    public   Overlay  getOverLay(int  index){
        for (Overlay overlay : mOverlayList) {
         Bundle  bundle =  overlay.getExtraInfo();
         if(null != bundle && bundle.containsKey("index")){
             if(index == bundle.getInt("index")){
                 return  overlay;
             }
         }
        }
        return  null;
    }

    public   final   Marker   addToMap(MarkerOptions overlayOptions){
        if (mBaiduMap == null) {
            return null;
        }

       return (Marker) mBaiduMap.addOverlay(overlayOptions);
    }

    /**
     * 将所有Overlay 添加到地图上
     */
    public final void addToMap() {
        if (mBaiduMap == null) {
            return;
        }

        removeFromMap();
        List<OverlayOptions> overlayOptions = getOverlayOptions();
        if (overlayOptions != null) {
            mOverlayOptionList.addAll(overlayOptions);
           // mOverlayOptionList.addAll(getOverlayOptions());
        }

        for (OverlayOptions option : mOverlayOptionList) {
            mOverlayList.add(mBaiduMap.addOverlay(option));
        }
    }

    /**
     * 将所有Overlay 从 地图上消除
     */
    public final void removeFromMap() {
        if (mBaiduMap == null) {
            return;
        }
        for (Overlay marker : mOverlayList) {
            marker.remove();
        }
        mOverlayOptionList.clear();
        mOverlayList.clear();

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
            for (Overlay overlay : mOverlayList) {
                // polyline 中的点可能太多，只按marker 缩放
                if (overlay instanceof Marker) {
                    builder.include(((Marker) overlay).getPosition());
                }

            }
            mBaiduMap.setMapStatus(MapStatusUpdateFactory
                    .newLatLngBounds(builder.build()));
        }
    }


    public void zoomToSpanAll() {
        if (mBaiduMap == null) {
            return;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (mOverlayList.size() > 0) {
            for (Overlay overlay : mOverlayList) {
                // polyline 中的点可能太多，只按marker 缩放
                if (overlay instanceof Marker) {
                    builder.include(((Marker) overlay).getPosition());
                }

            }

        }
        if(null != mOverlayOptionList && mOverlayOptionList.size()>0){
            for (OverlayOptions overlayOptions : mOverlayOptionList) {
                if(overlayOptions instanceof PolygonOptions){
                   List<LatLng>  latLngs = ((PolygonOptions) overlayOptions).getPoints();
                   if(null != latLngs && latLngs.size()>0){
                       for (LatLng latLng : latLngs) {
                           builder.include(latLng);
                       }
                   }
                }
            }
        }
        LatLngBounds  latLngBounds = builder.build();
        if(null != latLngBounds.northeast && null != latLngBounds.southwest){
            if(latLngBounds.northeast.latitude!=0 && latLngBounds.northeast.longitude!=0){
                mBaiduMap.setMapStatus(MapStatusUpdateFactory
                    .newLatLngBounds(builder.build()));
            }
        }

    }

}
