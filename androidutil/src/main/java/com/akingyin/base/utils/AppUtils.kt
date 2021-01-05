package com.akingyin.base.utils

import android.Manifest
import com.akingyin.base.utils.FileUtils.isFileExist
import android.graphics.drawable.Drawable
import android.app.Activity
import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import android.content.pm.ApplicationInfo
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import androidx.core.app.ActivityCompat
import android.telephony.TelephonyManager
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import java.io.File
import java.lang.Exception
import java.util.*

/**
 * @ Description:
 * @ Author king
 * @ Date 2017/9/4 17:33
 * @ Version V1.0
 */
@Suppress("DEPRECATION")
class AppUtils private constructor() {
    /**
     * 封装App信息的Bean类
     */

    /**
     * @param name        名称
     * @param icon        图标
     * @param packageName 包名
     * @param packagePath 包路径
     * @param versionName 版本号
     * @param versionCode 版本码
     * @param isSystem    是否系统应用
     */
    data class AppInfo(var packageName: String?, var name: String?, var icon: Drawable?, var packagePath: String?,
                 var  versionName: String?, var versionCode: Int, var isSystem: Boolean) {

        override fun toString(): String {
            return """
                 pkg name: $packageName
                 app name: $name
                 app path: $packagePath
                 app v name: $versionName
                 app v code: $versionCode
                 is system: $isSystem
                 """.trimIndent()
        }

    }

