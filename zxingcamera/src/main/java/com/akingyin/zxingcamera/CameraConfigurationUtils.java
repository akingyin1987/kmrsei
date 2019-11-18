/*
 * Copyright (C) 2014 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.akingyin.zxingcamera;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility methods for configuring the Android camera.
 *
 * @author Sean Owen
 */

public final class CameraConfigurationUtils {
	private static final Pattern COMMA_PATTERN = Pattern.compile(",");

  private static final String TAG = "CameraConfiguration";

  private static final Pattern SEMICOLON = Pattern.compile(";");

  //480 * 320
  private static final int MIN_PREVIEW_PIXELS = 540 * 540; // normal screen
  private static final float MAX_EXPOSURE_COMPENSATION = 1.5f;
  private static final float MIN_EXPOSURE_COMPENSATION = 0.0f;
  private static final double MAX_ASPECT_DISTORTION = 0.15;
  private static final int MIN_FPS = 10;
  private static final int MAX_FPS = 20;
  private static final int AREA_PER_1000 = 400;



  private CameraConfigurationUtils() {
  }

  /**
   * 设置对焦模式
   * @param parameters
   * @param autoFocus 是否自动对焦
   * @param disableContinuous 是否禁用连续对焦模式
   * @param safeMode 是否启用标准模式
   */
  @SuppressLint("InlinedApi")
public static void setFocus(Camera.Parameters parameters,
                              boolean autoFocus,
                              boolean disableContinuous,
                              boolean safeMode) {
      //获取当前摄像头支持的对焦模式
    List<String> supportedFocusModes = parameters.getSupportedFocusModes();
    String focusMode = null;
    if (autoFocus) {
      if (safeMode || disableContinuous) {
	// 标准对焦模式的话仅有一个选项：Camera.Parameters.FOCUS_MODE_AUTO
        focusMode = CameraConfigurationUtils.findSettableValue("focus mode",
                                                               supportedFocusModes,
                                                               Camera.Parameters.FOCUS_MODE_AUTO);
      } else {
	// 更加丰富的对焦模式
        focusMode = CameraConfigurationUtils.findSettableValue("focus mode",
                                                               supportedFocusModes,
                                                               Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE,
                                                               Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO,
                                                               Camera.Parameters.FOCUS_MODE_AUTO);
      }
    }
    // Maybe selected auto-focus but not available, so fall through here:
    if (!safeMode && focusMode == null) {
      focusMode = CameraConfigurationUtils.findSettableValue("focus mode",
                                                             supportedFocusModes,
                                                             Camera.Parameters.FOCUS_MODE_MACRO,
                                                             Camera.Parameters.FOCUS_MODE_EDOF);
    }
    if (focusMode != null) {
      if (focusMode.equals(parameters.getFocusMode())) {
        Log.i(TAG, "Focus mode already set to " + focusMode);
      } else {
    	  System.out.println("focusMode="+focusMode);
    	  Log.i(TAG, focusMode);
        parameters.setFocusMode(focusMode);
      }
    }
  }

  //设置闪光灯模式
  public static void setTorch(Camera.Parameters parameters, boolean on) {
    List<String> supportedFlashModes = parameters.getSupportedFlashModes();
    String flashMode;
    if (on) {
      flashMode = CameraConfigurationUtils.findSettableValue("flash mode",
                                                             supportedFlashModes,
                                                             Camera.Parameters.FLASH_MODE_TORCH,
                                                             Camera.Parameters.FLASH_MODE_ON);
    } else {
      flashMode = CameraConfigurationUtils.findSettableValue("flash mode",
                                                             supportedFlashModes,
                                                             Camera.Parameters.FLASH_MODE_OFF);
    }
    if (flashMode != null) {
      if (flashMode.equals(parameters.getFlashMode())) {
        Log.i(TAG, "Flash mode already set to " + flashMode);
      } else {
        Log.i(TAG, "Setting flash mode to " + flashMode);
        parameters.setFlashMode(flashMode);
      }
    }
  }

