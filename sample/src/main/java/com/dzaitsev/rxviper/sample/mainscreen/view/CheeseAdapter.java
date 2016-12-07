package com.dzaitsev.rxviper.sample.mainscreen.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.dzaitsev.rxviper.sample.databinding.ListItemBinding;
import com.dzaitsev.rxviper.sample.mainscreen.domain.CheeseViewModel;
import com.dzaitsev.rxviper.sample.mainscreen.presenter.MainPresenter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Dec-18, 12:58
 */
final class CheeseAdapter extends RecyclerView.Adapter<CheeseAdapter.CheeseViewHolder> {
  private final ArrayList<CheeseViewModel> models = new ArrayList<>();
  private final MainPresenter presenter;

  CheeseAdapter(MainPresenter presenter) {
    this.presenter = presenter;
    setHasStableIds(true);
  }

  @Override
  public CheeseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new CheeseViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.getContext())), presenter);
  }

  @Override
  public void onBindViewHolder(CheeseViewHolder holder, int position) {
    holder.bind(models.get(position));
  }

  @Override
  public long getItemId(int position) {
    return models.get(position)
        .getId();
  }

  @Override
  public int getItemCount() {
    return models.size();
  }

  void setModels(Collection<CheeseViewModel> models) {
    this.models.clear();
    this.models.addAll(models);
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
}
