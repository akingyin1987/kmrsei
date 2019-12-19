/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.base.taskmanager;

import android.text.TextUtils;
import com.akingyin.base.taskmanager.enums.TaskManagerStatusEnum;
import com.akingyin.base.taskmanager.enums.TaskStatusEnum;
import com.blankj.utilcode.util.StringUtils;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import retrofit2.HttpException;

/**
 * 执行的任务
 *
 * @author
 * @date 2016/7/3
 */
public  abstract class AbsTaskRunner implements Runnable ,ApiSonTaskCallBack{


    /** 任务唯一ID标识 */
    private     String tag = UUID.randomUUID().toString();

    /** 基础信息错误信息时使用 */
    private     String    baseInfo;

    public String getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(String baseInfo) {
        this.baseInfo = baseInfo;
    }

    public String getTag() {
        return tag;
    }

    private TaskStatusEnum taskStatusEnum = TaskStatusEnum.NULL;

    /** 子集任务状态 */
    private TaskStatusEnum  sonTaskStatusEnum = TaskStatusEnum.NULL;

    /** 监听子集结果异步 */
    private  ApiSonTaskCallBack   sonTaskCallBack;

    public ApiSonTaskCallBack getSonTaskCallBack() {
        return sonTaskCallBack;
    }

    public void setSonTaskCallBack(ApiSonTaskCallBack sonTaskCallBack) {
        this.sonTaskCallBack = sonTaskCallBack;
    }

    public TaskStatusEnum getSonTaskStatusEnum() {
        return sonTaskStatusEnum;
    }

    public void setSonTaskStatusEnum(TaskStatusEnum sonTaskStatusEnum) {
        this.sonTaskStatusEnum = sonTaskStatusEnum;
    }

    public TaskStatusEnum getTaskStatusEnum() {
        return taskStatusEnum;
    }

    public void setTaskStatusEnum(TaskStatusEnum taskStatusEnum) {
        this.taskStatusEnum = taskStatusEnum;
    }

    /** 子任务只需要依次执行与当前任务在同一线程 */
    private LinkedBlockingQueue<AbsTaskRunner> queueTasks = new LinkedBlockingQueue<>();


    public LinkedBlockingQueue<AbsTaskRunner> getQueueTasks() {
        return queueTasks;
    }

    public   void    addTask(AbsTaskRunner   taskRunner){
        queueTasks.offer(taskRunner);
    }

    public   void   addTasks(List<AbsTaskRunner>   taskRunners){
        queueTasks.addAll(taskRunners);
    }

    private  ITaskResultCallBack     callBack;

    public ITaskResultCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(ITaskResultCallBack callBack) {
        this.callBack = callBack;
    }

    /** 错误信息描述 */
    private     String    errorMsg;


    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {

        this.errorMsg = errorMsg;
        if(!StringUtils.isEmpty(baseInfo)){
            this.errorMsg = baseInfo+" "+errorMsg;
        }
    }

    public   void    TaskDoing(){
        taskStatusEnum = TaskStatusEnum.DOING;
    }

    public   void    TaskError(){
        taskStatusEnum = TaskStatusEnum.ERROR;
        onComplete();
        if(null != callBack){
            callBack.onCallBack(taskStatusEnum,errorMsg);
        }
        if(null != sonTaskCallBack){
            sonTaskCallBack.call(taskStatusEnum,this);
        }

    }

    public   void    TaskOnNetError(){
        taskStatusEnum = TaskStatusEnum.NETERROR;
        if(TextUtils.isEmpty(errorMsg)){
            errorMsg ="网络错误，请稍候再试";
        }

        if(null != callBack){
            callBack.onCallBack(taskStatusEnum,errorMsg);
        }
        if(null != sonTaskCallBack){
            sonTaskCallBack.call(taskStatusEnum,this);
        }
    }

    public  void   TaskOnDoing(){
        if(taskStatusEnum != TaskStatusEnum.CANCEL){
            taskStatusEnum = TaskStatusEnum.DOING;
        }

    }

    /**
     * 关闭整 个任务管理器
     */
    public  void   closeTaskManager(){
        if(null != callBack){
            callBack.onCancelAllTask(TaskManagerStatusEnum.CANCEL,getErrorMsg());
        }
    }

