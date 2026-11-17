/* Copyright 2016–2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0. */
package com.dzaitsev.rxviper.sample.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF765A00),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDF8C),
    onPrimaryContainer = Color(0xFF251A00),
    secondary = Color(0xFF685E40),
    background = Color(0xFFFFF9EE),
    surface = Color(0xFFFFF9EE),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFE9C56F),
    onPrimary = Color(0xFF3E2E00),
    primaryContainer = Color(0xFF584300),
    onPrimaryContainer = Color(0xFFFFDF8C),
    secondary = Color(0xFFD3C6A1),
    background = Color(0xFF17130C),
    surface = Color(0xFF17130C),
)

@Composable
fun RxViperTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}
