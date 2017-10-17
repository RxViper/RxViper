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

package com.dzaitsev.viper;

import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2017-Aug-15, 18:32
 */
public class ViperInteractorTest {
  @Test
  public void testName() throws InterruptedException {
    final Lock lock = new ReentrantLock();
    final ViperInteractor<Void, String> interactor = new VoidStringViperInteractor();

    interactor.execute(item -> {
      System.out.println("~~~~~~~~~~~");
      assertThat(Thread.currentThread()
          .getName()).isEqualTo("jobExecutor_");
      lock.unlock();
    });
    lock.lock();
  }

  private static Executor newThreadExecutor(String name) {
    return runnable -> new Thread(runnable, name).start();
  }

  private static Executor newThreadExecutor2(String name) {
    return Runnable::run;
  }

  private static class VoidStringViperInteractor extends ViperInteractor<Void, String> {
    VoidStringViperInteractor() {
      super(ViperInteractorTest.newThreadExecutor("jobExecutor"), ViperInteractorTest.newThreadExecutor("resultExecutor"));
    }

    @Nonnull
    @Override
    protected String getData(@Nullable Void o) {
      return "Hello World";
    }
  }
}