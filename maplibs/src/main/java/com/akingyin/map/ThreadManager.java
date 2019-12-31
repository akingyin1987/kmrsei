package com.akingyin.map;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池管理器
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/12/30 16:06
 */
public class ThreadManager {

  private volatile static ExecutorService singleton;

  public static ExecutorService createPool(int poolSize) {
    if (singleton == null) {
      synchronized (ThreadManager.class) {
        if (singleton == null) {
          singleton = Executors.newFixedThreadPool(poolSize);
        }
      }
    }
    return singleton;
  }



  public static void  shutdown(){
    if(null != singleton){
      singleton.shutdown();
    }
    singleton = null;
  }


  private ThreadManager() {

  }
}
