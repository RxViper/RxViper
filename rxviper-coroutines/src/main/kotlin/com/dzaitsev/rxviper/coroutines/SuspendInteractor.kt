// Copyright 2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0.

package com.dzaitsev.rxviper.coroutines

import com.dzaitsev.rxviper.SuspendInteractor as SuspendInteractorContract
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** A single-result interactor. Work inherits the caller's lifetime; this class owns no scope. */
abstract class SuspendInteractor<in Request, out Response>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : SuspendInteractorContract<Request, Response> {
    protected abstract suspend fun run(request: Request): Response

    /** Dispatches work, propagating failures and cancellation to the caller. */
    final override suspend operator fun invoke(request: Request): Response =
        withContext(dispatcher) { run(request) }
}
