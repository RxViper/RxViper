package com.dzaitsev.rxviper;

import static com.google.common.truth.Truth.assertThat;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Nov-14, 02:08
 */
final class BasePresenter<V extends ViewCallbacks> extends Presenter<V> {
  boolean dummy = true;

  BasePresenter(V view) {
    super(view);
    assertThatViewIsSet(true);
  }

  BasePresenter() {}

  @Override
  protected void onDropView(V view) {
    super.onDropView(view);
    assertThatViewIsSet(false);
  }

  @Override
  protected void onTakeView(V view) {
    super.onTakeView(view);
    assertThatViewIsSet(false);
  }

  private void assertThatViewIsSet(boolean forceCheck) {
    if (forceCheck || !dummy) {
      assertThat(hasView()).isTrue();
      assertThat(getView()).isNotNull();
    }
  }
}
