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

import android.content.res.Resources;
import com.dzaitsev.rxviper.sample.mainscreen.MainScreenSubcomponent;
import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Jun-07, 12:24
 */
@Module(subcomponents = MainScreenSubcomponent.class)
abstract class AppModule {
  @Provides
  @Job
  static Scheduler provideJobScheduler() {
    return Schedulers.computation();
  }

  @Provides
  @Main
  static Scheduler provideMainScheduler() {
    return AndroidSchedulers.mainThread();
  }

  @Provides
  static Resources provideResources(App app) {
    return app.getResources();
  }
}