    /**
     * 完成整个任务管理器
     */
    public  void   completeTaskManager(){
        if(null != callBack){
            callBack.onCancelAllTask(TaskManagerStatusEnum.COMPLETE,getErrorMsg());
        }
    }

    public  void  TaskOnSuccess(){
        onComplete();
        taskStatusEnum = TaskStatusEnum.SUCCESS;
        if(null != callBack){
            callBack.onCallBack(taskStatusEnum,errorMsg);
        }
        if(null != sonTaskCallBack){
            sonTaskCallBack.call(taskStatusEnum,this);
        }

    }

    /**
     * 取消任务
     */
    protected        void onCancel(){
      for(AbsTaskRunner   taskRunner : queueTasks){
          taskRunner.onCancel();
      }
        queueTasks.clear();
        if(taskStatusEnum == TaskStatusEnum.NULL || taskStatusEnum == TaskStatusEnum.WAITING
                || taskStatusEnum == TaskStatusEnum.DOING){
            taskStatusEnum = TaskStatusEnum.CANCEL;
        }


    }

  /**
   * 任务执行前处理
    * @return TaskStatusEnum
   */
  public  abstract   TaskStatusEnum  onBefore();

    /** 执行当前任务 */
  public  abstract  void    onToDo();

    /**
     * 任务完成onComplete
     */
  protected   void    onComplete(){

  }


    private      void   doBackground(){
        try {
            TaskOnDoing();
            TaskStatusEnum  temp  = onBefore();
            if(taskStatusEnum == TaskStatusEnum.SUCCESS){
                return;
            }
            if(temp == TaskStatusEnum.SUCCESS){

                doSonTaskBackground();
            }
        }catch (Exception e){
            e.printStackTrace();
            setErrorMsg("出错了"+e.getMessage());
            TaskError();
        }


    }



    @Override
    public void run() {

       if(taskStatusEnum == TaskStatusEnum.CANCEL){
           return;
       }
        try {
            doBackground();
        }catch (Exception e){
           e.printStackTrace();
           onExceptionBack(e);
        }

    }


    private     void     doSonTaskBackground(){
        if( queueTasks.size() == 0 ){
            sonTaskStatusEnum = TaskStatusEnum.SUCCESS;
            //当子任务执行完
            onToDo();
            return;
        }
        if(taskStatusEnum == TaskStatusEnum.CANCEL){
            sonTaskStatusEnum = TaskStatusEnum.CANCEL;
           return;
        }

        //执行子任务
        AbsTaskRunner   absTaskRunner = queueTasks.poll();
        if(null != absTaskRunner && absTaskRunner.taskStatusEnum != TaskStatusEnum.CANCEL){
            absTaskRunner.setSonTaskCallBack(this);
            absTaskRunner.doBackground();
        }

    }

    @Override public void call(TaskStatusEnum taskStatusEnum, AbsTaskRunner taskRunner) {
        setSonTaskStatusEnum(taskStatusEnum);
        setErrorMsg(taskRunner.getErrorMsg());
        if(taskStatusEnum == TaskStatusEnum.SUCCESS){
            doSonTaskBackground();
        }else if(taskStatusEnum == TaskStatusEnum.ERROR ||
            taskStatusEnum == TaskStatusEnum.WAITING){
            TaskError();
        }else if(taskStatusEnum == TaskStatusEnum.NETERROR){
            TaskOnNetError();
        }

    }

    public   void   onExceptionBack(Throwable  throwable){
        if (throwable instanceof SocketTimeoutException) {
            setErrorMsg("连接服务器超时，请稍候再试!");
            TaskOnNetError();
        } else if (throwable instanceof ConnectException) {
            setErrorMsg("网络连接错误，请检查网络是否正常或稍候重试!");
            TaskOnNetError();
        } else if (throwable instanceof UnknownHostException) {
            setErrorMsg("连接服务器失败，请检查连接地址是否正确或服务器是否正常开启！");
            TaskOnNetError();
        } else if(throwable instanceof HttpException){

            setErrorMsg("连接服务器失败！"+throwable.getMessage());
            TaskOnNetError();
        }
        else {
            setErrorMsg("出错了"+throwable.getMessage());
            TaskError();
        }
    }

}
