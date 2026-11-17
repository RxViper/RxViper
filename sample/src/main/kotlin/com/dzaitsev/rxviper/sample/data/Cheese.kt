/* Copyright 2016–2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0. */
package com.dzaitsev.rxviper.sample.data

data class Cheese(val id: Long, val name: String, val type: CheeseType)

enum class CheeseType(val label: String) {
    HARD("Hard"), SEMI_HARD("Semi-hard"), SEMI_SOFT("Semi-soft"), SOFT("Soft")
}
