package com.zlcdgroup.mrsei.utils;


import android.text.TextUtils;
import com.alibaba.fastjson.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class RQ {



  /**
   * 获取加密后的数据签名
   */
  public final static synchronized String getToken(String data) {
    // 初步想法为先用MD5获取其缩略16个字节的文本。
    // 然后执行DSA或者RSA加密，采取公钥和私钥的形式。
    try {
      String md5Txt = MD5.md5(data);
      String rsa = RSA.encrypt(md5Txt);

      if (TextUtils.isEmpty(rsa)) {
        md5Txt = MD5.md5(data);
        rsa = RSA.encrypt(md5Txt);
        return rsa;
      }
      return rsa;
    } catch (Exception e) {
      return "no token";
    }
  }

  public static String getJsonData(String method, String userid, String imei,
      Map<String, Object> dataMap) {
    JSONObject dataJson = new JSONObject(dataMap);
    HashMap<String, Object> hashMap = new HashMap<String, Object>();
    hashMap.put("method", method);

    hashMap.put("personId", userid);
    hashMap.put("imei", imei);
    hashMap.put("data", dataJson);
    JSONObject jsonObject = new JSONObject(hashMap);
    String data = "";
    try {
      String objStr = jsonObject.toJSONString();

      // LogUtil.log("client", objStr);
      if (TextUtils.equals("zlcd_mrmsei_upload_img_base64", method) ||
          TextUtils.equals("zlcd_mrmsei_upload_img_base64_check",method)) {

      } else {
        System.out.println("终端>>：" + objStr);

      }
      data = URLEncoder.encode(objStr, "utf-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return data;
  }
}
