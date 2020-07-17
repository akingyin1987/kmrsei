/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.utils;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2020/7/16 16:32
 */
public class CalculationUtil {

  // 最大公约数：递归法
  public static int MaxCommonDivisor(int m, int n) {
    if (m < n) {
      int temp = m;
      m = n;
      n = temp;
    }
    if (m % n == 0) {
      return n;
    }
    return MaxCommonDivisor(n, m % n);
  }

  // 最大公约数：循环法求
  public static int MaxCommonDivisor2(int m, int n) {
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

  // 最小公倍数
  public static int MinCommonMultiple(int m, int n) {
    return m * n / MaxCommonDivisor(m, n);
  }

  public static int gcd(int a, int b) {
    while (b != 0) {
      int c = b;
      b = a % b;
      a = c;
    }
    return a;
  }

  public static void main(String[] args) {
    int  value =  CalculationUtil.gcd(128,52);
    System.out.println("value="+value);
  }

}
