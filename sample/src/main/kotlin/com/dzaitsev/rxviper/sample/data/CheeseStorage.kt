/* Copyright 2016–2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0. */
package com.dzaitsev.rxviper.sample.data

import kotlinx.coroutines.delay

/** Fake repository; suspending delay is cancelled when the owning screen stops. */
class CheeseStorage(private val cheeses: List<Cheese>) {
    suspend fun getCheeses(count: Int): List<Cheese> {
        require(count >= 0) { "Count must be nonnegative." }
        delay(750)
        return cheeses.shuffled().take(count)
    }
}
