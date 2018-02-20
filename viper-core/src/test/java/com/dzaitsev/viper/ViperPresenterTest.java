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

package com.dzaitsev.viper;

import com.dzaitsev.nullobject.NullObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.dzaitsev.viper.TestUtil.assertNotThrows;
import static com.dzaitsev.viper.TestUtil.assertThrowsNPE;
import static com.google.common.truth.Truth.assertThat;
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
public final class ViperPresenterTest {
  private TestRouter         router;
  private TestViperPresenter presenter;

  @Before
  public void setUp() {
    router = mock(TestRouter.class);
    presenter = spy(new TestViperPresenter());
  }

  @Test
  public void routerShouldNeverBeNull() {
    assertThat(presenter.getRouter()).isNotNull();

    presenter.takeRouter(router);
    assertThat(presenter.getRouter()).isNotNull();

    presenter.dropRouter(router);
    assertThat(presenter.getRouter()).isNotNull();
  }

  @Test
  public void shouldNotHaveRouter() {
    assertThat(presenter.hasRouter()).isFalse();
  }

  @Test
  public void shouldTakeRouter() {
    presenter.takeRouter(router);
    assertThat(presenter.getRouter()).isEqualTo(router);
    assertThat(presenter.hasRouter()).isTrue();
  }

  @Test
  public void shouldDropRouter() {
    presenter.takeRouter(router);

    presenter.dropRouter(router);
    assertThat(presenter.hasRouter()).isFalse();
  }

  @Test
  public void shouldCallOnTakeRouter() {
    presenter.takeRouter(router);
    verify(presenter).onTakeRouter(router);
  }

  @Test
  public void shouldCallOnTakeRouterOncePerRouter() {
    presenter.takeRouter(router);
    presenter.takeRouter(router);
    verify(presenter).onTakeRouter(router);
  }

  @Test
  public void shouldNotCallOnDropIfRouterIsNotAttached() {
    presenter.dropRouter(router);
    verify(presenter, never()).onDropRouter(router);
  }

  @Test
  public void shouldIgnoreOnDropIfRouterIsNotTheSame() {
    presenter.takeRouter(router);

    final TestRouter anotherRouter = mock(TestRouter.class);
    presenter.dropRouter(anotherRouter);
    verify(presenter, never()).onDropRouter(router);
  }

  @Test
  public void shouldDropPreviousRouterWhenNewRouterIsTaken() {
    presenter.takeRouter(router);

    final TestRouter newRouter = mock(TestRouter.class);
    presenter.takeRouter(newRouter);
    verify(presenter).onDropRouter(router);
  }

  @Test
  public void shouldCallOnTakeRouterAfterRouterIsTaken() {
    presenter.dummy = false;
    presenter.takeRouter(router);
  }

  @Test
  public void shouldCallOnDropRouterBeforeRouterIsDropped() {
    presenter.dummy = false;
    presenter.takeRouter(router);
    presenter.dropRouter(router);
  }

  @Test
  public void takenRouterShouldNotBeNull() {
    assertThrowsNPE(() -> presenter.takeRouter(null));
  }

  @Test
  public void droppedRouterShouldNotBeNull() {
    assertThrowsNPE(() -> presenter.dropRouter(null));
  }

  @Test
  public void shouldBePresenter() {
    assertThat(presenter).isInstanceOf(Presenter.class);
  }

  @Test
  public void constructorArgsShouldNotBeNull() {
    assertThrowsNPE(() -> new TestViperPresenter((TestRouter) null));
    assertThrowsNPE(() -> new TestViperPresenter((TestViewCallbacks) null));
    assertThrowsNPE(() -> new TestViperPresenter(null, router));
    assertThrowsNPE(() -> new TestViperPresenter(mock(TestViewCallbacks.class), null));
  }

  @Test
  public void constructorShouldSetArgs() {
    assertNotThrows(IllegalArgumentException.class, () -> new TestViperPresenter(router));
    assertNotThrows(IllegalArgumentException.class, () -> new TestViperPresenter(mock(TestViewCallbacks.class)));
    assertNotThrows(IllegalArgumentException.class, () -> new TestViperPresenter(mock(TestViewCallbacks.class), router));
  }

  @Test
  public void shouldReturnProxyRouter() {
    presenter.takeRouter(router);

    final TestRouter proxyRouter = presenter.getRouter();
    assertThat(proxyRouter).isNotSameAs(router);
    assertThat(NullObject.isWrapped(proxyRouter.getClass())).isTrue();
    assertThat(NullObject.unwrap(proxyRouter)).isInstanceOf(NullObject.class);
  }

  @Test
  public void proxyShouldWrapRouter() {
    final NullObject<TestRouter> nullObject = NullObject.unwrap(presenter.getRouter());
    assertThat(nullObject.get()).isNull();

    presenter.takeRouter(router);
    assertThat(nullObject.get()).isSameAs(router);

    presenter.dropRouter(router);
    assertThat(nullObject.get()).isNull();
  }

  @Ignore("Probably should be moved to null-object")
  @Test
  public void shouldCreateProxyRouter() {
    class TestRouterImpl implements TestRouter {}
    final TestRouter router = new TestRouterImpl();
    final TestRouter proxyRouter = NullObject.createProxy(router, ViperPresenter.class, TestViperPresenter.class, 1);
    assertThat(NullObject.isWrapped(proxyRouter.getClass())).isTrue();
    final NullObject handler = NullObject.unwrap(proxyRouter);
    assertThat(handler).isInstanceOf(NullObject.class);
    assertThat(handler.get()).isSameAs(router);
  }
}