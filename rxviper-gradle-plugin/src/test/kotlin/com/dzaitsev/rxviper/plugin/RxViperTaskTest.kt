package com.dzaitsev.rxviper.plugin

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RxViperTaskTest {
  @Test
  fun `group should be correct`() {
    assertThat(RxViperTask.GROUP).isEqualTo("RxViper")
  }
}