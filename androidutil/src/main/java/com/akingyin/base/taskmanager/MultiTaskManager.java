/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.base.taskmanager;

import com.akingyin.base.rx.RxUtil;
import com.akingyin.base.taskmanager.enums.TaskManagerStatusEnum;
import com.akingyin.base.taskmanager.enums.TaskStatusEnum;
import com.akingyin.base.taskmanager.enums.ThreadTypeEnum;
import com.blankj.utilcode.util.StringUtils;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多任务管理器
 *
 * @author akingyin
 * @date 2019-12-19
 */
public class MultiTaskManager implements  ITaskResultCallBack{



    private ThreadTypeEnum mThreadTypeEnum = ThreadTypeEnum.CurrentThread;

    public ThreadTypeEnum getThreadTypeEnum() {
        return mThreadTypeEnum;
    }

    public void setThreadTypeEnum(ThreadTypeEnum threadTypeEnum) {
        mThreadTypeEnum = threadTypeEnum;

    }

    /** 线程池 */
    private ExecutorService threadPool;

    private  MultiTaskManager(){

    }

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

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();


    public   void  addSubscription(Disposable disposable){
        mCompositeDisposable.add(disposable);
    }

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


    /**
     *   // 创建线程池，核心线程数、最大线程数、空闲保持时间、队列长度、拒绝策略可自行定义
     * @param poolSize
     * @return
     */
    public  static  MultiTaskManager createPool(int  poolSize){

        MultiTaskManager  taskManager = new MultiTaskManager();

        // 创建线程池，核心线程数、最大线程数、空闲保持时间、队列长度、拒绝策略可自行定义
        taskManager.threadPool = new ThreadPoolExecutor(poolSize, poolSize*40,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(MAX_CACHE_SIZE),
            new ThreadFactoryBuilder().setNameFormat("multitask-pool-%d").build(),new ThreadPoolExecutor.AbortPolicy());
         taskManager.nThreads = poolSize;
        return  taskManager;
    }

    private   ApiTaskCallBack  callBack;


    public ApiTaskCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(ApiTaskCallBack callBack) {
        this.callBack = callBack;
    }



    public void addTasks(List<AbsTaskRunner> tasks) {
        for (AbsTaskRunner taskRunner : tasks) {
            addTask(taskRunner);
        }
    }

    public  static  int  MAX_CACHE_SIZE= 1024;

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
        int   index = 1;
        for(AbsTaskRunner  taskRunner : queueTasks){
          if(index<=MAX_CACHE_SIZE){

            threadPool.execute(taskRunner);
          }
          index++;
        }
    }

  private void executeTask(int  postion) {
    int   index = 1;

    for(AbsTaskRunner  taskRunner : queueTasks){
      if(index>= postion && index<(MAX_CACHE_SIZE+postion)){
        threadPool.execute(taskRunner);
      }
      index++;
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
            threadPool = new ThreadPoolExecutor(nThreads, nThreads*40,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024),
                new ThreadFactoryBuilder().setNameFormat("multitask-pool-%d").build(),new ThreadPoolExecutor.AbortPolicy());
            for(AbsTaskRunner  absTaskRunner : queueTasks){
                if(absTaskRunner.getTaskStatusEnum().getCode()<=3){
                    threadPool.execute(absTaskRunner);
                }
            }

        }
    }

    public   int    getTotal(){
        System.out.println("count="+count.get()+":"+queueTasks.size());
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
      System.out.println("取消任务---->");

        status.getAndSet(3);
        count.set(0);
        mCompositeDisposable.dispose();
        mCompositeDisposable.clear();
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
      System.out.println("onCallBack-->>>");
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

            default:
                overTotal.getAndIncrement();
                errorTotal.getAndIncrement();
                break;

        }
        if(overTotal.get()>0 && overTotal.get()%MAX_CACHE_SIZE == 0 && count.get()>overTotal.get()){
          System.out.println("执行下一轮-->");
          executeTask(overTotal.get());
        }
        if(null != callBack ){
            if(statusEnum != TaskStatusEnum.NETERROR){
                final   int  errorNum = errorTotal.get();
                final    int  sucNum = successTotal.get();

                if(mThreadTypeEnum == ThreadTypeEnum.MainThread){
                   Disposable  disposable = Observable.just(count.get()).compose(RxUtil.IO_Main())
                          .subscribe(
                              integer -> callBack.onCallBack(count.get(),errorNum+sucNum,errorNum),
                              Throwable::printStackTrace);
                    addSubscription(disposable);

                }else{
                    callBack.onCallBack(count.get(),errorNum+sucNum,errorNum);
                }

                if(count.get() == errorNum +sucNum){
                    status.getAndSet(4);
                    if(errorNum>0){
                        Disposable  disposable = Observable.just(count.get()).compose(RxUtil.IO_Main())
                            .subscribe(
                                integer -> callBack.onError(errorMsg.toString(),TaskManagerStatusEnum.COMPLETE),
                                Throwable::printStackTrace);
                        addSubscription(disposable);


                    }else{
                        Disposable  disposable = Observable.just(count.get()).compose(RxUtil.IO_Main())
                            .subscribe(
                                integer -> callBack.onComplete(),
                                Throwable::printStackTrace);
                        addSubscription(disposable);


                    }
                }
            }else{
                status.getAndSet(5);

                Disposable  disposable = Observable.just(count.get()).compose(RxUtil.IO_Main())
                    .subscribe(
                        integer -> callBack.onError(error,TaskManagerStatusEnum.NETError),
                        Throwable::printStackTrace);
                addSubscription(disposable);


            }
        }
    }

    @Override public void onCancelAllTask(final TaskManagerStatusEnum statusEnum, final String error) {

        if(null != callBack){
            Disposable  disposable = Observable.just(count.get()).compose(RxUtil.IO_Main())
                .subscribe(
                    integer ->  callBack.onError(error,statusEnum),
                    Throwable::printStackTrace);
            addSubscription(disposable);

        }
        if(statusEnum == TaskManagerStatusEnum.CANCEL ||
                statusEnum == TaskManagerStatusEnum.COMPLETE){

            cancelTasks();
        }
    }
}
