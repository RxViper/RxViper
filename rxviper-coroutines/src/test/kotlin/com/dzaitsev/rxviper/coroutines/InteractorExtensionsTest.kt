// Copyright 2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0.
@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.dzaitsev.rxviper.coroutines

import com.dzaitsev.rxviper.SuspendInteractor as SuspendInteractorContract
import com.dzaitsev.rxviper.Interactor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

class InteractorExtensionsTest {
    @Test
    fun `suspend lambda execution belongs to caller and cancellation releases its work`() = runTest {
        var started: Int? = null
        var cleaned = false
        val interactor = SuspendInteractorContract<Int, String> { request ->
            started = request
            try { awaitCancellation() } finally { cleaned = true }
        }
        val job = interactor.execute(this, 7, { fail("Cancelled work must not succeed") }, { fail("Cancellation must not report an error") })
        assertEquals(null, started)
        runCurrent()
        assertEquals(7, started)
        job.cancel()
        advanceUntilIdle()
        assertTrue(job.isCancelled)
        assertTrue(cleaned)
        assertTrue(isActive)
    }

    @Test
    fun `flow lambda invocation is deferred and delivers values followed by completion`() = runTest {
        val requests = mutableListOf<Int>()
        val events = mutableListOf<String>()
        val interactor = Interactor<Int, Flow<Int>> { request ->
            requests += request
            flowOf(request, request + 1)
        }
        val job = interactor.execute(this, 3, { events += "value:$it" }, { fail("Unexpected error", it) }, { events += "complete" })
        assertTrue(requests.isEmpty())
        advanceUntilIdle()
        assertEquals(listOf(3), requests)
        assertEquals(listOf("value:3", "value:4", "complete"), events)
        assertFalse(job.isCancelled)
    }

    @Test
    fun `plain flow interactor factory failure reaches onError without completion`() = runTest {
        val failures = mutableListOf<Throwable>()
        val interactor = Interactor<Unit, Flow<Int>> { error("Factory failed") }
        val job = interactor.execute(this, Unit, { fail("No values expected") }, failures::add, { fail("Error is not completion") })
        advanceUntilIdle()
        assertEquals(1, failures.size)
        assertTrue(failures.single() is IllegalStateException)
        assertEquals("Factory failed", failures.single().message)
        assertFalse(job.isCancelled)
    }

    @Test
    fun `plain flow interactor factory cancellation bypasses error and completion`() = runTest {
        val interactor = Interactor<Unit, Flow<Int>> { throw CancellationException("Cancelled factory") }
        val job = interactor.execute(this, Unit, { fail("No values expected") }, { fail("Cancellation is not error") }, { fail("Cancellation is not completion") })
        advanceUntilIdle()
        assertTrue(job.isCancelled)
        assertTrue(isActive)
    }
}
