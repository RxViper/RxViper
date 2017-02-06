package com.dzaitsev.rxviper.plugin

class FeatureOptions(private val _name: String) {
  val name = _name.capitalize()
  val fullPackage: String
    get() = "$packageName.${_name.toLowerCase()}"

  var packageName = RxViperDSL.packageName
    @JvmName("packageName") set

  var hasRouter = RxViperDSL.hasRouter
    @JvmName("hasRouter") set

  var splitPackages = RxViperDSL.splitPackages
    @JvmName("splitPackages") set

  var useLambdas = RxViperDSL.useLambdas
    @JvmName("useLambdas") set

  var justMvp = RxViperDSL.justMvp
    @JvmName("justMvp") set

  var skipExisting = RxViperDSL.skipExisting
    @JvmName("skipExisting") set

  var withTests = RxViperDSL.withTests @JvmName("withTests") set

  var useCases = emptyArray<String>()
    @JvmName("useCases") set

  var routesTo = emptyArray<String>()
    @JvmName("routesTo") set

  var requestModel = clazz<Any>()
    @JvmName("requestModel") set

  var responseModel = clazz<Any>()
    @JvmName("responseModel") set
}

