/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import com.akingyin.map.base.AbstractMapMarkersActivity;
import com.akingyin.map.model.IMarkerModel;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import java.util.LinkedList;
import java.util.List;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2017/11/25 14:45
 */

public class TestMarkerMapActivity  extends AbstractMapMarkersActivity {

  @Override public void onOperation(int postion, IMarkerModel iMarkerModel) {

  }

  @Override public void onTuWen(int postion, IMarkerModel iMarkerModel) {

  }

  @Override public void onObjectImg(int postion, IMarkerModel iMarkerModel, View view) {

  }

  @Override public void loadImageView(String path, Context context, ImageView imageView) {

  }

  @Override protected List<IMarkerModel> onLoadMarkerDatas() {
    List<IMarkerModel>  iMarkerModels = new LinkedList<>();
    for(int i=0;i<10;i++){
      double[]  latlngs = TestUtil.Latlng();
      TestModel  testModel = new TestModel(latlngs[0],latlngs[1],i,"test"+i);
      iMarkerModels.add(testModel);
      if(i%2==0){
        List<IMarkerModel> iMarkerModels1 = new LinkedList<>();
        for(int k=0;k<5;k++){
          iMarkerModels1.add(new TestModel(latlngs[0],latlngs[1],k,"sstest"+i*k));
        }
        testModel.setIMarkerModels(iMarkerModels1);
      }
    }
    return iMarkerModels;
  }

  protected BitmapDescriptor test = BitmapDescriptorFactory.fromResource(R.drawable.marker_cluster_50);
  @Override protected BitmapDescriptor getMarkerBitmapDescriptor(IMarkerModel iMarkerModel) {
    return test;
  }

  @Override protected void filterAddMakerToMap(List<IMarkerModel> iMarkerModels) {

  }

  @Override protected void filterMakerToPath(List<IMarkerModel> iMarkerModels) {

  }

  @Override protected boolean displayInOrder() {
    return false;
  }

  @Override protected boolean showPathPlan() {
    return true;
  }

  @Override protected int getMinDisFlushPath() {
    return 50;
  }

  @Override public View onCreateView(LayoutInflater inflater) {
    return inflater.inflate(R.layout.activity_test_map_show_markers,null);
  }

  @Override protected BitmapDescriptor getLocationBitmap() {
    return null;
  }

  @Override protected String getTitleInfo() {
    return "测试";
  }

  public   class   TestModel implements IMarkerModel{
    @Override public int getAppointSort() {
      return sort;
    }

    @Override public void setSort(int sort) {

    }

    private   double  lat,lng;

    private   int    sort;

    private   String   info;

    private   boolean   completed;

    private   List<IMarkerModel>   mIMarkerModels;

    public void setIMarkerModels(List<IMarkerModel> IMarkerModels) {
      mIMarkerModels = IMarkerModels;
    }

    public TestModel(double lat, double lng, String info) {
      this.lat = lat;
      this.lng = lng;
      this.info = info;
    }

    public TestModel(double lat, double lng, int sort, String info) {
      this.lat = lat;
      this.lng = lng;
      this.sort = sort;
      this.info = info;
      if(sort<5){
        completed = true;
      }
    }

    @Override public double getLat() {
      return lat;
    }

    @Override public double getLng() {
      return lng;
    }

    @Override public String getMarkerDetaiImgPath() {
      return null;
    }

    @Override public String getBaseInfo() {
      return info;
    }

    @Override public Object getData() {
      return this;
    }

    @Override public List<IMarkerModel> getMarkes() {
      return mIMarkerModels;
    }

    @Override public boolean isComplete() {

      return false;
    }

    @Override public String getSortInfo() {
      return null;
    }

    @Override public void setSortInfo(String sortInfo) {

    }

    @Override public String getTitle() {
      return "test";
    }
  }


}
