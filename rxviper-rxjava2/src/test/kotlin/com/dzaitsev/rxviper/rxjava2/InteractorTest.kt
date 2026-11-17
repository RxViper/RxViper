package com.dzaitsev.rxviper.rxjava2

import com.dzaitsev.rxviper.Interactor as InteractorContract

import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.subscribers.TestSubscriber
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import io.reactivex.processors.PublishProcessor

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class InteractorTest {
    private fun newInteractor(
        subscribe: Scheduler = Schedulers.trampoline(),
        observe: Scheduler = Schedulers.trampoline(),
        factory: (Int) -> Flowable<Int>,
    ): Interactor<Int, Int> = object : Interactor<Int, Int>(subscribe, observe) {
        override fun createFlowable(request: Int): Flowable<Int> = factory(request)
    }

    @Test
    fun `each subscription constructs a fresh source only when subscribed`() {
        val requests = mutableListOf<Int>()
        val interactor = newInteractor { request ->
            requests += request
            Flowable.just(request)
        }
        val source = interactor(7)
        assertTrue(requests.isEmpty())
        source.test().assertResult(7)
        source.test().assertResult(7)
        assertEquals(listOf(7, 7), requests)
    }

    @Test
    fun `source construction and delivery use independent injected schedulers`() {
        val subscribe = TestScheduler()
        val observe = TestScheduler()
        val events = mutableListOf<String>()
        val interactor = newInteractor(subscribe, observe) {
            events += "constructed:$it"
            Flowable.just(it)
        }
        val handle = interactor.execute(5, { events += "next:$it" }, { throw AssertionError(it) }) {
            events += "complete"
        }
        assertTrue(events.isEmpty())
        subscribe.triggerActions()
        assertEquals(listOf("constructed:5"), events)
        assertFalse(handle.isDisposed)
        observe.triggerActions()
        assertEquals(listOf("constructed:5", "next:5", "complete"), events)
        assertTrue(handle.isDisposed)
    }

    @Test
    fun `factory exceptions are delivered through the error callback`() {
        val expected = IllegalArgumentException("factory")
        val errors = mutableListOf<Throwable>()
        val interactor = newInteractor { throw expected }
        val handle = interactor.execute(0, { error("Unexpected value") }, errors::add)
        assertEquals(1, errors.size)
        assertSame(expected, errors.single())
        assertTrue(handle.isDisposed)
    }

    @Test
    fun `source errors are delivered through the error callback`() {
        val expected = IllegalStateException("source")
        val errors = mutableListOf<Throwable>()
        var completed = false
        val interactor = newInteractor { Flowable.error(expected) }
        val handle = interactor.execute(0, { error("Unexpected value") }, errors::add) {
            completed = true
        }
        assertSame(expected, errors.single())
        assertFalse(completed)
        assertTrue(handle.isDisposed)
    }

    @Test
    fun `synchronous completion closes its execution handle`() {
        val values = mutableListOf<Int>()
        var completions = 0
        val interactor = newInteractor { Flowable.just(it) }
        val handle = interactor.execute(9, values::add, { throw AssertionError(it) }) {
            completions++
        }
        assertEquals(listOf(9), values)
        assertEquals(1, completions)
        assertTrue(handle.isDisposed)
        assertFalse(interactor.isDisposed)
    }

    @Test
    fun `individual cancellation leaves sibling executions active`() {
        val source = PublishProcessor.create<Int>()
        val firstValues = mutableListOf<Int>()
        val secondValues = mutableListOf<Int>()
        val interactor = newInteractor { source }
        val first = interactor.execute(1, firstValues::add, { throw AssertionError(it) })
        val second = interactor.execute(2, secondValues::add, { throw AssertionError(it) })
        source.onNext(10)
        first.dispose()
        source.onNext(20)
        assertEquals(listOf(10), firstValues)
        assertEquals(listOf(10, 20), secondValues)
        assertTrue(first.isDisposed)
        assertFalse(second.isDisposed)
        source.onComplete()
        assertTrue(second.isDisposed)
    }

    @Test
    fun `clear cancels active work and permits another execution`() {
        val source = PublishProcessor.create<Int>()
        val values = mutableListOf<Int>()
        val interactor = newInteractor { source }
        val first = interactor.execute(1, values::add, { throw AssertionError(it) })
        interactor.clear()
        assertTrue(first.isDisposed)
        assertFalse(interactor.isDisposed)
        source.onNext(1)
        assertTrue(values.isEmpty())
        val second = interactor.execute(2, values::add, { throw AssertionError(it) })
        source.onNext(2)
        assertEquals(listOf(2), values)
        assertFalse(second.isDisposed)
        interactor.dispose()
        assertTrue(second.isDisposed)
    }

    @Test
    fun `terminal close cancels work and rejects subsequent managed execution`() {
        val source = PublishProcessor.create<Int>()
        val interactor = newInteractor { source }
        val handle = interactor.execute(0, {}, { throw AssertionError(it) })
        interactor.dispose()
        interactor.dispose()
        interactor.clear()
        assertTrue(interactor.isDisposed)
        assertTrue(handle.isDisposed)
        assertFailsWith<IllegalStateException> {
            interactor.execute(1, {}, { throw AssertionError(it) })
        }
    }

    @Test
    fun `cancellation before scheduler activation prevents source construction`() {
        val scheduler = TestScheduler()
        var constructions = 0
        val interactor = newInteractor(scheduler, scheduler) {
            constructions++
            Flowable.just(it)
        }
        val handle = interactor.execute(0, { error("Unexpected value") }, { throw AssertionError(it) })
        handle.dispose()
        scheduler.triggerActions()
        assertEquals(0, constructions)
        assertTrue(handle.isDisposed)
    }

    @Test
    fun `direct invocation is caller owned independently of managed lifecycle`() {
        val source = PublishProcessor.create<Int>()
        val interactor = newInteractor { source }
        val subscriber = TestSubscriber<Int>()
        val contract: InteractorContract<Int, Flowable<Int>> = interactor
        contract(1).subscribe(subscriber)
        interactor.clear()
        interactor.dispose()
        source.onNext(8)
        source.onComplete()
        subscriber.assertValues(8)
        subscriber.assertComplete()
        val next = TestSubscriber<Int>()
        newInteractor { Flowable.just(it) }.also { it.dispose() }(9).subscribe(next)
        next.assertValues(9)
        next.assertComplete()
    }

    @Test
    fun `direct flowable invocation preserves downstream backpressure`() {
        val interactor = newInteractor { Flowable.range(it, 3) }
        val subscriber = interactor(3).test(0)
        subscriber.assertNoValues().assertNotComplete()
        subscriber.request(1)
        subscriber.assertValues(3).assertNotComplete()
        subscriber.request(2)
        subscriber.assertResult(3, 4, 5)
    }

    @Test
    fun `clear inside source factory cancels before synchronous values and side effects`() {
        val values = mutableListOf<Int>()
        val sourceEffects = mutableListOf<Int>()
        var completions = 0
        lateinit var interactor: Interactor<Int, Int>
        interactor = newInteractor { request ->
            interactor.clear()
            Flowable.just(request).doOnNext { sourceEffects += it }
        }
        val handle = interactor.execute(7, values::add, { throw AssertionError(it) }) { completions++ }
        assertTrue(values.isEmpty())
        assertTrue(sourceEffects.isEmpty())
        assertEquals(0, completions)
        assertTrue(handle.isDisposed)
        assertFalse(interactor.isDisposed)
    }

    @Test
    fun `clear inside first synchronous callback stops later values and side effects`() {
        val values = mutableListOf<Int>()
        val sourceEffects = mutableListOf<Int>()
        var completions = 0
        val interactor = newInteractor { request ->
            Flowable.range(request, 3).doOnNext { sourceEffects += it }
        }
        val handle = interactor.execute(7, {
            values += it
            interactor.clear()
        }, { throw AssertionError(it) }) { completions++ }
        assertEquals(listOf(7), values)
        assertEquals(listOf(7), sourceEffects)
        assertEquals(0, completions)
        assertTrue(handle.isDisposed)
        assertFalse(interactor.isDisposed)
    }

}
