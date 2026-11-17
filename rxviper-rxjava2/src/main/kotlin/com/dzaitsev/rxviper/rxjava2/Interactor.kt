// Copyright 2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0.

package com.dzaitsev.rxviper.rxjava2

import com.dzaitsev.rxviper.Interactor as InteractorContract
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.DisposableSubscriber
import io.reactivex.subscribers.SafeSubscriber

/**
 * A reusable, scheduler-aware interactor. Each invocation creates a fresh cold source.
 *
 * [clear] cancels managed executions and allows reuse; [dispose] permanently closes execution.
 * Sources returned by [invoke] have caller-owned subscriptions, independent of this container.
 * Schedulers are injected: Android applications can pass their main-thread scheduler explicitly.
 */
abstract class Interactor<Request, Response : Any>(
    private val subscribeScheduler: Scheduler,
    private val observeScheduler: Scheduler,
) : InteractorContract<Request, Flowable<Response>>, Disposable {
    private val executions = CompositeDisposable()

    protected abstract fun createFlowable(request: Request): Flowable<Response>

    /** Source construction failures become onError signals on the subscription scheduler. */
    final override operator fun invoke(request: Request): Flowable<Response> =
        Flowable.defer { createFlowable(request) }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)

    /** Registers ownership before subscription so synchronous cancellation is effective. */
    internal fun subscribeManaged(request: Request, subscriber: DisposableSubscriber<Response>): Disposable {
        check(!isDisposed) { "This interactor has been disposed" }
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
