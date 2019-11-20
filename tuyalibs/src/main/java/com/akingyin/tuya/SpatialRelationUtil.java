package com.akingyin.tuya;

import com.akingyin.tuya.shape.Pt;
import java.util.List;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2018/7/23 11:23
 */
public class SpatialRelationUtil {


  public   static   boolean  isPolygonContainsPoint(Pt point, Pt... mPoints){
    if(null == point || mPoints.length==0){
      return  false;
    }
    for (Pt mPoint : mPoints) {
      if(mPoint.x == point.x && mPoint.y == point.y){
        return  true;
      }
    }
    int nCross = 0;
    for (int i = 0; i < mPoints.length; i++) {
      Pt p1 = mPoints[i];
      Pt p2 = mPoints[(i + 1) % mPoints.length];
      // 取多边形任意一个边,做点point的水平延长线,求解与当前边的交点个数
      // p1p2是水平线段,要么没有交点,要么有无限个交点
      if (p1.y == p2.y){
        continue;
      }
      // point 在p1p2 底部 --> 无交点
      if (point.y < Math.min(p1.y, p2.y)){
        continue;
      }

      // point 在p1p2 顶部 --> 无交点
      if (point.y >= Math.max(p1.y, p2.y)){
        continue;
      }

      // 求解 point点水平线与当前p1p2边的交点的 X 坐标
      double x = (point.y - p1.y) * (p2.x - p1.x) / (p2.y - p1.y) + p1.x;

      // 当x=point.x时,说明point在p1p2线段上
      if(x == point.x){
        return  true;
      }
      if (x > point.x){
        nCross++; // 只统计单边交点
      }

    }
    // 单边交点为偶数，点在多边形之外 ---
    return (nCross % 2 == 1);
  }

  /**
   * 返回一个点是否在一个多边形区域内
   *
   * @param mPoints 多边形坐标点列表
   * @param point   待判断点
   * @return true 多边形包含这个点,false 多边形未包含这个点。
   */
  public static boolean isPolygonContainsPoint(List<Pt> mPoints, Pt point) {
    if(null == point || mPoints.size()==0){
      return  false;
    }
    for (Pt mPoint : mPoints) {
      if(mPoint.x == point.x && mPoint.y == point.y){
        return  true;
      }
    }
    int nCross = 0;
    for (int i = 0; i < mPoints.size(); i++) {
      Pt p1 = mPoints.get(i);
      Pt p2 = mPoints.get((i + 1) % mPoints.size());
      // 取多边形任意一个边,做点point的水平延长线,求解与当前边的交点个数
      // p1p2是水平线段,要么没有交点,要么有无限个交点
      if (p1.y == p2.y){
        continue;
      }
      // point 在p1p2 底部 --> 无交点
      if (point.y < Math.min(p1.y, p2.y)){
        continue;
      }

      // point 在p1p2 顶部 --> 无交点
      if (point.y >= Math.max(p1.y, p2.y)){
        continue;
      }

      // 求解 point点水平线与当前p1p2边的交点的 X 坐标
      double x = (point.y - p1.y) * (p2.x - p1.x) / (p2.y - p1.y) + p1.x;

      // 当x=point.x时,说明point在p1p2线段上
      if(x == point.x){
        return  true;
      }
      if (x > point.x){
        nCross++; // 只统计单边交点
      }

    }
    // 单边交点为偶数，点在多边形之外 ---
    return (nCross % 2 == 1);
  }



}
