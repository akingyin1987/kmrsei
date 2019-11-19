/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.map.base;

import android.view.View;
import android.widget.TextView;
import com.akingyin.map.model.IMarkerModel;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2017/11/25 12:58
 */

public interface IOperationListen {

  /**
   * 初始化操作名称
   * @param left
   * @param center
   * @param right
   */
  void   initView(TextView left, TextView center, TextView right, int postion,
      IMarkerModel iMarkerModel, View... views);

  /**
   * 点击操作
   * @param postion  当前位置
   * @param iMarkerModel  当前对象
   */
  void   onOperation(int postion, IMarkerModel iMarkerModel);

  /**
   * 路径规划
   * @param postion
   * @param iMarkerModel
   */
  void   onPathPlan(int postion, IMarkerModel iMarkerModel);

  /**
   * 图文信息
   * @param postion
   * @param iMarkerModel
   */
  void   onTuWen(int postion, IMarkerModel iMarkerModel);

  /**
   * 点击详情图片
   * @param postion
   * @param iMarkerModel
   */
   void   onObjectImg(int postion, IMarkerModel iMarkerModel, View view);

  /**
   * 其它操作
   * @param postion
   * @param iMarkerModel
   * @param view
   */
   void   onOtherOperation(int postion, IMarkerModel iMarkerModel, View view);
}
