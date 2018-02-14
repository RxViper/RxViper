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
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.dzaitsev.viper.Intrinsics.requireNotNull;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2017-Aug-15, 13:26
 */
class AlternateExecutor implements Executor {
  private final PriorityBlockingQueue<TimedDecorator> queue   = new PriorityBlockingQueue<>();
  private final AtomicInteger                         counter = new AtomicInteger();
  private final AtomicInteger                         wip     = new AtomicInteger();

  @Override
  public void execute(Runnable runnable) {
    requireNotNull(runnable, "runnable");

    final TimedDecorator timed = new TimedDecorator(runnable, System.currentTimeMillis(), counter.incrementAndGet());
    queue.add(timed);

    if (wip.getAndIncrement() == 0) {
      do {
        final TimedDecorator head = queue.poll();
        if (head != null) {
          head.run();
        }
      } while (wip.decrementAndGet() > 0);
    }
  }

  private static final class TimedDecorator implements Comparable<TimedDecorator>, Runnable {
    private final Runnable delegate;
    private final long     execTime;
    private final int      count;

    TimedDecorator(Runnable delegate, long execTime, int count) {
      requireNotNull(delegate, "delegate");
      this.delegate = delegate;
      this.execTime = execTime;
      this.count = count;
    }

    @Override
    public int compareTo(TimedDecorator that) {
      final int result = Long.compare(execTime, that.execTime);
      return result == 0 ? Integer.compare(count, that.count) : result;
    }

    @Override
    public void run() {
      delegate.run();
    }
  }
}
