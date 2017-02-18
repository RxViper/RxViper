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

import com.android.build.gradle.AndroidConfig
import com.android.build.gradle.internal.api.DefaultAndroidSourceSet
import com.dzaitsev.rxviper.plugin.internal.codegen.InteractorGenerator
import com.dzaitsev.rxviper.plugin.internal.codegen.PresenterGenerator
import com.dzaitsev.rxviper.plugin.internal.codegen.RouterGenerator
import com.dzaitsev.rxviper.plugin.internal.codegen.ViewCallbacksGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME
import org.gradle.api.tasks.TaskAction

open class RxViperTask : DefaultTask() {
  private val androidConfig = project.extensions.getByName("android") as AndroidConfig

  protected var destinationDir = (androidConfig.sourceSets.findByName(MAIN_SOURCE_SET_NAME)
      ?: DefaultAndroidSourceSet(MAIN_SOURCE_SET_NAME, project, true)).java.srcDirs.first()
    @JvmName("destinationDir") set

  protected var screens = (project.extensions.getByName(RxViperExtension.NAME) as RxViperExtension).screens
    @JvmName("screens") set

  @TaskAction
  fun generateRxViper() {
    screens.all {
      val generators = mutableListOf(ViewCallbacksGenerator(it), PresenterGenerator(it))

      if (it.hasRouter) {
        generators.add(RouterGenerator(it))
      }
      if (it.hasInteractor) {
        generators.add(InteractorGenerator(it))
      }

      generators.forEach {
        it.saveTo(destinationDir)
      }
    }
  }

  companion object {
    @JvmStatic
    val NAME = "generateRxViper"

    @JvmStatic
    val GROUP = "RxViper"
  }
}