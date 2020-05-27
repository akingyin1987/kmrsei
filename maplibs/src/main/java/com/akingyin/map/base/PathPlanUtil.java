/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.map.base;

import com.akingyin.map.model.IMarkerModel;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 路径规划工具
 *
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2017/11/27 15:54
 */

public class PathPlanUtil {

  public static final int MIN_MARKER_COUNT = 3;

  public static final int MIN_CALCULATION_COUNT = 5;

  public  static   final  int  MAX_MARKER_COUNT=50;

  /**
   * 通过当前位置计算最优路径
   */
  public static List<IMarkerModel> getOptimalPathPlan(List<IMarkerModel> iMarkerModels,
      LatLng currentLatlnt) {

    try {
      SimpleDateFormat  simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      System.out.println("开始："+simpleDateFormat.format(new Date()));
      Iterator<IMarkerModel> iterable = iMarkerModels.iterator();
      List<IMarkerModel>  tempcompleteMarkers = new ArrayList<>();
      while (iterable.hasNext()){
        IMarkerModel   planModel = iterable.next();
        //去掉已完成的
        if(planModel.isComplete()){
          iterable.remove();
          tempcompleteMarkers.add(planModel);
        }
      }
      List<PathPlanModel> pathPlanModelList = getCalculatDistance(iMarkerModels, currentLatlnt);

      if (iMarkerModels.size() <= MIN_MARKER_COUNT) {
        List<IMarkerModel> iMarkerModelList = new LinkedList<>();

        for (PathPlanModel planModel : pathPlanModelList) {
          iMarkerModelList.add(planModel.getIMarkerModel());
        }
        iMarkerModelList.addAll(0,tempcompleteMarkers);
        return iMarkerModelList;
      }
      if(pathPlanModelList.size() >MAX_MARKER_COUNT){
        for (int i = pathPlanModelList.size() - 1; i >= MAX_MARKER_COUNT; i--) {
          pathPlanModelList.remove(i);
        }
      }
      //待计算的点
      List<PathPlanModel> pathPlanModels = new LinkedList<>();
      //已算好的点
      List<PathPlanModel> sortPathPlanModels = new LinkedList<>();
      pathPlanModels.addAll(pathPlanModelList);
      sortPathPlanModels.add(pathPlanModelList.get(0));
      pathPlanModels.remove(0);
      //以最近的一个为启点

      while (pathPlanModels.size() > 0) {
        //每次以计算好的最后一个点为起点
        PathPlanModel fristpPlanModel  = sortPathPlanModels.get(sortPathPlanModels.size()-1);


        PathPlanModel  nextPlanModel = getLatelyDistance(fristpPlanModel,pathPlanModels);
        System.out.println("null =="+(null == nextPlanModel));
        if(null == nextPlanModel){
          break;
        }
        sortPathPlanModels.add(nextPlanModel);
        pathPlanModels.remove(nextPlanModel);

        ////第一次确定启点
        //List<PathPlanModel>   pathPlanModels1 = getOptimumNext(fristpPlanModel, pathPlanModels);
        //
        //if (pathPlanModels1.size() > 0) {
        //  pathPlanModels1.remove(0);
        //}
        //if (pathPlanModels1.size() == 0) {
        //  System.out.println("break");
        //  break;
        //}
        //if (pathPlanModels1.size() > 0) {
        //  sortPathPlanModels.addAll(pathPlanModels1);
        //  pathPlanModels.removeAll(pathPlanModels1);
        //}

      }
      List<IMarkerModel>  iMarkerModelList = new LinkedList<>();
      for (PathPlanModel sortPathPlanModel : sortPathPlanModels) {
        if(null != sortPathPlanModel.getIMarkerModel()){
          iMarkerModelList.add(sortPathPlanModel.getIMarkerModel());
        }
      }
      //iMarkerModelList.addAll(0,tempcompleteMarkers);
      System.out.println("结束："+simpleDateFormat.format(new Date()));
      return iMarkerModelList;
    }catch (Exception e){
      e.printStackTrace();
    }
   return  iMarkerModels;
  }

