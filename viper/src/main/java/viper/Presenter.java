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

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmitriy Zaitsev
 * @since 2016-Feb-13, 22:33
 */
public abstract class Presenter<V extends ViewCallbacks, R extends Router> {
  private V mView;
  private R mRouter;

  public final void dropRouter() {
    onDropRouter();
    mRouter = null;
  }

  public final void dropView() {
    onDropView();
    mView = null;
  }

  public final R getRouter() {
    return mRouter;
  }

  public final V getView() {
    return mView;
  }

  public final void takeRouter(R router) {
    mRouter = router;
    onTakeRouter(mRouter);
  }

  public final void takeView(V view) {
    mView = view;
    onTakeView(mView);
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
}
