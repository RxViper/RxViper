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

package com.dzaitsev.rxviper

import com.google.common.truth.Truth.assertThat
import rx.Observable
import rx.schedulers.Schedulers

internal class TestInteractor : Interactor<Int, String>(Schedulers.immediate(), Schedulers.immediate()) {
  override public fun createObservable(requestModel: Int?): Observable<String> = Observable.just(requestModel.toString())
}

internal class TestMapper : Mapper<Int, String>() {
  override fun map(entity: Int): String = entity.toString()
}

internal class TestPresenter : Presenter<ViewCallbacks> {
  internal val protectedView: ViewCallbacks?
    get() = view

  private var dummy: Boolean = true

  internal constructor(dummy: Boolean) {
    this.dummy = dummy
  }

  internal constructor(view: ViewCallbacks) : super(view)

  internal constructor() : super()

  public override fun onDropView(view: ViewCallbacks) {
    assertThatViewIsSet()
  }

  public override fun onTakeView(view: ViewCallbacks) {
    assertThatViewIsSet()
  }

  private fun assertThatViewIsSet() {
    if (!dummy) {
      assertThat(this.view).isNotNull()
      assertThat(hasView()).isTrue()
    }
  }
}

internal class TestViperPresenter : ViperPresenter<ViewCallbacks, Router> {
  internal val protectedRouter: Router?
    get() = router

  private var dummy: Boolean = true

  internal constructor(dummy: Boolean) {
    this.dummy = dummy
  }

  internal constructor(view: ViewCallbacks, router: Router) : super(view, router)

  internal constructor(router: Router) : super(router)

  internal constructor() : super()

  public override fun onDropRouter(router: Router) {
    assertThatRouterIsSet()
  }

  public override fun onTakeRouter(router: Router) {
    assertThatRouterIsSet()
  }

  private fun assertThatRouterIsSet() {
    if (!dummy) {
      assertThat(this.router).isNotNull()
      assertThat(hasRouter()).isTrue()
    }
  }
}
