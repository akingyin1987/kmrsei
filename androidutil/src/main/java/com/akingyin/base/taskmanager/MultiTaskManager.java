/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.base.taskmanager;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.akingyin.base.taskmanager.enums.TaskManagerStatusEnum;
import com.akingyin.base.taskmanager.enums.TaskStatusEnum;
import com.akingyin.base.taskmanager.enums.ThreadTypeEnum;
import com.blankj.utilcode.util.StringUtils;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多任务管理器
 *
 * @author Administrator
 * @date 2016/7/3
 */
public class MultiTaskManager implements  ITaskResultCallBack{

    private Handler  mainHandler = new Handler(Looper.getMainLooper());

    private ThreadTypeEnum mThreadTypeEnum = ThreadTypeEnum.CurrentThread;

    public ThreadTypeEnum getThreadTypeEnum() {
        return mThreadTypeEnum;
    }

    public void setThreadTypeEnum(ThreadTypeEnum threadTypeEnum) {
        mThreadTypeEnum = threadTypeEnum;

    }

    /** 线程池 */
    private ExecutorService threadPool;

    /** 待执行任务 */
    private LinkedBlockingQueue<AbsTaskRunner> queueTasks = new LinkedBlockingQueue<>();

    /**  错误日志 */
    private LinkedBlockingQueue<String> errorStrings = new LinkedBlockingQueue<>();
    /** 任务总数 */
    private  AtomicInteger count = new AtomicInteger(0);
    /** 成功数 */
    private  AtomicInteger successTotal = new AtomicInteger(0);
    /** 已执行数 */
    private  AtomicInteger overTotal = new AtomicInteger(0);

    /** 错误数 */
    private  AtomicInteger errorTotal = new AtomicInteger(0);

    /** 状态TaskManagerStatusEnum */
    private  AtomicInteger  status = new AtomicInteger(0);
    private   StringBuilder    errorMsg = new StringBuilder();
    private   String    lastErrorMsg;

    public String getLastErrorMsg() {
        return lastErrorMsg;
    }


    public String getErrorMsg() {
        return errorMsg.toString();
    }

    public LinkedBlockingQueue<String> getErrorStrings() {
        return errorStrings;
    }

    public void setErrorStrings(LinkedBlockingQueue<String> errorStrings) {
        this.errorStrings = errorStrings;
    }

    /**
     * 获取当前任务状态
     * @return
     */
    public TaskManagerStatusEnum getTaskManagerStatus(){
        return  TaskManagerStatusEnum.getTaskManagerStatus(status.get());
    }

    private   int   nThreads;
    public MultiTaskManager() {
        this(3);
    }

    private   ApiTaskCallBack  callBack;


    public ApiTaskCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(ApiTaskCallBack callBack) {
        this.callBack = callBack;
    }

    public MultiTaskManager(int nThreads) {

        threadPool = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
            @Override public Thread newThread(@NonNull Runnable r) {
                return new Thread(r);
            }
        });
        this.nThreads = nThreads;
    }

    public void addTasks(List<AbsTaskRunner> tasks) {
        for (AbsTaskRunner taskRunner : tasks) {
            addTask(taskRunner);
        }
    }

    public void addTask(AbsTaskRunner task) {
        task.setCallBack(this);
        try {
            queueTasks.put(task);
            count.getAndIncrement();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }



    public void executeTask() {

        for(AbsTaskRunner  taskRunner : queueTasks){

            threadPool.execute(taskRunner);
        }
    }

    public   void   onPauseTask(){
        if(status.get() ==3 || status.get() ==4 ){
            return;
        }
        status.getAndSet(6);
        if(null != threadPool){
            threadPool.shutdownNow();
        }
    }

    public    void   onStartTask(){
        errorMsg = new StringBuilder();
        lastErrorMsg="";
        errorStrings.clear();
        status.getAndSet(2);
        if(null != threadPool){
            threadPool = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
                @Override public Thread newThread(@NonNull Runnable r) {
                    return new Thread(r);
                }
            });
            for(AbsTaskRunner  absTaskRunner : queueTasks){
                if(absTaskRunner.getTaskStatusEnum().getCode()<=3){
                    threadPool.submit(absTaskRunner);
                }
            }

        }
    }

    public   int    getTotal(){
        return  count.get();
    }

    public  int    getSuccessTotal(){
        return  successTotal.get();
    }

    public  int   getErrorTotal(){
        return  errorTotal.get();
    }

    public   int   getOverTotal(){
        return  overTotal.get();
    }


    public boolean isOver() {
      return  null == threadPool || getTotal() ==  getOverTotal();
    }

    /**
     * 取消正在下载的任务
     */
    public synchronized void cancelTasks() {
        status.getAndSet(3);
        if (threadPool != null) {
            for(AbsTaskRunner  taskRunner : queueTasks){
                taskRunner.onCancel();
            }
            queueTasks.clear();
            try {
                threadPool.shutdownNow();
            } catch (Exception e) {
                e.printStackTrace();
            }

            threadPool = null;
        }
    }
    @Override
    public void onCallBack(TaskStatusEnum statusEnum,final String error) {

        if(status.get() == 4 || status.get() == 3 || status.get() ==5 ){

            return;
        }
        if( !StringUtils.isEmpty(error)){
            errorMsg.append(error).append(" ");
            lastErrorMsg = error;
            errorStrings.offer(error);
        }

        switch (statusEnum){
            case NETERROR:

                cancelTasks();
                break;
            case SUCCESS:
                overTotal.getAndIncrement();
                successTotal.getAndIncrement();
                break;
            case  ERROR:
                overTotal.getAndIncrement();
                errorTotal.getAndIncrement();
                break;
            default:
                overTotal.getAndIncrement();
                errorTotal.getAndIncrement();
                break;

        }

        if(null != callBack ){
            if(statusEnum != TaskStatusEnum.NETERROR){
                final   int  errorNum = errorTotal.get();
                final    int  sucNum = successTotal.get();

                if(mThreadTypeEnum == ThreadTypeEnum.MainThread){
                    mainHandler.post(new Runnable() {
                        @Override public void run() {
                            callBack.onCallBack(count.get(),errorNum+sucNum,errorNum);
                        }
                    });
                }else{
                    callBack.onCallBack(count.get(),errorNum+sucNum,errorNum);
                }

                if(count.get() == errorNum +sucNum){
                    status.getAndSet(4);
                    if(errorNum>0){
                        mainHandler.post(new Runnable() {
                            @Override public void run() {
                                callBack.onError(errorMsg.toString(),TaskManagerStatusEnum.COMPLETE);
                            }
                        });

                    }else{
                        mainHandler.post(new Runnable() {
                            @Override public void run() {
                                callBack.onComplete();
                            }
                        });

                    }
                }
            }else{
                status.getAndSet(5);
                mainHandler.post(new Runnable() {
                    @Override public void run() {
                        callBack.onError(error,TaskManagerStatusEnum.NETError);
                    }
                });

            }
        }
    }

    @Override public void onCancelAllTask(final TaskManagerStatusEnum statusEnum, final String error) {

        if(null != callBack){
            mainHandler.post(new Runnable() {
                @Override public void run() {
                    callBack.onError(error,statusEnum);
                }
            });
        }
        if(statusEnum == TaskManagerStatusEnum.CANCEL ||
                statusEnum == TaskManagerStatusEnum.COMPLETE){

            cancelTasks();
        }
    }
}
