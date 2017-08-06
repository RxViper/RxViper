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

import android.content.Intent;
import com.dzaitsev.rxviper.sample.detailsscreen.DetailsActivity;
import javax.inject.Provider;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Dec-07, 22:54
 */
final class MainRouterImpl implements MainRouter {
  private final Provider<MainActivity> activity;
  private final MainPresenter          presenter;

  MainRouterImpl(Provider<MainActivity> activity, MainPresenter presenter) {
    this.activity = activity;
    this.presenter = presenter;
  }

  @Override
  public void navigateToDetails(CheeseViewModel model) {
    final MainActivity activity = this.activity.get();
    activity.startActivity(new Intent(activity, DetailsActivity.class).putExtra(DetailsActivity.NAME, model.getName())
        .putExtra(DetailsActivity.CHECKED, model.isChecked())
        .putExtra(DetailsActivity.TYPE, model.getType()));
  }

  @Override
  public void onStart() {
    presenter.takeRouter(this);
  }

  @Override
  public void onStop() {
    presenter.dropRouter(this);
  }
}
