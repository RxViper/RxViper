// Copyright 2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0.

package com.dzaitsev.rxviper.rxjava

import com.dzaitsev.rxviper.Interactor as InteractorContract
import rx.Observable
import rx.Scheduler
import rx.Subscription
import rx.Subscriber
import rx.subscriptions.CompositeSubscription

/**
 * RxJava 1 interactor with injected scheduling. [clear] cancels current executions and allows reuse;
 * [unsubscribe] permanently closes execution. Subscriptions to [invoke] are caller-owned.
 */
abstract class Interactor<Request, Response : Any>(
    private val subscribeScheduler: Scheduler,
    private val observeScheduler: Scheduler,
) : InteractorContract<Request, Observable<Response>>, Subscription {
    private val executions = CompositeSubscription()

    protected abstract fun createObservable(request: Request): Observable<Response>

    final override operator fun invoke(request: Request): Observable<Response> =
        Observable.defer { createObservable(request) }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)

    /** Registers ownership before subscription so synchronous cancellation is effective. */
    internal fun subscribeManaged(request: Request, subscriber: Subscriber<Response>): Subscription {
        check(!isUnsubscribed) { "This interactor has been unsubscribed" }
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
