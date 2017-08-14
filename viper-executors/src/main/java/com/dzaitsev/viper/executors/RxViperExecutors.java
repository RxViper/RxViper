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

import com.dzaitsev.viper.internal.Preconditions;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2017-Aug-07, 16:53
 */
public final class RxViperExecutors {
  private static final AtomicReference<RxViperExecutors> SELF = new AtomicReference<>();
  private final Executor instantExecutor;
  private final Executor alternateExecutor;

  public static Executor instant() {
    return getSelf().instantExecutor;
  }

  public static Executor alternate() {
    return getSelf().alternateExecutor;
  }

  private RxViperExecutors() {
    instantExecutor = new Executor() {
      @Override
      public void execute(@Nonnull Runnable runnable) {
        Preconditions.requireNotNull(runnable);
        runnable.run();
      }
    };

    alternateExecutor = new Executor() {
      private final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(4);
      private final AtomicInteger counter = new AtomicInteger();

      @Override
      public void execute(@Nonnull Runnable runnable) {
        Preconditions.requireNotNull(runnable);
        counter.incrementAndGet();
        queue.add(runnable);
        if (counter.getAndIncrement() == 0) {
          do {
            final Runnable head = queue.poll();
            if (head != null) {
              head.run();
            }
          } while (counter.decrementAndGet() > 0);
        }
      }
    };
  }

  private static RxViperExecutors getSelf() {
    for (; ; ) {
      RxViperExecutors current = SELF.get();
      if (current != null) {
        return current;
      }
      current = new RxViperExecutors();
      if (SELF.compareAndSet(null, current)) {
        return current;
      }
    }
  }
}
