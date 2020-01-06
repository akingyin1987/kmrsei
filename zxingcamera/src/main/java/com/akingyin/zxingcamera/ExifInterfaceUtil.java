package com.akingyin.zxingcamera;

import android.media.ExifInterface;
import android.text.TextUtils;
import java.util.Map;

/**
 * 图片属性获取
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2017/11/20 10:01
 */
public class ExifInterfaceUtil {

    public   static  String  getExifinterAttr(String   localPath,String  key){
        if(TextUtils.isEmpty(localPath)){
            return  "";
        }
        try {
            ExifInterface   exifInterface = new ExifInterface(localPath);
            return exifInterface.getAttribute(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public  static  void  saveExifinterAttr(String  localpath,String  key,String value){
        try {
            System.out.println("保存图片属性="+key+""+value);
            ExifInterface  exifInterface = new ExifInterface(localpath);
            exifInterface.setAttribute(key,value);
            exifInterface.saveAttributes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  static  void  saveExifinterAttr(String  localpath, Map<String,String>  data){
        try {
            ExifInterface  exifInterface = new ExifInterface(localpath);
            for (String key : data.keySet()) {
                exifInterface.setAttribute(key,data.get(key));
            }
            exifInterface.saveAttributes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
