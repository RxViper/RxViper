package com.dzaitsev.rxviper.plugin

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GenerateRxViperTaskTest {
  @Test
  fun `group should be correct`() {
    assertThat(GenerateRxViperTask.GROUP).isEqualTo("RxViper")
  }
}