  //设备摄像头的暴光补尝值
  public static void setBestExposure(Camera.Parameters parameters, boolean lightOn) {
    int minExposure = parameters.getMinExposureCompensation();
    int maxExposure = parameters.getMaxExposureCompensation();
    float step = parameters.getExposureCompensationStep();
    if ((minExposure != 0 || maxExposure != 0) && step > 0.0f) {
      // Set low when light is on
      float targetCompensation = lightOn ? MIN_EXPOSURE_COMPENSATION : MAX_EXPOSURE_COMPENSATION;
      int compensationSteps = Math.round(targetCompensation / step);
      float actualCompensation = step * compensationSteps;
      // Clamp value:
      compensationSteps = Math.max(Math.min(compensationSteps, maxExposure), minExposure);
      if (parameters.getExposureCompensation() == compensationSteps) {
        Log.i(TAG, "Exposure compensation already set to " + compensationSteps + " / " + actualCompensation);
      } else {
        Log.i(TAG, "Setting exposure compensation to " + compensationSteps + " / " + actualCompensation);
        parameters.setExposureCompensation(compensationSteps);
      }
    } else {
      Log.i(TAG, "Camera does not support exposure compensation");
    }
  }

  public static void setBestPreviewFPS(Camera.Parameters parameters) {
    setBestPreviewFPS(parameters, MIN_FPS, MAX_FPS);
  }

  //设置当前摄像头的预览帧数
  public static void setBestPreviewFPS(Camera.Parameters parameters, int minFPS, int maxFPS) {
    List<int[]> supportedPreviewFpsRanges = parameters.getSupportedPreviewFpsRange();
    Log.i(TAG, "Supported FPS ranges: " + toString(supportedPreviewFpsRanges));
    if (supportedPreviewFpsRanges != null && !supportedPreviewFpsRanges.isEmpty()) {
      int[] suitableFPSRange = null;
      for (int[] fpsRange : supportedPreviewFpsRanges) {
        int thisMin = fpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX];
        int thisMax = fpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX];
        if (thisMin >= minFPS * 1000 && thisMax <= maxFPS * 1000) {
          suitableFPSRange = fpsRange;
          break;
        }
      }
      if (suitableFPSRange == null) {
        Log.i(TAG, "No suitable FPS range?");
      } else {
        int[] currentFpsRange = new int[2];
        parameters.getPreviewFpsRange(currentFpsRange);
        if (Arrays.equals(currentFpsRange, suitableFPSRange)) {
          Log.i(TAG, "FPS range already set to " + Arrays.toString(suitableFPSRange));
        } else {
          Log.i(TAG, "Setting FPS range to " + Arrays.toString(suitableFPSRange));
          parameters.setPreviewFpsRange(suitableFPSRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
                                        suitableFPSRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
        }
      }
    }
  }

  //设置光感度范围
  @SuppressLint("NewApi")
public static void setFocusArea(Camera.Parameters parameters) {
      //判断是否支持
    if (parameters.getMaxNumFocusAreas() > 0) {
      Log.i(TAG, "Old focus areas: " + toString(parameters.getFocusAreas()));
      List<Camera.Area> middleArea = buildMiddleArea(AREA_PER_1000);
      Log.i(TAG, "Setting focus area to : " + toString(middleArea));
      parameters.setFocusAreas(middleArea);
    } else {
      Log.i(TAG, "Device does not support focus areas");
    }
  }

  //设置 焦距
  @SuppressLint("NewApi")
public static void setMetering(Camera.Parameters parameters) {
    if (parameters.getMaxNumMeteringAreas() > 0) {
      Log.i(TAG, "Old metering areas: " + parameters.getMeteringAreas());
      List<Camera.Area> middleArea = buildMiddleArea(AREA_PER_1000);
      Log.i(TAG, "Setting metering area to : " + toString(middleArea));
      parameters.setMeteringAreas(middleArea);
    } else {
      Log.i(TAG, "Device does not support metering areas");
    }
  }

  @SuppressLint("NewApi")
private static List<Camera.Area> buildMiddleArea(int areaPer1000) {
    return Collections.singletonList(
        new Camera.Area(new Rect(-areaPer1000, -areaPer1000, areaPer1000, areaPer1000), 1));
  }

  
  /**
   * 使图像稳定化 API>= 15
   * @param parameters
   */
