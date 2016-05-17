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
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmitriy Zaitsev
 * @since 2016-Feb-13, 22:33
 */
public abstract class Presenter<V extends ViewCallbacks, R extends Router> {
  private WeakReference<V> mViewRef;
  private WeakReference<R> mRouterRef;

  public final void dropRouter() {
    onDropRouter();
    releaseRouter();
  }

  public final void dropView() {
    onDropView();
    releaseView();
  }

  public final R getRouter() {
    return mRouterRef == null ? null : mRouterRef.get();
  }

  public final V getView() {
    return mViewRef == null ? null : mViewRef.get();
  }

  public final void takeRouter(R router) {
    Preconditions.checkNotNull(router, "router");

    assignRouter(router);
    onTakeRouter(mRouterRef.get());
  }

  public final void takeView(V view) {
    Preconditions.checkNotNull(view, "view");

    assignView(view);
    onTakeView(mViewRef.get());
  }

  public final boolean hasRouter() {
    return mRouterRef != null && mRouterRef.get() != null;
  }

  public final boolean hasView() {
    return mViewRef != null && mViewRef.get() != null;
  }

  /** Called before Router is dropped */
  protected void onDropRouter() {
  }

  /** Called after Router is taken */
  protected void onTakeRouter(R router) {
  }

  /** Called before View is dropped */
  protected void onDropView() {
  }

  /** Called after View is taken */
  protected void onTakeView(V view) {
  }

  void releaseView() {
    if (mViewRef != null) {
      mViewRef.clear();
      mViewRef = null;
    }
  }

  void assignView(V view) {
    mViewRef = new WeakReference<>(view);
  }

  void assignRouter(R router) {
    mRouterRef = new WeakReference<>(router);
  }

  void releaseRouter() {
    if (mRouterRef != null) {
      mRouterRef.clear();
      mRouterRef = null;
    }
  }
}
