package com.akingyin.base.taskmanager;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/12/20 11:03
 */
public class RxSchedulersOverrideRule implements TestRule {

  private Scheduler immediate = new Scheduler() {

    @Override
    public Disposable scheduleDirect(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
      return super.scheduleDirect(run, 0, unit);
    }

    @Override
    public Worker createWorker() {
      return new ExecutorScheduler.ExecutorWorker(new Executor() {
        @Override
        public void execute(@NonNull Runnable command) {
          command.run();
        }
      },false);
    }
  };

  @Override
  public Statement apply(final Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        RxJavaPlugins.setInitIoSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>() {
          @Override
          public Scheduler apply(@NonNull Callable<Scheduler> schedulerCallable) throws Exception {
            return immediate;
          }
        });
        RxJavaPlugins.setInitComputationSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>() {
          @Override
          public Scheduler apply(@NonNull Callable<Scheduler> schedulerCallable) throws Exception {
            return immediate;
          }
        });
        RxJavaPlugins.setInitNewThreadSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>() {
          @Override
          public Scheduler apply(@NonNull Callable<Scheduler> schedulerCallable) throws Exception {
            return immediate;
          }
        });
        RxJavaPlugins.setInitSingleSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>() {
          @Override
          public Scheduler apply(@NonNull Callable<Scheduler> schedulerCallable) throws Exception {
            return immediate;
          }
        });
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>() {
          @Override
          public Scheduler apply(@NonNull Callable<Scheduler> schedulerCallable) throws Exception {
            return immediate;
          }
        });

        try {
          base.evaluate();
        }finally {
          RxJavaPlugins.reset();
          RxAndroidPlugins.reset();
        }
      }
    };
  }
}
