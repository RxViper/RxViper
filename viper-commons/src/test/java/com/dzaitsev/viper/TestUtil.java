/*
 * Copyright 2016 Dmytro Zaitsev
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

package com.dzaitsev.viper;

import static com.google.common.truth.Truth.assertThat;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Nov-19, 23:35
 */

public final class TestUtil {
  static void assertThrows(Class<? extends Throwable> throwableClass, Runnable action) {
    check(action, true, throwableClass);
  }

  static void assertNotThrows(Class<? extends Throwable> throwableClass, Runnable action) {
    check(action, false, throwableClass);
  }

  public static void checkIllegalArgumentException(Runnable action) {
    assertThrows(IllegalArgumentException.class, action);
  }

  private static void check(Runnable action, boolean shouldThrow, Class<? extends Throwable> throwableClass) {
    boolean thrown = false;
    try {
      thrown = false;
      action.run();
    } catch (Throwable t) {
      if (throwableClass.isInstance(t)) {
        thrown = true;
      }
    }
    if (shouldThrow) {
      assertThat(thrown).isTrue();
    } else {
      assertThat(thrown).isFalse();
    }
  }
}
