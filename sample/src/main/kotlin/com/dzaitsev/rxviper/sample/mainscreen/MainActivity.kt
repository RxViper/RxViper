/* Copyright 2016–2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0. */
package com.dzaitsev.rxviper.sample.mainscreen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import com.dzaitsev.rxviper.sample.R
import com.dzaitsev.rxviper.sample.data.Cheese
import com.dzaitsev.rxviper.sample.data.CheeseStorage
import com.dzaitsev.rxviper.sample.data.CheeseType
import com.dzaitsev.rxviper.sample.detailsscreen.DetailsActivity
import com.dzaitsev.rxviper.sample.mainscreen.domain.GetCheesesInteractor
import com.dzaitsev.rxviper.sample.mainscreen.presenter.MainPresenter
import com.dzaitsev.rxviper.sample.mainscreen.router.MainRouter
import com.dzaitsev.rxviper.sample.mainscreen.view.MainViewCallbacks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class MainActivity : Activity(), MainViewCallbacks, MainRouter {
    private val screenScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var presenter: MainPresenter
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var progress: ProgressBar
    private lateinit var errorMessage: TextView
    private var cheeses = emptyList<Cheese>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progress = findViewById(R.id.progress)
        errorMessage = findViewById(R.id.error_message)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        findViewById<ListView>(R.id.cheeses).apply {
            adapter = this@MainActivity.adapter
            setOnItemClickListener { _, _, position, _ -> presenter.onItemClicked(cheeses[position]) }
        }

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
        presenter = MainPresenter(GetCheesesInteractor(CheeseStorage(catalog)), screenScope)
        findViewById<Button>(R.id.reload).setOnClickListener { presenter.fetchCheeses() }
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

    override fun onDestroy() {
        screenScope.cancel()
        super.onDestroy()
    }

    override fun showLoading(loading: Boolean) {
        progress.visibility = if (loading) View.VISIBLE else View.GONE
        if (loading) errorMessage.visibility = View.GONE
    }

    override fun showCheeses(cheeses: List<Cheese>) {
        this.cheeses = cheeses
        adapter.clear()
        adapter.addAll(cheeses.map { "${it.name} · ${it.type.label}" })
    }

    override fun showError(message: String) {
        errorMessage.text = message
        errorMessage.visibility = View.VISIBLE
    }

    override fun navigateToDetails(cheese: Cheese) {
        startActivity(Intent(this, DetailsActivity::class.java).apply {
            putExtra(DetailsActivity.CHEESE_NAME, cheese.name)
            putExtra(DetailsActivity.CHEESE_TYPE, cheese.type.label)
        })
    }
}
