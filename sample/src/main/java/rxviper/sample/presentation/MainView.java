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

import android.app.ProgressDialog;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collection;
import javax.inject.Inject;
import rxviper.sample.App;
import rxviper.sample.R;
import rxviper.sample.databinding.ListItemBinding;
import rxviper.sample.databinding.ViewMainBinding;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Jun-07, 12:33
 */
public class MainView extends ConstraintLayout implements MainViewCallbacks {
  @Inject MainPresenter  mPresenter;
  private ProgressDialog mProgressDialog;
  private CheeseAdapter  mAdapter;

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

      mProgressDialog = new ProgressDialog(context);
      mProgressDialog.setIndeterminate(true);
      mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      mProgressDialog.setCancelable(false);
      mProgressDialog.setMessage(getResources().getString(R.string.loading));
    }
  }

  @Override public void hideProgress() {
    if (mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
    }
  }

  @Override public void onNewCheeses(Collection<CheeseViewModel> cheeses) {
    mAdapter.setModels(cheeses);
    mAdapter.notifyDataSetChanged();
  }

  @Override public void showError() {
    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT)
        .show();
  }

  @Override public void showProgress() {
    if (!mProgressDialog.isShowing()) {
      mProgressDialog.show();
    }
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    /* The best place to attach the view to presenter */
    if (mPresenter != null) {
      mPresenter.takeView(this);
      mPresenter.takeRouter(((MainRouter) getContext()));
    }
  }

  @Override protected void onDetachedFromWindow() {
    /* The best place to detach the view from presenter */
    mPresenter.dropView(this);
    mPresenter.dropRouter(((MainRouter) getContext()));
    super.onDetachedFromWindow();
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    if (!isInEditMode()) {
      final ViewMainBinding binding = ViewMainBinding.bind(this);
      binding.btnLoadDataSuccess.setOnClickListener(v -> mPresenter.fetchCheeses(40));
      binding.btnLoadDataError.setOnClickListener(v -> mPresenter.fetchCheeses(-1));
      mAdapter = new CheeseAdapter(mPresenter);
      binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
      binding.recyclerView.setAdapter(mAdapter);
    }
  }

  static class CheeseViewHolder extends RecyclerView.ViewHolder {
    private final ListItemBinding mBinding;

    CheeseViewHolder(ListItemBinding binding, MainPresenter presenter) {
      super(binding.getRoot());
      mBinding = binding;
      mBinding.getRoot()
          .setOnClickListener(v -> presenter.onItemClicked(mBinding.getModel()));
    }

    void bind(CheeseViewModel model) {
      mBinding.setModel(model);
    }
  }

  @SuppressWarnings("WeakerAccess")
  static class CheeseAdapter extends RecyclerView.Adapter<CheeseViewHolder> {
    private final ArrayList<CheeseViewModel> mModels = new ArrayList<>();
    private final MainPresenter mPresenter;

    public CheeseAdapter(MainPresenter presenter) {
      mPresenter = presenter;
      setHasStableIds(true);
    }

    @Override public CheeseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new CheeseViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.getContext())), mPresenter);
    }

    @Override public void onBindViewHolder(CheeseViewHolder holder, int position) {
      holder.bind(mModels.get(position));
    }

    @Override public long getItemId(int position) {
      return mModels.get(position)
          .getId();
    }

    @Override public int getItemCount() {
      return mModels.size();
    }

    void setModels(Collection<CheeseViewModel> models) {
      mModels.clear();
      mModels.addAll(models);
    }
  }
}
