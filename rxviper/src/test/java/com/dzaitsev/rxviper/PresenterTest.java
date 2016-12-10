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

import java.lang.reflect.Proxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-May-13, 12:31
 */
final class PresenterTest {
  private TestViewCallbacks view;
  private TestPresenter     presenter;

  @BeforeEach
  void setUp() {
    view = mock(TestViewCallbacks.class);
    presenter = spy(new TestPresenter());
  }

  @Test
  void viewShouldNeverBeNull() {
    assertThat(presenter.getView()).isNotNull();

    presenter.takeView(view);
    assertThat(presenter.getView()).isNotNull();

    presenter.dropView(view);
    assertThat(presenter.getView()).isNotNull();
  }

  @Test
  void shouldNotHaveView() {
    assertThat(presenter.hasView()).isFalse();
  }

  @Test
  void shouldTakeView() {
    presenter.takeView(view);
    assertThat(presenter.getView()).isEqualTo(view);
    assertThat(presenter.hasView()).isTrue();
  }

  @Test
  void shouldDropView() {
    presenter.takeView(view);

    presenter.dropView(view);
    assertThat(presenter.hasView()).isFalse();
  }

  @Test
  void shouldCallOnTakeView() {
    presenter.takeView(view);
    verify(presenter).onTakeView(view);
  }

  @Test
  void shouldCallOnTakeViewOncePerView() {
    presenter.takeView(view);
    presenter.takeView(view);
    verify(presenter).onTakeView(view);
  }

  @Test
  void shouldNotCallOnDropIfViewIsNotAttached() {
    presenter.dropView(view);
    verify(presenter, never()).onDropView(view);
  }

  @Test
  void shouldIgnoreOnDropIfViewIsNotTheSame() {
    presenter.takeView(view);

    final TestViewCallbacks anotherView = mock(TestViewCallbacks.class);
    presenter.dropView(anotherView);
    verify(presenter, never()).onDropView(view);
  }

  @Test
  void shouldDropPreviousViewWhenNewViewIsTaken() {
    presenter.takeView(view);

    final TestViewCallbacks newView = mock(TestViewCallbacks.class);
    presenter.takeView(newView);
    verify(presenter).onDropView(view);
  }

  @Test
  void shouldCallOnTakeViewAfterViewIsTaken() {
    presenter.dummy = false;
    presenter.takeView(view);
  }

  @Test
  void shouldCallOnDropViewBeforeViewIsDropped() {
    presenter.dummy = false;
    presenter.takeView(view);
    presenter.dropView(view);
  }

  @Test
  void takenViewShouldNotBeNull() {
    assertThrows(IllegalArgumentException.class, () -> presenter.takeView(null));
  }

  @Test
  void droppedViewShouldNotBeNull() {
    assertThrows(IllegalArgumentException.class, () -> presenter.dropView(null));
  }

  @Test
  void constructorViewShouldNotBeNull() {
    assertThrows(IllegalArgumentException.class, () -> new TestPresenter(null));
  }

  @Test
  void constructorShouldSetView() {
    new TestPresenter(view);
  }

  @Test
  void shouldReturnProxyView() {
    presenter.takeView(view);

    final TestViewCallbacks proxyView = presenter.getView();
    assertThat(proxyView).isNotSameAs(view);
    assertThat(Proxy.isProxyClass(proxyView.getClass())).isTrue();
    assertThat(Proxy.getInvocationHandler(proxyView)).isInstanceOf(NullObject.class);
  }

  @Test
  void proxyShouldWrapView() {
    final NullObject<TestViewCallbacks> nullObject = RxViper.getProxy(presenter.getView());
    assertThat(nullObject.get()).isNull();

    presenter.takeView(view);
    assertThat(nullObject.get()).isSameAs(view);

    presenter.dropView(view);
    assertThat(nullObject.get()).isNull();
  }
}