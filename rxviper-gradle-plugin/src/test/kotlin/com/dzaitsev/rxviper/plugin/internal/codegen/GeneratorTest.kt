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

package com.dzaitsev.rxviper.plugin.internal.codegen

import com.dzaitsev.rxviper.plugin.RxViperPlugin
import com.dzaitsev.rxviper.plugin.aClass
import com.dzaitsev.rxviper.plugin.internal.dsl.Screen
import com.dzaitsev.rxviper.plugin.internal.dsl.ScreenFactory
import com.google.common.truth.Truth.assertThat
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

abstract class GeneratorTest {
  @Rule
  @JvmField
  val temporaryFolder = TemporaryFolder()

  protected lateinit var project: Project

  @Before
  fun setUp() {
    project = ProjectBuilder()
        .withProjectDir(temporaryFolder.newFolder())
        .build()
    project.plugins.apply(aClass<RxViperPlugin>())
  }

  @Test
  fun `should generate code for default screen config`() {
    val factory = ScreenFactory(project)
    arrayOf(
        factory.create("Lorem"),
        factory.create("Ipsum").apply { routesTo = arrayOf("Foo", "Bar") }
    ).forEach { screen ->
      // given
      val sourceDir = temporaryFolder.newFolder()
      val printer = TestLog(screen.name)
      val generator = createGenerator(screen)
      generator.log = printer
      // when
      generator.saveTo(sourceDir)
      // then
      with(File(sourceDir, "${path(screen)}${generator.typeSpecName}.java")) {
        assertThat(exists()).isTrue()
        assertThat(readText()).isEqualTo(defaultSource(screen, generator))
      }
    }
  }

  @Test
  fun `should print correct log`() {
    // given
    val screen = ScreenFactory(project).create("PrintLog")
    val sourceDir = temporaryFolder.newFolder()
    val printer = TestLog(screen.name)
    val generator = createGenerator(screen)
    generator.log = printer
    // when
    generator.saveTo(sourceDir)
    // then
    assertThat(printer.message).isEqualTo("Generated ${screen.fullPackage}.${generator.typeSpecName}")
  }

  internal abstract fun defaultSource(screen: Screen, generator: Generator): String

  internal abstract fun createGenerator(screen: Screen): Generator

  internal fun generatedAnnotation(generator: Generator) = "@Generated(\n" +
      "    value = \"${generator.javaClass.name}\",\n" +
      "    date = \"${generator.dateTime}\",\n" +
      "    comments = \"Created by RxViper Gradle Plugin\"\n" +
      ")"

  internal fun packageLine(screen: Screen) = "package ${screen.fullPackage};"

  protected fun path(screen: Screen) = "${screen.fullPackage.replace('.', File.separatorChar)}${File.separatorChar}"
}