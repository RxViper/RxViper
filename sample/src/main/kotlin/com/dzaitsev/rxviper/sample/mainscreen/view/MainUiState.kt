/* Copyright 2016–2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0. */
package com.dzaitsev.rxviper.sample.mainscreen.view

import com.dzaitsev.rxviper.sample.data.Cheese

/** A snapshot of presenter callbacks, rendered by Compose without owning the presenter lifecycle. */
data class MainUiState(
    val cheeses: List<Cheese> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
