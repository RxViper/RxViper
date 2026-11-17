// Copyright 2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0.

package com.dzaitsev.rxviper.coroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

/** A streaming use case. Every collection creates a source in the injected dispatcher. */
abstract class FlowInteractor<in Request, out Response>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    protected abstract fun createFlow(request: Request): Flow<Response>

    /** Cold Flow: both source creation and upstream emissions run in [dispatcher]. */
    operator fun invoke(request: Request): Flow<Response> = flow {
        emitAll(createFlow(request))
    }.flowOn(dispatcher)

    /** Collection belongs to [scope]; downstream callback errors propagate to its exception handler. */
    fun execute(
        scope: CoroutineScope,
        request: Request,
        onNext: (Response) -> Unit,
        onError: (Throwable) -> Unit,
        onComplete: () -> Unit = {},
    ): Job = scope.launch {
        var failed = false
        invoke(request).catch { failure ->
            if (failure is CancellationException) throw failure
            failed = true
            onError(failure)
        }.collect { onNext(it) }
        currentCoroutineContext().ensureActive()
        if (!failed) onComplete()
    }
}
