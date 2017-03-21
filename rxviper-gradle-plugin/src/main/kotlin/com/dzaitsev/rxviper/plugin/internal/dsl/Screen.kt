/*
 * Copyright 2017 Dmytro Zaitsev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dzaitsev.rxviper.plugin.internal.dsl

import com.dzaitsev.rxviper.plugin.RxViperExtension
import groovy.lang.Closure
import org.gradle.api.NamedDomainObjectContainer

class Screen(_name: String, rxViper: RxViperExtension, val useCases: NamedDomainObjectContainer<UseCase>) {
  val name = _name.capitalize()

  val fullPackage: String; get() = if (splitPackages) "$packageName.${name.toLowerCase()}" else packageName

  var packageName = rxViper.packageName; @JvmName("packageName") set

  var includeRouter = rxViper.includeRouter; @JvmName("includeRouter") set

  var useLambdas = rxViper.useLambdas; @JvmName("useLambdas") set

  var includeInteractor = rxViper.includeInteractor; @JvmName("includeInteractor") set

  var addMetaInfo = rxViper.addMetaInfo; @JvmName("addMetaInfo") set

  var splitPackages = rxViper.splitPackages; @JvmName("splitPackages") set

  var routesTo = emptyArray<String>(); @JvmName("routesTo") set

  fun useCases(closure: Closure<NamedDomainObjectContainer<UseCase>>) = useCases.configure(closure)
}
