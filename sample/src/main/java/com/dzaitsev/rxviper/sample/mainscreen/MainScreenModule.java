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

package com.dzaitsev.rxviper.sample.mainscreen;

import com.dzaitsev.rxviper.sample.Job;
import com.dzaitsev.rxviper.sample.Main;
import com.dzaitsev.rxviper.sample.StartStop;
import com.dzaitsev.rxviper.sample.data.CheeseStorage;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import javax.inject.Provider;
import rx.Scheduler;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Dec-08, 00:11
 */
@Module
public abstract class MainScreenModule {
  @Provides
  @MainScreenScope
  static MainRouterImpl provideMainRouterImpl(Provider<MainActivity> activity, MainPresenter presenter) {
    return new MainRouterImpl(activity, presenter);
  }

  @Provides
  @MainScreenScope
  static GetCheesesInteractor provideGetCheeseInteractor(@Job Scheduler jobScheduler, @Main Scheduler mainScheduler,
      CheeseStorage storage, CheeseMapper mapper) {
    return new GetCheesesInteractor(jobScheduler, mainScheduler, storage, mapper);
  }

  @Provides
  @MainScreenScope
  static CheeseMapper provideCheeseMapper() {
    return new CheeseMapper();
  }

  @Provides
  @MainScreenScope
  static MainPresenter provideMainPresenter(GetCheesesInteractor interactor) {
    return new MainPresenter(interactor);
  }

  @Binds
  @MainScreenScope
  abstract MainRouter bindMainRouter(MainRouterImpl router);

  @Provides
  @IntoSet
  @MainScreenScope
  static StartStop bindRouter(MainRouterImpl router) {
    return router;
  }
}
