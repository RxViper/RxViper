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

import com.google.common.truth.Truth.assertThat
import org.gradle.api.tasks.TaskAction
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files

class RxViperTaskTest {
  private lateinit var rxViperTask: RxViperTask

  @Before
  fun setUp() {
    rxViperTask = with(ProjectBuilder().build()) {
      plugins.apply(aClass<RxViperPlugin>())
      tasks.findByName(RxViperTask.NAME) as RxViperTask
    }
  }

  @Test
  fun `group should be correct`() {
    assertThat(RxViperTask.GROUP).isEqualTo("Viper")
  }

  @Test
  fun `name should be correct`() {
    assertThat(RxViperTask.NAME).isEqualTo("generateRxViper")
  }

  @Test
  fun `description should be correct`() {
    assertThat(RxViperTask.DESCRIPTION).isEqualTo("Generates VIPER modules adding them to right targets")
  }

  @Test
  fun `group should be set`() {
    assertThat(rxViperTask.group).isEqualTo(RxViperTask.GROUP)
  }

  @Test
  fun `description should be set`() {
    assertThat(rxViperTask.description).isEqualTo(RxViperTask.DESCRIPTION)
  }

  @Test
  fun `destinationDir should be set`() {
    // arrange
    val project = rxViperTask.project
    val rxViperExtension = project.extensions.getByName(RxViperExtension.NAME) as RxViperExtension
    // assert
    assertThat(rxViperTask.destinationDir).isSameAs(rxViperExtension.destinationDir)
  }

  @Test
  fun `screens should be set`() {
    // arrange
    val project = rxViperTask.project
    val rxViperExtension = project.extensions.getByName(RxViperExtension.NAME) as RxViperExtension
    // assert
    assertThat(rxViperTask.screens).isSameAs(rxViperExtension.screens)
  }

  @Test
  fun `should have only one task action`() {
    val taskActions = RxViperTask::class.java
        .declaredMethods
        .filter { it.isAnnotationPresent(aClass<TaskAction>()) }
    assertThat(taskActions).hasSize(1)
    assertThat(taskActions[0].name).isEqualTo("generateRxViper")
  }

  @Test
  fun `task action should generate the code`() {
    with(rxViperTask) {
      // given
      val screenName = "TestScreen"
      val screen = screens.create(screenName)
      destinationDir = Files.createTempDirectory("temp${System.nanoTime()}").toFile()
      // when
      generateRxViper()
      // then
      with(File(File(destinationDir, screen.packageName), screen.name.toLowerCase())) {
        assertThat(exists()).isTrue()
        assertThat(list().toList()).containsExactly(
            "${screenName}RxInteractor.java",
            "${screenName}Presenter.java",
            "${screenName}Router.java",
            "${screenName}ViewCallbacks.java"
        )
      }
      destinationDir.deleteRecursively()
    }
  }
}