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
import com.dzaitsev.rxviper.plugin.aClass
import com.google.common.truth.Truth.assertThat
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test


class ScreenTest {
  @Test
  fun `check defaults`() {
    // arrange
    val project = ProjectBuilder().build()
    val rxViper = RxViperExtension(project)
    // act
    val screen = Screen("test", rxViper, project.container(aClass<UseCase>()))
    // assert
    verify(screen, rxViper)
  }

  companion object {
    fun verify(screen: Screen, rxViper: RxViperExtension) {
      with(screen) {
        assertThat(name).isEqualTo("test")
        assertThat(useCases).isEmpty()
        assertThat(fullPackage).isEqualTo("$packageName.${name.toLowerCase()}")
        assertThat(packageName).isSameAs(rxViper.packageName)
        assertThat(includeRouter).isSameAs(rxViper.includeRouter)
        assertThat(useLambdas).isSameAs(rxViper.useLambdas)
        assertThat(includeInteractor).isSameAs(rxViper.includeInteractor)
        assertThat(addMetaInfo).isSameAs(rxViper.addMetaInfo)
        assertThat(splitPackages).isSameAs(rxViper.splitPackages)
        assertThat(routesTo).isEmpty()
      }
    }
  }
}