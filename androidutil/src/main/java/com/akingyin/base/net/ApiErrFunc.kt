/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.base.net

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Function
import java.lang.Exception

/**
 * Throwable转Observable<T>
 * Created by Administrator on 2017/9/12.
</T> */
class ApiErrFunc<T> : Function<Throwable?, Observable<T>?> {
    @Throws(Exception::class)
    override fun apply(throwable: Throwable?): Observable<T> {
        return Observable.error(throwable)
    }
}