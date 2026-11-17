// Copyright 2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0.

package com.dzaitsev.rxviper.coroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

/** A single-result use case. Work inherits the caller's lifetime; this class owns no scope. */
abstract class SuspendInteractor<in Request, out Response>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    protected abstract suspend fun run(request: Request): Response

    /** Dispatches work, propagating failures and cancellation to the caller. */
    suspend operator fun invoke(request: Request): Response = withContext(dispatcher) { run(request) }

    /** Callbacks run on [scope]'s context. Cancellation is never reported as an ordinary error. */
    fun execute(
        scope: CoroutineScope,
        request: Request,
        onSuccess: (Response) -> Unit,
        onError: (Throwable) -> Unit,
    ): Job = scope.launch {
        val result = try {
            invoke(request)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (failure: Exception) {
            onError(failure)
            return@launch
        }
        currentCoroutineContext().ensureActive()
        onSuccess(result)
    }
}
