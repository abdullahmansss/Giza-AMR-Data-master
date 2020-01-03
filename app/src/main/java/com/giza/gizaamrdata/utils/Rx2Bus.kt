package com.giza.gizaamrdata.utils

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject


/**
 * @author hossam.
 */
object Rx2Bus {
    private val bus = PublishSubject.create<Any>()

    fun send(event: Any) {
        bus.onNext(event)
    }

    fun hasObservers(): Boolean {
        return bus.hasObservers()
    }

    fun <T> listen(eventType: Class<T>): Observable<T> = bus.ofType(eventType)

    fun removeListener(disposable: Disposable) {
        if (!disposable.isDisposed) disposable.dispose()
    }

}