@SuppressLint("NewApi")
public static void setVideoStabilization(Camera.Parameters parameters) {
      
    if (parameters.isVideoSnapshotSupported()) {
	
      if (parameters.getVideoStabilization()) {
        Log.i(TAG, "Video stabilization already enabled");
      } else {
        Log.i(TAG, "Enabling video stabilization...");
        parameters.setVideoStabilization(true);
      }
    } else {
      Log.i(TAG, "This device does not support video stabilization");
    }
  }

  public static void setBarcodeSceneMode(Camera.Parameters parameters) {
    if (Camera.Parameters.SCENE_MODE_BARCODE.equals(parameters.getSceneMode())) {
      Log.i(TAG, "Barcode scene mode already set");
      return;
    }
    String sceneMode = findSettableValue("scene mode",
                                         parameters.getSupportedSceneModes(),
                                         Camera.Parameters.SCENE_MODE_BARCODE);
    if (sceneMode != null) {
      parameters.setSceneMode(sceneMode);
    }
  }

  public static void setZoom(Camera.Parameters parameters, double targetZoomRatio) {
    if (parameters.isZoomSupported()) {
      Integer zoom = indexOfClosestZoom(parameters, targetZoomRatio);
      if (zoom == null) {
        return;
      }
      if (parameters.getZoom() == zoom) {
        Log.i(TAG, "Zoom is already set to " + zoom);
      } else {
        Log.i(TAG, "Setting zoom to " + zoom);
        parameters.setZoom(zoom);
      }
    } else {
      Log.i(TAG, "Zoom is not supported");
    }
  }

  private static Integer indexOfClosestZoom(Camera.Parameters parameters, double targetZoomRatio) {
    List<Integer> ratios = parameters.getZoomRatios();
    Log.i(TAG, "Zoom ratios: " + ratios);
    int maxZoom = parameters.getMaxZoom();
    if (ratios == null || ratios.isEmpty() || ratios.size() != maxZoom + 1) {
      Log.w(TAG, "Invalid zoom ratios!");
      return null;
    }
    double target100 = 100.0 * targetZoomRatio;
    double smallestDiff = Double.POSITIVE_INFINITY;
    int closestIndex = 0;
    for (int i = 0; i < ratios.size(); i++) {
      double diff = Math.abs(ratios.get(i) - target100);
      if (diff < smallestDiff) {
        smallestDiff = diff;
        closestIndex = i;
      }
    }
    Log.i(TAG, "Chose zoom ratio of " + (ratios.get(closestIndex) / 100.0));
    return closestIndex;
  }

  /**
   * 设置相机为负面效果
   * @param parameters
   */
  public static void setInvertColor(Camera.Parameters parameters) {
    if (Camera.Parameters.EFFECT_NEGATIVE.equals(parameters.getColorEffect())) {
      Log.i(TAG, "Negative effect already set");
      return;
    }
    String colorMode =
        CameraConfigurationUtils.findSettableValue("color effect",
                                                   parameters.getSupportedColorEffects(),
                                                   Camera.Parameters.EFFECT_NEGATIVE);
    if (colorMode != null) {
      parameters.setColorEffect(colorMode);
    }
  }


  public static Size getPictureSize(List<Size> choices, int mediaQuality) {
    if (choices == null || choices.isEmpty()) return null;
    if (choices.size() == 1) return choices.get(0);

    Size result = null;
    Size maxPictureSize = Collections.max(choices, new CompareSizesByArea1());
    Size minPictureSize = Collections.min(choices, new CompareSizesByArea1());

    Collections.sort(choices, new CompareSizesByArea1());

    if (mediaQuality == CameraPreferences.MEDIA_QUALITY_HIGHEST) {
      result = maxPictureSize;
    } else if (mediaQuality == CameraPreferences.MEDIA_QUALITY_LOW) {
      if (choices.size() == 2) {
        result = minPictureSize;
      } else {
        int half = choices.size() / 2;
        int lowQualityIndex = (choices.size() - half) / 2;
        result = choices.get(lowQualityIndex + 1);
      }
    } else if (mediaQuality == CameraPreferences.MEDIA_QUALITY_HIGH) {
      if (choices.size() == 2) {
        result = maxPictureSize;
      } else {
        int half = choices.size() / 2;
        int highQualityIndex = (choices.size() - half) / 2;
        result = choices.get(choices.size() - highQualityIndex - 1);
      }
    } else if (mediaQuality == CameraPreferences.MEDIA_QUALITY_MEDIUM) {
      if (choices.size() == 2) {
        result = minPictureSize;
      } else {
        int mediumQualityIndex = choices.size() / 2;
        result = choices.get(mediumQualityIndex);
      }
    } else if (mediaQuality == CameraPreferences.MEDIA_QUALITY_LOWEST) {
      result = minPictureSize;
    }

    return result;
  }



  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public static android.util.Size getPictureSize(android.util.Size[] sizes,  int mediaQuality) {
    if (sizes == null || sizes.length == 0) {
      return null;
    }

    List<android.util.Size> choices = Arrays.asList(sizes);

    if (choices.size() == 1) {
      return choices.get(0);
    }

    android.util.Size result = null;
    android.util.Size maxPictureSize = Collections.max(choices, new CompareSizesByArea2());
    android.util.Size minPictureSize = Collections.min(choices, new CompareSizesByArea2());

    Collections.sort(choices, new CompareSizesByArea2());

    if (mediaQuality == CameraPreferences.MEDIA_QUALITY_HIGHEST) {
      result = maxPictureSize;
    } else if (mediaQuality == CameraPreferences.MEDIA_QUALITY_LOW) {
      if (choices.size() == 2) {
        result = minPictureSize;
      } else {
        int half = choices.size() / 2;
        int lowQualityIndex = (choices.size() - half) / 2;
        result = choices.get(lowQualityIndex + 1);
      }
    } else if (mediaQuality == CameraPreferences.MEDIA_QUALITY_HIGH) {
      if (choices.size() == 2) {
        result = maxPictureSize;
      } else {
        int half = choices.size() / 2;
        int highQualityIndex = (choices.size() - half) / 2;
        result = choices.get(choices.size() - highQualityIndex - 1);
      }
    } else if (mediaQuality == CameraPreferences.MEDIA_QUALITY_MEDIUM) {
      if (choices.size() == 2) {
        result = minPictureSize;
      } else {
        int mediumQualityIndex = choices.size() / 2;
        result = choices.get(mediumQualityIndex);
      }
    } else if (mediaQuality == CameraPreferences.MEDIA_QUALITY_LOWEST) {
      result = minPictureSize;
    }

    return result;
  }


  private static class CompareSizesByArea2 implements Comparator<android.util.Size> {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override
    public int compare(android.util.Size lhs, android.util.Size rhs) {
      // We cast here to ensure the multiplications won't overflow
      return Long.signum((long) lhs.getWidth() * lhs.getHeight()-
          (long) rhs.getWidth() * rhs.getHeight());
    }

  }

  private static class CompareSizesByArea1 implements Comparator<Size> {
    @Override
    public int compare(Size lhs, Size rhs) {
      // We cast here to ensure the multiplications won't overflow
      return Long.signum((long) lhs.width * lhs.height-
              (long) rhs.width * rhs.height);
    }

  }


  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public static android.util.Size[] fromArray2(android.util.Size[] sizes) {
    if (sizes == null) return null;
    android.util.Size[] result = new android.util.Size[sizes.length];

    for (int i = 0; i < sizes.length; ++i) {
      result[i] = new android.util.Size(sizes[i].getWidth(),sizes[i].getHeight());
    }

    return result;
  }


  /**
   * 获取支持的分辨率
   * @param parameters
   *
   * @return
   */
  public   static   List<Point>   findSuportCameraSizeValue(Camera.Parameters parameters){
    List<Point>   list = new ArrayList<>();
    //找到所有支持的Size
    List<Size> rawSupportedSizes = parameters.getSupportedPreviewSizes();

    Size  size = getPictureSize(rawSupportedSizes,CameraPreferences.MEDIA_QUALITY_HIGH);
    if(null != size){
      list.add(new Point( size.width,size.height));

    }

    size = getPictureSize(rawSupportedSizes,CameraPreferences.MEDIA_QUALITY_MEDIUM);
    if(null != size){
      list.add(new Point( size.width,size.height));
    }
    size = getPictureSize(rawSupportedSizes,CameraPreferences.MEDIA_QUALITY_HIGHEST);
    if(null != size){
      list.add(new Point( size.width,size.height));

    }

    if (rawSupportedSizes == null) {
      Log.w(TAG, "Device returned no supported preview sizes; using default");

      Size defaultSize = parameters.getPreviewSize();
      if (defaultSize == null) {
        return  null;
      }

      Point  point = new Point(defaultSize.width, defaultSize.height);
      list.add(point);
      return list;
    }
    // Sort by size, descending
    List<Size> supportedPreviewSizes = new ArrayList<>(rawSupportedSizes);

    //自定义排序规则
    Collections.sort(supportedPreviewSizes, new Comparator<Size>() {

      @Override
      public int compare(Size a, Size b) {
        int aPixels = a.height * a.width;
        int bPixels = b.height * b.width;
        if (bPixels < aPixels) {
          return -1;
        }
        if (bPixels > aPixels) {
          return 1;
        }
        return 0;
      }
    });

    Iterator<Size> it = supportedPreviewSizes.iterator();


    while (it.hasNext()) {
      Size supportedPreviewSize = it.next();
      int realWidth = supportedPreviewSize.width;
      int realHeight = supportedPreviewSize.height;
      if (realWidth * realHeight < MIN_PREVIEW_PIXELS) {
        it.remove();
        continue;
      }
      list.add(new Point(supportedPreviewSize.width,supportedPreviewSize.height));

    }
    if(list.size()>1){
       removeDuplicate(list);
    }
    return  list;
  }
  public   static <T>  List<T>  removeDuplicate(List<T> list)  {
    for  ( int  i  =   0 ; i  <  list.size()  -   1 ; i ++ )  {
      for  ( int  j  =  list.size()  -   1 ; j  >  i; j -- )  {
        if  (list.get(j).equals(list.get(i)))  {
          list.remove(j);
        }
      }
    }
    return list;
  }


   public  static  double BEST_SCALE=16.0/9.0;

  /**
   * 找到最合适的PreviewSize，如果有当前技能的Size 进行计算
   * 1.Size >= 480 * 320
   * 2.屏幕的Size 与当前 Size 宽与高比值之差《=0.15
   * @param parameters
   * @param screenResolution 当前屏幕Size
   * @return
   */
  public static Point findBestPreviewSizeValue(Camera.Parameters parameters, Point screenResolution) {
    
      //找到所有支持的Size
    List<Size> rawSupportedSizes = parameters.getSupportedPreviewSizes();
    if (rawSupportedSizes == null) {
      Log.w(TAG, "Device returned no supported preview sizes; using default");
      
      Size defaultSize = parameters.getPreviewSize();
      if (defaultSize == null) {
        throw new IllegalStateException("Parameters contained no preview size!");
      }
      return new Point(defaultSize.width, defaultSize.height);
    }

    // Sort by size, descending
    List<Size> supportedPreviewSizes = new ArrayList<>(rawSupportedSizes);
   
    //自定义排序规则
    Collections.sort(supportedPreviewSizes, new Comparator<Size>() {
	
      @Override
      public int compare(Size a, Size b) {
        int aPixels = a.height * a.width;
        int bPixels = b.height * b.width;
        if (bPixels < aPixels) {
          return -1;
        }
        if (bPixels > aPixels) {
          return 1;
        }
        return 0;
      }
    });

    if (Log.isLoggable(TAG, Log.INFO)) {
      StringBuilder previewSizesString = new StringBuilder();
      for (Size supportedPreviewSize : supportedPreviewSizes) {
        previewSizesString.append(supportedPreviewSize.width).append('x')
            .append(supportedPreviewSize.height).append(' ');
      }
      Log.i(TAG, "Supported preview sizes: " + previewSizesString);
    }

    double screenAspectRatio = (double) Math.max(screenResolution.x, screenResolution.y) / (double) Math.min(screenResolution.x, screenResolution.y);
    System.out.println("screenAspectRatio="+screenAspectRatio);
    // Remove sizes that are unsuitable
    Iterator<Size> it = supportedPreviewSizes.iterator();
    while (it.hasNext()) {
      Size supportedPreviewSize = it.next();
      int realWidth = supportedPreviewSize.width;
      int realHeight = supportedPreviewSize.height;
      if (realWidth * realHeight < MIN_PREVIEW_PIXELS) {
        it.remove();
        continue;
      }

      boolean isCandidatePortrait = realWidth < realHeight;
      int maybeFlippedWidth = isCandidatePortrait ? realHeight : realWidth;
      int maybeFlippedHeight = isCandidatePortrait ? realWidth : realHeight;
      double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
     
      double distortion = Math.abs(aspectRatio - screenAspectRatio);
      if (distortion > MAX_ASPECT_DISTORTION) {
        it.remove();
        continue;
      }

      if (maybeFlippedWidth == screenResolution.x && maybeFlippedHeight == screenResolution.y) {
        Point exactPoint = new Point(realWidth, realHeight);
        Log.i(TAG, "Found preview size exactly matching screen size: " + exactPoint);
        return exactPoint;
      }
    }

    // If no exact match, use largest preview size. This was not a great idea on older devices because
    // of the additional computation needed. We're likely to get here on newer Android 4+ devices, where
    // the CPU is much more powerful.
    //排除完上述条件后，获取第一个Size
    if (!supportedPreviewSizes.isEmpty()) {
    	
      Size largestPreview = supportedPreviewSizes.get(0);
      for (Size supportedPreviewSize : supportedPreviewSizes) {
        double  scale = Math.max(supportedPreviewSize.width,supportedPreviewSize.height)/(Math.min(supportedPreviewSize.width,supportedPreviewSize.height)*1.0);
        if(Math.abs(BEST_SCALE-scale)<= 0.01 && Math.max(supportedPreviewSize.width,supportedPreviewSize.height)>960){
          largestPreview = supportedPreviewSize;
          break;
        }
      }
      Point largestSize = new Point(largestPreview.width, largestPreview.height);
      Log.i(TAG, "Using largest suitable preview size: " + largestSize);
      return largestSize;
    }

    // If there is nothing at all suitable, return current preview size
    Size defaultPreview = parameters.getPreviewSize();
    if (defaultPreview == null) {
      throw new IllegalStateException("Parameters contained no preview size!");
    }
    Point defaultSize = new Point(defaultPreview.width, defaultPreview.height);
    Log.i(TAG, "No suitable preview sizes, using default: " + defaultSize);
    return defaultSize;
  }

  /**
   * 在supportedValues中寻找desiredValues，找不到则返回null
   * @param name
   * @param supportedValues 被找数据集合
   * @param desiredValues   要找的数据
   * @return
   */
  public static String findSettableValue(String name,
                                          Collection<String> supportedValues,
                                          String... desiredValues) {
    Log.i(TAG, "Requesting " + name + " value from among: " + Arrays.toString(desiredValues));
    Log.i(TAG, "Supported " + name + " values: " + supportedValues);
    if (supportedValues != null) {
      for (String desiredValue : desiredValues) {
        if (supportedValues.contains(desiredValue)) {
          Log.i(TAG, "Can set " + name + " to: " + desiredValue);
          return desiredValue;
        }
      }
    }
    Log.i(TAG, "No supported values match");
    return null;
  }

  private static String toString(Collection<int[]> arrays) {
    if (arrays == null || arrays.isEmpty()) {
      return "[]";
    }
    StringBuilder buffer = new StringBuilder();
    buffer.append('[');
    Iterator<int[]> it = arrays.iterator();
    while (it.hasNext()) {
      buffer.append(Arrays.toString(it.next()));
      if (it.hasNext()) {
        buffer.append(", ");
      }
    }
    buffer.append(']');
    return buffer.toString();
  }

  @SuppressLint("NewApi")
