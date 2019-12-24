package com.akingyin.base.taskmanager;

import com.akingyin.base.taskmanager.enums.TaskStatusEnum;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/12/20 11:25
 */
public class TestTask  extends   AbsTaskRunner {

  private    int     index;

  private Semaphore   mSemaphore;

  private CountDownLatch  mCountDownLatch;

  public TestTask(int index, Semaphore semaphore, CountDownLatch countDownLatch) {
    this.index = index;
    mSemaphore = semaphore;
    mCountDownLatch = countDownLatch;
  }

  @Override public TaskStatusEnum onBefore() {
    System.out.println("onBefore="+index);

    return TaskStatusEnum.SUCCESS;
  }

  @Override public void onToDo() {
    System.out.println("onTodo");
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("--------onToDo-----"+index);

    TaskOnSuccess();
  }
}
