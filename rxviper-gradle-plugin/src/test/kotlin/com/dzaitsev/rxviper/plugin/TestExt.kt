package com.dzaitsev.rxviper.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Project

internal fun Project.applyAndroidAppPlugin() = project.apply(mapOf("plugin" to clazz<AppPlugin>()))

internal fun Project.applyAndroidLibraryPlugin() = project.apply(mapOf("plugin" to clazz<LibraryPlugin>()))

internal fun Project.applyRxViperPlugin() = project.apply(mapOf("plugin" to clazz<RxViperPlugin>()))