  /**
   * 计算当前点与所有marker距离并按重近到远排序
   */
  public static List<PathPlanModel> getCalculatDistance(List<IMarkerModel> iMarkerModels,
      LatLng latLng) {
    List<PathPlanModel> pathPlanModels = new LinkedList<>();
    for (IMarkerModel iMarkerModel : iMarkerModels) {
      float d = (float) DistanceUtil.getDistance(latLng,
          new LatLng(iMarkerModel.getLat(), iMarkerModel.getLng()));
      pathPlanModels.add(new PathPlanModel(iMarkerModel, d));
    }
    Collections.sort(pathPlanModels, new Comparator<PathPlanModel>() {
      @Override public int compare(PathPlanModel o1, PathPlanModel o2) {
        if (o1.getDistance() < o2.getDistance()) {
          return -1;
        } else if (o1.getDistance() > o2.getDistance()) {
          return 1;
        }
        return 0;
      }
    });
    return pathPlanModels;
  }

  public static List<PathPlanModel> calculatDistance(List<PathPlanModel> iMarkerModels,
      LatLng latLng) {
    List<PathPlanModel> pathPlanModels = new LinkedList<>();
    for (PathPlanModel iMarkerModel : iMarkerModels) {
      iMarkerModel.setDistance((float) DistanceUtil.getDistance(latLng, iMarkerModel.getLatlng()));
      pathPlanModels.add(iMarkerModel);
    }
    Collections.sort(pathPlanModels, new Comparator<PathPlanModel>() {
      @Override public int compare(PathPlanModel o1, PathPlanModel o2) {
        if (o1.getDistance() < o2.getDistance()) {
          return -1;
        } else if (o1.getDistance() > o2.getDistance()) {
          return 1;
        }
        return 0;
      }
    });
    return pathPlanModels;
  }

  /**
   * 获取最近距离点
   */
  public static PathPlanModel getLatelyDistance(PathPlanModel pathPlanModel,
      List<PathPlanModel> pathPlanModelList) {
    float dis = 0;
    PathPlanModel planModel = null;
    LatLng latLng = pathPlanModel.getLatlng();

    for (PathPlanModel model : pathPlanModelList) {
      if (pathPlanModel != model) {
        float d = (float) DistanceUtil.getDistance(latLng, model.getLatlng());
        if (dis == 0) {
          dis = d;
          planModel = model;
        }
        if (d < dis) {
          dis = d;
          planModel = model;
        }
      }else{
        System.out.println("---=====---");
      }
    }
    if(null == planModel){
      return null;
    }
   // planModel.setAccumulateDis(dis + planModel.getAccumulateDis());
     planModel.setAccumulateDis(dis);
    return planModel;
  }

  /**
   * 通过当前点获取最短距离5个点
   * @param pathPlanModel
   * @param pathPlanModelList
   * @return
   */
  public static List<PathPlanModel> getOptimumNext(PathPlanModel pathPlanModel,
      List<PathPlanModel> pathPlanModelList) {

    //排序
    List<PathPlanModel> disPathPlanModels = calculatDistance(pathPlanModelList, pathPlanModel.getLatlng());
    disPathPlanModels.remove(pathPlanModel);

    List<PathPlanModel> sortPathPlanModels = new LinkedList<>();
    float dis = 0L;
    float baseDis = 0L;
    for (int i = 0; i < (Math.min(disPathPlanModels.size(), MIN_CALCULATION_COUNT)); i++) {
      List<PathPlanModel>  temp = new LinkedList<>(disPathPlanModels);

      temp.remove(i);
      baseDis = (float) DistanceUtil.getDistance(pathPlanModel.getLatlng(),disPathPlanModels.get(i).getLatlng());
      getMinimumDistance(pathPlanModel, disPathPlanModels.get(i), temp, 0);
      if (dis == 0) {
        dis = pathPlanModel.getAccumulateDis()+baseDis;
        sortPathPlanModels.clear();
        sortPathPlanModels.addAll(pathPlanModel.getPlanModels());
      } else {
        if (dis > pathPlanModel.getAccumulateDis()+baseDis) {
          dis = pathPlanModel.getAccumulateDis();
          sortPathPlanModels.clear();
          sortPathPlanModels.addAll(pathPlanModel.getPlanModels());
        }
      }
    }

    sortPathPlanModels.add(0,pathPlanModel);

    return sortPathPlanModels;
  }

