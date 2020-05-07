package com.akingyin.base.utils;

import java.util.Random;
import java.util.UUID;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/12/9 13:52
 */
public class RandomUtil {

  public static String getRandomUUID() {
    return UUID.randomUUID().toString().replace("-","").toUpperCase();
  }

  /**
   * 生成随机字符串
   *
   * @param length 生成字符串的长度
   * @return
   */
  public static String getRandomString(int length) {
    String base = "abcdefghijklmnopqrstuvwxyz0123456789";
    Random random = new Random();
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < length; i++) {
      int number = random.nextInt(base.length());
      sb.append(base.charAt(number));
    }
    return sb.toString();
  }

  /**
   * 获取随机整数
   *
   * @param min 最小值
   * @param max 最大值
   * @return
   */
  public static int getRandomNum(int min, int max) {
    return new Random().nextInt(max - min + 1) + min;
  }
}
