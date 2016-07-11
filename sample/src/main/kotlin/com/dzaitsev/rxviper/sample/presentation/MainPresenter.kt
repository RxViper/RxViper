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

import com.dzaitsev.rxviper.ViperPresenter
import com.dzaitsev.rxviper.sample.domain.GetCheeseInteractor
import rx.functions.Action1
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Jun-07, 10:34
 */
@Singleton
internal class MainPresenter
@Inject constructor(private val getCheeseInteractor: GetCheeseInteractor) : ViperPresenter<MainViewCallbacks, MainRouter>() {
  private var cachedData: Collection<CheeseViewModel>? = null

  override fun onDropView(view: MainViewCallbacks) {
    /* The best place to unsubscribe all your interactors */
    getCheeseInteractor.unsubscribe()
  }

  override fun onTakeView(view: MainViewCallbacks) {
    /* The place where you can set cached data */
    cachedData?.let {
      view.onNewCheese(cachedData!!)
    }
  }

  /**
   * @param amount
   *     amount of items you want to get
   *
   * @throws RuntimeException
   *     if amount < 0
   */
  fun fetchCheese(amount: Int) {
    view.showProgress()
    getCheeseInteractor.execute(
        Action1<Collection<CheeseViewModel>> { cheese ->
          cachedData = cheese
          view.onNewCheese(cheese)
          view.hideProgress()
        },
        Action1<Throwable> {
          view.showError()
          view.hideProgress()
        },
        amount)
  }

  fun onItemClicked(model: CheeseViewModel) = router.navigateToDetails(model)
}
