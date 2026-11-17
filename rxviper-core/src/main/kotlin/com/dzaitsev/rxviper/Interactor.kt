// Copyright 2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0.

package com.dzaitsev.rxviper

/**
 * Backend-neutral invocation contract. [Result] can be a value or a backend's native stream.
 * Scheduling and resource ownership belong to the implementation and its caller.
 */
fun interface Interactor<in Request, out Result> {
    operator fun invoke(request: Request): Result
}
