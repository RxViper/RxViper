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
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.inOrder;
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
  @Mock Router                                dummyRouter;
  @Spy  ViperPresenter<ViewCallbacks, Router> spyViperPresenter;

  @Before public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test public void shouldNotHaveRouter() {
    assertThat(spyViperPresenter.getRouter()).isNull();
    assertThat(spyViperPresenter.hasRouter()).isFalse();
  }

  @Test public void shouldTakeRouter() {
    spyViperPresenter.takeRouter(dummyRouter);
    assertThat(spyViperPresenter.getRouter()).isEqualTo(dummyRouter);
    assertThat(spyViperPresenter.hasRouter()).isTrue();
  }

  @Test public void shouldDropRouter() {
    spyViperPresenter.takeRouter(dummyRouter);

    spyViperPresenter.dropRouter(dummyRouter);
    assertThat(spyViperPresenter.getRouter()).isNull();
    assertThat(spyViperPresenter.hasRouter()).isFalse();
  }

  @Test public void shouldCallOnTakeRouter() {
    spyViperPresenter.takeRouter(dummyRouter);
    verify(spyViperPresenter).onTakeRouter(dummyRouter);
  }

  @Test public void shouldCallOnTakeRouterOncePerView() {
    spyViperPresenter.takeRouter(dummyRouter);
    spyViperPresenter.takeRouter(dummyRouter);
    verify(spyViperPresenter).onTakeRouter(dummyRouter);
  }

  @Test public void shouldNotCallOnDropIfRouterIsNotAttached() {
    spyViperPresenter.dropRouter(dummyRouter);
    verify(spyViperPresenter, never()).onDropRouter(dummyRouter);
  }

  @Test public void shouldIgnoreOnDropIfRouterIsNotTheSame() {
    spyViperPresenter.takeRouter(dummyRouter);

    final Router anotherRouter = mock(Router.class);
    spyViperPresenter.dropRouter(anotherRouter);
    verify(spyViperPresenter, never()).onDropRouter(dummyRouter);
  }

  @Test public void shouldDropPreviousRouterWhenNewRouterIsTaken() {
    spyViperPresenter.takeRouter(dummyRouter);

    final Router newRouter = mock(Router.class);
    spyViperPresenter.takeRouter(newRouter);
    verify(spyViperPresenter).onDropRouter(dummyRouter);
  }

  @Test public void shouldCallOnTakeRouterAfterRouterIsTaken() {
    spyViperPresenter.takeRouter(dummyRouter);
    spyViperPresenter.dropRouter(dummyRouter);

    final InOrder inOrder = inOrder(spyViperPresenter);
    (inOrder.verify(spyViperPresenter)).assignRouter(dummyRouter);
    (inOrder.verify(spyViperPresenter)).onTakeRouter(dummyRouter);
  }

  @Test public void shouldCallOnDropRouterBeforeRouterIsDropped() {
    spyViperPresenter.takeRouter(dummyRouter);
    spyViperPresenter.dropRouter(dummyRouter);

    final InOrder inOrder = inOrder(spyViperPresenter);
    (inOrder.verify(spyViperPresenter)).onDropRouter(dummyRouter);
    (inOrder.verify(spyViperPresenter)).releaseRouter();
  }

  @Test(expected = IllegalArgumentException.class) //
  public void takenRouterShouldNotBeNull() {
    spyViperPresenter.takeRouter(null);
  }

  @Test(expected = IllegalArgumentException.class) //
  public void droppedRouterShouldNotBeNull() {
    spyViperPresenter.dropRouter(null);
  }
}