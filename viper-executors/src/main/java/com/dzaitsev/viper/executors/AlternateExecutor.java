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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;

import static com.dzaitsev.viper.Preconditions.requireNotNull;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2017-Aug-15, 13:26
 */
class AlternateExecutor implements Executor {
  private final BlockingQueue<Runnable> queue   = new ArrayBlockingQueue<>(4);
  private final AtomicInteger           counter = new AtomicInteger();

  @Override
  public void execute(@Nonnull Runnable runnable) {
    requireNotNull(runnable);
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
}
