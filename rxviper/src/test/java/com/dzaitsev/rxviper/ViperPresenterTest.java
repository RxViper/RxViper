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
 * @since 2016-Jun-30, 11:01
 */
final class ViperPresenterTest {
  private TestRouter         router;
  private TestViperPresenter presenter;

  @BeforeEach
  void setUp() {
    router = mock(TestRouter.class);
    presenter = spy(new TestViperPresenter());
  }

  @Test
  void routerShouldNeverBeNull() {
    assertThat(presenter.getRouter()).isNotNull();

    presenter.takeRouter(router);
    assertThat(presenter.getRouter()).isNotNull();

    presenter.dropRouter(router);
    assertThat(presenter.getRouter()).isNotNull();
  }

  @Test
  void shouldNotHaveRouter() {
    assertThat(presenter.hasRouter()).isFalse();
  }

  @Test
  void shouldTakeRouter() {
    presenter.takeRouter(router);
    assertThat(presenter.getRouter()).isEqualTo(router);
    assertThat(presenter.hasRouter()).isTrue();
  }

  @Test
  void shouldDropRouter() {
    presenter.takeRouter(router);

    presenter.dropRouter(router);
    assertThat(presenter.hasRouter()).isFalse();
  }

  @Test
  void shouldCallOnTakeRouter() {
    presenter.takeRouter(router);
    verify(presenter).onTakeRouter(router);
  }

  @Test
  void shouldCallOnTakeRouterOncePerRouter() {
    presenter.takeRouter(router);
    presenter.takeRouter(router);
    verify(presenter).onTakeRouter(router);
  }

  @Test
  void shouldNotCallOnDropIfRouterIsNotAttached() {
    presenter.dropRouter(router);
    verify(presenter, never()).onDropRouter(router);
  }

  @Test
  void shouldIgnoreOnDropIfRouterIsNotTheSame() {
    presenter.takeRouter(router);

    final TestRouter anotherRouter = mock(TestRouter.class);
    presenter.dropRouter(anotherRouter);
    verify(presenter, never()).onDropRouter(router);
  }

  @Test
  void shouldDropPreviousRouterWhenNewRouterIsTaken() {
    presenter.takeRouter(router);

    final TestRouter newRouter = mock(TestRouter.class);
    presenter.takeRouter(newRouter);
    verify(presenter).onDropRouter(router);
  }

  @Test
  void shouldCallOnTakeRouterAfterRouterIsTaken() {
    presenter.dummy = false;
    presenter.takeRouter(router);
  }

  @Test
  void shouldCallOnDropRouterBeforeRouterIsDropped() {
    presenter.dummy = false;
    presenter.takeRouter(router);
    presenter.dropRouter(router);
  }

  @Test
  void takenRouterShouldNotBeNull() {
    assertThrows(IllegalArgumentException.class, () -> presenter.takeRouter(null));
  }

  @Test
  void droppedRouterShouldNotBeNull() {
    assertThrows(IllegalArgumentException.class, () ->presenter.dropRouter(null));
  }

  @Test
  void shouldBePresenter() {
    assertThat(presenter).isInstanceOf(Presenter.class);
  }

  @Test
  void constructorArgsShouldNotBeNull() {
    TestUtil.check(() -> new TestViperPresenter((TestRouter) null), true, IllegalArgumentException.class);
    TestUtil.check(() -> new TestViperPresenter((TestViewCallbacks) null), true, IllegalArgumentException.class);
    TestUtil.check(() -> new TestViperPresenter(null, router), true, IllegalArgumentException.class);
    TestUtil.check(() -> new TestViperPresenter(mock(TestViewCallbacks.class), null), true, IllegalArgumentException.class);
  }

  @Test
  void constructorShouldSetArgs() {
    TestUtil.check(() -> new TestViperPresenter(router), false, IllegalArgumentException.class);
    TestUtil.check(() -> new TestViperPresenter(mock(TestViewCallbacks.class)), false, IllegalArgumentException.class);
    TestUtil.check(() -> new TestViperPresenter(mock(TestViewCallbacks.class), router), false, IllegalArgumentException.class);
  }

  @Test
  void shouldReturnProxyRouter() {
    presenter.takeRouter(router);

    final TestRouter proxyRouter = presenter.getRouter();
    assertThat(proxyRouter).isNotSameAs(router);
    assertThat(Proxy.isProxyClass(proxyRouter.getClass())).isTrue();
    assertThat(Proxy.getInvocationHandler(proxyRouter)).isInstanceOf(NullObject.class);
  }

  @Test
  void proxyShouldWrapRouter() {
    final NullObject<TestRouter> nullObject = RxViper.getProxy(presenter.getRouter());
    assertThat(nullObject.get()).isNull();

    presenter.takeRouter(router);
    assertThat(nullObject.get()).isSameAs(router);

    presenter.dropRouter(router);
    assertThat(nullObject.get()).isNull();
  }
}