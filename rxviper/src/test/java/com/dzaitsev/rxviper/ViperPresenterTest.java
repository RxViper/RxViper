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
  private Router             dummyRouter;
  private TestViperPresenter spyViperPresenter;

  @Before
  public void setUp() {
    dummyRouter = mock(Router.class);
    spyViperPresenter = spy(new TestViperPresenter());
  }

  @Test
  public void constructorRouterShouldNotBeNull() {
    thrown.expect(IllegalArgumentException.class);
    new TestViperPresenter(null);
  }

  @Test
  public void constructorViewShouldNotBeNull() {
    thrown.expect(IllegalArgumentException.class);
    new TestViperPresenter(null, dummyRouter);
  }

  @Test
  public void constructorShouldSetRouter() {
    new TestViperPresenter(dummyRouter);
  }

  @Test
  public void constructorShouldSetViewAndRouter() {
    new TestViperPresenter(mock(ViewCallbacks.class), dummyRouter);
  }

  @Test
  public void routerShouldNeverBeNull() {
    assertThat(spyViperPresenter.getRouter()).isNotNull();

    spyViperPresenter.takeRouter(dummyRouter);
    assertThat(spyViperPresenter.getRouter()).isNotNull();

    spyViperPresenter.dropRouter(dummyRouter);
    assertThat(spyViperPresenter.getRouter()).isNotNull();
  }

  @Test
  public void shouldNotHaveRouter() {
    assertThat(spyViperPresenter.hasRouter()).isFalse();
  }

  @Test
  public void shouldTakeRouter() {
    spyViperPresenter.takeRouter(dummyRouter);
    assertThat(spyViperPresenter.getRouter()).isEqualTo(dummyRouter);
    assertThat(spyViperPresenter.hasRouter()).isTrue();
  }

  @Test
  public void shouldDropRouter() {
    spyViperPresenter.takeRouter(dummyRouter);

    spyViperPresenter.dropRouter(dummyRouter);
    assertThat(spyViperPresenter.hasRouter()).isFalse();
  }

  @Test
  public void shouldCallOnTakeRouter() {
    spyViperPresenter.takeRouter(dummyRouter);
    verify(spyViperPresenter).onTakeRouter(dummyRouter);
  }

  @Test
  public void shouldCallOnTakeRouterOncePerRouter() {
    spyViperPresenter.takeRouter(dummyRouter);
    spyViperPresenter.takeRouter(dummyRouter);
    verify(spyViperPresenter).onTakeRouter(dummyRouter);
  }

  @Test
  public void shouldNotCallOnDropIfRouterIsNotAttached() {
    spyViperPresenter.dropRouter(dummyRouter);
    verify(spyViperPresenter, never()).onDropRouter(dummyRouter);
  }

  @Test
  public void shouldIgnoreOnDropIfRouterIsNotTheSame() {
    spyViperPresenter.takeRouter(dummyRouter);

    final Router anotherRouter = mock(Router.class);
    spyViperPresenter.dropRouter(anotherRouter);
    verify(spyViperPresenter, never()).onDropRouter(dummyRouter);
  }

  @Test
  public void shouldDropPreviousRouterWhenNewRouterIsTaken() {
    spyViperPresenter.takeRouter(dummyRouter);

    final Router newRouter = mock(Router.class);
    spyViperPresenter.takeRouter(newRouter);
    verify(spyViperPresenter).onDropRouter(dummyRouter);
  }

  @Test
  public void shouldCallOnTakeRouterAfterRouterIsTaken() {
    spyViperPresenter.dummy = false;
    spyViperPresenter.takeRouter(dummyRouter);
  }

  @Test
  public void shouldCallOnDropRouterBeforeRouterIsDropped() {
    spyViperPresenter.dummy = false;
    spyViperPresenter.takeRouter(dummyRouter);
    spyViperPresenter.dropRouter(dummyRouter);
  }

  @Test
  public void takenRouterShouldNotBeNull() {
    thrown.expect(IllegalArgumentException.class);
    spyViperPresenter.takeRouter(null);
  }

  @Test
  public void droppedRouterShouldNotBeNull() {
    thrown.expect(IllegalArgumentException.class);
    spyViperPresenter.dropRouter(null);
  }

  private static class TestViperPresenter extends ViperPresenter<ViewCallbacks, Router> {
    boolean dummy = true;

    TestViperPresenter(ViewCallbacks view, Router router) {
      super(view, router);
      assertThatRouterIsSet();
      assertThat(hasView()).isTrue();
    }

    TestViperPresenter(Router router) {
      super(router);
      assertThatRouterIsSet();
    }

    TestViperPresenter() {}

    @Override
    protected void onDropRouter(Router router) {
      super.onDropRouter(router);
      assertThatRouterIsSet();
    }

    @Override
    protected void onTakeRouter(Router router) {
      super.onTakeRouter(router);
      assertThatRouterIsSet();
    }

    private void assertThatRouterIsSet() {
      if (!dummy) {
        assertThat(hasRouter()).isTrue();
      }
    }
  }
}