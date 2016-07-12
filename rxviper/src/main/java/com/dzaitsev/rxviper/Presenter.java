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

import java.lang.ref.WeakReference;

/**
 * Contains view logic for preparing content for display (as received from the {@link Interactor}) and for reacting to
 * user inputs (by requesting new data from the Interactor).
 *
 * @author Dmytro Zaitsev
 * @since 0.1.0
 */
public abstract class Presenter<V extends ViewCallbacks> {
  private WeakReference<V> mViewRef;

  /**
   * Called to surrender control of taken view.
   * <p>
   * It is expected that this method will be called with the same argument as {@link #takeView}. Mismatched views are
   * ignored. This is to provide protection in the not uncommon case that {@code dropView} and {@code takeView} are
   * called out of order.
   * <p>
   * Calls {@link #onDropView} before the reference to the view is cleared.
   *
   * @param view
   *     view is going to be dropped.
   *
   * @since 0.1.0
   */
  public final void dropView(V view) {
    Preconditions.checkNotNull(view, "view");

    if (getView() == view) {
      onDropView(view);
      releaseView();
    }
  }

  /**
   * Checks if a view is attached to this presenter. You should always call this method before calling {@link #getView}
   * to get the view instance.
   *
   * @return {@code true} if presenter has attached view
   *
   * @since 0.7.0
   */
  public final boolean hasView() {
    return mViewRef != null && mViewRef.get() != null;
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
  public final void takeView(V view) {
    Preconditions.checkNotNull(view, "view");

    final V currentView = getView();
    if (currentView != view) {
      if (currentView != null) {
        dropView(currentView);
      }
      assignView(view);
      onTakeView(view);
    }
  }

  /**
   * Returns the view managed by this presenter, or {@code null} if {@link #takeView} has never been called, or after
   * {@link #dropView}.
   * <p>
   * You should always call {@link #hasView} to check if the view is taken to avoid NullPointerExceptions.
   *
   * @return {@code null}, if view is not taken, otherwise the concrete view instance.
   *
   * @since 0.1.0
   */
  protected final V getView() {
    return mViewRef == null ? null : mViewRef.get();
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
  protected void onDropView(final V view) {
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
  protected void onTakeView(V view) {
  }

  /** @since 0.7.0 */
  void assignView(V view) {
    mViewRef = new WeakReference<>(view);
  }

  /** @since 0.7.0 */
  void releaseView() {
    if (mViewRef != null) {
      mViewRef.clear();
      mViewRef = null;
    }
  }
}
