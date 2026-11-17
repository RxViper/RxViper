/* Copyright 2016–2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0. */
package com.dzaitsev.rxviper.sample.mainscreen

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.dzaitsev.rxviper.sample.R
import com.dzaitsev.rxviper.sample.data.Cheese
import com.dzaitsev.rxviper.sample.data.CheeseStorage
import com.dzaitsev.rxviper.sample.data.CheeseType
import com.dzaitsev.rxviper.sample.detailsscreen.DetailsActivity
import com.dzaitsev.rxviper.sample.mainscreen.domain.GetCheesesInteractor
import com.dzaitsev.rxviper.sample.mainscreen.presenter.MainPresenter
import com.dzaitsev.rxviper.sample.mainscreen.router.MainRouter
import com.dzaitsev.rxviper.sample.mainscreen.view.MainScreen
import com.dzaitsev.rxviper.sample.mainscreen.view.MainUiState
import com.dzaitsev.rxviper.sample.mainscreen.view.MainViewCallbacks
import com.dzaitsev.rxviper.sample.ui.RxViperTheme

class MainActivity : ComponentActivity(), MainViewCallbacks, MainRouter {
    private lateinit var presenter: MainPresenter
    private var uiState by mutableStateOf(MainUiState())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Construct dependencies at the edge; presenter and interactor have no Android dependency.
        val arrays = listOf(
            R.array.hard_cheeses to CheeseType.HARD,
            R.array.semi_hard_cheeses to CheeseType.SEMI_HARD,
            R.array.semi_soft_cheeses to CheeseType.SEMI_SOFT,
            R.array.soft_cheeses to CheeseType.SOFT,
        )
        val catalog = arrays.flatMap { (array, type) ->
            resources.getStringArray(array).map { name -> name to type }
        }.mapIndexed { index, (name, type) -> Cheese(index.toLong(), name, type) }
        presenter = MainPresenter(GetCheesesInteractor(CheeseStorage(catalog)), lifecycleScope)
        setContent {
            RxViperTheme {
                MainScreen(
                    state = uiState,
                    onReload = { presenter.fetchCheeses() },
                    onCheeseSelected = presenter::onItemClicked,
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.takeRouter(this)
        presenter.takeView(this)
    }

    override fun onStop() {
        presenter.dropView(this)
        presenter.dropRouter(this)
        super.onStop()
    }

    override fun showLoading(loading: Boolean) {
        uiState = uiState.copy(
            isLoading = loading,
            errorMessage = if (loading) null else uiState.errorMessage,
        )
    }

    override fun showCheeses(cheeses: List<Cheese>) {
        uiState = uiState.copy(cheeses = cheeses.toList(), errorMessage = null)
    }

    override fun showError(message: String) {
        uiState = uiState.copy(errorMessage = message)
    }

    override fun navigateToDetails(cheese: Cheese) {
        startActivity(Intent(this, DetailsActivity::class.java).apply {
            putExtra(DetailsActivity.CHEESE_NAME, cheese.name)
            putExtra(DetailsActivity.CHEESE_TYPE, cheese.type.label)
        })
    }
}
