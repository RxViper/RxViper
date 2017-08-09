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

package com.dzaitsev.rxviper.sample;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2017-Aug-07, 18:12
 */
class LooperExecutor implements Executor {
  private final Handler handler;

  static Executor mainThread() {
    return new LooperExecutor(Looper.getMainLooper());
  }

  LooperExecutor(Looper looper) {
    this.handler = new Handler(looper);
  }

  @Override
  public void execute(Runnable command) {
    handler.post(command);
  }
}
