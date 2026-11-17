// Copyright 2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0.

package com.dzaitsev.rxviper.rxjava

import rx.Subscriber
import rx.Subscription

/**
 * Starts a managed execution; the returned handle cancels only this subscription.
 * [Interactor.clear] cancels all managed work and allows reuse. Error handling is explicit.
 */
fun <Request, Response : Any> Interactor<Request, Response>.execute(
    request: Request,
    onNext: (Response) -> Unit,
    onError: (Throwable) -> Unit,
    onComplete: () -> Unit = {},
): Subscription = subscribeManaged(request, object : Subscriber<Response>() {
    override fun onNext(value: Response) { onNext.invoke(value) }
    override fun onError(failure: Throwable) { onError.invoke(failure) }
    override fun onCompleted() { onComplete.invoke() }
})
