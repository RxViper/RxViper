/* Copyright 2016–2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0. */
package com.dzaitsev.rxviper.sample.detailsscreen

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import com.dzaitsev.rxviper.sample.R

class DetailsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        findViewById<TextView>(R.id.details).text = getString(
            R.string.cheese_details,
            intent.getStringExtra(CHEESE_NAME).orEmpty(),
            intent.getStringExtra(CHEESE_TYPE).orEmpty(),
        )
    }

    companion object {
        const val CHEESE_NAME = "cheese_name"
        const val CHEESE_TYPE = "cheese_type"
    }
}
