// Copyright 2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0.

package com.dzaitsev.rxviper.coroutines

import com.dzaitsev.rxviper.Interactor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * Collects this interactor in [scope]. Source creation/collection failures go to [onError];
 * downstream callback failures propagate to the scope. Cancellation never calls [onComplete].
 */
fun <Request, Response> Interactor<Request, Flow<Response>>.execute(
    scope: CoroutineScope,
    request: Request,
    onNext: (Response) -> Unit,
    onError: (Throwable) -> Unit,
    onComplete: () -> Unit = {},
): Job = scope.launch {
    var failed = false
    // Defer invocation as well: a plain Interactor can throw before returning its Flow.
    flow { emitAll(this@execute(request)) }.catch { failure ->
        if (failure is CancellationException) throw failure
        failed = true
        onError(failure)
    }.collect { onNext(it) }
    currentCoroutineContext().ensureActive()
    if (!failed) onComplete()
}
