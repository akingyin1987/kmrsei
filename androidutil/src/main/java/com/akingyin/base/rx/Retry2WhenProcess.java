package com.akingyin.base.rx;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import timber.log.Timber;

/**
 *  rxjava2 重试机制
 * @ Description:
 * @ Author king
 * @ Date 2016/12/31 11:06
 * @ Version V1.0
 */

public class Retry2WhenProcess  implements Function<Observable<? extends Throwable>, Observable<?>> {
  private long mInterval;

  public Retry2WhenProcess(long mInterval) {
    this.mInterval = mInterval;
  }


  @Override public Observable<?> apply(Observable<? extends Throwable> observable)
      throws Exception {
    return observable.flatMap((Function<Throwable, ObservableSource<?>>) throwable -> {
      if(throwable  instanceof UnknownHostException){
        return  Observable.error(throwable);
      }
      return Observable.just(throwable).zipWith(Observable.range(1, 3),new BiFunction<Throwable, Integer, Integer>(){
        @Override public Integer apply(Throwable throwable, Integer integer) throws Exception {
          Timber.d("重试第" + integer + "次");
          return integer;
        }
      }).flatMap((Function<Integer, ObservableSource<Long>>) integer -> {
        Timber.d("间隔时间：" + Math.pow(mInterval, integer) + "秒");
       return   Observable.timer((long) Math.pow(mInterval, integer), TimeUnit.SECONDS);
      });
    });
  }
}
