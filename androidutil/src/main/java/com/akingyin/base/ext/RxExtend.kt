package com.akingyin.base.ext

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import autodispose2.ObservableSubscribeProxy
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import autodispose2.autoDispose
import com.akingyin.base.net.utils.RxSchedulers
import io.reactivex.rxjava3.core.Observable


/**
 * @ Description:
 * @author king
 * @ Date 2019/12/30 12:34
 * @version V1.0
 */


/*
* 对Observable进行线程调度和生命周期绑定
*
* */
fun <T> Observable<T>.transform(owner: LifecycleOwner): ObservableSubscribeProxy<T> {
    return this.compose(RxSchedulers.IO_Main()).autoDispose(AndroidLifecycleScopeProvider.from(owner, Lifecycle.Event.ON_DESTROY))
}
