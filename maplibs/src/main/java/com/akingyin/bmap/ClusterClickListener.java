package com.akingyin.bmap;

import com.akingyin.map.model.IMarkerModel;
import com.baidu.mapapi.map.Marker;
import java.util.List;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/12/24 18:23
 */
public interface ClusterClickListener {

  /**
   * 点击聚合点的回调处理函数
   *
   * @param marker
   *            点击的聚合点
   * @param clusterItems
   *            聚合点所包含的元素
   */
  public void onClick(Marker marker, List<IMarkerModel> clusterItems);
}
