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

package viper;

import java.lang.ref.WeakReference;

/**
 * Contains view logic for preparing content for display (as received from the {@link Interactor}) and for reacting to
 * user inputs (by requesting new data from the Interactor).
 *
 * @author Dmitriy Zaitsev
 * @since 0.1.0
 */
public abstract class Presenter<V extends ViewCallbacks, R extends Router> {
  private WeakReference<V> mViewRef;
  private WeakReference<R> mRouterRef;

  /**
   * Called to surrender control of taken router.
   * <p>
   * Calls {@link #onDropRouter} before the reference to the router is cleared.
   *
   * @since 0.1.0
   */
  public final void dropRouter() {
    onDropRouter();
    releaseRouter();
  }

  /**
   * Called to surrender control of taken view.
   * <p>
   * Calls {@link #onDropView} before the reference to the view is cleared.
   *
   * @since 0.1.0
   */
  public final void dropView() {
    onDropView();
    releaseView();
  }

  /**
   * Returns the router managed by this presenter, or {@code null} if {@link #takeRouter} has never been called, or
   * after {@link #dropRouter}.
   * <p>
   * You should always call {@link #hasRouter} to check if the router is taken to avoid NullPointerExceptions.
   *
   * @return {@code null}, if router is not taken, otherwise the concrete router instance.
   *
   * @since 0.1.0
   */
  public final R getRouter() {
    return mRouterRef == null ? null : mRouterRef.get();
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
  public final V getView() {
    return mViewRef == null ? null : mViewRef.get();
  }

  /**
   * Called to give this presenter control of a router.
   * <p>
   * As soon as the reference to the router is assigned, it calls {@link #onTakeRouter} callback.
   *
   * @param router
   *     router that will be returned from {@link #getRouter()}.
   *
   * @since 0.1.0
   */
  public final void takeRouter(R router) {
    Preconditions.checkNotNull(router, "router");

    assignRouter(router);
    onTakeRouter(mRouterRef.get());
  }

  /**
   * Called to give this presenter control of a view.
   * <p>
   * As soon as the reference to the view is assigned, it calls {@link #onTakeView} callback.
   *
   * @param view
   *     view that will be returned from {@link #getView()}.
   *
   * @since 0.1.0
   */
  public final void takeView(V view) {
    Preconditions.checkNotNull(view, "view");

    assignView(view);
    onTakeView(mViewRef.get());
  }

  /**
   * Checks if a router is attached to this presenter. You should always call this method before calling {@link
   * #getRouter} to get the view instance.
   *
   * @since 0.7.0
   */
  public final boolean hasRouter() {
    return mRouterRef != null && mRouterRef.get() != null;
  }

  /**
   * Checks if a view is attached to this presenter. You should always call this method before calling {@link
   * #getView} to get the view instance.
   *
   * @since 0.7.0
   */
  public final boolean hasView() {
    return mViewRef != null && mViewRef.get() != null;
  }

  /** @since 0.7.0 */
  void releaseView() {
    if (mViewRef != null) {
      mViewRef.clear();
      mViewRef = null;
    }
  }

  /** @since 0.7.0 */
  void assignView(V view) {
    mViewRef = new WeakReference<>(view);
  }

  /** @since 0.7.0 */
  void assignRouter(R router) {
    mRouterRef = new WeakReference<>(router);
  }

  /** @since 0.7.0 */
  void releaseRouter() {
    if (mRouterRef != null) {
      mRouterRef.clear();
      mRouterRef = null;
    }
  }

  /**
   * Called before router is dropped.
   *
   * @see #dropRouter()
   * @since 0.6.0
   */
  protected void onDropRouter() {
  }

  /**
   * Called after router is taken.
   *
   * @see #takeRouter(Router)
   * @since 0.6.0
   */
  protected void onTakeRouter(R router) {
  }

  /**
   * Called before view is dropped.
   *
   * @see #dropView()
   * @since 0.6.0
   */
  protected void onDropView() {
  }

  /**
   * Called after view is taken.
   *
   * @see #takeView(ViewCallbacks)
   * @since 0.6.0
   */
  protected void onTakeView(V view) {
  }
}
