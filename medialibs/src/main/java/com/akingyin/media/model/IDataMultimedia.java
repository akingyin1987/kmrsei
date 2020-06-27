/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.media.model;

import android.graphics.Rect;
import androidx.annotation.Nullable;

/**

 *
 * @ Description:      图文接口                                    #
 * @author king
 * @ Date 2016/10/31 15:45
 * @ Version V1.0
 */

public abstract class IDataMultimedia {

  /**
   * 通过数据获取多媒体类型
   */
  public abstract MultimediaEnum getMultimediaEnum();

  /**
   * 本地文件路径
   */
  @Nullable
  public abstract String getLocalPath();

  /**
   * 文件的顺序
   */
  public abstract int getSort();

  /**
   * 服务器地址
   */
  @Nullable
  public abstract String getServerPath();

  /**
   * 本地原始文件地址
   */
  @Nullable
  public abstract String getLocalOriginalPath();

  //文本描述
  @Nullable
  public abstract String getTextDes();

  //设置值
  public abstract void setData(MultimediaEnum multimediaEnum, String textdesc, String localPath,
      String originalPath, int sort);

  /**
   * 是否允许获取网络图片
   */
  private   boolean   netToWeb;

  public boolean isNetToWeb() {
    return netToWeb;
  }

  public void setNetToWeb(boolean netToWeb) {
    this.netToWeb = netToWeb;
  }

  /**
   * 获取网络地址
   */
  private   String baseUrl;

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  /**
   * 获取当前视图状态
   */
  private  ViewDataStatusEnum viewStatus;

  /**
   * 当前选中状态
   */
   private  boolean   selected;

  public ViewDataStatusEnum getViewStatus() {
    return viewStatus;
  }

  public void setViewStatus(ViewDataStatusEnum viewStatus) {
    this.viewStatus = viewStatus;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  private Rect   mRect;

  public Rect getBounds() {
    return mRect;
  }

  public void setRect(Rect rect) {
    mRect = rect;
  }
}
