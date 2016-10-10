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

import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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

  @BeforeMethod public final void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @AfterMethod public final void tearDown() {
    dummyRouter = null;
    spyViperPresenter = null;
  }

  @Test(expectedExceptions = IllegalArgumentException.class) //
  public final void takenRouterShouldNotBeNull() {
    spyViperPresenter.takeRouter(null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class) //
  public final void droppedRouterShouldNotBeNull() {
    spyViperPresenter.dropRouter(null);
  }

  @Test public final void shouldNotHaveRouter() {
    assertThat(spyViperPresenter.getRouter()).isNull();
    assertThat(spyViperPresenter.hasRouter()).isFalse();
  }

  @Test(dependsOnMethods = { "shouldNotHaveRouter", "takenRouterShouldNotBeNull" }) //
  public final void shouldTakeRouter() {
    spyViperPresenter.takeRouter(dummyRouter);
    assertThat(spyViperPresenter.getRouter()).isEqualTo(dummyRouter);
    assertThat(spyViperPresenter.hasRouter()).isTrue();
  }

  @Test(dependsOnMethods = { "shouldTakeRouter", "droppedRouterShouldNotBeNull" }) //
  public final void shouldDropRouter() {
    spyViperPresenter.takeRouter(dummyRouter);

    spyViperPresenter.dropRouter(dummyRouter);
    assertThat(spyViperPresenter.getRouter()).isNull();
    assertThat(spyViperPresenter.hasRouter()).isFalse();
  }

  @Test(dependsOnMethods = "shouldTakeRouter") //
  public final void shouldCallOnTakeRouter() {
    spyViperPresenter.takeRouter(dummyRouter);
    verify(spyViperPresenter).onTakeRouter(dummyRouter);
  }

  @Test(dependsOnMethods = { "shouldTakeRouter", "shouldDropRouter" }) //
  public final void shouldCallOnDropRouter() {
    spyViperPresenter.takeRouter(dummyRouter);
    spyViperPresenter.dropRouter(dummyRouter);
    verify(spyViperPresenter).onDropRouter(dummyRouter);
  }

  @Test(dependsOnMethods = "shouldTakeRouter") //
  public final void shouldCallOnTakeRouterOncePerView() {
    spyViperPresenter.takeRouter(dummyRouter);
    spyViperPresenter.takeRouter(dummyRouter);
    verify(spyViperPresenter).onTakeRouter(dummyRouter);
  }

  @Test(dependsOnMethods = "shouldDropRouter") //
  public final void shouldNotCallOnDropIfRouterIsNotAttached() {
    spyViperPresenter.dropRouter(dummyRouter);
    verify(spyViperPresenter, never()).onDropRouter(dummyRouter);
  }

  @Test(dependsOnMethods = { "shouldTakeRouter", "shouldDropRouter" }) //
  public final void shouldIgnoreOnDropIfRouterIsNotTheSame() {
    spyViperPresenter.takeRouter(dummyRouter);

    final Router anotherRouter = mock(Router.class);
    spyViperPresenter.dropRouter(anotherRouter);
    verify(spyViperPresenter, never()).onDropRouter(dummyRouter);
  }

  @Test(dependsOnMethods = "shouldTakeRouter") //
  public final void shouldDropPreviousRouterWhenNewRouterIsTaken() {
    spyViperPresenter.takeRouter(dummyRouter);

    final Router newRouter = mock(Router.class);
    spyViperPresenter.takeRouter(newRouter);
    verify(spyViperPresenter).onDropRouter(dummyRouter);
  }

  @Test(dependsOnMethods = { "shouldCallOnTakeRouter", "shouldDropRouter" }) //
  public final void shouldCallOnTakeRouterAfterRouterIsTaken() {
    spyViperPresenter.takeRouter(dummyRouter);
    spyViperPresenter.dropRouter(dummyRouter);

    final InOrder inOrder = inOrder(spyViperPresenter);
    (inOrder.verify(spyViperPresenter)).assignRouter(dummyRouter);
    (inOrder.verify(spyViperPresenter)).onTakeRouter(dummyRouter);
  }

  @Test(dependsOnMethods = { "shouldTakeRouter", "shouldDropRouter" }) //
  public final void shouldCallOnDropRouterBeforeRouterIsDropped() {
    spyViperPresenter.takeRouter(dummyRouter);
    spyViperPresenter.dropRouter(dummyRouter);

    final InOrder inOrder = inOrder(spyViperPresenter);
    (inOrder.verify(spyViperPresenter)).onDropRouter(dummyRouter);
    (inOrder.verify(spyViperPresenter)).releaseRouter();
  }
}