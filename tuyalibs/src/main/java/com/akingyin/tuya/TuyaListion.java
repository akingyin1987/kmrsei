package com.akingyin.tuya;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2018/4/18 14:47
 */
public interface TuyaListion {

  /**
   * 画之前涂鸦数量
   * @param length
   */
     void   onBefore(int length);

     void   onAfter(int length);

     //删除某一项
     void   onDelect(int postion);

     void   onDelayInvalidate(long delay);
}
