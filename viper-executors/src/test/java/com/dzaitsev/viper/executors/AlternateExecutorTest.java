/*
 * Copyright 2018 Dmytro Zaitsev
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

import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2018-Feb-15, 01:54
 */
public class AlternateExecutorTest {
  @Test
  public void executesAllTasksOnTheSameThread() {
    class SimpleTask implements Runnable {
      private final long threadId;

      private SimpleTask(long threadId) {
        this.threadId = threadId;
      }

      @Override
      public void run() {
        if (threadId != Thread.currentThread().getId()) {
          fail();
        }
      }
    }

    final long id = Thread.currentThread().getId();
    final AlternateExecutor executor = new AlternateExecutor();

    for (int i = 0; i < 100; i++) {
      executor.execute(new SimpleTask(id));
    }
  }
}