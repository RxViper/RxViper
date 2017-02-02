package com.dzaitsev.rxviper.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

internal fun String.firstLower() = "${first().toLowerCase()}${substring(1)}"
internal fun String.firstUpper() = "${first().toUpperCase()}${substring(1)}"
inline internal fun <reified P : Plugin<*>> Project.has() = plugins.hasPlugin(clazz<P>())
inline internal fun <reified Task : Any> Project.task(name: String) = task(mapOf("type" to clazz<Task>()), name) as Task
inline internal fun <reified Ext> Project.getExtension(name: String) = extensions.getByName(name) as Ext
inline internal fun <reified C : Any> clazz() = C::class.java
