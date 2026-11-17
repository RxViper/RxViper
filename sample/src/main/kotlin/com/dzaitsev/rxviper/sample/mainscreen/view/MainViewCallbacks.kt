/* Copyright 2016–2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0. */
package com.dzaitsev.rxviper.sample.mainscreen.view

import com.dzaitsev.rxviper.ViewCallbacks
import com.dzaitsev.rxviper.sample.data.Cheese

interface MainViewCallbacks : ViewCallbacks {
    fun showLoading(loading: Boolean)
    fun showCheeses(cheeses: List<Cheese>)
    fun showError(message: String)
}
