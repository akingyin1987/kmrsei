package com.akingyin.base.taskmanager;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Supplier;
import io.reactivex.rxjava3.internal.schedulers.ExecutorScheduler;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
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
      },false,false);
    }
  };

  @Override
  public Statement apply(final Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        RxJavaPlugins.setInitIoSchedulerHandler(new Function<Supplier<Scheduler>, Scheduler>() {
          @Override public Scheduler apply(Supplier<Scheduler> schedulerSupplier) throws Throwable {
            return immediate;
          }
        });
        RxJavaPlugins.setInitNewThreadSchedulerHandler(new Function<Supplier<Scheduler>, Scheduler>() {
          @Override public Scheduler apply(Supplier<Scheduler> schedulerSupplier) throws Throwable {
            return immediate;
          }
        });
        RxJavaPlugins.setInitSingleSchedulerHandler(new Function<Supplier<Scheduler>, Scheduler>() {
          @Override public Scheduler apply(Supplier<Scheduler> schedulerSupplier) throws Throwable {
            return immediate;
          }
        });

        RxAndroidPlugins.setInitMainThreadSchedulerHandler(new io.reactivex.rxjava3.functions.Function<Callable<io.reactivex.rxjava3.core.Scheduler>, io.reactivex.rxjava3.core.Scheduler>() {
          @Override public io.reactivex.rxjava3.core.Scheduler apply(
              Callable<io.reactivex.rxjava3.core.Scheduler> schedulerCallable) throws Throwable {
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
