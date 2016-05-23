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

package rxviper.sample.presentation;

import javax.inject.Inject;
import javax.inject.Singleton;
import rxviper.sample.domain.GetCheesesInteractor;
import viper.Presenter;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Jun-07, 10:34
 */
@Singleton
class MainPresenter extends Presenter<MainViewCallbacks, MainRouter> {
  private final GetCheesesInteractor mCheesesInteractor;

  @Inject MainPresenter(GetCheesesInteractor getCheesesInteractor) {
    mCheesesInteractor = getCheesesInteractor;
  }

  void onItemClicked(CheeseViewModel model) {
    getRouter().navigateToDetails(model);
  }

  @Override protected void onDropView(MainViewCallbacks view) {
    /* The best place to unsubscribe all your interactors */
    mCheesesInteractor.unsubscribe();
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
}
