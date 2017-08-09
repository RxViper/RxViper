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
import org.gradle.api.tasks.SourceSet
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class RxViperExtensionTest {
  @Test
  fun `check defaults`() {
    with(RxViperExtension(ProjectBuilder().build())) {
      assertThat(packageName).isEqualTo("generated")
      assertThat(useLambdas).isFalse()
      assertThat(includeInteractor).isTrue()
      assertThat(includeRouter).isTrue()
      assertThat(splitPackages).isTrue()
      assertThat(destinationDir).isEqualTo(project.file("src/${SourceSet.MAIN_SOURCE_SET_NAME}/java"))
    }
  }

  @Test
  fun `extension name should be correct`() {
    assertThat(RxViperExtension.NAME).isEqualTo("rxViper")
  }
}