// Copyright 2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0.

package com.dzaitsev.rxviper.coroutines

import com.dzaitsev.rxviper.SuspendInteractor as SuspendInteractorContract
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

/**
 * Launches this interactor in [scope] and delivers callbacks in that scope's context.
 * Cancellation is never reported as an ordinary error; callback failures propagate to the scope.
 */
fun <Request, Response> SuspendInteractorContract<Request, Response>.execute(
    scope: CoroutineScope,
    request: Request,
    onSuccess: (Response) -> Unit,
    onError: (Throwable) -> Unit,
): Job = scope.launch {
    val result = try {
        this@execute(request)
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (failure: Exception) {
        onError(failure)
        return@launch
    }
    currentCoroutineContext().ensureActive()
    onSuccess(result)
}
