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
class MainPresenter extends ViperPresenter<MainViewCallbacks, MainRouter> {
  private final GetCheesesInteractor        mCheesesInteractor;
  private       Collection<CheeseViewModel> mCachedData;

  @Inject MainPresenter(GetCheesesInteractor getCheesesInteractor) {
    mCheesesInteractor = getCheesesInteractor;
  }

  @Override protected void onDropView(MainViewCallbacks view) {
    /* The best place to unsubscribe all your interactors */
    mCheesesInteractor.unsubscribe();
  }

  @Override protected void onTakeView(MainViewCallbacks view) {
    /* The place where you can set cached data */
    if (mCachedData != null) {
      view.onNewCheeses(mCachedData);
    }
  }

  /**
   * @param amount
   *     amount of items you want to get
   *
   * @throws RuntimeException
   *     if amount < 0
   */
  void fetchCheeses(int amount) {
    getView().showProgress();
    mCheesesInteractor.execute(
        // onNext
        cheeses -> {
          mCachedData = cheeses;
          getView().onNewCheeses(cheeses);
          getView().hideProgress();
        },
        // onError
        throwable -> {
          getView().showError();
          getView().hideProgress();
        },
        // parameter
        amount);
  }

  void onItemClicked(CheeseViewModel model) {
    getRouter().navigateToDetails(model);
  }
}
