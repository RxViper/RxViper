package com.dzaitsev.rxviper.plugin

import org.gradle.api.Incubating

class Screen(private val _name: String) {
  val name = _name.capitalize()
  val fullPackage: String
    get() = "$packageName.${_name.toLowerCase()}"

  var packageName = RxViperExtension.packageName
    @JvmName("packageName") set

  var hasRouter = RxViperExtension.hasRouter
    @JvmName("hasRouter") set

  var useLambdas = RxViperExtension.useLambdas
    @JvmName("useLambdas") set

  var justMvp = RxViperExtension.justMvp
    @JvmName("justMvp") set

  var useCases = emptyArray<String>()
    @JvmName("useCases") set

  var routesTo = emptyArray<String>()
    @JvmName("routesTo") set

  var requestModel = clazz<Any>()
    @JvmName("requestModel") set

  var responseModel = clazz<Any>()
    @JvmName("responseModel") set


  @Incubating internal var splitPackages = RxViperExtension.splitPackages
    @JvmName("splitPackages") set

  @Incubating internal var skipExisting = RxViperExtension.skipExisting
    @JvmName("skipExisting") set

  @Incubating internal var withTests = RxViperExtension.withTests
    @JvmName("withTests") set
}

