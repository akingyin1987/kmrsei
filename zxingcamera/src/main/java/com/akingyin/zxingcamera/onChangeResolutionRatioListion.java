package com.akingyin.zxingcamera;

import android.graphics.Point;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/4/11 14:47
 */
public interface onChangeResolutionRatioListion {

  void    onSuccess(Point point);

  void    onFail(String msg, Point point);

}
