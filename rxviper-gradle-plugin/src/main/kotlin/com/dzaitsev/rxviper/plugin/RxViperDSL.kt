package com.dzaitsev.rxviper.plugin

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer

open class RxViperDSL(internal val options: NamedDomainObjectContainer<FeatureOptions>) {

  fun screens(action: Action<NamedDomainObjectContainer<FeatureOptions>>) {
    action.execute(options)
  }

  companion object {
    @JvmStatic var packageName = "generated"
      @JvmName("packageName") set

    @JvmStatic var splitPackages = false
      @JvmName("splitPackages") set

    @JvmStatic var useLambdas = false
      @JvmName("useLambdas") set

    @JvmStatic var justMvp = false
      @JvmName("justMvp") set

    @JvmStatic var skipExisting = false
      @JvmName("skipExisting") set

    @JvmStatic var hasRouter = true
      @JvmName("hasRouter") set

    @JvmStatic var withTests = false
      @JvmName("withTests") set
  }
}