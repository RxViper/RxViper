package com.dzaitsev.rxviper.plugin

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer

open class RxViperDSL(val featureOptionsContainer: NamedDomainObjectContainer<FeatureOptions>) {
  fun screens(action: Action<NamedDomainObjectContainer<FeatureOptions>>) {
    action.execute(featureOptionsContainer)
  }

  companion object {
    @JvmStatic var splitPackages = false
    @JvmStatic var useLambdas = false
    @JvmStatic var justMvp = false
    @JvmStatic var skipExisting = false
  }
}