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

import java.lang.ref.WeakReference

/**
 * Contains view logic for preparing content for display (as received from the [Interactor]) and for reacting to user inputs (by
 * requesting new data from the Interactor).
 *
 * Contains additional routing logic for switching screens.
 *
 * @author Dmytro Zaitsev
 * @since 0.10.0
 */
abstract class ViperPresenter<V : ViewCallbacks, R : Router> : Presenter<V>() {
  /**
   * Returns the router managed by this presenter, or `null` if [takeRouter] has never been called, or after [dropRouter].
   *
   * You should always call [hasRouter] to check if the router is taken to avoid `NullPointerException`s.
   *
   * @return `null`, if router is not taken, otherwise the concrete router instance.
   *
   * @since 0.1.0
   */
  protected val router: R?
    get() = routerRef?.get()

  private var routerRef: WeakReference<R>? = null

  /**
   * Called to surrender control of taken router.
   *
   * It is expected that this method will be called with the same argument as [takeRouter]. Mismatched routers are ignored. This is to
   * provide protection in the not uncommon case that `dropRouter` and `takeRouter` are called out of order.
   *
   * Calls [onDropRouter] before the reference to the router is cleared.
   *
   * @param router
   *     router is going to be dropped
   *
   * @since 0.1.0
   */
  fun dropRouter(router: R) {
    requireNotNull(router)
    if (this.router === router) {
      onDropRouter(router)
      routerRef?.clear()
    }
  }

  /**
   * Checks if a router is attached to this presenter. You should always call this method before calling [router] to get the router
   * instance.
   *
   * @return `true` if presenter has attached router
   *
   * @since 0.7.0
   */
  fun hasRouter(): Boolean = router != null

  /**
   * Called to give this presenter control of a router.
   *
   * As soon as the reference to the router is assigned, it calls [onTakeRouter] callback.
   *
   * @param router
   *     router that will be returned from [router].
   *
   * @see dropRouter
   * @since 0.1.0
   */
  fun takeRouter(router: R) {
    requireNotNull(router)
    val currentRouter = this.router
    if (currentRouter !== router) {
      if (currentRouter != null) {
        dropRouter(currentRouter)
      }
      routerRef = WeakReference(router)
      onTakeRouter(router)
    }
  }

  /**
   * Called before router is dropped.
   *
   * @param router
   *     router is going to be dropped
   *
   * @see dropRouter
   * @since 0.7.0
   */
  protected open fun onDropRouter(router: R) {
  }

  /**
   * Called after router is taken.
   *
   * @param router
   *     router attached to this presenter
   *
   * @see takeRouter
   * @since 0.6.0
   */
  protected open fun onTakeRouter(router: R) {
  }
}
