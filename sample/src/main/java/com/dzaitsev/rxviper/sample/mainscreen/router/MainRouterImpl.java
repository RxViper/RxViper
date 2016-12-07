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

package com.dzaitsev.rxviper.sample.mainscreen.router;

import android.content.Intent;
import com.dzaitsev.rxviper.sample.mainscreen.MainActivity;
import com.dzaitsev.rxviper.sample.detailsscreen.DetailsActivity;
import com.dzaitsev.rxviper.sample.mainscreen.domain.CheeseViewModel;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Dec-07, 22:54
 */
public final class MainRouterImpl implements MainRouter {
  private final MainActivity activity;

  public MainRouterImpl(MainActivity activity) {
    this.activity = activity;
  }

  @Override
  public void navigateToDetails(CheeseViewModel model) {
    final Intent intent = new Intent(activity, DetailsActivity.class);
    intent.putExtra(DetailsActivity.NAME, model.getName());
    intent.putExtra(DetailsActivity.CHECKED, model.isChecked());
    intent.putExtra(DetailsActivity.TYPE, model.getType());
    activity.startActivity(intent);
  }
}
