/*
 * Copyright 2016 Dmytro Zaitsev
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

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.dzaitsev.rxviper.plugin.internal.dsl.Screen
import com.dzaitsev.rxviper.plugin.internal.dsl.ScreenFactory
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.dzaitsev.rxviper.plugin.RxViperExtension.Companion.NAME as RXVIPER_EXT
import com.dzaitsev.rxviper.plugin.RxViperTask.Companion.NAME as RXVIPER_TASK

class RxViperPlugin : Plugin<Project> {
  override fun apply(project: Project) = with(project) {
    check(applied<LibraryPlugin>() || applied<AppPlugin>()) {
      "RxViper plugin can only be applied to Android projects.\n" +
          "Required plugins: 'com.android.application' or 'com.android.library'."
    }

    extensions.create(RXVIPER_EXT, aClass<RxViperExtension>(), container(aClass<Screen>(), ScreenFactory(this)))

    afterEvaluate {
      val task = task(mapOf("type" to aClass<RxViperTask>()), RXVIPER_TASK)
      task.group = RxViperTask.GROUP
    }
  }

  private inline fun <reified P : Plugin<*>> Project.applied() = plugins.hasPlugin(aClass<P>())
}