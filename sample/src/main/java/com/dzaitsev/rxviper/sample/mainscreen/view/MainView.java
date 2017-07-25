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

package com.dzaitsev.rxviper.sample.mainscreen.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.widget.Toast;
import com.dzaitsev.rxviper.sample.R;
import com.dzaitsev.rxviper.sample.databinding.ViewMainBinding;
import com.dzaitsev.rxviper.sample.mainscreen.domain.CheeseViewModel;
import com.dzaitsev.rxviper.sample.mainscreen.presenter.MainPresenter;
import java.util.Collection;
import javax.inject.Inject;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Jun-07, 12:33
 */
public final class MainView extends ConstraintLayout implements MainViewCallbacks {
  private CheeseAdapter   adapter;
  private ViewMainBinding binding;

  public MainView(Context context) {
    this(context, null);
  }

  public MainView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MainView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public void hideProgress() {
    binding.progressBar.setVisibility(GONE);
  }

  @Override
  public void onNewCheeses(Collection<CheeseViewModel> cheeses) {
    adapter.setModels(cheeses);
    adapter.notifyDataSetChanged();
  }

  @Override
  public void showError() {
    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT)
        .show();
  }

  @Override
  public void showProgress() {
    binding.progressBar.setVisibility(VISIBLE);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    if (!isInEditMode()) {
      binding = ViewMainBinding.bind(this);
      binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
  }

  @Inject
  void setPresenter(MainPresenter presenter) {
    binding.btnLoadDataSuccess.setOnClickListener(v -> presenter.fetchCheeses(40));
    binding.btnLoadDataError.setOnClickListener(v -> presenter.fetchCheeses(-1));
    adapter = new CheeseAdapter(presenter);
    binding.recyclerView.setAdapter(adapter);
  }
}
