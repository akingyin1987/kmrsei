package com.akingyin.base.taskmanager;

import com.akingyin.base.taskmanager.enums.TaskManagerStatusEnum;
import com.akingyin.base.taskmanager.enums.ThreadTypeEnum;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/12/19 18:48
 */
public class QueueTaskManagerTest {


  @ClassRule
  public static final RxSchedulersOverrideRule  mRxSchedulersOverrideRule = new RxSchedulersOverrideRule();


    @Test
    public  void   testAddTask() throws InterruptedException {
        MultiTaskManager  taskManager = MultiTaskManager.createPool(5);
        taskManager.setThreadTypeEnum(ThreadTypeEnum.CurrentThread);
      final Semaphore semaphore = new Semaphore(1);


      CountDownLatch latch = new CountDownLatch(100);
        taskManager.setCallBack(new ApiTaskCallBack() {
          @Override public void onCallBack(int total, int progress, int error) {
            System.out.println("onCallBack--->"+total+":"+progress+":"+error);
          }

          @Override public void onComplete() {
            System.out.println("onComplete运行完了");


          }

          @Override public void onError(String message, TaskManagerStatusEnum statusEnum) {
            System.out.println("onError运行完了");


          }
        });
        for (int i=0;i<2000;i++){

          taskManager.addTask(new TestTask(i,null,null));
        }
        taskManager.executeTask();
        Thread.sleep(50000);

    }

    public   void  resolve(int  k){
      System.out.println("doing--->"+k);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

}