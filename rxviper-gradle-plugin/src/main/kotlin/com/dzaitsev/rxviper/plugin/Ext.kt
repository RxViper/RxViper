package com.dzaitsev.rxviper.plugin

import groovy.lang.Closure
import org.gradle.api.Plugin
import org.gradle.api.Project

internal inline fun <reified C : Any> clazz() = C::class.java

internal inline fun <reified P : Plugin<*>> Project.has() = plugins.hasPlugin(clazz<P>())

internal inline fun <reified Task : Any> Project.task(name: String) = task(mapOf("type" to clazz<Task>()), name) as Task

internal inline fun <reified Ext> Project.getExtension(name: String) = extensions.getByName(name) as Ext

internal inline fun <T> Project.configure(obj: T, crossinline func: T.() -> Unit): Any {
  return configure(obj, object : Closure<Unit>(obj) {
    override fun call(vararg args: Any?) {
      obj.func()
    }
  })
}
