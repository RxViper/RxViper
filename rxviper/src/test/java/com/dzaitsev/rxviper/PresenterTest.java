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
 * @since 2016-May-13, 12:31
 */
public class PresenterTest {
  @Mock ViewCallbacks            dummyView;
  @Spy  Presenter<ViewCallbacks> spyPresenter;

  @BeforeMethod public final void setUp() {
    System.out.println("PresenterTest.setUp");
    MockitoAnnotations.initMocks(this);
  }

  @AfterMethod public final void tearDown() {
    System.out.println("PresenterTest.tearDown");
    dummyView = null;
    spyPresenter = null;
  }

  @Test(expectedExceptions = IllegalArgumentException.class, dependsOnMethods = "shouldNotHaveView") //
  public final void takenViewShouldNotBeNull() {
    System.out.println("PresenterTest.takenViewShouldNotBeNull");
    spyPresenter.takeView(null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class) //
  public final void droppedViewShouldNotBeNull() {
    System.out.println("PresenterTest.droppedViewShouldNotBeNull");
    spyPresenter.dropView(null);
  }

  @Test public final void shouldNotHaveView() {
    System.out.println("PresenterTest.shouldNotHaveView");
    assertThat(spyPresenter.getView()).isNull();
    assertThat(spyPresenter.hasView()).isFalse();
  }

  @Test(dependsOnMethods = { "shouldNotHaveView", "takenViewShouldNotBeNull" }) //
  public final void shouldTakeView() {
    System.out.println("PresenterTest.shouldTakeView");
    spyPresenter.takeView(dummyView);
    assertThat(spyPresenter.getView()).isEqualTo(dummyView);
    assertThat(spyPresenter.hasView()).isTrue();
  }

  @Test(dependsOnMethods = { "shouldTakeView", "droppedViewShouldNotBeNull" }) //
  public final void shouldDropView() {
    System.out.println("PresenterTest.shouldDropView");
    spyPresenter.takeView(dummyView);
    spyPresenter.dropView(dummyView);
    assertThat(spyPresenter.getView()).isNull();
    assertThat(spyPresenter.hasView()).isFalse();
  }

  @Test(dependsOnMethods = "shouldTakeView") //
  public final void shouldCallOnTakeView() {
    System.out.println("PresenterTest.shouldCallOnTakeView");
    spyPresenter.takeView(dummyView);
    verify(spyPresenter).onTakeView(dummyView);
  }

  @Test(dependsOnMethods = { "shouldTakeView", "shouldDropView" }) //
  public final void shouldCallOnDropView() {
    System.out.println("PresenterTest.shouldCallOnDropView");
    spyPresenter.takeView(dummyView);
    spyPresenter.dropView(dummyView);
    verify(spyPresenter).onDropView(dummyView);
  }

  @Test(dependsOnMethods = "shouldTakeView") //
  public final void shouldCallOnTakeViewOncePerView() {
    System.out.println("PresenterTest.shouldCallOnTakeViewOncePerView");
    spyPresenter.takeView(dummyView);
    spyPresenter.takeView(dummyView);
    verify(spyPresenter).onTakeView(dummyView);
  }

  @Test(dependsOnMethods = "shouldDropView") //
  public final void shouldNotCallOnDropIfViewIsNotAttached() {
    System.out.println("PresenterTest.shouldNotCallOnDropIfViewIsNotAttached");
    spyPresenter.dropView(dummyView);
    verify(spyPresenter, never()).onDropView(dummyView);
  }

  @Test(dependsOnMethods = { "shouldTakeView", "shouldDropView" }) //
  public final void shouldIgnoreOnDropIfViewIsNotTheSame() {
    System.out.println("PresenterTest.shouldIgnoreOnDropIfViewIsNotTheSame");
    spyPresenter.takeView(dummyView);

    final ViewCallbacks anotherView = mock(ViewCallbacks.class);
    spyPresenter.dropView(anotherView);
    verify(spyPresenter, never()).onDropView(dummyView);
  }

  @Test(dependsOnMethods = "shouldTakeView") //
  public final void shouldDropPreviousViewWhenNewViewIsTaken() {
    System.out.println("PresenterTest.shouldDropPreviousViewWhenNewViewIsTaken");
    spyPresenter.takeView(dummyView);

    final ViewCallbacks newView = mock(ViewCallbacks.class);
    spyPresenter.takeView(newView);
    verify(spyPresenter).onDropView(dummyView);
  }

  @Test(dependsOnMethods = { "shouldCallOnTakeView", "shouldDropView" }) //
  public final void shouldCallOnTakeViewAfterViewIsTaken() {
    System.out.println("PresenterTest.shouldCallOnTakeViewAfterViewIsTaken");
    spyPresenter.takeView(dummyView);
    spyPresenter.dropView(dummyView);

    final InOrder inOrder = inOrder(spyPresenter);
    (inOrder.verify(spyPresenter)).assignView(dummyView);
    (inOrder.verify(spyPresenter)).onTakeView(dummyView);
  }

  @Test(dependsOnMethods = { "shouldTakeView", "shouldDropView" }) //
  public final void shouldCallOnDropViewBeforeViewIsDropped() {
    System.out.println("PresenterTest.shouldCallOnDropViewBeforeViewIsDropped");
    spyPresenter.takeView(dummyView);
    spyPresenter.dropView(dummyView);

    final InOrder inOrder = inOrder(spyPresenter);
    (inOrder.verify(spyPresenter)).onDropView(dummyView);
    (inOrder.verify(spyPresenter)).releaseView();
  }
}