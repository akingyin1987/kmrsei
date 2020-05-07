package com.akingyin.base.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import androidx.core.app.ActivityCompat;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * @ Description:
 * @ Author king
 * @ Date 2017/9/4 17:33
 * @ Version V1.0
 */

public final class AppUtils {

  private AppUtils() {
    throw new UnsupportedOperationException("u can't instantiate me...");
  }

  /**
   * 判断App是否安装
   *
   * @param packageName 包名
   * @return {@code true}: 已安装<br>{@code false}: 未安装
   */
  public static boolean isInstallApp(final String packageName) {
    return !isSpace(packageName) && IntentUtils.getLaunchAppIntent(packageName) != null;
  }

  /**
   * 安装App(支持7.0)
   *
   * @param filePath  文件路径
   * @param authority 7.0及以上安装需要传入清单文件中的{@code <provider>}的authorities属性
   *                  <br>参看https://developer.android.com/reference/android/support/v4/content/FileProvider.html
   */
  public static void installApp(final String filePath, final String authority) {
    installApp(new File(filePath), authority);
  }

  /**
   * 安装App（支持7.0）
   *
   * @param file      文件
   * @param authority 7.0及以上安装需要传入清单文件中的{@code <provider>}的authorities属性
   *                  <br>参看https://developer.android.com/reference/android/support/v4/content/FileProvider.html
   */
  public static void installApp(final File file, final String authority) {
    if (null == file) {
      return;
    }
    if (!FileUtils.isFileExist(file.getAbsolutePath())) {
      return;
    }
    Utils.getApp().startActivity(IntentUtils.getInstallAppIntent(file, authority));
  }

  /**
   * 安装App（支持6.0）
   *
   * @param activity    activity
   * @param filePath    文件路径
   * @param authority   7.0及以上安装需要传入清单文件中的{@code <provider>}的authorities属性
   *                    <br>参看https://developer.android.com/reference/android/support/v4/content/FileProvider.html
   * @param requestCode 请求值
   */
  public static void installApp(final Activity activity, final String filePath, final String authority, final int requestCode) {
    installApp(activity, new File(filePath), authority, requestCode);
  }

  /**
   * 安装App(支持6.0)
   *
   * @param activity    activity
   * @param file        文件
   * @param authority   7.0及以上安装需要传入清单文件中的{@code <provider>}的authorities属性
   *                    <br>参看https://developer.android.com/reference/android/support/v4/content/FileProvider.html
   * @param requestCode 请求值
   */
  public static void installApp(final Activity activity, final File file, final String authority,
      final int requestCode) {
    if (null == file || !file.exists()) {
      return;
    }
    activity.startActivityForResult(IntentUtils.getInstallAppIntent(file, authority), requestCode);
  }

  /**
   * 卸载App
   *
   * @param packageName 包名
   */
  public static void uninstallApp(final String packageName) {
    if (isSpace(packageName)) {
      return;
    }
    Utils.getApp().startActivity(IntentUtils.getUninstallAppIntent(packageName));
  }

  /**
   * 卸载App
   *
   * @param activity    activity
   * @param packageName 包名
   * @param requestCode 请求值
   */
  public static void uninstallApp(final Activity activity, final String packageName, final int requestCode) {
    if (isSpace(packageName)) {
      return;
    }
    activity.startActivityForResult(IntentUtils.getUninstallAppIntent(packageName), requestCode);
  }

  /**
   * 打开App
   *
   * @param packageName 包名
   */
  public static void launchApp(final String packageName) {
    if (isSpace(packageName)) {
      return;
    }
    Utils.getApp().startActivity(IntentUtils.getLaunchAppIntent(packageName));
  }

  /**
   * 打开App
   *
   * @param activity    activity
   * @param packageName 包名
   * @param requestCode 请求值
   */
  public static void launchApp(final Activity activity, final String packageName, final int requestCode) {
    if (isSpace(packageName)) {
      return;
    }
    activity.startActivityForResult(IntentUtils.getLaunchAppIntent(packageName), requestCode);
  }

  /**
   * 关闭App
   */
  public static void exitApp() {
    List<Activity> activityList = Utils.sActivityList;
    for (int i = activityList.size() - 1; i >= 0; --i) {
      activityList.get(i).finish();
      activityList.remove(i);
    }
    System.exit(0);
  }

  /**
   * 获取App具体设置
   */
  public static void getAppDetailsSettings() {
    getAppDetailsSettings(Utils.getApp().getPackageName());
  }

