package com.dzaitsev.rxviper.plugin.internal.dsl

import com.dzaitsev.rxviper.plugin.RxViperExtension
import groovy.lang.Closure
import org.gradle.api.NamedDomainObjectContainer

class Screen(_name: String, internal val useCases: NamedDomainObjectContainer<UseCase>) {
  val name = _name.capitalize()

  val fullPackage: String; get() = "$packageName.${name.toLowerCase()}"

  var packageName = RxViperExtension.packageName; @JvmName("packageName") set

  var hasRouter = RxViperExtension.hasRouter; @JvmName("hasRouter") set

  var useLambdas = RxViperExtension.useLambdas; @JvmName("useLambdas") set

  var hasInteractor = RxViperExtension.hasInteractor; @JvmName("hasInteractor") set

  var routesTo = emptyArray<String>(); @JvmName("routesTo") set

  var addMetaInfo = RxViperExtension.addMetaInfo; @JvmName("addMetaInfo") set

  fun useCases(closure: Closure<NamedDomainObjectContainer<UseCase>>) = useCases.configure(closure)
}

