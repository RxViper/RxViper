/* Copyright 2016–2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0. */
package com.dzaitsev.rxviper.sample.mainscreen.domain

import com.dzaitsev.rxviper.coroutines.SuspendInteractor
import com.dzaitsev.rxviper.sample.data.Cheese
import com.dzaitsev.rxviper.sample.data.CheeseStorage

class GetCheesesInteractor(private val storage: CheeseStorage) : SuspendInteractor<Int, List<Cheese>>() {
    override suspend fun run(request: Int): List<Cheese> = storage.getCheeses(request)
}
