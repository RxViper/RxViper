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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Jun-30, 11:01
 */
public class ViperPresenterTest {
  @Mock Router                   mRouter;
  @Spy  ViperPresenter<ViewCallbacks, Router> mPresenter;

  @Before public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test public void shouldNotHaveRouter() {
    assertThat(mPresenter.getRouter()).isNull();
    assertThat(mPresenter.hasRouter()).isFalse();
  }

  @Test public void shouldTakeRouter() {
    mPresenter.takeRouter(mRouter);
    assertThat(mPresenter.getRouter()).isEqualTo(mRouter);
    assertThat(mPresenter.hasRouter()).isTrue();
  }

  @Test public void shouldDropRouter() {
    mPresenter.takeRouter(mRouter);

    mPresenter.dropRouter(mRouter);
    assertThat(mPresenter.getRouter()).isNull();
    assertThat(mPresenter.hasRouter()).isFalse();
  }

  @Test public void shouldCallOnTakeRouter() {
    mPresenter.takeRouter(mRouter);
    verify(mPresenter).onTakeRouter(mRouter);
  }

  @Test public void shouldCallOnTakeRouterOncePerView() {
    mPresenter.takeRouter(mRouter);
    mPresenter.takeRouter(mRouter);
    verify(mPresenter).onTakeRouter(mRouter);
  }

  @Test public void shouldNotCallOnDropIfRouterIsNotAttached() {
    mPresenter.dropRouter(mRouter);
    verify(mPresenter, never()).onDropRouter(mRouter);
  }

  @Test public void shouldIgnoreOnDropIfRouterIsNotTheSame() {
    mPresenter.takeRouter(mRouter);

    final Router anotherRouter = mock(Router.class);
    mPresenter.dropRouter(anotherRouter);
    verify(mPresenter, never()).onDropRouter(mRouter);
  }

  @Test public void shouldDropPreviousRouterWhenNewRouterIsTaken() {
    mPresenter.takeRouter(mRouter);

    final Router newRouter = mock(Router.class);
    mPresenter.takeRouter(newRouter);
    verify(mPresenter).onDropRouter(mRouter);
  }

  @Test public void shouldCallOnTakeRouterAfterRouterIsTaken() {
    mPresenter.takeRouter(mRouter);
    mPresenter.dropRouter(mRouter);

    final InOrder inOrder = Mockito.inOrder(mPresenter);
    (inOrder.verify(mPresenter)).assignRouter(mRouter);
    (inOrder.verify(mPresenter)).onTakeRouter(mRouter);
  }

  @Test public void shouldCallOnDropRouterBeforeRouterIsDropped() {
    mPresenter.takeRouter(mRouter);
    mPresenter.dropRouter(mRouter);

    final InOrder inOrder = Mockito.inOrder(mPresenter);
    (inOrder.verify(mPresenter)).onDropRouter(mRouter);
    (inOrder.verify(mPresenter)).releaseRouter();
  }

  @Test(expected = IllegalArgumentException.class) //
  public void takenRouterShouldNotBeNull() {
    mPresenter.takeRouter(null);
  }

  @Test(expected = IllegalArgumentException.class) //
  public void droppedRouterShouldNotBeNull() {
    mPresenter.dropRouter(null);
  }
}