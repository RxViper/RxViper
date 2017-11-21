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

package com.dzaitsev.viper.executors;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2017-Aug-07, 16:53
 */
public final class ViperExecutors {
  private static final AtomicReference<ViperExecutors> SELF = new AtomicReference<>();
  @Nonnull private final Executor instantExecutor;
  @Nonnull private final Executor alternateExecutor;

  @Nonnull
  public static Executor instant() {
    return getSelf().instantExecutor;
  }

  @Nonnull
  public static Executor alternate() {
    return getSelf().alternateExecutor;
  }

  private ViperExecutors() {
    instantExecutor = new InstantExecutor();
    alternateExecutor = new AlternateExecutor();
  }

  @Nonnull
  private static ViperExecutors getSelf() {
    while (true) {
      ViperExecutors self = SELF.get();
      if (self != null) {
        return self;
      }
      self = new ViperExecutors();
      if (SELF.compareAndSet(null, self)) {
        return self;
      }
    }
  }
}
