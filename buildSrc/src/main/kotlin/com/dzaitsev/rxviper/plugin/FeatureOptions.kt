package com.dzaitsev.rxviper.plugin

data class FeatureOptions(private val _name: String) {
  val name = _name.firstUpper()
  var packageName = ""
  var withTests = false
  var hasRouter = true
  var splitPackage = RxViperDSL.splitPackages
  var useLambdas = RxViperDSL.useLambdas
  var justMvp = RxViperDSL.justMvp
  var skipExisting = RxViperDSL.skipExisting
  var useCases = emptyArray<String>()
  var routesTo = emptyArray<String>()
  var requestModel = clazz<Any>()
  var responseModel = clazz<Any>()

  val fullPackage: String
    get() = "$packageName.${_name.toLowerCase()}"
}

