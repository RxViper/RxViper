package com.dzaitsev.rxviper.plugin

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer

open class RxViperDSL(val options: NamedDomainObjectContainer<FeatureOptions>) {

  fun screens(action: Action<NamedDomainObjectContainer<FeatureOptions>>) {
    action.execute(options)
  }

  fun packageName(value: String) {
    packageName = value
  }

  fun splitPackages(value: Boolean) {
    splitPackages = value
  }

  fun useLambdas(value: Boolean) {
    useLambdas = value
  }

  fun justMvp(value: Boolean) {
    justMvp = value
  }

  fun skipExisting(value: Boolean) {
    skipExisting = value
  }

  fun hasRouter(value: Boolean) {
    hasRouter = value
  }

  fun withTests(value: Boolean) {
    withTests = value
  }

  companion object {
    internal var packageName = "generated"
    internal var splitPackages = false
    internal var useLambdas = false
    internal var justMvp = false
    internal var skipExisting = false
    internal var hasRouter = true
    internal var withTests = false
  }
}