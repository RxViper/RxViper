// Copyright 2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0.
@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.dzaitsev.rxviper.coroutines

import kotlin.coroutines.ContinuationInterceptor
import kotlin.test.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*

class InteractorTest {
    // Coroutine stacktrace recovery may copy an exception; its type/message/cause retain the failure.
    private fun assertFailure(expected: Throwable, actual: Throwable?) {
        assertNotNull(actual)
        assertEquals(expected::class, actual::class)
        assertEquals(expected.message, actual.message)
        assertTrue(generateSequence(actual) { it.cause }.any { it === expected })
    }

    @Test fun `suspend work uses injected dispatcher and returns result to caller`() = runTest {
        val worker = StandardTestDispatcher(testScheduler, "worker")
        val caller = StandardTestDispatcher(testScheduler, "caller")
        val interactor = object : SuspendInteractor<Int, Int>(worker) {
            override suspend fun run(request: Int): Int {
                assertSame(worker, currentCoroutineContext()[ContinuationInterceptor])
                delay(10)
                return request * 2
            }
        }
        val results = mutableListOf<Int>()
        val scope = CoroutineScope(coroutineContext + caller)
        val job = interactor.execute(scope, 21, {
            results += it
        }, { fail("Unexpected failure", it) })
        assertTrue(results.isEmpty())
        advanceUntilIdle()
        assertEquals(listOf(42), results)
        assertTrue(job.isCompleted)
        assertFalse(job.isCancelled)
    }

    @Test fun `suspend failures propagate through invoke and execute routes ordinary errors`() = runTest {
        val failure = IllegalStateException("source")
        val interactor = object : SuspendInteractor<Unit, Int>(StandardTestDispatcher(testScheduler)) {
            override suspend fun run(request: Unit): Int = throw failure
        }
        assertFailure(failure, assertFailsWith<IllegalStateException> { interactor(Unit) })
        var observed: Throwable? = null
        val job = interactor.execute(this, Unit, { fail("Must not succeed") }, { observed = it })
        advanceUntilIdle()
        assertFailure(failure, observed)
        assertFalse(job.isCancelled)
    }

    @Test fun `cancelling execution leaves sibling and caller scope alive`() = runTest {
        var finalized = 0
        val interactor = object : SuspendInteractor<Int, Int>(StandardTestDispatcher(testScheduler)) {
            override suspend fun run(request: Int): Int = try {
                delay(100)
                request
            } finally { finalized++ }
        }
        val values = mutableListOf<Int>()
        val first = interactor.execute(this, 1, { values += it }, { fail("Cancellation is not error") })
        val second = interactor.execute(this, 2, { values += it }, { fail("Unexpected failure") })
        runCurrent()
        first.cancel()
        advanceUntilIdle()
        assertTrue(first.isCancelled)
        assertFalse(second.isCancelled)
        assertTrue(isActive)
        assertEquals(listOf(2), values)
        assertEquals(2, finalized)
    }

    @Test fun `parent scope cancellation cancels work and never invokes error callback`() = runTest {
        val parent = SupervisorJob(coroutineContext[Job])
        val scope = CoroutineScope(coroutineContext + parent)
        var cleaned = false
        val interactor = object : SuspendInteractor<Unit, Unit>(StandardTestDispatcher(testScheduler)) {
            override suspend fun run(request: Unit) {
                try { awaitCancellation() } finally { cleaned = true }
            }
        }
        val job = interactor.execute(scope, Unit, { fail("No result") }, { fail("Cancellation is not error") })
        runCurrent()
        parent.cancel()
        advanceUntilIdle()
        assertTrue(job.isCancelled)
        assertTrue(cleaned)
    }

    @Test fun `success callback exceptions propagate without entering source error handler`() = runTest {
        val failure = IllegalStateException("callback")
        val errors = mutableListOf<Throwable>()
        val parent = SupervisorJob()
        val scope = CoroutineScope(StandardTestDispatcher(testScheduler) + parent + CoroutineExceptionHandler { _, t -> errors += t })
        try {
            val interactor = object : SuspendInteractor<Unit, Int>(StandardTestDispatcher(testScheduler)) {
                override suspend fun run(request: Unit) = 1
            }
            val job = interactor.execute(scope, Unit, { throw failure }, { fail("Callback is not source") })
            advanceUntilIdle()
            assertTrue(job.isCancelled)
            assertEquals(1, errors.size)
            assertFailure(failure, errors.single())
        } finally { parent.cancel() }
    }

