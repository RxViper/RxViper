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
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test
import java.lang.IllegalStateException

class RxViperPluginTest {
  private lateinit var project: Project

  @Before
  fun setUp() {
    project = ProjectBuilder().build()
  }

  @Test
  fun `should fail without Android plugin applied`() {
    // arrange
    var thrown = false
    // act
    try {
      project.applyRxViperPlugin()
    } catch(e: Exception) {
      val ise = e.cause
      assertThat(ise).isInstanceOf(aClass<IllegalStateException>())
      assertThat(ise?.message).isEqualTo("RxViper plugin can only be applied to Android projects.\n" +
          "Required plugins: 'com.android.application' or 'com.android.library'.")
      thrown = true
    }
    // assert
    assertThat(thrown).isTrue()
  }

  @Test
  fun `should be applied with Android library plugin`() {
    // arrange
    project.applyAndroidLibraryPlugin()
    var thrown = false
    // act
    try {
      project.applyRxViperPlugin()
    } catch(e: Exception) {
      thrown = true
    }
    // assert
    assertThat(thrown).isFalse()
  }

  @Test
  fun `should be applied with Android application plugin`() {
    // arrange
    project.applyAndroidAppPlugin()
    var thrown = false
    // act
    try {
      project.applyRxViperPlugin()
    } catch(e: Exception) {
      thrown = true
    }
    // assert
    assertThat(thrown).isFalse()
  }

  @Test
  fun `plugin should add the extension`() {
    // arrange
    project.applyAndroidAppPlugin()
    // act
    project.applyRxViperPlugin()
    // assert
    val extension = project.extensions.getByName("rxViper")
    assertThat(extension).isInstanceOf(aClass<RxViperExtension>())
  }

  @Test
  fun `plugin should add the task`() {
    // arrange
    project.applyAndroidAppPlugin()
    // act
    project.applyRxViperPlugin()
    // assert
    project.afterEvaluate {
      val task = project.tasks.getByName(RxViperTask.NAME)
      assertThat(task).isInstanceOf(aClass<RxViperTask>())
      assertThat(task.group).isEqualTo(RxViperTask.GROUP)
    }
  }
}
