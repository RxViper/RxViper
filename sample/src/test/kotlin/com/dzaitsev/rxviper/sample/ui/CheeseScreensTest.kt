/* Copyright 2016–2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0. */
package com.dzaitsev.rxviper.sample.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.dzaitsev.rxviper.sample.R
import com.dzaitsev.rxviper.sample.data.Cheese
import com.dzaitsev.rxviper.sample.data.CheeseType
import com.dzaitsev.rxviper.sample.detailsscreen.DetailsScreen
import com.dzaitsev.rxviper.sample.mainscreen.view.MainScreen
import com.dzaitsev.rxviper.sample.mainscreen.view.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w412dp-h915dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CheeseScreensTest {
    @get:Rule
    val compose = createComposeRule()

    private val cheddar = Cheese(1, "Cheddar", CheeseType.SEMI_HARD)
    private val context get() = RuntimeEnvironment.getApplication()

    @Test
    fun `list renders presenter data and forwards selection and reload`() {
        var selected: Cheese? = null
        var reloads = 0
        compose.setContent {
            RxViperTheme {
                MainScreen(MainUiState(cheeses = listOf(cheddar)), { reloads++ }, { selected = it })
            }
        }
        compose.onNodeWithText(cheddar.name).assertIsDisplayed().performClick()
        compose.onNodeWithText(cheddar.type.label).assertIsDisplayed()
        compose.onNodeWithText(context.getString(R.string.reload_cheeses)).performClick()
        compose.runOnIdle {
            assertEquals(cheddar, selected)
            assertEquals(1, reloads)
        }
    }

    @Test
    fun `loading state is announced without showing an empty result`() {
        compose.setContent {
            RxViperTheme { MainScreen(MainUiState(isLoading = true), {}, {}) }
        }
        compose.onNodeWithContentDescription(context.getString(R.string.loading_cheeses)).assertIsDisplayed()
        compose.onNodeWithText(context.getString(R.string.no_cheeses)).assertDoesNotExist()
    }

    @Test
    fun `error remains visible and reload is available`() {
        var reloads = 0
        compose.setContent {
            RxViperTheme { MainScreen(MainUiState(errorMessage = "Offline"), { reloads++ }, {}) }
        }
        compose.onNodeWithText("Offline").assertIsDisplayed()
        compose.onNodeWithText(context.getString(R.string.reload_cheeses)).performClick()
        compose.runOnIdle { assertEquals(1, reloads) }
    }

    @Test
    fun `empty state explains that there are no cheeses`() {
        compose.setContent {
            RxViperTheme { MainScreen(MainUiState(), {}, {}) }
        }
        compose.onNodeWithText(context.getString(R.string.no_cheeses)).assertIsDisplayed()
    }

    @Test
    fun `details shows selected cheese and forwards back action`() {
        var backs = 0
        compose.setContent {
            RxViperTheme { DetailsScreen(cheddar.name, cheddar.type.label) { backs++ } }
        }
        compose.onNodeWithText(cheddar.name).assertIsDisplayed()
        compose.onNodeWithText(context.getString(R.string.cheese_type, cheddar.type.label)).assertIsDisplayed()
        compose.onNodeWithText(context.getString(R.string.back)).performClick()
        compose.runOnIdle { assertEquals(1, backs) }
    }
}
