/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.baidu.mapapi.clusterutil.clustering.algo;

import com.baidu.mapapi.clusterutil.clustering.Cluster;
import com.baidu.mapapi.clusterutil.clustering.ClusterItem;
import com.baidu.mapapi.map.MapStatus;
import java.util.Collection;
import java.util.Set;

/**
 * 基于屏幕算法适本器
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2020/6/23 10:43
 */
public class ScreenBasedAlgorithmAdapter <T extends ClusterItem> implements
    ScreenBasedAlgorithm<T> {


  private Algorithm<T> mAlgorithm;

  public ScreenBasedAlgorithmAdapter(Algorithm<T> algorithm) {
    mAlgorithm = algorithm;
  }

  @Override public boolean shouldReclusterOnMapMovement() {
    return false;
  }

  @Override public void onMapStatusChange(MapStatus mapStatus) {
      // 未实现
  }

  @Override public void addItem(T item) {
    mAlgorithm.addItem(item);
  }

  @Override public void addItems(Collection<T> items) {
        mAlgorithm.addItems(items);
  }

  @Override public void clearItems() {
       mAlgorithm.clearItems();
  }

  @Override public void removeItem(T item) {
       mAlgorithm.removeItem(item);
  }

  @Override public Set<? extends Cluster<T>> getClusters(double zoom) {
    return mAlgorithm.getClusters(zoom);
  }

  @Override public Collection<T> getItems() {
    return mAlgorithm.getItems();
  }

  @Override public void setMaxDistanceBetweenClusteredItems(int maxDistance) {
      mAlgorithm.setMaxDistanceBetweenClusteredItems(maxDistance);
  }

  @Override public int getMaxDistanceBetweenClusteredItems() {
    return mAlgorithm.getMaxDistanceBetweenClusteredItems();
  }
}