  /**
   * 最取最近
   * @param fristPlanModel 父级坐标点存放计算数据
   * @param pathPlanModel   基准坐标点
   * @param pathPlanModelList  待计算坐标点
   * @param number
   */
  public static void getMinimumDistance(PathPlanModel fristPlanModel, PathPlanModel pathPlanModel,
      List<PathPlanModel> pathPlanModelList, int number) {
    float dis = 0L;
    if (number == 0) {
      fristPlanModel.setAccumulateDis(0);
      if (null == fristPlanModel.getPlanModels()) {
        fristPlanModel.setPlanModels(new LinkedList<PathPlanModel>());
      }
      fristPlanModel.getPlanModels().clear();
      fristPlanModel.getPlanModels().add(pathPlanModel);
    }


    List<PathPlanModel> pathPlanModels = null;
    if (fristPlanModel.getAccumulateDis() == 0) {
      pathPlanModels = new LinkedList<>(pathPlanModelList);
    } else {
      pathPlanModels = pathPlanModelList;
    }

    PathPlanModel planModel = getLatelyDistance(pathPlanModel, pathPlanModelList);
    if(null == planModel){
      return;
    }

    fristPlanModel.getPlanModels().add(planModel);
    pathPlanModels.remove(planModel);
    number++;
    fristPlanModel.setAccumulateDis(
        fristPlanModel.getAccumulateDis() + planModel.getAccumulateDis());

    if (number < MIN_CALCULATION_COUNT && pathPlanModels.size() > 0) {
      getMinimumDistance(fristPlanModel, planModel, pathPlanModels, number);
    }

  }

  static class PathPlanModel {

    private IMarkerModel mIMarkerModel;

    private float distance;

    /** 累积距离 */
    private float accumulateDis;

    private int sort;

    private LatLng mLatLng;

    private List<PathPlanModel> mPlanModels;

    public List<PathPlanModel> getPlanModels() {
      return mPlanModels;
    }

    public void setPlanModels(List<PathPlanModel> planModels) {
      mPlanModels = planModels;
    }

    public LatLng getLatLng() {
      return mLatLng;
    }

    public void setLatLng(LatLng latLng) {
      mLatLng = latLng;
    }

    public int getSort() {
      return sort;
    }

    public void setSort(int sort) {
      this.sort = sort;
    }

    public LatLng getLatlng() {
      if (null == mLatLng) {
        mLatLng = new LatLng(mIMarkerModel.getLat(), mIMarkerModel.getLng());
      }

      return mLatLng;
    }

    public float getAccumulateDis() {
      return accumulateDis;
    }

    public void setAccumulateDis(float accumulateDis) {
      this.accumulateDis = accumulateDis;
    }

    public PathPlanModel(IMarkerModel IMarkerModel, float distance) {
      mIMarkerModel = IMarkerModel;
      this.distance = distance;
    }

    public PathPlanModel(LatLng latLng) {
      mLatLng = latLng;
    }

    public IMarkerModel getIMarkerModel() {
      return mIMarkerModel;
    }

    public void setIMarkerModel(IMarkerModel IMarkerModel) {
      mIMarkerModel = IMarkerModel;
    }

    public float getDistance() {
      return distance;
    }

    public void setDistance(float distance) {
      this.distance = distance;
    }
  }
}
