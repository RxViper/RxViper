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
      assertThat(ise).isInstanceOf(clazz<IllegalStateException>())
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
    assertThat(extension).isInstanceOf(clazz<RxViperExtension>())
  }

  @Test
  fun `plugin should add the task`() {
    // arrange
    project.applyAndroidAppPlugin()
    // act
    project.applyRxViperPlugin()
    // assert
    project.afterEvaluate {
      val task = project.tasks.getByName(GenerateRxViperTask.NAME)
      assertThat(task).isInstanceOf(clazz<GenerateRxViperTask>())
      assertThat(task.group).isEqualTo(GenerateRxViperTask.GROUP)
    }
  }
}
