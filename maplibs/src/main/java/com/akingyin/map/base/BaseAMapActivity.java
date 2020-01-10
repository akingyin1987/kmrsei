package com.akingyin.map.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.akingyin.map.R;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.baidu.mapapi.utils.CoordinateConverter;

/**
 * 基于高德地图
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/12/31 15:03
 */
public abstract class BaseAMapActivity extends AppCompatActivity {

  private MapView mMapView;
  private AMap mAMap;

  /** 地图模式（正常，跟随，罗盘） */
  protected ImageView location_icon;

  private ImageButton zoom_in, zoom_out;
  /** 交通 */
  private ImageButton road_condition;

  /** 地图类型（普通2d,普通3d,卫星） */
  private ImageButton map_layers;

  /** 全景 */
  protected ImageButton map_street;


  protected ViewSwitcher vs_seeall;
  protected ImageView iv_seeall;

  /** 显示当前位置 */
  protected ViewSwitcher vs_showloc;
  protected ImageView iv_showloc;
  private   int   mCurrentMode = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;

  /** 地图类型 */
  private View maplayer;
  protected MapLoadingDialog mLoadingDialog;

  protected ProgressBar location_progress;




  public MapView getmMapView() {
    return mMapView;
  }

  public AMap getAMap() {
    return mAMap;
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    View view = onCreateView(LayoutInflater.from(this));
    setContentView(view);
    initView(savedInstanceState);

    initLoc();
    initialization();
  }


