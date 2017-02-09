package com.dzaitsev.rxviper.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

internal inline fun <reified C : Any> clazz() = C::class.java

internal inline fun <reified P : Plugin<*>> Project.has() = plugins.hasPlugin(clazz<P>())

internal inline fun <reified Task : Any> Project.task(name: String) = task(mapOf("type" to clazz<Task>()), name) as Task

internal inline fun <reified Ext> Project.getExtension(name: String) = extensions.getByName(name) as Ext
