/* Copyright 2016–2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0. */
package com.dzaitsev.rxviper.sample.mainscreen.router

import com.dzaitsev.rxviper.Router
import com.dzaitsev.rxviper.sample.data.Cheese

interface MainRouter : Router {
    fun navigateToDetails(cheese: Cheese)
}
