package com.akingyin.base.ext

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.akingyin.base.net.utils.RxSchedulers
import com.uber.autodispose.ObservableSubscribeProxy
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable

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
    return this.compose(RxSchedulers.IO_Main()).autoDisposable(AndroidLifecycleScopeProvider.from(owner, Lifecycle.Event.ON_DESTROY))
}