    @Test fun `flow is cold and construction runs in worker context for each collection`() = runTest {
        val worker = StandardTestDispatcher(testScheduler, "worker")
        var constructions = 0
        val interactor = object : FlowInteractor<Int, Int>(worker) {
            override fun createFlow(request: Int): Flow<Int> {
                constructions++
                return flow {
                    assertSame(worker, currentCoroutineContext()[ContinuationInterceptor])
                    emit(request)
                    emit(request + 1)
                }
            }
        }
        val source = interactor(10)
        assertEquals(0, constructions)
        assertEquals(listOf(10, 11), source.toList())
        assertEquals(listOf(10, 11), source.toList())
        assertEquals(2, constructions)
    }

    @Test fun `flow executes values and completes exactly once`() = runTest {
        val interactor = object : FlowInteractor<Unit, Int>(StandardTestDispatcher(testScheduler)) {
            override fun createFlow(request: Unit) = flowOf(1, 2)
        }
        val events = mutableListOf<String>()
        val job = interactor.execute(this, Unit, { events += "value:$it" }, { fail("Unexpected", it) }, { events += "complete" })
        advanceUntilIdle()
        assertEquals(listOf("value:1", "value:2", "complete"), events)
        assertFalse(job.isCancelled)
    }

    @Test fun `flow construction and emission failures use error without completion`() = runTest {
        val failure = IllegalArgumentException("source")
        for (duringConstruction in listOf(true, false)) {
            val interactor = object : FlowInteractor<Unit, Int>(StandardTestDispatcher(testScheduler)) {
                override fun createFlow(request: Unit): Flow<Int> {
                    if (duringConstruction) throw failure
                    return flow { throw failure }
                }
            }
            val errors = mutableListOf<Throwable>()
            val job = interactor.execute(this, Unit, { fail("No value") }, { errors += it }, { fail("Error is not completion") })
            advanceUntilIdle()
            assertEquals(1, errors.size)
            assertFailure(failure, errors.single())
            assertFalse(job.isCancelled)
            assertFailure(failure, assertFailsWith<IllegalArgumentException> { interactor(Unit).toList() })
        }
    }

    @Test fun `flow cancellation cleans upstream and does not complete or report error`() = runTest {
        var cleaned = false
        val interactor = object : FlowInteractor<Unit, Int>(StandardTestDispatcher(testScheduler)) {
            override fun createFlow(request: Unit) = flow {
                try { emit(1); awaitCancellation() } finally { cleaned = true }
            }
        }
        val values = mutableListOf<Int>()
        val job = interactor.execute(this, Unit, { values += it }, { fail("Cancellation is not error") }, { fail("Cancellation is not completion") })
        runCurrent()
        assertEquals(listOf(1), values)
        job.cancel()
        advanceUntilIdle()
        assertTrue(cleaned)
        assertTrue(isActive)
    }

    @Test fun `flow downstream failure is not caught as upstream error`() = runTest {
        val failure = IllegalStateException("downstream")
        val errors = mutableListOf<Throwable>()
        val parent = SupervisorJob()
        val scope = CoroutineScope(StandardTestDispatcher(testScheduler) + parent + CoroutineExceptionHandler { _, t -> errors += t })
        try {
            val interactor = object : FlowInteractor<Unit, Int>(StandardTestDispatcher(testScheduler)) {
                override fun createFlow(request: Unit) = flowOf(1)
            }
            interactor.execute(scope, Unit, { throw failure }, { fail("Downstream failure") }, { fail("No completion") })
            advanceUntilIdle()
            assertEquals(1, errors.size)
            assertFailure(failure, errors.single())
        } finally { parent.cancel() }
    }
    @Test fun `cancelling from last flow callback prevents completion callback`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val parent = SupervisorJob(coroutineContext[Job])
        val scope = CoroutineScope(coroutineContext + dispatcher + parent)
        try {
            val interactor = object : FlowInteractor<Unit, Int>(dispatcher) {
                override fun createFlow(request: Unit) = flowOf(1)
            }
            val values = mutableListOf<Int>()
            var completions = 0
            lateinit var job: Job
            job = interactor.execute(scope, Unit, {
                values += it
                job.cancel()
            }, { fail("Cancellation is not error") }, { completions++ })
            advanceUntilIdle()
            assertEquals(listOf(1), values)
            assertTrue(job.isCancelled)
            assertEquals(0, completions)
        } finally { parent.cancel() }
    }

}
