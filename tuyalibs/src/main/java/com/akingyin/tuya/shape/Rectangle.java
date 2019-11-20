package com.akingyin.tuya.shape;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG              #
 * #
 *
 * @ Description:                                          #
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/12/28 16:37
 * @ Version V1.0
 */

public class Rectangle extends Shape{

  @Override public void draw(Canvas canvas, Paint paint) {
    if (end.x == 0 && end.y == 0) {
      return;
    }
    int color = paint.getColor();
    paint.setStyle(Paint.Style.STROKE);
    paint.setColor(Color.RED);
    canvas.drawRect(start.x, start.y, end.x, end.y, paint);
    paint.setColor(color);
  }

  @Override public void calculate() {

  }

  public void setStart(int x, int y) {
    start.x = x;
    start.y = y;
  }

  public void setEnd(int x, int y) {
    end.x = x;
    end.y = y;
    calculate();
  }

}
