package com.akingyin.base

import android.app.Activity
import com.akingyin.base.ext.spGetInt
import java.lang.ref.WeakReference
import java.util.*
import kotlin.system.exitProcess

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/3 15:51
 * @version V1.0
 */
class  AppManager  private  constructor(){ //私有的主构造器


    companion object{ ////被companion object包裹的语句都是private的

        @Volatile
        private var instance: AppManager? = null

        @Synchronized
        fun getInstance():AppManager?{
            if (instance == null){
                instance = AppManager()
            }

            return instance
        }
    }

     var activityStack: Stack<WeakReference<Activity>> = Stack()



    /**
     * 添加Activity到堆栈
     */
    fun addActivity(activity: Activity) {

        activityStack.add(WeakReference(activity))
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    fun currentActivity(): Activity? {
        return activityStack.lastElement().get()
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    fun finishActivity() {
        activityStack.lastElement().get()?.let {activity ->
            finishActivity(activity)
        }

    }

    /**
     * 结束指定的Activity
     */
    fun finishActivity(activity: Activity?) {
        if (activity != null) {
            activityStack.forEach {
                it.get()?.let {act->
                    if(activity == act){
                        if(!act.isFinishing){
                            act.finish()
                        }
                        activityStack.remove(it)
                    }

                }
            }

        }
    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivity(cls: Class<*>) {
        for (activity in activityStack) {
            activity.get()?.let {
                if(it.javaClass == cls){
                    finishActivity(it)
                }
            }

        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        if (activityStack.isNotEmpty()) {
            for (activityWeakReference in activityStack) {
                val activity = activityWeakReference?.get()
                if (activity != null && !activity.isFinishing) {
                    activity.finish()
                }
            }
            activityStack.clear()
        }
    }

    /**
     * 退出应用程序
     */
    fun AppExit() {
        try {
            finishAllActivity()
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}