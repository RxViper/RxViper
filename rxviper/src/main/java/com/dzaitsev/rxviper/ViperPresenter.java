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

package com.dzaitsev.rxviper;

/**
 * Contains view logic for preparing content for display (as received from the {@link Interactor}) and for reacting to user inputs (by
 * requesting new data from the Interactor).
 * <p>
 * Contains additional routing logic for switching screens.
 *
 * @author Dmytro Zaitsev
 * @since 0.10.0
 */
public abstract class ViperPresenter<V extends ViewCallbacks, R extends Router> extends Presenter<V> {
  private final R proxiedRouter = RxViper.createRouter(null, getClass());

  /**
   * Creates a presenter with pre-attached view and router.
   * <p>
   * Doesn't call {@link #onTakeView} and {@link #onTakeRouter} callbacks.
   *
   * @param view
   *     view that will be returned from {@link #getView()}.
   * @param router
   *     router that will be returned from {@link #getRouter()}.
   *
   * @since 0.11.0
   */
  protected ViperPresenter(V view, R router) {
    super(view);
    RxViper.requireNotNull(router);
    RxViper.getProxy(proxiedRouter).set(router);
  }

  /**
   * Creates a presenter with pre-attached router.
   * <p>
   * Doesn't call {@link #onTakeRouter} callback.
   *
   * @param router
   *     router that will be returned from {@link #getRouter()}.
   *
   * @since 0.11.0
   */
  protected ViperPresenter(R router) {
    RxViper.requireNotNull(router);
    RxViper.getProxy(proxiedRouter).set(router);
  }

  /**
   * Creates a presenter without pre-attached view and router.
   *
   * @since 0.11.0
   */
  protected ViperPresenter() {}

  /**
   * Called to surrender control of taken router.
   * <p>
   * It is expected that this method will be called with the same argument as {@link #takeRouter}. Mismatched routers are ignored. This
   * is to provide protection in the not uncommon case that {@code dropRouter} and {@code takeRouter} are called out of order.
   * <p>
   * Calls {@link #onDropRouter} before the reference to the router is cleared.
   *
   * @param router
   *     router is going to be dropped
   *
   * @since 0.1.0
   */
  public final void dropRouter(R router) {
    RxViper.requireNotNull(router);

    if (currentRouter() == router) {
      onDropRouter(router);
      RxViper.getProxy(proxiedRouter).clear();
    }
  }

  /**
   * Checks if a router is attached to this presenter. You should always call this method before calling {@link #getRouter} to get the
   * router instance.
   *
   * @return {@code true} if presenter has attached router
   *
   * @since 0.7.0
   */
  public final boolean hasRouter() {
    return currentRouter() != null;
  }

  /**
   * Called to give this presenter control of a router.
   * <p>
   * As soon as the reference to the router is assigned, it calls {@link #onTakeRouter} callback.
   *
   * @param router
   *     router that will be returned from {@link #getRouter()}.
   *
   * @see #dropRouter(Router)
   * @since 0.1.0
   */
  public final void takeRouter(R router) {
    RxViper.requireNotNull(router);

    final R currentRouter = currentRouter();
    if (currentRouter != router) {
      if (currentRouter != null) {
        dropRouter(currentRouter);
      }
      RxViper.getProxy(proxiedRouter).set(router);
      onTakeRouter(router);
    }
  }

  /**
   * Returns the router managed by this presenter, or {@code null} if {@link #takeRouter} has never been called, or after
   * {@link #dropRouter}.
   * <p>
   * You should always call {@link #hasRouter} to check if the router is taken to avoid {@code NullPointerException}s.
   *
   * @return {@code null}, if router is not taken, otherwise the concrete router instance.
   *
   * @since 0.1.0
   */
  protected final R getRouter() {
    return proxiedRouter;
  }

  /**
   * Called before router is dropped.
   *
   * @param router
   *     router is going to be dropped
   *
   * @see #dropRouter(Router)
   * @since 0.7.0
   */
  protected void onDropRouter(R router) {
  }

  /**
   * Called after router is taken.
   *
   * @param router
   *     router attached to this presenter
   *
   * @see #takeRouter(Router)
   * @since 0.6.0
   */
  protected void onTakeRouter(R router) {
  }

  private R currentRouter() {
    return RxViper.getProxy(proxiedRouter).get();
  }
}
