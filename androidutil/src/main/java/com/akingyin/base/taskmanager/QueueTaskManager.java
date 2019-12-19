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
import com.akingyin.base.taskmanager.enums.TaskManagerStatusEnum;
import com.akingyin.base.taskmanager.enums.TaskStatusEnum;
import com.akingyin.base.taskmanager.enums.ThreadTypeEnum;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多任务管理器 队列one by one
 * Created by Administrator on 2016/7/3.
 */
public class QueueTaskManager implements  ITaskResultCallBack{
    private ThreadTypeEnum mThreadTypeEnum = ThreadTypeEnum.CurrentThread;

    public ThreadTypeEnum getThreadTypeEnum() {
        return mThreadTypeEnum;
    }

    public void setThreadTypeEnum(ThreadTypeEnum threadTypeEnum) {
        mThreadTypeEnum = threadTypeEnum;

    }
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    /** 线程池 */
    private ExecutorService threadPool;
    private LinkedBlockingQueue<AbsTaskRunner> queueTasks = new LinkedBlockingQueue<>();
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
    //获取当前任务状态
    public TaskManagerStatusEnum getTaskManagerStatus(){
        return  TaskManagerStatusEnum.getTaskManagerStatus(status.get());
    }

    public  QueueTaskManager(){
        threadPool  = Executors.newFixedThreadPool(1);
    }



    private   ApiTaskCallBack  callBack;


    public ApiTaskCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(ApiTaskCallBack callBack) {
        this.callBack = callBack;
    }


    public void addTask(List<AbsTaskRunner> tasks) {
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
        if (threadPool == null) {
            return true;
        }
        return queueTasks.size() <= 0;
    }

    public void doNext() {
        if (isOver()) {
            return;
        }
        AbsTaskRunner qt = queueTasks.poll();
        if (qt != null && null != threadPool){
            threadPool.execute(qt);
        }

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
            }
            threadPool = null;
        }
    }
    @Override
    public void onCallBack(TaskStatusEnum statusEnum, final String error) {
        if(status.get() == 4 || status.get() == 3 || status.get() ==5){
            return;
        }
        overTotal.getAndIncrement();
        switch (statusEnum){
            case NETERROR:
                cancelTasks();
                break;
            case SUCCESS:
                overTotal.getAndIncrement();
                successTotal.getAndIncrement();
                doNext();
                break;
            case  ERROR:
                doNext();
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
                                callBack.onError(error,TaskManagerStatusEnum.COMPLETE);
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

    @Override public void onCancelAllTask(final  TaskManagerStatusEnum statusEnum, final  String error) {

        if(null != callBack){
            mainHandler.post(new Runnable() {
                @Override public void run() {
                    callBack.onError(error,statusEnum);
                }
            });
        }
        if(statusEnum == TaskManagerStatusEnum.CANCEL){

            cancelTasks();
        }
    }
}
