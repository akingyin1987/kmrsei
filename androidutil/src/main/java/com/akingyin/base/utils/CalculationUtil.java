/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.utils;

import android.graphics.Point;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2020/7/16 16:32
 */
public class CalculationUtil {

  /**
   * 最大公约数：递归法
   * @param m
   * @param n
   * @return
   */
  public static int maxCommonDivisor(int m, int n) {
    if (m < n) {
      int temp = m;
      m = n;
      n = temp;
    }
    if (m % n == 0) {
      return n;
    }
    return maxCommonDivisor(n, m % n);
  }

  /**
   * 最大公约数：循环法求
   * @param m
   * @param n
   * @return
   */
  public static int maxCommonDivisor2(int m, int n) {
    if (m < n) {
      int temp = m;
      m = n;
      n = temp;
    }
    while (m % n != 0) {
      int temp = m % n;
      m = n;
      n = temp;
    }
    return n;
  }

  /**
   * 最小公倍数
   * @param m
   * @param n
   * @return
   */
  public static int minCommonMultiple(int m, int n) {
    return m * n / maxCommonDivisor(m, n);
  }

  public static int gcd(int a, int b) {
    while (b != 0) {
      int c = b;
      b = a % b;
      a = c;
    }
    return a;
  }


  public static double getPointsDistance(Point p1, Point p2) {
    return getPointsDistance(p1.x, p1.y, p2.x, p2.y);
  }

  public static double getPointsDistance(float x1, float y1, float x2, float y2) {
    return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
  }

  public static void main(String[] args) {
    int  value =  CalculationUtil.gcd(128,52);
    System.out.println("value="+value);
  }

}
