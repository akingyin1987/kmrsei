package com.akingyin.map;

import com.baidu.mapapi.model.LatLng;
import java.util.Collection;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2018/3/2 17:30
 */

public class MapUtil {


  public   static LatLng    getLatlngAverage(Collection<LatLng>   latLngs){
    double   lat=0.0;
    double   lng =0.0;
    for (LatLng latLng : latLngs) {
      if(lat == 0 || lng == 0){
         lat = latLng.latitude;
         lng = latLng.longitude;
      }else{
        lat = (lat+latLng.latitude)/2.0;
        lng = (lng+latLng.longitude)/2.0;
      }
    }
    return  new LatLng(lat,lng);
  }

  public   static LatLng    getLatlngCentent(Collection<LatLng>   latLngs){
    double   minlat=0.0,maxlat=0.0;
    double   minlng =0.0,maxlng=0.0;
    for (LatLng latLng : latLngs) {
      if(minlat == 0 || minlng == 0){
        minlat = latLng.latitude;
        minlng = latLng.longitude;
        maxlat = latLng.latitude;
        maxlng = latLng.longitude;
      }else{
        if(minlat>latLng.latitude){
          minlat = latLng.latitude;
        }
        if(minlng>latLng.longitude){
          minlng = latLng.longitude;
        }
        if(maxlat<latLng.latitude){
          maxlat = latLng.latitude;
        }
        if(maxlng<latLng.longitude){
          maxlng = latLng.longitude;
        }
      }
    }
    return  new LatLng((minlat+maxlat)/2.0,(minlng+maxlng)/2.0);
  }
}
