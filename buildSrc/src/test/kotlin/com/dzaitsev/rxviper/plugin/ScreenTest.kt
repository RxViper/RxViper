package com.dzaitsev.rxviper.plugin

import com.google.common.truth.Truth.assertThat
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test


class ScreenTest {
  @Test
  fun `check defaults`() {
    // arrange
    val project = ProjectBuilder().build()
    // act
    val screen = Screen("Test", project.container(clazz<UseCase>()))
    // assert
    with(screen) {
      assertThat(packageName).isSameAs(RxViperExtension.packageName)
      assertThat(useLambdas).isSameAs(RxViperExtension.useLambdas)
      assertThat(justMvp).isSameAs(RxViperExtension.justMvp)
      assertThat(hasRouter).isSameAs(RxViperExtension.hasRouter)
      assertThat(skipExisting).isSameAs(RxViperExtension.skipExisting)
      assertThat(splitPackages).isSameAs(RxViperExtension.splitPackages)
      assertThat(withTests).isSameAs(RxViperExtension.withTests)
    }
  }
}