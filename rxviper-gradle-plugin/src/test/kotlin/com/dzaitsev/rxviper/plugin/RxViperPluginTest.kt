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

class RxViperPluginTest {
  private lateinit var project: Project

  @Before
  fun setUp() {
    project = ProjectBuilder().build()
    project.apply(mapOf("plugin" to aClass<RxViperPlugin>()))
  }

  @Test
  fun `plugin should add the extension`() {
    assertThat(project.extensions.getByName("rxViper")).isInstanceOf(aClass<RxViperExtension>())
  }

  @Test
  fun `plugin should add the task`() {
    with(project.tasks.getByName(RxViperTask.NAME)) {
      assertThat(this).isInstanceOf(aClass<RxViperTask>())
      assertThat(this.group).isEqualTo(RxViperTask.GROUP)
    }
  }
}
