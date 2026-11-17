// Copyright 2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0.

package com.dzaitsev.rxviper.rxjava3

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subscribers.DisposableSubscriber
import io.reactivex.rxjava3.subscribers.SafeSubscriber

/**
 * A reusable, scheduler-aware use case. Each invocation creates a fresh cold source.
 *
 * [clear] cancels managed executions and allows reuse; [dispose] permanently closes execution.
 * Sources returned by [invoke] have caller-owned subscriptions, independent of this container.
 * Schedulers are injected: Android applications can pass their main-thread scheduler explicitly.
 */
abstract class Interactor<Request, Response : Any>(
    private val subscribeScheduler: Scheduler,
    private val observeScheduler: Scheduler,
) : Disposable {
    private val executions = CompositeDisposable()

    protected abstract fun createFlowable(request: Request): Flowable<Response>

    /** Source construction failures become onError signals on the subscription scheduler. */
    operator fun invoke(request: Request): Flowable<Response> =
        Flowable.defer { createFlowable(request) }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)

    /** Returns a handle that cancels only this execution. Error handling is explicit. */
    fun execute(
        request: Request,
        onNext: (Response) -> Unit,
        onError: (Throwable) -> Unit,
        onComplete: () -> Unit = {},
    ): Disposable {
        check(!isDisposed) { "This interactor has been disposed" }
        // Register the subscriber before subscribing: clear/dispose must also work from a
        // synchronous source factory or the very first callback, before subscribe returns.
        val subscriber = object : DisposableSubscriber<Response>() {
            override fun onNext(value: Response) { onNext.invoke(value) }
            override fun onError(failure: Throwable) { onError.invoke(failure) }
            override fun onComplete() { onComplete.invoke() }
        }
        if (executions.add(subscriber)) {
            invoke(request).doFinally {
                executions.delete(subscriber)
                subscriber.dispose()
            }.subscribe(SafeSubscriber(subscriber))
        }
        return subscriber
    }

    fun clear() { executions.clear() }

    final override fun dispose() { executions.dispose() }

    final override fun isDisposed(): Boolean = executions.isDisposed
}
