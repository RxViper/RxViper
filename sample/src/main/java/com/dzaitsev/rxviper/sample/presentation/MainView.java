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

import android.app.ProgressDialog;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import com.dzaitsev.rxviper.sample.App;
import com.dzaitsev.rxviper.sample.R;
import com.dzaitsev.rxviper.sample.databinding.ListItemBinding;
import com.dzaitsev.rxviper.sample.databinding.ViewMainBinding;
import java.util.ArrayList;
import java.util.Collection;
import javax.inject.Inject;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Jun-07, 12:33
 */
public final class MainView extends ConstraintLayout implements MainViewCallbacks {
  @Inject MainPresenter  presenter;
  private ProgressDialog progressDialog;
  private CheeseAdapter  adapter;

  public MainView(Context context) {
    this(context, null);
  }

  public MainView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MainView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    if (!isInEditMode()) {
      ((App) context.getApplicationContext()).getComponent()
          .inject(this);

      progressDialog = new ProgressDialog(context);
      progressDialog.setIndeterminate(true);
      progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      progressDialog.setCancelable(false);
      progressDialog.setMessage(getResources().getString(R.string.loading));
    }
  }

  @Override public void hideProgress() {
    if (progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
  }

  @Override public void onNewCheeses(Collection<CheeseViewModel> cheeses) {
    adapter.setModels(cheeses);
    adapter.notifyDataSetChanged();
  }

  @Override public void showError() {
    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT)
        .show();
  }

  @Override public void showProgress() {
    if (!progressDialog.isShowing()) {
      progressDialog.show();
    }
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    /* The best place to attach the view to presenter */
    if (presenter != null) {
      presenter.takeView(this);
      presenter.takeRouter((MainRouter) getContext());
    }
  }

  @Override protected void onDetachedFromWindow() {
    /* The best place to detach the view from presenter */
    presenter.dropView(this);
    presenter.dropRouter((MainRouter) getContext());
    super.onDetachedFromWindow();
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    if (!isInEditMode()) {
      final ViewMainBinding binding = ViewMainBinding.bind(this);
      binding.btnLoadDataSuccess.setOnClickListener(v -> presenter.fetchCheeses(40));
      binding.btnLoadDataError.setOnClickListener(v -> presenter.fetchCheeses(-1));
      adapter = new CheeseAdapter(presenter);
      binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
      binding.recyclerView.setAdapter(adapter);
    }
  }

  static final class CheeseViewHolder extends RecyclerView.ViewHolder {
    private final ListItemBinding binding;

    CheeseViewHolder(ListItemBinding binding, MainPresenter presenter) {
      super(binding.getRoot());
      this.binding = binding;
      this.binding.getRoot()
          .setOnClickListener(v -> presenter.onItemClicked(this.binding.getModel()));
    }

    void bind(CheeseViewModel model) {
      binding.setModel(model);
    }
  }

  @SuppressWarnings("WeakerAccess")
  static final class CheeseAdapter extends RecyclerView.Adapter<CheeseViewHolder> {
    private final ArrayList<CheeseViewModel> models = new ArrayList<>();
    private final MainPresenter presenter;

    CheeseAdapter(MainPresenter presenter) {
      this.presenter = presenter;
      setHasStableIds(true);
    }

    @Override public CheeseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new CheeseViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.getContext())), presenter);
    }

    @Override public void onBindViewHolder(CheeseViewHolder holder, int position) {
      holder.bind(models.get(position));
    }

    @Override public long getItemId(int position) {
      return models.get(position)
          .getId();
    }

    @Override public int getItemCount() {
      return models.size();
    }

    void setModels(Collection<CheeseViewModel> models) {
      this.models.clear();
      this.models.addAll(models);
    }
  }
}