  public void initView(Bundle savedInstanceState) {
    mMapView =  findViewById(R.id.map_content);
    mMapView.onCreate(savedInstanceState);
    mAMap = mMapView.getMap();
    vs_showloc =  findViewById(R.id.vs_showloc);
    iv_showloc =  findViewById(R.id.iv_showloc);
    iv_seeall =  findViewById(R.id.iv_seeall);
    vs_seeall =  findViewById(R.id.vs_seeall);
    location_icon =  findViewById(R.id.location_icon);
    location_progress =  findViewById(R.id.location_progress);
    zoom_out = findViewById(R.id.zoom_out);
    zoom_in =  findViewById(R.id.zoom_in);
    road_condition =  findViewById(R.id.road_condition);
    map_layers =  findViewById(R.id.map_layers);
    map_street = findViewById(R.id.map_street);
    iv_showloc.setTag("0");
    iv_showloc.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (mAMap.isMyLocationEnabled()) {
           hideViewInfo();
        } else {
           showViewInfo();
        }
      }
    });
    baseInitialization(savedInstanceState);

  }



  protected void showViewInfo() {
    iv_showloc.setImageResource(R.drawable.ic_visibility_black_24dp);
    iv_showloc.setTag("0");
    if (null != getAMap()) {
      getAMap().setMyLocationEnabled(true);
      MyLocationStyle  myLocationStyle = new MyLocationStyle();
      myLocationStyle.myLocationType(mCurrentMode);
      myLocationStyle.interval(2000);
      mAMap.setMyLocationStyle(myLocationStyle);
    }
  }

  protected void hideViewInfo() {
    if (null != getAMap()) {
      getAMap().setMyLocationEnabled(false);
    }
    iv_showloc.setImageResource(R.drawable.ic_visibility_off_black_24dp);
    iv_showloc.setTag("1");
  }


  private  MyLocationStyle  myLocationStyle;
  protected boolean isFirstLoc = true;
  private  void  initLoc(){

    if(null == myLocationStyle){
      myLocationStyle =  new MyLocationStyle();
    }
    myLocationStyle.interval(2000);

    myLocationStyle.myLocationType(mCurrentMode);
    mAMap.setMyLocationStyle(myLocationStyle);
    mAMap.setMyLocationEnabled(true);

    mAMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
      @Override public void onMyLocationChange(Location location) {

        // map view 销毁后不在处理新接收的位置
        if (location == null || mMapView == null) {
          return;
        }

        System.out.println("onMyLocationChange->"+location.toString());
        initMyLocationData(location);
        if (isFirstLoc) {
          isFirstLoc = false;
          onFristLocationInitMap(location);

        }
        onLocation(location);
      }
    });
  }
  /**
   * 第一次定位初始化地图
   * @param location
   */
  public    void    onFristLocationInitMap(Location location){
    System.out.println("第一次定位初始化");
     mAMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
  }
  protected void initMyLocationData(Location  location) {

  }

  public void showLoadDialog() {
    runOnUiThread(new Runnable() {
      @Override public void run() {
        if (null == mLoadingDialog) {
          mLoadingDialog = new MapLoadingDialog(BaseAMapActivity.this);
        }

        if (!mLoadingDialog.isShowing()) {
          mLoadingDialog.show();
        }
        mLoadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
          @Override public void onCancel(DialogInterface dialog) {
            onCancelLocation();
          }
        });
      }
    });
  }

  /**
   * 取消定位
   */
  public   void   onCancelLocation(){

  }

  public void hidLoadialog() {
    runOnUiThread(new Runnable() {
      @Override public void run() {
        if (null != mLoadingDialog && mLoadingDialog.isShowing()) {
          mLoadingDialog.dismiss();
        }
      }
    });
  }


  private void baseInitialization(Bundle bundle) {

    mAMap.getUiSettings().setZoomControlsEnabled(false);
    mAMap.setMapType(AMap.MAP_TYPE_NORMAL);

    //开始交通地图
    mAMap.setTrafficEnabled(false);
    // 开启定位图层
    mAMap.setMyLocationEnabled(true);

    if (null != bundle) {
      double lat = bundle.getDouble("lat", 0);
      double lng = bundle.getDouble("lng", 0);
      LatLng   latLng = null;
      if (lat > 0 && lng > 0) {
        System.out.println("默认中心位置-->");
        latLng = new LatLng(lat, lng);
        mAMap.moveCamera( CameraUpdateFactory.newLatLngZoom(latLng,15F));
      }
    }

    location_icon.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        switch (mCurrentMode) {
          case MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER:
            mCurrentMode = MyLocationStyle.LOCATION_TYPE_FOLLOW;
            location_icon.setImageResource(R.drawable.main_icon_follow);
            break;

          case MyLocationStyle.LOCATION_TYPE_FOLLOW:
            mCurrentMode = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
            location_icon.setImageResource(R.drawable.main_icon_location);
            break;
          default:
            break;
        }
         myLocationStyle.myLocationType(mCurrentMode);
         mAMap.setMyLocationStyle(myLocationStyle);
      }
    });

    road_condition.setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(View v) {
        if (mAMap.isTrafficEnabled()) {
          road_condition.setImageResource(R.drawable.main_icon_roadcondition_off);
          mAMap.setTrafficEnabled(false);
        } else {
          road_condition.setImageResource(R.drawable.main_icon_roadcondition_on);
          mAMap.setTrafficEnabled(true);
        }
      }
    });
    map_layers.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        showMapLayerDialog(v, 10, -5);
      }
    });

    maplayer = LayoutInflater.from(this).inflate(R.layout.map_layer, null);
    layer_selector =  maplayer.findViewById(R.id.layer_selector);
    layer_satellite =  maplayer.findViewById(R.id.layer_satellite);
    layer_2d =  maplayer.findViewById(R.id.layer_2d);
    layer_3d =  maplayer.findViewById(R.id.layer_3d);
    layer_2d.setChecked(true);
    layer_selector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.layer_satellite) {
          //卫星
          mAMap.setMapType(AMap.MAP_TYPE_SATELLITE);
        } else if (checkedId == R.id.layer_2d) {
          mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
          mAMap.moveCamera(CameraUpdateFactory.changeTilt(0));
        } else if (checkedId == R.id.layer_3d) {
          mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
           mAMap.moveCamera(CameraUpdateFactory.changeTilt(-45F));

        }
      }
    });
    zoom_in.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        float supportmax = mAMap.getMaxZoomLevel();
        float localzoom = mAMap.getCameraPosition().zoom;

        if (localzoom == supportmax) {
          zoom_in.setEnabled(false);
          Toast.makeText(BaseAMapActivity.this, "已到支持最大级别", Toast.LENGTH_SHORT).show();
          return;
        }
        if (!zoom_out.isEnabled()) {
          zoom_out.setEnabled(true);
        }
        mAMap.moveCamera(CameraUpdateFactory.zoomBy(localzoom + 0.5f));

      }
    });
    zoom_out.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        float supportmin = mAMap.getMinZoomLevel();
        float localzoom = mAMap.getCameraPosition().zoom;
        if (localzoom <= supportmin) {
          zoom_out.setEnabled(false);
          Toast.makeText(BaseAMapActivity.this, "已到支持最小级别", Toast.LENGTH_SHORT).show();
          return;
        }
        if (!zoom_in.isEnabled()) {
          zoom_in.setEnabled(true);
        }
        mAMap.moveCamera(CameraUpdateFactory.zoomBy(localzoom-0.5F));
      }
    });

    map_street.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        goToMapStreet();
      }
    });
  }



  private PopupWindow mPopupWindow;
  private RadioGroup layer_selector;
  private RadioButton layer_satellite, layer_2d, layer_3d;

  public void showMapLayerDialog(View v, int xoff, int yoff) {

    if (mPopupWindow == null) {
      mPopupWindow = new PopupWindow(maplayer, RadioGroup.LayoutParams.WRAP_CONTENT,
          RadioGroup.LayoutParams.WRAP_CONTENT, true);

      mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),(Bitmap) null));
    }
    if (mPopupWindow.isShowing()) {
      mPopupWindow.setAnimationStyle(R.anim.layer_pop_out);

      mPopupWindow.dismiss();
    } else {
      mPopupWindow.setAnimationStyle(R.anim.layer_pop_in);
      mPopupWindow.showAsDropDown(v, xoff, yoff);
    }
  }

  public void showToast(final String msg) {
    runOnUiThread(new Runnable() {
      @Override public void run() {
        Toast.makeText(BaseAMapActivity.this, msg, Toast.LENGTH_SHORT).show();
      }
    });
  }

  public   void   goToMapStreet(){
    LatLng  latLng =  mAMap.getCameraPosition().target;
    if (null == latLng) {
      showToast("当前没有位置信息无法查看");
      return;
    }
    CoordinateConverter  coordinateConverter = new CoordinateConverter();
    com.baidu.mapapi.model.LatLng  latLng1 = coordinateConverter.coord(new com.baidu.mapapi.model.LatLng(latLng.latitude,latLng.longitude))
        .from(CoordinateConverter.CoordType.BD09LL).convert();
    Intent intent = new Intent(BaseAMapActivity.this, BaiduPanoramaActivity.class);
    intent.putExtra("lat", latLng1.latitude);
    intent.putExtra("lng", latLng1.longitude);
    startActivity(intent);
  }


  /**
   * 初如化VIEW
   */
  public abstract View onCreateView(@NonNull LayoutInflater inflater);


  /** 初始化 */
  public abstract void initialization();


  /**
   * 返回百度定位数据
   */
  protected abstract void onLocation(Location bdLocation);


  @Override public void onPause() {
    super.onPause();
    if (null != mMapView) {
      mMapView.onPause();
    }

  }

  @Override public void onResume() {
    super.onResume();
    if (null != mMapView) {
      mMapView.onResume();
    }

  }


  @Override protected void onDestroy() {
    if(null != mMapView){
      mMapView.onDestroy();
    }
    super.onDestroy();
  }
}
