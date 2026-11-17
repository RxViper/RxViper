/* Copyright 2016–2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0. */
package com.dzaitsev.rxviper.sample.detailsscreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dzaitsev.rxviper.sample.ui.RxViperTheme

class DetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val name = intent.getStringExtra(CHEESE_NAME).orEmpty()
        val type = intent.getStringExtra(CHEESE_TYPE).orEmpty()
        setContent {
            RxViperTheme {
                DetailsScreen(name = name, type = type, onBack = { finish() })
            }
        }
    }

    companion object {
        const val CHEESE_NAME = "cheese_name"
        const val CHEESE_TYPE = "cheese_type"
    }
}
