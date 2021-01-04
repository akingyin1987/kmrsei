package com.akingyin.base.rx

import io.reactivex.rxjava3.core.Observable

import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject

/**
 * Created by xiaguangcheng on 16/5/17.
 */
@Suppress("UNCHECKED_CAST")
class RxBus {
    private val _bus: Subject<Any> = PublishSubject.create<Any>().toSerialized()
    fun send(o: Any) {
        _bus.onNext(o)
    }

    fun toObservable(): Observable<Any> {
        return _bus
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     * @param eventType 事件类型
     * @param <T>
     * @return
    </T> */
    fun <T> toObservable(eventType: Class<T>): Observable<T> {
        return _bus.ofType(eventType)
    }

    /**
     * 提供了一个新的事件,根据code进行分发
     * @param code 事件code
     * @param o
     */
    fun post(code: Int, o: Any) {
        _bus.onNext(RxBusBaseMessage(code, o))
    }

    /**
     * 根据传递的code和 eventType 类型返回特定类型(eventType)的 被观察者
     * 对于注册了code为0，class为voidMessage的观察者，那么就接收不到code为0之外的voidMessage。
     * @param code 事件code
     * @param eventType 事件类型
     * @param <T>
     * @return
    </T> */
    fun <T> toObservable(code: Int, eventType: Class<T>): Observable<T> {
        return _bus.ofType(RxBusBaseMessage::class.java)
                .filter {
                    //过滤code和eventType都相同的事件
                    it.code == code && eventType.isInstance(it.getObject())
                }.map {
                    it.getObject() as T
                }.cast(eventType)
    }

    /**
     * 判断是否有订阅者
     */
    fun hasObservers(): Boolean {
        return _bus.hasObservers()
    }

    companion object {
        /**
         * 参考网址: http://hanhailong.com/2015/10/09/RxBus%E2%80%94%E9%80%9A%E8%BF%87RxJava%E6%9D%A5%E6%9B%BF%E6%8D%A2EventBus/
         * http://www.loongwind.com/archives/264.html
         * https://theseyears.gitbooks.io/android-architecture-journey/content/rxbus.html
         */
        @Volatile
        private var mDefaultInstance: RxBus? = null
        val default: RxBus?
            get() {
                if (mDefaultInstance == null) {
                    synchronized(RxBus::class.java) {
                        if (mDefaultInstance == null) {
                            mDefaultInstance = RxBus()
                        }
                    }
                }
                return mDefaultInstance
            }
    }
}