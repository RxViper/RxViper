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

package com.dzaitsev.rxviper.sample.presentation

import android.app.ProgressDialog
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.dzaitsev.rxviper.sample.App
import com.dzaitsev.rxviper.sample.R
import com.dzaitsev.rxviper.sample.databinding.ListItemBinding
import com.dzaitsev.rxviper.sample.databinding.ViewMainBinding
import java.util.ArrayList
import javax.inject.Inject

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Jun-07, 12:33
 */
class MainView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr), MainViewCallbacks {
  @Inject internal lateinit var presenter: MainPresenter
  private lateinit var progressDialog: ProgressDialog
  private lateinit var cheeseAdapter: CheeseAdapter

  init {
    if (!isInEditMode) {
      (context.applicationContext as App).component.inject(this)
      cheeseAdapter = CheeseAdapter(presenter)
      progressDialog = ProgressDialog(context).apply {
        with(this) {
          isIndeterminate = true
          setProgressStyle(ProgressDialog.STYLE_SPINNER)
          setCancelable(false)
          setMessage(resources.getString(R.string.loading))
        }
      }
    }
  }

  override fun hideProgress() {
    if (progressDialog.isShowing) progressDialog.dismiss()
  }

  override fun onNewCheese(cheese: Collection<CheeseViewModel>) {
    cheeseAdapter.setModels(cheese)
    cheeseAdapter.notifyDataSetChanged()
  }

  override fun showError() {
    Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
  }

  override fun showProgress() {
    if (!progressDialog.isShowing) progressDialog.show()
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    /* The best place to attach the view to presenter */
    if (!isInEditMode) {
      presenter.takeView(this)
      presenter.takeRouter(context as MainRouter)
    }
  }

  override fun onDetachedFromWindow() {
    /* The best place to detach the view from presenter */
    presenter.dropView(this)
    presenter.dropRouter(context as MainRouter)
    super.onDetachedFromWindow()
  }

  override fun onFinishInflate() {
    super.onFinishInflate()
    if (!isInEditMode) {
      with(ViewMainBinding.bind(this)) {
        btnLoadDataSuccess.setOnClickListener { presenter.fetchCheese(40) }
        btnLoadDataError.setOnClickListener { presenter.fetchCheese(-1) }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = cheeseAdapter
      }
    }
  }

  internal class CheeseViewHolder(private val binding: ListItemBinding, presenter: MainPresenter) : RecyclerView.ViewHolder(binding.root) {

    init {
      binding.root.setOnClickListener { presenter.onItemClicked(binding.model) }
    }

    fun bind(model: CheeseViewModel) {
      binding.model = model
    }
  }

  @SuppressWarnings("WeakerAccess")
  internal class CheeseAdapter(private val mPresenter: MainPresenter) : RecyclerView.Adapter<CheeseViewHolder>() {
    private val models = ArrayList<CheeseViewModel>()

    init {
      setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheeseViewHolder {
      return CheeseViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.context)), mPresenter)
    }

    override fun onBindViewHolder(holder: CheeseViewHolder, position: Int) = holder.bind(models[position])

    override fun getItemId(position: Int): Long = models[position].id

    override fun getItemCount(): Int = models.size

    fun setModels(models: Collection<CheeseViewModel>) {
      this.models.clear()
      this.models.addAll(models)
    }
  }
}
