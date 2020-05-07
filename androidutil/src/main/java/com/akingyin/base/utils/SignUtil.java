package com.akingyin.base.utils;

import com.blankj.utilcode.util.EncryptUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/12/9 13:50
 */
public class SignUtil {

  private static final String DEFAULT_SECRET = "FF10E31A-D695-4697-AA0E-D2E9BECADCC9";

  /**
   * 生成随机密钥
   *
   * @return 随机密钥
   */
  public static String createRandomKeySecret() {
    return createRandomKeySecret(16);
  }

  /**
   * 生成随机密钥
   *
   * @param length 长度
   * @return
   */
  private static String createRandomKeySecret(int length) {

    return RandomUtil.getRandomString(length).toUpperCase();
  }

  /**
   * 判断签名是否相等(默认secret的情况)
   *
   * @param sign
   * @param map
   * @return
   */
  public static Boolean checkSign(String sign, SortedMap map) {
    if (StringUtils.isEmpty(sign)) {
      return false;
    }
    return sign.equals(createSign(map));
  }

  /**
   * 判断签名是否相等(默认secret的情况)
   *
   * @param sign
   * @param map
   * @return
   */
  public static Boolean checkSign(String sign, Map map) {
    return checkSign(sign, map2SortedMap(map));
  }

  /**
   * 判断签名是否相等(默认secret的情况)
   *
   * @param sign
   * @param map
   * @param param 对于某些场景,map中不是所有字段都需要校验签名的情况,会根据传入的param当做要校验的字段
   * @return
   */
  public static Boolean checkSign(String sign, Map map, String... param) {
    return checkSign(sign, map2SortedMap(map,param));
  }


  /**
   * 生成签名(map中所有字段都会参与生成签名)
   * <p>
   * 注意，如果map中的value为null，目前是忽略操作
   *
   * @param map
   * @return
   */
  public static String createSign(Map<Object, Object> map) {
    return createSign(DEFAULT_SECRET, map);
  }

  /**
   * 生成签名(map中部分字段生成签名)
   * <p>
   * 注意，如果map中的value为null，目前是忽略操作
   *
   * @param map
   * @param param 对于某些场景,map中不是所有字段都需要校验签名的情况,会根据传入的param当做要校验的字段
   * @return
   */
  public static String createSign(Map<Object, Object> map, String... param) {
    return createSign(DEFAULT_SECRET, map, param);
  }

  /**
   * 生成签名
   * 注意，如果map中的value为null，目前是忽略操作
   *
   * @param keySecret 自定义密钥
   * @param map       需要生成签名的
   * @return
   */
  public static String createSign(String keySecret, Map<Object, Object> map) {
    return createSign(keySecret, map2SortedMap(map));
  }

  /**
   * 生成签名
   * 注意，如果map中的value为null，目前是忽略操作
   *
   * @param keySecret 自定义密钥
   * @param map       需要生成签名的map
   * @param param     对于某些场景,map中不是所有字段都需要校验签名的情况,会根据传入的param当做要校验的字段
   * @return
   */
  public static String createSign(String keySecret, Map<Object, Object> map, String... param) {
    return createSign(keySecret, map2SortedMap(map, param));
  }

  /**
   * 签名算法sign（微信支付使用的方式）
   * 注意，如果map中的value为null，目前是忽略操作
   *
   * @param keySecret
   * @param parameters 因为需要排序，所以使用
   * @return 返回签名，异常返回null
   */
  @SuppressWarnings("unchecked")
  public static String createSign(String keySecret, SortedMap<Object, Object> parameters) {
    try {
      StringBuilder sb = new StringBuilder();
      //所有参与传参的参数按照accsii排序（升序）
      Set es = parameters.entrySet();
      for (Object e : es) {
        Map.Entry entry = (Map.Entry) e;
        String k = (String) entry.getKey();
        Object v = entry.getValue();
        if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
          sb.append(k).append("=").append(v).append("&");
        }
      }
      sb.append("key=").append(keySecret);

      return  EncryptUtils.encryptMD5ToString(sb.toString()).toUpperCase();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * map转SortedMap
   *
   * @param map
   * @return
   */
  private static SortedMap map2SortedMap(Map<Object,Object> map) {
    SortedMap<Object, Object> parameters = new TreeMap<>();

    for (Object o : map.entrySet()) {
      Map.Entry entry = (Map.Entry) o;
      Object key = entry.getKey();
      Object val = entry.getValue();
      parameters.put(key, val);
    }
    return parameters;
  }

  /**
   * map转SortedMap
   *
   * @param map
   * @param param 对于某些场景,map中不是所有字段都需要校验签名的情况,会根据传入的param当做要校验的字段
   * @return
   */
  private static SortedMap map2SortedMap(Map map, String... param) {
    SortedMap<Object, Object> parameters = new TreeMap<>();

    for (String key : param) {
      parameters.put(key, map.get(key));
    }
    return parameters;
  }


  public static void main(String[] args) {
    System.out.println(">>>模拟微信支付<<<");
    System.out.println("==========华丽的分隔符==========");
    //微信api提供的参数
    String appid = "wxd930ea5d5a258f4f";
    String mch_id = "10000100";
    String device_info = "1000";
    String body = "test";
    String nonce_str = "ibuaiVcKdpRxkhJA";

    Map<Object, Object> parameters = new HashMap<Object, Object>();
    // null会被忽略的
    parameters.put("id", null);
    parameters.put("appid", appid);
    parameters.put("mch_id", mch_id);
    parameters.put("device_info", device_info);
    parameters.put("body", body);
    parameters.put("nonce_str", nonce_str);

    //        String characterEncoding = "UTF-8";
    String Key = "192006250b4c09247ec02edce69f6a2d";
    String weixinApiSign = "9A0A8659F005D6984697E2CA0A9CF3B7";
    System.out.println("微信的签名是：" + weixinApiSign);
    String mySign = createSign(Key, parameters);
    System.out.println("我     的签名是：" + mySign);

    if (weixinApiSign.equals(mySign)) {
      System.out.println("恭喜你成功了~");
    } else {
      System.out.println("失败~");
    }
  }
}
