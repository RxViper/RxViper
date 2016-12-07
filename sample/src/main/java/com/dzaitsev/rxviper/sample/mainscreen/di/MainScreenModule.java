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

package com.dzaitsev.rxviper.sample.mainscreen.di;

import com.dzaitsev.rxviper.sample.Job;
import com.dzaitsev.rxviper.sample.Main;
import com.dzaitsev.rxviper.sample.data.CheeseStorage;
import com.dzaitsev.rxviper.sample.data.DataModule;
import com.dzaitsev.rxviper.sample.mainscreen.MainActivity;
import com.dzaitsev.rxviper.sample.mainscreen.domain.CheeseMapper;
import com.dzaitsev.rxviper.sample.mainscreen.domain.GetCheesesInteractor;
import com.dzaitsev.rxviper.sample.mainscreen.presenter.MainPresenter;
import com.dzaitsev.rxviper.sample.mainscreen.router.MainRouter;
import com.dzaitsev.rxviper.sample.mainscreen.router.MainRouterImpl;
import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Dec-08, 00:11
 */
@Module(includes = DataModule.class)
public final class MainScreenModule {
  private MainActivity activity;

  public void setMainActivity(MainActivity activity) {
    this.activity = activity;
  }

  @Provides
  MainRouter provideMainRouter() {
    return new MainRouterImpl(activity);
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
}