  /**
   * 获取App具体设置
   *
   * @param packageName 包名
   */
  public static void getAppDetailsSettings(final String packageName) {
    if (isSpace(packageName)) {
      return;
    }
    Utils.getApp().startActivity(IntentUtils.getAppDetailsSettingsIntent(packageName));
  }

  /**
   * 获取App图标
   *
   * @return App图标
   */
  public static Drawable getAppIcon() {
    return getAppIcon(Utils.getApp().getPackageName());
  }

  /**
   * 获取App图标
   *
   * @param packageName 包名
   * @return App图标
   */
  public static Drawable getAppIcon(final String packageName) {
    if (isSpace(packageName)) {
      return null;
    }
    try {
      PackageManager pm = Utils.getApp().getPackageManager();
      PackageInfo pi = pm.getPackageInfo(packageName, 0);
      return pi == null ? null : pi.applicationInfo.loadIcon(pm);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 获取App路径
   *
   * @return App路径
   */
  public static String getAppPath() {
    return getAppPath(Utils.getApp().getPackageName());
  }

  /**
   * 获取App路径
   *
   * @param packageName 包名
   * @return App路径
   */
  public static String getAppPath(final String packageName) {
    if (isSpace(packageName)) {
      return null;
    }
    try {
      PackageManager pm = Utils.getApp().getPackageManager();
      PackageInfo pi = pm.getPackageInfo(packageName, 0);
      return pi == null ? null : pi.applicationInfo.sourceDir;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 获取App版本号
   *
   * @return App版本号
   */
  public static String getAppVersionName() {
    return getAppVersionName(Utils.getApp().getPackageName());
  }

  /**
   * 获取App版本号
   *
   * @param packageName 包名
   * @return App版本号
   */
  public static  String getAppVersionName(final String packageName) {
    if (isSpace(packageName)) {
      return null;
    }
    try {
      PackageManager pm = Utils.getApp().getPackageManager();
      PackageInfo pi = pm.getPackageInfo(packageName, 0);
      return pi == null ? null : pi.versionName;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 获取App版本码
   *
   * @return App版本码
   */
  public static int getAppVersionCode() {
    return getAppVersionCode(Utils.getApp().getPackageName());
  }

  /**
   * 获取App版本码
   *
   * @param packageName 包名
   * @return App版本码
   */
  public static int getAppVersionCode(final String packageName) {
    if (isSpace(packageName)) {
      return -1;
    }
    try {
      PackageManager pm = Utils.getApp().getPackageManager();
      PackageInfo pi = pm.getPackageInfo(packageName, 0);
      return pi == null ? -1 : pi.versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
      return -1;
    }
  }

  /**
   * 判断App是否是系统应用
   *
   * @return {@code true}: 是<br>{@code false}: 否
   */
  public static boolean isSystemApp() {
    return isSystemApp(Utils.getApp().getPackageName());
  }

  /**
   * 判断App是否是系统应用
   *
   * @param packageName 包名
   * @return {@code true}: 是<br>{@code false}: 否
   */
  public static boolean isSystemApp(final String packageName) {
    if (isSpace(packageName)) {
      return false;
    }
    try {
      PackageManager pm = Utils.getApp().getPackageManager();
      ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
      return ai != null && (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 判断App是否是Debug版本
   *
   * @return {@code true}: 是<br>{@code false}: 否
   */
  public static boolean isAppDebug() {
    return isAppDebug(Utils.getApp().getPackageName());
  }

  /**
   * 判断App是否是Debug版本
   *
   * @param packageName 包名
   * @return {@code true}: 是<br>{@code false}: 否
   */
  public static boolean isAppDebug(final String packageName) {
    if (isSpace(packageName)) {
      return false;
    }
    try {
      PackageManager pm = Utils.getApp().getPackageManager();
      ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
      return ai != null && (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 判断App是否处于前台
   *
   * @return {@code true}: 是<br>{@code false}: 否
   */
  public static boolean isAppForeground() {
    ActivityManager manager = (ActivityManager) Utils.getApp().getSystemService(Context.ACTIVITY_SERVICE);
    List<ActivityManager.RunningAppProcessInfo> info = manager.getRunningAppProcesses();
    if (info == null || info.size() == 0) {
      return false;
    }
    for (ActivityManager.RunningAppProcessInfo aInfo : info) {
      if (aInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
        return aInfo.processName.equals(Utils.getApp().getPackageName());
      }
    }
    return false;
  }

  /**
   * 封装App信息的Bean类
   */
  public static class AppInfo {

    private String name;
    private Drawable icon;
    private String packageName;
    private String packagePath;
    private String versionName;
    private int versionCode;
    private boolean isSystem;

    public Drawable getIcon() {
      return icon;
    }

    public void setIcon(final Drawable icon) {
      this.icon = icon;
    }

    public boolean isSystem() {
      return isSystem;
    }

    public void setSystem(final boolean isSystem) {
      this.isSystem = isSystem;
    }

    public String getName() {
      return name;
    }

    public void setName(final String name) {
      this.name = name;
    }

    public String getPackageName() {
      return packageName;
    }

    public void setPackageName(final String packageName) {
      this.packageName = packageName;
    }

    public String getPackagePath() {
      return packagePath;
    }

    public void setPackagePath(final String packagePath) {
      this.packagePath = packagePath;
    }

    public int getVersionCode() {
      return versionCode;
    }

    public void setVersionCode(final int versionCode) {
      this.versionCode = versionCode;
    }

    public String getVersionName() {
      return versionName;
    }

    public void setVersionName(final String versionName) {
      this.versionName = versionName;
    }

    /**
     * @param name        名称
     * @param icon        图标
     * @param packageName 包名
     * @param packagePath 包路径
     * @param versionName 版本号
     * @param versionCode 版本码
     * @param isSystem    是否系统应用
     */
    public AppInfo(String packageName, String name, Drawable icon, String packagePath,
        String versionName, int versionCode, boolean isSystem) {
      this.setName(name);
      this.setIcon(icon);
      this.setPackageName(packageName);
      this.setPackagePath(packagePath);
      this.setVersionName(versionName);
      this.setVersionCode(versionCode);
      this.setSystem(isSystem);
    }

    @Override public String toString() {
      return "pkg name: "
          + getPackageName()
          + "\napp name: "
          + getName()
          + "\napp path: "
          + getPackagePath()
          + "\napp v name: "
          + getVersionName()
          + "\napp v code: "
          + getVersionCode()
          + "\nis system: "
          + isSystem();
    }
  }

  /**
   * 获取App信息
   * <p>AppInfo（名称，图标，包名，版本号，版本Code，是否系统应用）</p>
   *
   * @return 当前应用的AppInfo
   */
  public static AppInfo getAppInfo() {
    return getAppInfo(Utils.getApp().getPackageName());
  }

  /**
   * 获取App信息
   * <p>AppInfo（名称，图标，包名，版本号，版本Code，是否系统应用）</p>
   *
   * @param packageName 包名
   * @return 当前应用的AppInfo
   */
  public static AppInfo getAppInfo(final String packageName) {
    try {
      PackageManager pm = Utils.getApp().getPackageManager();
      PackageInfo pi = pm.getPackageInfo(packageName, 0);
      return getBean(pm, pi);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 得到AppInfo的Bean
   *
   * @param pm 包的管理
   * @param pi 包的信息
   * @return AppInfo类
   */
  private static AppInfo getBean(final PackageManager pm, final PackageInfo pi) {
    if (pm == null || pi == null) {
      return null;
    }
    ApplicationInfo ai = pi.applicationInfo;
    String packageName = pi.packageName;
    String name = ai.loadLabel(pm).toString();
    Drawable icon = ai.loadIcon(pm);
    String packagePath = ai.sourceDir;
    String versionName = pi.versionName;
    int versionCode = pi.versionCode;
    boolean isSystem = (ApplicationInfo.FLAG_SYSTEM & ai.flags) != 0;
    return new AppInfo(packageName, name, icon, packagePath, versionName, versionCode, isSystem);
  }

  /**
   * 获取所有已安装App信息
   * <p>{@link #getBean(PackageManager, PackageInfo)}（名称，图标，包名，包路径，版本号，版本Code，是否系统应用）</p>
   * <p>依赖上面的getBean方法</p>
   *
   * @return 所有已安装的AppInfo列表
   */
  public static List<AppInfo> getAppsInfo() {
    List<AppInfo> list = new ArrayList<>();
    PackageManager pm = Utils.getApp().getPackageManager();
    // 获取系统中安装的所有软件信息
    List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
    for (PackageInfo pi : installedPackages) {
      AppInfo ai = getBean(pm, pi);
      if (ai == null) {
        continue;
      }
      list.add(ai);
    }
    return list;
  }

  /**
   * 清除App所有数据
   *
   * @param dirPaths 目录路径
   * @return {@code true}: 成功<br>{@code false}: 失败
   */
  public static boolean cleanAppData(final String... dirPaths) {
    File[] dirs = new File[dirPaths.length];
    int i = 0;
    for (String dirPath : dirPaths) {
      dirs[i++] = new File(dirPath);
    }
    return cleanAppData(dirs);
  }

  /**
   * 清除App所有数据
   *
   * @param dirs 目录
   * @return {@code true}: 成功<br>{@code false}: 失败
   */
  public static boolean cleanAppData(final File... dirs) {
    boolean isSuccess = CleanUtils.cleanInternalCache();
    isSuccess &= CleanUtils.cleanInternalDbs();
    isSuccess &= CleanUtils.cleanInternalSP();
    isSuccess &= CleanUtils.cleanInternalFiles();
    isSuccess &= CleanUtils.cleanExternalCache();
    for (File dir : dirs) {
      isSuccess &= CleanUtils.cleanCustomCache(dir);
    }
    return isSuccess;
  }

  private static boolean isSpace(final String s) {
    if (s == null) {
      return true;
    }
    for (int i = 0, len = s.length(); i < len; ++i) {
      if (!Character.isWhitespace(s.charAt(i))) {
        return false;
      }
    }
    return true;
  }


  public  static  String  getAndroidId(Context context){
    try {

      return Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

    }catch (Exception e){
      e.printStackTrace();
    }
    return "";
  }

  public static String getIMEI(Context context) {
    String deviceId = "";
    try {

      if (Build.VERSION.SDK_INT >= 29) {
        deviceId = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
      } else {
        // request old storage permission
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
          // TODO: Consider calling
          //    ActivityCompat#requestPermissions
          // here to request the missing permissions, and then overriding
          //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
          //                                          int[] grantResults)
          // to handle the case where the user grants the permission. See the documentation
          // for ActivityCompat#requestPermissions for more details.
          return "";
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            deviceId = tm.getImei();
          } else {
            deviceId = tm.getDeviceId();
          }
        }
      }
      if (deviceId == null || "".equals(deviceId)) {
        return "";
      }
    } catch (Exception e) {
      e.printStackTrace();
      if (deviceId == null || "".equals(deviceId)) {
        return "";
      }
    }

    return deviceId.toUpperCase(Locale.getDefault());
  }

  public static String getIMSI(Context context) {
    try {
      TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      //获取IMSI号
      if (ActivityCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_STATE)
          != PackageManager.PERMISSION_GRANTED) {
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for Activity#requestPermissions for more details.
        return "";
      }
      String imsi = telephonyManager.getSubscriberId();
      if(null==imsi){
        imsi="";
      }
      return imsi;
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }


  public static String getSerial(Context context) {
    try {

      if (ActivityCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_STATE)
          != PackageManager.PERMISSION_GRANTED) {
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for Activity#requestPermissions for more details.
        return "";
      }
      String imsi = "";
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        imsi = Build.getSerial();
      }else{
        imsi = Build.SERIAL;
      }
      if(null==imsi){
        imsi="";
      }
      return imsi;
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }

  /**
   * 获取设备唯一标识符
   *
   * @return 唯一标识符
   */
  @SuppressLint("HardwareIds")
  public static String getDeviceId(Context context) {
    String m_szDevIDShort = "35" + Build.BOARD.length() % 10
        + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10
        + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10
        + Build.HOST.length() % 10 + Build.ID.length() % 10
        + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10
        + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10
        + Build.TYPE.length() % 10 + Build.USER.length() % 10;// 13 位

    String serial = "serial";// 默认serial可随便定义
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if (ActivityCompat.checkSelfPermission(context,
            Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
          // 由于 Android Q 唯一标识符权限的更改会导致
          // android.os.Build.getSerial() 返回 unknown,
          // 但是 m_szDevIDShort 是由硬件信息拼出来的，所以仍然保证了UUID 的唯一性和持久性。
          serial = android.os.Build.getSerial();// Android Q 中返回 unknown
        }
      } else {
        serial = Build.SERIAL;
      }
    } catch (Exception ignored) {
    }
    return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
  }


  public static final boolean isOPenLocation( Context context) {

    try {
      LocationManager locationManager
              = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
      // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
      boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
      // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
      boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
      if (gps || network) {
        return true;
      }
    }catch (Exception e){
      e.printStackTrace();
    }


    return false;
  }
}
