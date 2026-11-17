// Copyright 2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0.

package com.dzaitsev.rxviper.coroutines

import com.dzaitsev.rxviper.Interactor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/** A streaming interactor. Every collection creates a source in the injected dispatcher. */
abstract class FlowInteractor<in Request, out Response>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : Interactor<Request, Flow<Response>> {
    protected abstract fun createFlow(request: Request): Flow<Response>

    /** Cold Flow: both source creation and upstream emissions run in [dispatcher]. */
    final override operator fun invoke(request: Request): Flow<Response> = flow {
        emitAll(createFlow(request))
    }.flowOn(dispatcher)
}
