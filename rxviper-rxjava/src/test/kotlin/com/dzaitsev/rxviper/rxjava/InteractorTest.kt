package com.dzaitsev.rxviper.rxjava

import com.dzaitsev.rxviper.Interactor as InteractorContract

import rx.Observable
import rx.Scheduler
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers
import rx.schedulers.TestScheduler
import rx.subjects.PublishSubject

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
        factory: (Int) -> Observable<Int>,
    ): Interactor<Int, Int> = object : Interactor<Int, Int>(subscribe, observe) {
        override fun createObservable(request: Int): Observable<Int> = factory(request)
    }

    @Test
    fun `each subscription constructs a fresh source only when subscribed`() {
        val requests = mutableListOf<Int>()
        val interactor = newInteractor { request ->
            requests += request
            Observable.just(request)
        }
        val source = interactor(7)
        assertTrue(requests.isEmpty())
        val first = TestSubscriber<Int>()
        source.subscribe(first)
        first.assertValues(7)
        first.assertCompleted()
        val second = TestSubscriber<Int>()
        source.subscribe(second)
        second.assertValues(7)
        second.assertCompleted()
        assertEquals(listOf(7, 7), requests)
    }

    @Test
    fun `source construction and delivery use independent injected schedulers`() {
        val subscribe = TestScheduler()
        val observe = TestScheduler()
        val events = mutableListOf<String>()
        val interactor = newInteractor(subscribe, observe) {
            events += "constructed:$it"
            Observable.just(it)
        }
        val handle = interactor.execute(5, { events += "next:$it" }, { throw AssertionError(it) }) {
            events += "complete"
        }
        assertTrue(events.isEmpty())
        subscribe.triggerActions()
        assertEquals(listOf("constructed:5"), events)
        assertFalse(handle.isUnsubscribed)
        observe.triggerActions()
        assertEquals(listOf("constructed:5", "next:5", "complete"), events)
        assertTrue(handle.isUnsubscribed)
    }

    @Test
    fun `factory exceptions are delivered through the error callback`() {
        val expected = IllegalArgumentException("factory")
        val errors = mutableListOf<Throwable>()
        val interactor = newInteractor { throw expected }
        val handle = interactor.execute(0, { error("Unexpected value") }, errors::add)
        assertEquals(1, errors.size)
        assertSame(expected, errors.single())
        assertTrue(handle.isUnsubscribed)
    }

    @Test
    fun `source errors are delivered through the error callback`() {
        val expected = IllegalStateException("source")
        val errors = mutableListOf<Throwable>()
        var completed = false
        val interactor = newInteractor { Observable.error(expected) }
        val handle = interactor.execute(0, { error("Unexpected value") }, errors::add) {
            completed = true
        }
        assertSame(expected, errors.single())
        assertFalse(completed)
        assertTrue(handle.isUnsubscribed)
    }

    @Test
    fun `synchronous completion closes its execution handle`() {
        val values = mutableListOf<Int>()
        var completions = 0
        val interactor = newInteractor { Observable.just(it) }
        val handle = interactor.execute(9, values::add, { throw AssertionError(it) }) {
            completions++
        }
        assertEquals(listOf(9), values)
        assertEquals(1, completions)
        assertTrue(handle.isUnsubscribed)
        assertFalse(interactor.isUnsubscribed)
    }

    @Test
    fun `individual cancellation leaves sibling executions active`() {
        val source = PublishSubject.create<Int>()
        val firstValues = mutableListOf<Int>()
        val secondValues = mutableListOf<Int>()
        val interactor = newInteractor { source }
        val first = interactor.execute(1, firstValues::add, { throw AssertionError(it) })
        val second = interactor.execute(2, secondValues::add, { throw AssertionError(it) })
        source.onNext(10)
        first.unsubscribe()
        source.onNext(20)
        assertEquals(listOf(10), firstValues)
        assertEquals(listOf(10, 20), secondValues)
        assertTrue(first.isUnsubscribed)
        assertFalse(second.isUnsubscribed)
        source.onCompleted()
        assertTrue(second.isUnsubscribed)
    }

    @Test
    fun `clear cancels active work and permits another execution`() {
        val source = PublishSubject.create<Int>()
        val values = mutableListOf<Int>()
        val interactor = newInteractor { source }
        val first = interactor.execute(1, values::add, { throw AssertionError(it) })
        interactor.clear()
        assertTrue(first.isUnsubscribed)
        assertFalse(interactor.isUnsubscribed)
        source.onNext(1)
        assertTrue(values.isEmpty())
        val second = interactor.execute(2, values::add, { throw AssertionError(it) })
        source.onNext(2)
        assertEquals(listOf(2), values)
        assertFalse(second.isUnsubscribed)
        interactor.unsubscribe()
        assertTrue(second.isUnsubscribed)
    }

    @Test
    fun `terminal close cancels work and rejects subsequent managed execution`() {
        val source = PublishSubject.create<Int>()
        val interactor = newInteractor { source }
        val handle = interactor.execute(0, {}, { throw AssertionError(it) })
        interactor.unsubscribe()
        interactor.unsubscribe()
        interactor.clear()
        assertTrue(interactor.isUnsubscribed)
        assertTrue(handle.isUnsubscribed)
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
            Observable.just(it)
        }
        val handle = interactor.execute(0, { error("Unexpected value") }, { throw AssertionError(it) })
        handle.unsubscribe()
        scheduler.triggerActions()
        assertEquals(0, constructions)
        assertTrue(handle.isUnsubscribed)
    }

    @Test
    fun `direct invocation is caller owned independently of managed lifecycle`() {
        val source = PublishSubject.create<Int>()
        val interactor = newInteractor { source }
        val subscriber = TestSubscriber<Int>()
        val contract: InteractorContract<Int, Observable<Int>> = interactor
        contract(1).subscribe(subscriber)
        interactor.clear()
        interactor.unsubscribe()
        source.onNext(8)
        source.onCompleted()
        subscriber.assertValues(8)
        subscriber.assertCompleted()
        val next = TestSubscriber<Int>()
        newInteractor { Observable.just(it) }.also { it.unsubscribe() }(9).subscribe(next)
        next.assertValues(9)
        next.assertCompleted()
    }

    @Test
    fun `clear inside source factory cancels before synchronous values and side effects`() {
        val values = mutableListOf<Int>()
        val sourceEffects = mutableListOf<Int>()
        var completions = 0
        lateinit var interactor: Interactor<Int, Int>
        interactor = newInteractor { request ->
            interactor.clear()
            Observable.just(request).doOnNext { sourceEffects += it }
        }
        val handle = interactor.execute(7, values::add, { throw AssertionError(it) }) { completions++ }
        assertTrue(values.isEmpty())
        assertTrue(sourceEffects.isEmpty())
        assertEquals(0, completions)
        assertTrue(handle.isUnsubscribed)
        assertFalse(interactor.isUnsubscribed)
    }

    @Test
    fun `clear inside first synchronous callback stops later values and side effects`() {
        val values = mutableListOf<Int>()
        val sourceEffects = mutableListOf<Int>()
        var completions = 0
        val interactor = newInteractor { request ->
            Observable.range(request, 3).doOnNext { sourceEffects += it }
        }
        val handle = interactor.execute(7, {
            values += it
            interactor.clear()
        }, { throw AssertionError(it) }) { completions++ }
        assertEquals(listOf(7), values)
        assertEquals(listOf(7), sourceEffects)
        assertEquals(0, completions)
        assertTrue(handle.isUnsubscribed)
        assertFalse(interactor.isUnsubscribed)
    }

}