    companion object {
        /**
         * 判断App是否安装
         *
         * @param packageName 包名
         * @return `true`: 已安装<br></br>`false`: 未安装
         */
        fun isInstallApp(packageName: String): Boolean {
            return !isSpace(packageName) && IntentUtils.getLaunchAppIntent(packageName) != null
        }

        /**
         * 安装App(支持7.0)
         *
         * @param filePath  文件路径
         * @param authority 7.0及以上安装需要传入清单文件中的`<provider>`的authorities属性
         * <br></br>参看https://developer.android.com/reference/android/support/v4/content/FileProvider.html
         */
        fun installApp(filePath: String, authority: String?) {
            installApp(File(filePath), authority)
        }

        /**
         * 安装App（支持7.0）
         *
         * @param file      文件
         * @param authority 7.0及以上安装需要传入清单文件中的`<provider>`的authorities属性
         * <br></br>参看https://developer.android.com/reference/android/support/v4/content/FileProvider.html
         */
        fun installApp(file: File?, authority: String?) {
            if (null == file) {
                return
            }
            if (!isFileExist(file.absolutePath)) {
                return
            }
            Utils.getApp().startActivity(IntentUtils.getInstallAppIntent(file, authority))
        }

        /**
         * 安装App（支持6.0）
         *
         * @param activity    activity
         * @param filePath    文件路径
         * @param authority   7.0及以上安装需要传入清单文件中的`<provider>`的authorities属性
         * <br></br>参看https://developer.android.com/reference/android/support/v4/content/FileProvider.html
         * @param requestCode 请求值
         */
        fun installApp(activity: Activity, filePath: String, authority: String?, requestCode: Int) {
            installApp(activity, File(filePath), authority, requestCode)
        }

        /**
         * 安装App(支持6.0)
         *
         * @param activity    activity
         * @param file        文件
         * @param authority   7.0及以上安装需要传入清单文件中的`<provider>`的authorities属性
         * <br></br>参看https://developer.android.com/reference/android/support/v4/content/FileProvider.html
         * @param requestCode 请求值
         */
        fun installApp(activity: Activity, file: File?, authority: String?,
                       requestCode: Int) {
            if (null == file || !file.exists()) {
                return
            }
            activity.startActivityForResult(IntentUtils.getInstallAppIntent(file, authority), requestCode)
        }

        /**
         * 卸载App
         *
         * @param packageName 包名
         */
        fun uninstallApp(packageName: String?) {
            if (isSpace(packageName)) {
                return
            }
            Utils.getApp().startActivity(IntentUtils.getUninstallAppIntent(packageName))
        }

        /**
         * 卸载App
         *
         * @param activity    activity
         * @param packageName 包名
         * @param requestCode 请求值
         */
        fun uninstallApp(activity: Activity, packageName: String?, requestCode: Int) {
            if (isSpace(packageName)) {
                return
            }
            activity.startActivityForResult(IntentUtils.getUninstallAppIntent(packageName), requestCode)
        }

        /**
         * 打开App
         *
         * @param packageName 包名
         */
        fun launchApp(packageName: String?) {
            if (isSpace(packageName)) {
                return
            }
            Utils.getApp().startActivity(IntentUtils.getLaunchAppIntent(packageName))
        }

        /**
         * 打开App
         *
         * @param activity    activity
         * @param packageName 包名
         * @param requestCode 请求值
         */
        fun launchApp(activity: Activity, packageName: String?, requestCode: Int) {
            if (isSpace(packageName)) {
                return
            }
            activity.startActivityForResult(IntentUtils.getLaunchAppIntent(packageName), requestCode)
        }

        /**
         * 关闭App
         */
        fun exitApp() {
            val activityList = Utils.sActivityList
            for (i in activityList.indices.reversed()) {
                activityList[i].finish()
                activityList.removeAt(i)
            }
            System.exit(0)
        }

        /**
         * 获取App具体设置
         */
        val appDetailsSettings: Unit
            get() {
                getAppDetailsSettings(Utils.getApp().packageName)
            }

        /**
         * 获取App具体设置
         *
         * @param packageName 包名
         */
        fun getAppDetailsSettings(packageName: String?) {
            if (isSpace(packageName)) {
                return
            }
            Utils.getApp().startActivity(IntentUtils.getAppDetailsSettingsIntent(packageName))
        }

        /**
         * 获取App图标
         *
         * @return App图标
         */
        val appIcon: Drawable?
            get() = getAppIcon(Utils.getApp().packageName)

        /**
         * 获取App图标
         *
         * @param packageName 包名
         * @return App图标
         */
        fun getAppIcon(packageName: String?): Drawable? {
            return if (isSpace(packageName)) {
                null
            } else try {
                val pm = Utils.getApp().packageManager
                val pi = pm.getPackageInfo(packageName!!, 0)
                pi?.applicationInfo?.loadIcon(pm)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                null
            }
        }

        /**
         * 获取App路径
         *
         * @return App路径
         */
        val appPath: String?
            get() = getAppPath(Utils.getApp().packageName)

        /**
         * 获取App路径
         *
         * @param packageName 包名
         * @return App路径
         */
        fun getAppPath(packageName: String?): String? {
            return if (isSpace(packageName)) {
                null
            } else try {
                val pm = Utils.getApp().packageManager
                val pi = pm.getPackageInfo(packageName!!, 0)
                pi?.applicationInfo?.sourceDir
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                null
            }
        }

        /**
         * 获取App版本号
         *
         * @return App版本号
         */
        val appVersionName: String?
            get() = getAppVersionName(Utils.getApp().packageName)

        /**
         * 获取App版本号
         *
         * @param packageName 包名
         * @return App版本号
         */
        fun getAppVersionName(packageName: String?): String? {
            return if (isSpace(packageName)) {
                null
            } else try {
                val pm = Utils.getApp().packageManager
                val pi = pm.getPackageInfo(packageName!!, 0)
                pi?.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                null
            }
        }

        /**
         * 获取App版本码
         *
         * @return App版本码
         */
        val appVersionCode: Int
            get() = getAppVersionCode(Utils.getApp().packageName)

        /**
         * 获取App版本码
         *
         * @param packageName 包名
         * @return App版本码
         */
        fun getAppVersionCode(packageName: String?): Int {
            return if (isSpace(packageName)) {
                -1
            } else try {
                val pm = Utils.getApp().packageManager
                val pi = pm.getPackageInfo(packageName!!, 0)
                pi?.longVersionCode?.toInt() ?: -1
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                -1
            }
        }

        /**
         * 判断App是否是系统应用
         *
         * @return `true`: 是<br></br>`false`: 否
         */
        val isSystemApp: Boolean
            get() = isSystemApp(Utils.getApp().packageName)

        /**
         * 判断App是否是系统应用
         *
         * @param packageName 包名
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isSystemApp(packageName: String): Boolean {
            return if (isSpace(packageName)) {
                false
            } else try {
                val pm = Utils.getApp().packageManager
                val ai = pm.getApplicationInfo(packageName, 0)
                 ai.flags and ApplicationInfo.FLAG_SYSTEM != 0
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                false
            }
        }

        /**
         * 判断App是否是Debug版本
         *
         * @return `true`: 是<br></br>`false`: 否
         */
        val isAppDebug: Boolean
            get() = isAppDebug(Utils.getApp().packageName)

        /**
         * 判断App是否是Debug版本
         *
         * @param packageName 包名
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isAppDebug(packageName: String): Boolean {
            return if (isSpace(packageName)) {
                false
            } else try {
                val pm = Utils.getApp().packageManager
                val ai = pm.getApplicationInfo(packageName, 0)
                 ai.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                false
            }
        }

        /**
         * 判断App是否处于前台
         *
         * @return `true`: 是<br></br>`false`: 否
         */
        val isAppForeground: Boolean
            get() {
                val manager = Utils.getApp().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val info = manager.runningAppProcesses
                if (info == null || info.size == 0) {
                    return false
                }
                for (aInfo in info) {
                    if (aInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return aInfo.processName == Utils.getApp().packageName
                    }
                }
                return false
            }

        /**
         * 获取App信息
         *
         * AppInfo（名称，图标，包名，版本号，版本Code，是否系统应用）
         *
         * @return 当前应用的AppInfo
         */
        val appInfo: AppInfo?
            get() = getAppInfo(Utils.getApp().packageName)

        /**
         * 获取App信息
         *
         * AppInfo（名称，图标，包名，版本号，版本Code，是否系统应用）
         *
         * @param packageName 包名
         * @return 当前应用的AppInfo
         */
        fun getAppInfo(packageName: String): AppInfo? {
            return try {
                val pm = Utils.getApp().packageManager
                val pi = pm.getPackageInfo(packageName, 0)
                getBean(pm, pi)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                null
            }
        }

        /**
         * 得到AppInfo的Bean
         *
         * @param pm 包的管理
         * @param pi 包的信息
         * @return AppInfo类
         */
        private fun getBean(pm: PackageManager?, pi: PackageInfo?): AppInfo? {
            if (pm == null || pi == null) {
                return null
            }
            val ai = pi.applicationInfo
            val packageName = pi.packageName
            val name = ai.loadLabel(pm).toString()
            val icon = ai.loadIcon(pm)
            val packagePath = ai.sourceDir
            val versionName = pi.versionName
            val versionCode = pi.longVersionCode.toInt()
            val isSystem = ApplicationInfo.FLAG_SYSTEM and ai.flags != 0
            return AppInfo(packageName, name, icon, packagePath, versionName, versionCode, isSystem)
        }// 获取系统中安装的所有软件信息

        /**
         * 获取所有已安装App信息
         *
         * [.getBean]（名称，图标，包名，包路径，版本号，版本Code，是否系统应用）
         *
         * 依赖上面的getBean方法
         *
         * @return 所有已安装的AppInfo列表
         */
        val appsInfo: List<AppInfo>
            get() {
                val list: MutableList<AppInfo> = ArrayList()
                val pm = Utils.getApp().packageManager
                // 获取系统中安装的所有软件信息
                val installedPackages = pm.getInstalledPackages(0)
                for (pi in installedPackages) {
                    val ai = getBean(pm, pi) ?: continue
                    list.add(ai)
                }
                return list
            }

        /**
         * 清除App所有数据
         *
         * @param dirPaths 目录路径
         * @return `true`: 成功<br></br>`false`: 失败
         */
        fun cleanAppData(vararg dirPaths: String): Boolean {
            val dirs = arrayOfNulls<File>(dirPaths.size)
            var i = 0
            for (dirPath in dirPaths) {
                dirs[i++] = File(dirPath)
            }
            return cleanAppData(*dirs)
        }

        /**
         * 清除App所有数据
         *
         * @param dirs 目录
         * @return `true`: 成功<br></br>`false`: 失败
         */
        fun cleanAppData(vararg dirs: File?): Boolean {
            var isSuccess = CleanUtils.cleanInternalCache()
            isSuccess = isSuccess and CleanUtils.cleanInternalDbs()
            isSuccess = isSuccess and CleanUtils.cleanInternalSP()
            isSuccess = isSuccess and CleanUtils.cleanInternalFiles()
            isSuccess = isSuccess and CleanUtils.cleanExternalCache()
            for (dir in dirs) {
                isSuccess = isSuccess and CleanUtils.cleanCustomCache(dir)
            }
            return isSuccess
        }

        private fun isSpace(s: String?): Boolean {
            if (s == null) {
                return true
            }
            var i = 0
            val len = s.length
            while (i < len) {
                if (!Character.isWhitespace(s[i])) {
                    return false
                }
                ++i
            }
            return true
        }

        fun getAndroidId(context: Context): String {
            try {
                return Settings.System.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

        fun getIMEI(context: Context): String {
            var deviceId: String? = ""
            try {
                if (Build.VERSION.SDK_INT >= 29) {
                    deviceId = Settings.System.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
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
                        return ""
                    }
                    val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    deviceId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        tm.imei
                    } else {
                        tm.deviceId
                    }
                }
                if (deviceId == null || "" == deviceId) {
                    return ""
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (deviceId == null || "" == deviceId) {
                    return ""
                }
            }
            return deviceId!!.toUpperCase(Locale.getDefault())
        }

        fun getIMSI(context: Context): String {
            return try {
                val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                //获取IMSI号
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return ""
                }
                var imsi = telephonyManager.subscriberId
                if (null == imsi) {
                    imsi = ""
                }
                imsi
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }

        fun getSerial(context: Context?): String {
            return try {
                if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return ""
                }

                var imsi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Build.getSerial()
                } else {
                    Build.SERIAL
                }
                if (null == imsi) {
                    imsi = ""
                }
                imsi
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }

        /**
         * 获取设备唯一标识符
         *
         * @return 唯一标识符
         */
        @SuppressLint("HardwareIds")
        fun getDeviceId(context: Context): String {
            val m_szDevIDShort = "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.CPU_ABI.length % 10 + Build.DEVICE.length % 10 + Build.DISPLAY.length % 10 + Build.HOST.length % 10 + Build.ID.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10 + Build.TAGS.length % 10 + Build.TYPE.length % 10 + Build.USER.length % 10 // 13 位
            var serial = "serial" // 默认serial可随便定义
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (ActivityCompat.checkSelfPermission(context,
                                    Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        // 由于 Android Q 唯一标识符权限的更改会导致
                        // android.os.Build.getSerial() 返回 unknown,
                        // 但是 m_szDevIDShort 是由硬件信息拼出来的，所以仍然保证了UUID 的唯一性和持久性。
                        serial = Build.getSerial() // Android Q 中返回 unknown
                    }
                } else {
                    serial = Build.SERIAL
                }
            } catch (ignored: Exception) {
            }
            return UUID(m_szDevIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
        }

        fun isOPenLocation(context: Context): Boolean {
            try {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
                val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
                val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                if (gps || network) {
                    return true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }
    }


}