private static String toString(Iterable<Camera.Area> areas) {
    if (areas == null) {
      return null;
    }
    StringBuilder result = new StringBuilder();
    for (Camera.Area area : areas) {
      result.append(area.rect).append(':').append(area.weight).append(' ');
    }
    return result.toString();
  }

  public static String collectStats(Camera.Parameters parameters) {
    return collectStats(parameters.flatten());
  }

  public static String collectStats(CharSequence flattenedParams) {
    StringBuilder result = new StringBuilder(1000);

    result.append("BOARD=").append(Build.BOARD).append('\n');
    result.append("BRAND=").append(Build.BRAND).append('\n');
    result.append("CPU_ABI=").append(Build.CPU_ABI).append('\n');
    result.append("DEVICE=").append(Build.DEVICE).append('\n');
    result.append("DISPLAY=").append(Build.DISPLAY).append('\n');
    result.append("FINGERPRINT=").append(Build.FINGERPRINT).append('\n');
    result.append("HOST=").append(Build.HOST).append('\n');
    result.append("ID=").append(Build.ID).append('\n');
    result.append("MANUFACTURER=").append(Build.MANUFACTURER).append('\n');
    result.append("MODEL=").append(Build.MODEL).append('\n');
    result.append("PRODUCT=").append(Build.PRODUCT).append('\n');
    result.append("TAGS=").append(Build.TAGS).append('\n');
    result.append("TIME=").append(Build.TIME).append('\n');
    result.append("TYPE=").append(Build.TYPE).append('\n');
    result.append("USER=").append(Build.USER).append('\n');
    result.append("VERSION.CODENAME=").append(Build.VERSION.CODENAME).append('\n');
    result.append("VERSION.INCREMENTAL=").append(Build.VERSION.INCREMENTAL).append('\n');
    result.append("VERSION.RELEASE=").append(Build.VERSION.RELEASE).append('\n');
    result.append("VERSION.SDK_INT=").append(Build.VERSION.SDK_INT).append('\n');

    if (flattenedParams != null) {
      String[] params = SEMICOLON.split(flattenedParams);
      Arrays.sort(params);
      for (String param : params) {
        result.append(param).append('\n');
      }
    }

    return result.toString();
  }

  /**
   * 获取最佳的分辨率
   * @param parameters
   * @param screenResolution
   * @return
   */
  public static Point getCameraResolution(Camera.Parameters parameters,
			Point screenResolution) {

		String previewSizeValueString = parameters.get("preview-size-values");
		// saw this on Xperia
		if (previewSizeValueString == null) {
			previewSizeValueString = parameters.get("preview-size-value");
		}

		Point cameraResolution = null;

		if (previewSizeValueString != null) {
			Log.d(TAG, "preview-size-values parameter: "
					+ previewSizeValueString);
			cameraResolution = findBestPreviewSizeValue(previewSizeValueString,
					screenResolution);
		}

		if (cameraResolution == null) {
			// Ensure that the camera resolution is a multiple of 8, as the
			// screen may not be.
			cameraResolution = new Point((screenResolution.x >> 3) << 3,
					(screenResolution.y >> 3) << 3);
		}

		return cameraResolution;
	}

	private static Point findBestPreviewSizeValue(
			CharSequence previewSizeValueString, Point screenResolution) {
		int bestX = 0;
		int bestY = 0;
		int diff = Integer.MAX_VALUE;
		int newDiff;
		for (String previewSize : COMMA_PATTERN.split(previewSizeValueString)) {

			previewSize = previewSize.trim();
			int dimPosition = previewSize.indexOf('x');
			if (dimPosition < 0) {
				Log.w(TAG, "Bad preview-size: " + previewSize);
				continue;
			}

			int newX;
			int newY;
			try {
				newX = Integer.parseInt(previewSize.substring(0, dimPosition));
				newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
			} catch (NumberFormatException nfe) {
				Log.w(TAG, "Bad preview-size: " + previewSize);
				continue;
			}
			if (screenResolution.x > screenResolution.y) {
				newDiff = Math.abs(newX - screenResolution.x)
						+ Math.abs(newY - screenResolution.y);
			}else {
				newDiff = Math.abs(newX - screenResolution.y)
						+ Math.abs(newY - screenResolution.x);
			}
			
			if (newDiff == 0) {
				bestX = newX;
				bestY = newY;
				break;
			} else if (newDiff < diff) {
				bestX = newX;
				bestY = newY;
				diff = newDiff;
			}

		}

		if (bestX > 0 && bestY > 0) {
			return new Point(bestX, bestY);
		}
		return null;
	}


	public  final static   double   SCALE_16_9=16.0/9.0;
    public  final static   double   SCALE_4_3=4.0/3.0;

	public   static    String   getCameraResolutionScale(Point  point){
	  double  scale = Math.max(point.x,point.y)*1.0/Math.min(point.x,point.y);
	  if(Math.abs(SCALE_16_9 - scale)<=0.1){
	    return "[16:9]";
      }
      if(Math.abs(SCALE_4_3-scale)<=0.1){
	    return "[4:3]";
      }

      int   result = GCD(point.x,point.y);

      return "["+Math.max(point.x,point.y)/result+":"+Math.min(point.x,point.y)/result+"]";

    }


  /**
   * 获取最大公约数
   * @param m
   * @param n
   * @return
   */
    public   static   int   GCD(int  m,int n){
      int result = 0;
      while (n != 0) {
        result = m % n;
        m = n;
        n = result;
      }
      return m;

    }
  
}
