package com.amap.overlayutil;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2020/1/6 10:58
 */
public  abstract class AMapCacheOverlyManager  extends  OverlayManager {

  public AMapCacheOverlyManager(AMap baiduMap) {
    super(baiduMap);
  }
  private List<MarkerOptions> mOverlayOptions = new ArrayList<>();



  public void setOverlayOptions(List<MarkerOptions> overlayOptions) {
    mOverlayOptions = overlayOptions;
  }

  @Override public List<MarkerOptions> getOverlayOptions() {
    return mOverlayOptions;
  }
}
