package com.akingyin.bmap;

import android.graphics.drawable.Drawable;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/12/24 18:24
 */
public interface  ClusterRender {

  /**
   * 根据聚合点的元素数目返回渲染背景样式
   *
   * @param clusterNum
   * @return
   */
  Drawable getDrawAble(int clusterNum);
}
