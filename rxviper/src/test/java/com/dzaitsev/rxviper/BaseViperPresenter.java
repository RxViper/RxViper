package com.dzaitsev.rxviper;

import static com.google.common.truth.Truth.assertThat;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Nov-14, 02:08
 */
final class BaseViperPresenter<V extends ViewCallbacks, R extends Router> extends ViperPresenter<V, R> {
  boolean dummy = true;

  BaseViperPresenter(V view, R router) {
    super(view, router);
    assertThatRouterIsSet(true);
    assertThatViewIsSet(true);
  }

  BaseViperPresenter(R router) {
    super(router);
    assertThatRouterIsSet(true);
  }

  BaseViperPresenter(V view) {
    super(view);
    assertThatViewIsSet(true);
  }

  BaseViperPresenter() {}

  @Override
  protected void onDropRouter(R router) {
    super.onDropRouter(router);
    assertThatRouterIsSet(false);
  }

  @Override
  protected void onTakeRouter(R router) {
    super.onTakeRouter(router);
    assertThatRouterIsSet(false);
  }

  private void assertThatRouterIsSet(boolean forceCheck) {
    if (forceCheck || !dummy) {
      assertThat(hasRouter()).isTrue();
      assertThat(getRouter()).isNotNull();
    }
  }

  private void assertThatViewIsSet(boolean forceCheck) {
    if (forceCheck || !dummy) {
      assertThat(hasView()).isTrue();
      assertThat(getView()).isNotNull();
    }
  }
}
