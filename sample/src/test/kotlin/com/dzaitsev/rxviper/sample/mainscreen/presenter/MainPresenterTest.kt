/* Copyright 2016–2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0. */
package com.dzaitsev.rxviper.sample.mainscreen.presenter

import com.dzaitsev.rxviper.SuspendInteractor as SuspendInteractorContract
import com.dzaitsev.rxviper.coroutines.SuspendInteractor
import com.dzaitsev.rxviper.sample.data.Cheese
import com.dzaitsev.rxviper.sample.data.CheeseType
import com.dzaitsev.rxviper.sample.mainscreen.router.MainRouter
import com.dzaitsev.rxviper.sample.mainscreen.view.MainViewCallbacks
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainPresenterTest {
    private val cheese = Cheese(1, "Cheddar", CheeseType.SEMI_HARD)

    @Test
    fun `completed data replays on reattachment without another request`() = runTest {
        var executions = 0
        val interactor = SuspendInteractorContract<Int, List<Cheese>> {
            executions++
            listOf(cheese)
        }
        val presenter = MainPresenter(interactor, this)
        val first = RecordingView()
        presenter.takeView(first)
        advanceUntilIdle()
        assertEquals(listOf(cheese), first.cheeses)
        assertFalse(first.loading)
        presenter.dropView(first)
        val second = RecordingView()
        presenter.takeView(second)
        assertEquals(listOf(cheese), second.cheeses)
        assertEquals(1, executions)
    }

    @Test
    fun `dropping view cancels loading without showing an error and permits reload`() = runTest {
        var cancellations = 0
        val interactor = object : SuspendInteractor<Int, List<Cheese>>(StandardTestDispatcher(testScheduler)) {
            override suspend fun run(request: Int): List<Cheese> {
                try {
                    delay(1000)
                    return listOf(cheese)
                } finally {
                    cancellations++
                }
            }
        }
        val presenter = MainPresenter(interactor, this)
        val first = RecordingView()
        presenter.takeView(first)
        runCurrent()
        assertTrue(first.loading)
        presenter.dropView(first)
        advanceUntilIdle()
        assertEquals(1, cancellations)
        assertTrue(first.errors.isEmpty())
        assertTrue(first.cheeses.isEmpty())
        assertFalse(first.loading)
        val second = RecordingView()
        presenter.takeView(second)
        advanceUntilIdle()
        assertEquals(listOf(cheese), second.cheeses)
        assertTrue(second.errors.isEmpty())
    }

    @Test
    fun `interactor failures are displayed and loading stops`() = runTest {
        val interactor = SuspendInteractorContract<Int, List<Cheese>> { error("Repository unavailable") }
        val presenter = MainPresenter(interactor, this)
        val view = RecordingView()
        presenter.takeView(view)
        advanceUntilIdle()
        assertEquals(listOf("Repository unavailable"), view.errors)
        assertFalse(view.loading)
    }

    @Test
    fun `reload replaces a running request`() = runTest {
        val requests = mutableListOf<Int>()
        val interactor = object : SuspendInteractor<Int, List<Cheese>>(StandardTestDispatcher(testScheduler)) {
            override suspend fun run(request: Int): List<Cheese> {
                requests += request
                delay(1000)
                return List(request) { cheese }
            }
        }
        val presenter = MainPresenter(interactor, this)
        val view = RecordingView()
        presenter.takeView(view)
        runCurrent()
        presenter.fetchCheeses(5)
        advanceUntilIdle()
        assertEquals(listOf(30, 5), requests)
        assertEquals(5, view.cheeses.size)
        assertFalse(view.loading)
        assertTrue(view.errors.isEmpty())
    }

    @Test
    fun `item selection delegates navigation to attached router`() = runTest {
        val interactor = object : SuspendInteractor<Int, List<Cheese>>(StandardTestDispatcher(testScheduler)) {
            override suspend fun run(request: Int): List<Cheese> = emptyList()
        }
        val presenter = MainPresenter(interactor, this)
        val router = object : MainRouter {
            var selected: Cheese? = null
            override fun navigateToDetails(cheese: Cheese) { selected = cheese }
        }
        presenter.takeRouter(router)
        presenter.onItemClicked(cheese)
        assertEquals(cheese, router.selected)
    }

    private class RecordingView : MainViewCallbacks {
        var loading = false
        var cheeses = emptyList<Cheese>()
        val errors = mutableListOf<String>()
        override fun showLoading(loading: Boolean) { this.loading = loading }
        override fun showCheeses(cheeses: List<Cheese>) { this.cheeses = cheeses }
        override fun showError(message: String) { errors += message }
    }
}
