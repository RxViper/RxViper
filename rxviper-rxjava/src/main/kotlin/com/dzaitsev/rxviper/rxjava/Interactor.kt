// Copyright 2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0.

package com.dzaitsev.rxviper.rxjava

import rx.Observable
import rx.Scheduler
import rx.Subscription
import rx.Subscriber
import rx.subscriptions.CompositeSubscription

/**
 * RxJava 1 use case with injected scheduling. [clear] cancels current executions and allows reuse;
 * [unsubscribe] permanently closes execution. Subscriptions to [invoke] are caller-owned.
 */
abstract class Interactor<Request, Response : Any>(
    private val subscribeScheduler: Scheduler,
    private val observeScheduler: Scheduler,
) : Subscription {
    private val executions = CompositeSubscription()

    protected abstract fun createObservable(request: Request): Observable<Response>

    operator fun invoke(request: Request): Observable<Response> =
        Observable.defer { createObservable(request) }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)

    fun execute(
        request: Request,
        onNext: (Response) -> Unit,
        onError: (Throwable) -> Unit,
        onComplete: () -> Unit = {},
    ): Subscription {
        check(!isUnsubscribed) { "This interactor has been unsubscribed" }
        val subscriber = object : Subscriber<Response>() {
            override fun onNext(value: Response) { onNext.invoke(value) }
            override fun onError(failure: Throwable) { onError.invoke(failure) }
            override fun onCompleted() { onComplete.invoke() }
        }
        executions.add(subscriber)
        if (!subscriber.isUnsubscribed) {
            invoke(request)
                .doOnUnsubscribe { executions.remove(subscriber) }
                .subscribe(subscriber)
        }
        return subscriber
    }

    fun clear() { executions.clear() }

    final override fun unsubscribe() { executions.unsubscribe() }

    final override fun isUnsubscribed(): Boolean = executions.isUnsubscribed
}
