package com.dzaitsev.rxviper.plugin

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RxViperExtensionTest {
  @Test
  fun `check defaults`() {
    with(RxViperExtension) {
      assertThat(packageName).isEqualTo("generated")
      assertThat(useLambdas).isFalse()
      assertThat(justMvp).isFalse()
      assertThat(hasRouter).isTrue()
      assertThat(skipExisting).isFalse()
      assertThat(splitPackages).isFalse()
      assertThat(withTests).isFalse()
    }
  }

  @Test
  fun `extension name should be correct`() {
    assertThat(RxViperExtension.NAME).isEqualTo("rxViper")
  }
}