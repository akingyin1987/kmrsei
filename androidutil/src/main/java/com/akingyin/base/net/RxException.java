/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.base.net;

import io.reactivex.rxjava3.functions.Consumer;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import timber.log.Timber;

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
      Timber.e("onError: SocketTimeoutException----%s", SOCKETTIMEOUTEXCEPTION);
     // ToastUtils.show(SOCKETTIMEOUTEXCEPTION);
      onError.accept(new Throwable(SOCKETTIMEOUTEXCEPTION));
    } else if (t instanceof ConnectException) {
      Timber.e("onError: ConnectException-----%s", CONNECTEXCEPTION);
     // ToastUtils.show(CONNECTEXCEPTION);
      onError.accept(new Throwable(CONNECTEXCEPTION));
    } else if (t instanceof UnknownHostException) {
      Timber.e("onError: UnknownHostException-----%s", UNKNOWNHOSTEXCEPTION);
     // ToastUtils.show(UNKNOWNHOSTEXCEPTION);
      onError.accept(new Throwable(UNKNOWNHOSTEXCEPTION));
    } else {
      Timber.e("onError:----%s", t.getMessage());
      onError.accept(t);
    }
  }
}
