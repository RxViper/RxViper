// Copyright 2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0.

package com.dzaitsev.rxviper

/**
 * A suspending interactor with a caller-owned lifetime. Implementations may switch execution context.
 * Suspension is a Kotlin language feature; this contract does not depend on kotlinx.coroutines.
 */
fun interface SuspendInteractor<in Request, out Result> {
    suspend operator fun invoke(request: Request): Result
}
