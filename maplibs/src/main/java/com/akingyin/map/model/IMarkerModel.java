/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.map.model;

import java.util.List;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2017/11/25 11:30
 */

public interface IMarkerModel  {

  /**
   * 当前地图使用 经度
   * @return
   */
  double   getLat();

  /**
   * 当前地图使用 纬度
   * @return
   */
  double   getLng();


  /**
   * 指定顺序
   * @return
   */
  int   getAppointSort();

  /**
   * 设置顺序
   * @param sort
   */
  void  setSort(int sort);



  /**
   * 定位对象基础图片路径
   * @return
   */
  String   getMarkerDetaiImgPath();

  /**
   * 定位对像基础信息
   * @return
   */
  String   getBaseInfo();

  /**
   * marker 在地图上显示的说明
   * @return
   */
  String   getTitle();

  /**
   * 定位对象
   * @return
   */
  Object    getData();

  /**
   * 同一个经纬度或聚合定位对象
   * @return
   */
  List<IMarkerModel>   getMarkes();

  /**
   * 顺序信息
   * @return
   */
  String   getSortInfo();

  /**
   * 设置顺序信息
   */
  void    setSortInfo(String sortInfo);

  /**
   * 是否已完成
   * @return
   */
  boolean    isComplete();
}
