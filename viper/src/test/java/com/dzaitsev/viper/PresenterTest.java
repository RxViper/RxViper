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
 * @since 2016-May-13, 12:31
 */
public class PresenterTest {
  @Mock ViewCallbacks            dummyView;
  @Spy  Presenter<ViewCallbacks> spyPresenter;

  @Before public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test public void shouldNotHaveView() {
    assertThat(spyPresenter.getView()).isNull();
    assertThat(spyPresenter.hasView()).isFalse();
  }

  @Test public void shouldTakeView() {
    spyPresenter.takeView(dummyView);
    assertThat(spyPresenter.getView()).isEqualTo(dummyView);
    assertThat(spyPresenter.hasView()).isTrue();
  }

  @Test public void shouldDropView() {
    spyPresenter.takeView(dummyView);

    spyPresenter.dropView(dummyView);
    assertThat(spyPresenter.getView()).isNull();
    assertThat(spyPresenter.hasView()).isFalse();
  }

  @Test public void shouldCallOnTakeView() {
    spyPresenter.takeView(dummyView);
    verify(spyPresenter).onTakeView(dummyView);
  }

  @Test public void shouldCallOnTakeViewOncePerView() {
    spyPresenter.takeView(dummyView);
    spyPresenter.takeView(dummyView);
    verify(spyPresenter).onTakeView(dummyView);
  }

  @Test public void shouldNotCallOnDropIfViewIsNotAttached() {
    spyPresenter.dropView(dummyView);
    verify(spyPresenter, never()).onDropView(dummyView);
  }

  @Test public void shouldIgnoreOnDropIfViewIsNotTheSame() {
    spyPresenter.takeView(dummyView);

    final ViewCallbacks anotherView = mock(ViewCallbacks.class);
    spyPresenter.dropView(anotherView);
    verify(spyPresenter, never()).onDropView(dummyView);
  }

  @Test public void shouldDropPreviousViewWhenNewViewIsTaken() {
    spyPresenter.takeView(dummyView);

    final ViewCallbacks newView = mock(ViewCallbacks.class);
    spyPresenter.takeView(newView);
    verify(spyPresenter).onDropView(dummyView);
  }

  @Test public void shouldCallOnTakeViewAfterViewIsTaken() {
    spyPresenter.takeView(dummyView);
    spyPresenter.dropView(dummyView);

    final InOrder inOrder = inOrder(spyPresenter);
    (inOrder.verify(spyPresenter)).assignView(dummyView);
    (inOrder.verify(spyPresenter)).onTakeView(dummyView);
  }

  @Test public void shouldCallOnDropViewBeforeViewIsDropped() {
    spyPresenter.takeView(dummyView);
    spyPresenter.dropView(dummyView);

    final InOrder inOrder = inOrder(spyPresenter);
    (inOrder.verify(spyPresenter)).onDropView(dummyView);
    (inOrder.verify(spyPresenter)).releaseView();
  }

  @Test(expected = IllegalArgumentException.class) //
  public void takenViewShouldNotBeNull() {
    spyPresenter.takeView(null);
  }

  @Test(expected = IllegalArgumentException.class) //
  public void droppedViewShouldNotBeNull() {
    spyPresenter.dropView(null);
  }
}