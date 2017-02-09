package com.dzaitsev.rxviper.plugin

import groovy.lang.Closure
import org.gradle.api.Incubating
import org.gradle.api.NamedDomainObjectContainer

open class RxViperExtension(internal val screens: NamedDomainObjectContainer<Screen>) {

  fun screens(closure: Closure<NamedDomainObjectContainer<Screen>>) {
    screens.configure(closure)
  }

  companion object {
    @JvmStatic var packageName = "generated"
      @JvmName("packageName") set

    @JvmStatic var useLambdas = false
      @JvmName("useLambdas") set

    @JvmStatic var justMvp = false
      @JvmName("justMvp") set

    @JvmStatic var hasRouter = true
      @JvmName("hasRouter") set

    @Incubating @JvmStatic internal var skipExisting = false
      @JvmName("skipExisting") set

    @Incubating @JvmStatic internal var splitPackages = false
      @JvmName("splitPackages") set

    @Incubating @JvmStatic internal var withTests = false
      @JvmName("withTests") set

    @JvmStatic internal val NAME = "rxViper"
  }
}