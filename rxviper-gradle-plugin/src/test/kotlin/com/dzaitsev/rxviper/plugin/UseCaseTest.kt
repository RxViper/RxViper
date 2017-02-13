package com.dzaitsev.rxviper.plugin

import com.dzaitsev.rxviper.plugin.internal.dsl.UseCase
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UseCaseTest {

  @Test
  fun `use case name should be capitalized`() {
    val name = "CamelCase"
    assertThat(UseCase(name.decapitalize()).name).isEqualTo(name)
    assertThat(UseCase(name).name).isEqualTo(name)
  }
}