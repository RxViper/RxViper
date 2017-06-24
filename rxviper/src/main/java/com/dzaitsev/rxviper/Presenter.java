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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.dzaitsev.rxviper.RxViper.getProxy;
import static com.dzaitsev.rxviper.RxViper.requireNotNull;

/**
 * Contains view logic for preparing content for display (as received from the {@link Interactor}) and for reacting to user inputs (by
 * requesting new data from the Interactor).
 *
 * @author Dmytro Zaitsev
 * @since 0.1.0
 */
public abstract class Presenter<V extends ViewCallbacks> {
  @Nonnull private final V viewProxy = RxViper.createView(null, getClass());

  /**
   * Creates a presenter with pre-attached view.
   * <p>
   * Doesn't call {@link #onTakeView} callback.
   *
   * @param view
   *     view that will be returned from {@link #getView()}.
   *
   * @since 0.11.0
   */
  protected Presenter(@Nonnull V view) {
    requireNotNull(view);
    getProxy(viewProxy).set(view);
  }

  /**
   * Creates a presenter without pre-attached view.
   *
   * @since 0.11.0
   */
  protected Presenter() {}

  /**
   * Called to surrender control of taken view.
   * <p>
   * It is expected that this method will be called with the same argument as {@link #takeView}. Mismatched views are ignored. This is
   * to provide protection in the not uncommon case that {@code dropView} and {@code takeView} are called out of order.
   * <p>
   * Calls {@link #onDropView} before the reference to the view is cleared.
   *
   * @param view
   *     view is going to be dropped.
   *
   * @since 0.1.0
   */
  public final void dropView(@Nonnull V view) {
    requireNotNull(view);

    if (currentView() == view) {
      onDropView(view);
      getProxy(viewProxy).clear();
    }
  }

  /**
   * Checks if a view is attached to this presenter.
   *
   * @return {@code true} if presenter has attached view
   *
   * @see #getView()
   * @since 0.7.0
   */
  public final boolean hasView() {
    return currentView() != null;
  }

  /**
   * Called to give this presenter control of a view.
   * <p>
   * As soon as the reference to the view is assigned, it calls {@link #onTakeView} callback.
   *
   * @param view
   *     view that will be returned from {@link #getView()}.
   *
   * @see #dropView(ViewCallbacks)
   * @since 0.1.0
   */
  public final void takeView(@Nonnull V view) {
    requireNotNull(view);

    final V currentView = currentView();
    if (currentView != view) {
      if (currentView != null) {
        dropView(currentView);
      }
      getProxy(viewProxy).set(view);
      onTakeView(view);
    }
  }

  /**
   * Returns the view managed by this presenter. You should always call {@link #hasView} to check if the view is taken to avoid no-op
   * behavior.
   *
   * @return an instance of a proxy class for the specified view interface
   *
   * @see #takeView(ViewCallbacks)
   * @since 0.1.0
   */
  @Nonnull
  protected final V getView() {
    return viewProxy;
  }

  /**
   * Called before view is dropped.
   *
   * @param view
   *     view is going to be dropped
   *
   * @see #dropView(ViewCallbacks)
   * @since 0.7.0
   */
  protected void onDropView(@Nonnull V view) {
  }

  /**
   * Called after view is taken.
   *
   * @param view
   *     attached to this presenter
   *
   * @see #takeView(ViewCallbacks)
   * @since 0.6.0
   */
  protected void onTakeView(@Nonnull V view) {
  }

  @Nullable
  private V currentView() {
    return getProxy(viewProxy).get();
  }
}
