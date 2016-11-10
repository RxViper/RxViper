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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
  @Rule public final ExpectedException thrown = ExpectedException.none();
  private Router                                    router;
  private BaseViperPresenter<ViewCallbacks, Router> viperPresenter;

  @Before
  public void setUp() {
    router = mock(Router.class);
    viperPresenter = spy(new BaseViperPresenter<>());
  }

  @Test
  public void shouldNotHaveRouter() {
    assertThat(viperPresenter.getRouter()).isNull();
    assertThat(viperPresenter.hasRouter()).isFalse();
  }

  @Test
  public void shouldTakeRouter() {
    viperPresenter.takeRouter(router);
    assertThat(viperPresenter.getRouter()).isEqualTo(router);
    assertThat(viperPresenter.hasRouter()).isTrue();
  }

  @Test
  public void shouldDropRouter() {
    viperPresenter.takeRouter(router);

    viperPresenter.dropRouter(router);
    assertThat(viperPresenter.getRouter()).isNull();
    assertThat(viperPresenter.hasRouter()).isFalse();
  }

  @Test
  public void shouldCallOnTakeRouter() {
    viperPresenter.takeRouter(router);
    verify(viperPresenter).onTakeRouter(router);
  }

  @Test
  public void shouldCallOnTakeRouterOncePerRouter() {
    viperPresenter.takeRouter(router);
    viperPresenter.takeRouter(router);
    verify(viperPresenter).onTakeRouter(router);
  }

  @Test
  public void shouldNotCallOnDropIfRouterIsNotAttached() {
    viperPresenter.dropRouter(router);
    verify(viperPresenter, never()).onDropRouter(router);
  }

  @Test
  public void shouldIgnoreOnDropIfRouterIsNotTheSame() {
    viperPresenter.takeRouter(router);

    final Router anotherRouter = mock(Router.class);
    viperPresenter.dropRouter(anotherRouter);
    verify(viperPresenter, never()).onDropRouter(router);
  }

  @Test
  public void shouldDropPreviousRouterWhenNewRouterIsTaken() {
    viperPresenter.takeRouter(router);

    final Router newRouter = mock(Router.class);
    viperPresenter.takeRouter(newRouter);
    verify(viperPresenter).onDropRouter(router);
  }

  @Test
  public void shouldCallOnTakeRouterAfterRouterIsTaken() {
    viperPresenter.dummy = false;
    viperPresenter.takeRouter(router);
  }

  @Test
  public void shouldCallOnDropRouterBeforeRouterIsDropped() {
    viperPresenter.dummy = false;
    viperPresenter.takeRouter(router);
    viperPresenter.dropRouter(router);
  }

  @Test
  public void takenRouterShouldNotBeNull() {
    thrown.expect(IllegalArgumentException.class);
    viperPresenter.takeRouter(null);
  }

  @Test
  public void droppedRouterShouldNotBeNull() {
    thrown.expect(IllegalArgumentException.class);
    viperPresenter.dropRouter(null);
  }

  @Test
  public void shouldBePresenter() {
    assertThat(viperPresenter).isInstanceOf(Presenter.class);
  }

  @Test
  public void constructorArgsShouldNotBeNull() {
    check(() -> new BaseViperPresenter<>((Router) null), true);
    check(() -> new BaseViperPresenter<>((ViewCallbacks) null), true);
    check(() -> new BaseViperPresenter<>(null, router), true);
    check(() -> new BaseViperPresenter<>(mock(ViewCallbacks.class), null), true);
  }

  @Test
  public void constructorShouldSetArgs() {
    check(() -> new BaseViperPresenter<>(router), false);
    check(() -> new BaseViperPresenter<>(mock(ViewCallbacks.class)), false);
    check(() -> new BaseViperPresenter<>(mock(ViewCallbacks.class), router), false);
  }

  private static void check(Runnable r, boolean shouldThrow) {
    boolean thrown;
    try {
      thrown = false;
      r.run();
    } catch (IllegalArgumentException e) {
      thrown = true;
    }
    if (shouldThrow) {
      assertThat(thrown).isTrue();
    } else {
      assertThat(thrown).isFalse();
    }
  }
}