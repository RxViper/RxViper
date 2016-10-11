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

package com.dzaitsev.rxviper.sample.presentation;

import com.dzaitsev.rxviper.ViperPresenter;
import com.dzaitsev.rxviper.sample.domain.GetCheesesInteractor;
import java.util.Collection;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Jun-07, 10:34
 */
@Singleton
final class MainPresenter extends ViperPresenter<MainViewCallbacks, MainRouter> {
  private final GetCheesesInteractor        interactor;
  private       Collection<CheeseViewModel> cachedData;

  @Inject MainPresenter(GetCheesesInteractor getCheesesInteractor) {
    interactor = getCheesesInteractor;
  }

  @Override protected void onDropView(MainViewCallbacks view) {
    /* The best place to unsubscribe all your interactors */
    interactor.unsubscribe();
  }

  @Override protected void onTakeView(MainViewCallbacks view) {
    /* The place where you can set cached data */
    if (cachedData != null) {
      view.onNewCheeses(cachedData);
    }
  }

  /**
   * @param count
   *     count of items you want to get
   *
   * @throws RuntimeException
   *     if amount < 0
   */
  void fetchCheeses(int count) {
    if (hasView()) {
      getView().showProgress();
    }
    interactor.execute(
        // onNext
        cheeses -> {
          cachedData = cheeses;
          if (hasView()) {
            getView().onNewCheeses(cheeses);
            getView().hideProgress();
          }
        },
        // onError
        throwable -> {
          if (hasView()) {
            getView().showError();
            getView().hideProgress();
          }
        },
        // parameter
        count);
  }

  void onItemClicked(CheeseViewModel model) {
    if (hasRouter()) {
      getRouter().navigateToDetails(model);
    }
  }
}
