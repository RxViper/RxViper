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

import com.dzaitsev.rxviper.plugin.internal.codegen.InteractorGenerator
import com.dzaitsev.rxviper.plugin.internal.codegen.PresenterGenerator
import com.dzaitsev.rxviper.plugin.internal.codegen.RouterGenerator
import com.dzaitsev.rxviper.plugin.internal.codegen.ViewCallbacksGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME
import org.gradle.api.tasks.TaskAction

open class RxViperTask : DefaultTask() {

  init {
    group = GROUP
    description = DESCRIPTION
  }

  var destinationDir = project.file("src/$MAIN_SOURCE_SET_NAME/java"); @JvmName("destinationDir") set
  var screens = (project.extensions.getByName(RxViperExtension.NAME) as RxViperExtension).screens; @JvmName("screens") set

  @TaskAction
  fun generateRxViper() {
    destinationDir.parentFile?.mkdirs()
    screens.all { screen ->
      with(mutableListOf(ViewCallbacksGenerator(screen), PresenterGenerator(screen))) {
        if (screen.includeRouter) {
          add(RouterGenerator(screen))
        }
        if (screen.includeInteractor) {
          add(InteractorGenerator(screen))
        }
        forEach { it.saveTo(destinationDir) }
      }
    }
  }

  companion object {
    @JvmStatic
    val NAME = "generateRxViper"

    @JvmStatic
    val GROUP = "RxViper"

    @JvmStatic
    val DESCRIPTION = "Generates VIPER modules adding them to right targets"
  }
}