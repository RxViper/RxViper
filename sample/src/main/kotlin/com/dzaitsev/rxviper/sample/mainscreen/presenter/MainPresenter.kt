/* Copyright 2016–2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0. */
package com.dzaitsev.rxviper.sample.mainscreen.presenter

import com.dzaitsev.rxviper.ViperPresenter
import com.dzaitsev.rxviper.coroutines.SuspendInteractor
import com.dzaitsev.rxviper.sample.data.Cheese
import com.dzaitsev.rxviper.sample.mainscreen.router.MainRouter
import com.dzaitsev.rxviper.sample.mainscreen.view.MainViewCallbacks
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** The Activity owns [scope]; the presenter owns only its current request. */
class MainPresenter(
    private val getCheeses: SuspendInteractor<Int, List<Cheese>>,
    private val scope: CoroutineScope,
) : ViperPresenter<MainViewCallbacks, MainRouter>(MainViewCallbacks::class.java, MainRouter::class.java) {
    private var cachedCheeses: List<Cheese>? = null
    private var request: Job? = null

    override fun onTakeView(view: MainViewCallbacks) {
        cachedCheeses?.let(view::showCheeses) ?: fetchCheeses()
    }

    override fun onDropView(view: MainViewCallbacks) {
        request?.cancel()
        request = null
        view.showLoading(false)
    }

    fun fetchCheeses(count: Int = 30) {
        request?.cancel()
        view.showLoading(true)
        request = scope.launch {
            val cheeses = try {
                getCheeses(count)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                view.showError(error.message ?: "Unable to load cheeses.")
                view.showLoading(false)
                return@launch
            }
            cachedCheeses = cheeses
            view.showCheeses(cheeses)
            view.showLoading(false)
        }
    }

    fun onItemClicked(cheese: Cheese) {
        router.navigateToDetails(cheese)
    }
}
