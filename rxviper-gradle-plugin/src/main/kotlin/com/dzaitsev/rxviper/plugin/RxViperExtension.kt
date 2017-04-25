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

package com.dzaitsev.rxviper.plugin

import com.dzaitsev.rxviper.plugin.internal.dsl.Screen
import com.dzaitsev.rxviper.plugin.internal.dsl.ScreenFactory
import groovy.lang.Closure
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet

open class RxViperExtension(internal val project: Project) {
  var screens: NamedDomainObjectContainer<Screen> = project.container(aClass<Screen>(), ScreenFactory(project))

  var packageName = "generated"; @JvmName("packageName") set

  var useLambdas = false; @JvmName("useLambdas") set

  var includeInteractor = true; @JvmName("includeInteractor") set

  var includeRouter = true; @JvmName("includeRouter") set

  var addMetaInfo = true; @JvmName("addMetaInfo") set

  var splitPackages = true; @JvmName("splitPackages") set

  var destinationDir = project.file("src/${SourceSet.MAIN_SOURCE_SET_NAME}/java"); @JvmName("destinationDir") set

  fun screens(closure: Closure<NamedDomainObjectContainer<Screen>>) = screens.configure(closure)

  companion object {
    @JvmStatic val NAME = "rxViper"
  }
}