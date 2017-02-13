@file:JvmName("Utils")

package com.dzaitsev.rxviper.plugin

internal inline fun <reified C : Any> aClass() = C::class.java