package com.dzaitsev.rxviper.plugin

import groovy.lang.Closure
import org.gradle.api.Incubating
import org.gradle.api.NamedDomainObjectContainer

open class RxViperExtension(val screens: NamedDomainObjectContainer<Screen>) {

  fun screens(closure: Closure<NamedDomainObjectContainer<Screen>>) {
    screens.configure(closure)
  }

  companion object {
    @JvmStatic val NAME = "rxViper"

    @JvmStatic var packageName = "generated"; @JvmName("packageName") set

    @JvmStatic var useLambdas = false; @JvmName("useLambdas") set

    @JvmStatic var hasInteractor = false; @JvmName("hasInteractor") set

    @JvmStatic var hasRouter = true; @JvmName("hasRouter") set

    @JvmStatic var addMetaInfo = true; @JvmName("addMetaInfo") set

    @Incubating
    @JvmStatic internal var skipExisting = false; @JvmName("skipExisting") set

    @Incubating
    @JvmStatic internal var splitPackages = false; @JvmName("splitPackages") set

    @Incubating
    @JvmStatic internal var withTests = false; @JvmName("withTests") set
  }
}