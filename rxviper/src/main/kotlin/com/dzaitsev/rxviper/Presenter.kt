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

import com.dzaitsev.rxviper.RxViper.getProxy

/**
 * Contains view logic for preparing content for display (as received from the [Interactor]) and for reacting to user inputs (by
 * requesting new data from the Interactor).
 *
 * @author Dmytro Zaitsev
 * @since 0.1.0
 */
abstract class Presenter<V : ViewCallbacks> {
  /**
   * Returns the view managed by this presenter, or `null` if [takeView] has never been called, or after [dropView].
   *
   * You should always call [hasView] to check if the view is taken to avoid `NullPointerException`s.
   *
   * @return `null`, if view is not taken, otherwise the concrete view instance.
   *
   * @since 0.1.0
   */
  protected val view: V = RxViper.createView<V>(null, javaClass)

  private var currentView: V? = null
    get() = getProxy(view).get()

  protected constructor(view: V) {
    getProxy(this.view).set(view)
  }

  protected constructor()

  /**
   * Called to surrender control of taken view.
   *
   * It is expected that this method will be called with the same argument as [takeView]. Mismatched views are ignored. This is to provide
   * protection in the not uncommon case that `dropView` and `takeView` are called out of order.
   *
   * Calls [onDropView] before the reference to the view is cleared.
   *
   * @param view
   *     view is going to be dropped.
   *
   * @since 0.1.0
   */
  fun dropView(view: V) {
    if (currentView === view) {
      onDropView(view)
      getProxy(this.view).clear()
    }
  }

  /**
   * Checks if a view is attached to this presenter. You should always call this method before calling [view] to get the view instance.
   *
   * @return `true` if presenter has attached view
   *
   * @since 0.7.0
   */
  fun hasView(): Boolean = currentView != null

  /**
   * Called to give this presenter control of a view.
   *
   * As soon as the reference to the view is assigned, it calls [onTakeView] callback.
   *
   * @param view
   *     view that will be returned from [view].
   *
   * @see dropView
   * @since 0.1.0
   */
  fun takeView(view: V) {
    val currentView = this.currentView
    if (currentView !== view) {
      if (currentView != null) {
        dropView(currentView)
      }
      getProxy(this.view).set(view)
      onTakeView(view)
    }
  }

  /**
   * Called before view is dropped.
   *
   * @param view
   *     view is going to be dropped
   *
   * @see dropView
   * @since 0.7.0
   */
  protected open fun onDropView(view: V) {
  }

  /**
   * Called after view is taken.
   *
   * @param view
   *     attached to this presenter
   *
   * @see takeView
   * @since 0.6.0
   */
  protected open fun onTakeView(view: V) {
  }
}
