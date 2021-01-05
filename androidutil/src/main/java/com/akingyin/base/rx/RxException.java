package com.akingyin.base.rx;

import android.util.Log;

import io.reactivex.rxjava3.functions.Consumer;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * @ Description:
 * @ Author king
 * @ Date 2017/2/28 17:54
 * @ Version V1.0
 */

public class RxException <T extends Throwable> implements Consumer<T> {
  private static final String TAG = "RxException";

  private static final String SOCKETTIMEOUTEXCEPTION = "网络连接超时，请检查您的网络状态，稍后重试";
  private static final String CONNECTEXCEPTION = "网络连接异常，请检查您的网络状态";
  private static final String UNKNOWNHOSTEXCEPTION = "网络异常，请检查您的网络状态";

  private Consumer<? super Throwable> onError;
  public RxException(Consumer<? super Throwable> onError) {
    this.onError=onError;

  }
  @Override public void accept(T t) throws Throwable {
    if (t instanceof SocketTimeoutException) {

      try {
        onError.accept(new SocketTimeoutException(SOCKETTIMEOUTEXCEPTION));
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    } else if (t instanceof ConnectException) {
      try {
        onError.accept(new ConnectException(CONNECTEXCEPTION));
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    } else if (t instanceof UnknownHostException) {

      try {
        onError.accept(new UnknownHostException(UNKNOWNHOSTEXCEPTION));
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    } else {

      try {
        onError.accept(t);
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }
  }
}
