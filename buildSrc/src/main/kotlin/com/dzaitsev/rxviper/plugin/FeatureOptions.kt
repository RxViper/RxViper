package com.dzaitsev.rxviper.plugin

class FeatureOptions(private val _name: String) {
  val name = _name.capitalize()
  val fullPackage: String
    get() = "$packageName.${_name.toLowerCase()}"

  internal var packageName = RxViperDSL.packageName
  internal var hasRouter = RxViperDSL.hasRouter
  internal var splitPackages = RxViperDSL.splitPackages
  internal var useLambdas = RxViperDSL.useLambdas
  internal var justMvp = RxViperDSL.justMvp
  internal var skipExisting = RxViperDSL.skipExisting
  internal var withTests = RxViperDSL.withTests

  internal var useCases = emptyArray<String>()
  internal var routesTo = emptyArray<String>()
  internal var requestModel = clazz<Any>()
  internal var responseModel = clazz<Any>()

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

  fun useCases(value: Array<String>) {
    useCases = value
  }

  fun routesTo(value: Array<String>) {
    routesTo = value
  }

  fun requestModel(value: Class<Any>) {
    requestModel = value
  }

  fun responseModel(value: Class<Any>) {
    responseModel = value
  }
}

