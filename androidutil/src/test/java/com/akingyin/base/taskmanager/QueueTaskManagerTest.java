package com.akingyin.base.taskmanager;

import com.akingyin.base.taskmanager.enums.TaskStatusEnum;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import org.junit.Test;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/12/19 18:48
 */
public class QueueTaskManagerTest {


    @Test
    public  void   testAddTask(){
        MultiTaskManager  taskManager = MultiTaskManager.createPool(2);
      final Semaphore semaphore = new Semaphore(200);
      CountDownLatch latch = new CountDownLatch(1024);
        for (int i=0;i<1025;i++){
          int finalI = i;
          taskManager.addTask(new AbsTaskRunner() {
            @Override public TaskStatusEnum onBefore() {
              return TaskStatusEnum.SUCCESS;
            }

            @Override public void onToDo() {
              System.out.println("----iii------"+ finalI);
              latch.countDown();
            }
          });
        }
      try {
        latch.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("运行完了");
    }

}