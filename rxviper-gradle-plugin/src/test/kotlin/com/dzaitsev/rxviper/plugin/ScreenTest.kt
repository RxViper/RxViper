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
import com.dzaitsev.rxviper.plugin.internal.dsl.UseCase
import com.google.common.truth.Truth.assertThat
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test


class ScreenTest {
  @Test
  fun `check defaults`() {
    // arrange
    val project = ProjectBuilder().build()
    // act
    val screen = Screen("Test", project.container(aClass<UseCase>()))
    // assert
    with(screen) {
      assertThat(packageName).isSameAs(RxViperExtension.packageName)
      assertThat(useLambdas).isSameAs(RxViperExtension.useLambdas)
      assertThat(hasInteractor).isSameAs(RxViperExtension.hasInteractor)
      assertThat(hasRouter).isSameAs(RxViperExtension.hasRouter)
      assertThat(split).isSameAs(RxViperExtension.split)
    }
  }
}