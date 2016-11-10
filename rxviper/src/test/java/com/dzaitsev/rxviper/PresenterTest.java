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
 * @since 2016-May-13, 12:31
 */
public final class PresenterTest {
  @Rule public final ExpectedException thrown = ExpectedException.none();
  private ViewCallbacks                view;
  private BasePresenter<ViewCallbacks> presenter;

  @Before
  public void setUp() {
    view = mock(ViewCallbacks.class);
    presenter = spy(new BasePresenter<>());
  }

  @Test
  public void shouldNotHaveView() {
    assertThat(presenter.getView()).isNull();
    assertThat(presenter.hasView()).isFalse();
  }

  @Test
  public void shouldTakeView() {
    presenter.takeView(view);
    assertThat(presenter.getView()).isEqualTo(view);
    assertThat(presenter.hasView()).isTrue();
  }

  @Test
  public void shouldDropView() {
    presenter.takeView(view);

    presenter.dropView(view);
    assertThat(presenter.getView()).isNull();
    assertThat(presenter.hasView()).isFalse();
  }

  @Test
  public void shouldCallOnTakeView() {
    presenter.takeView(view);
    verify(presenter).onTakeView(view);
  }

  @Test
  public void shouldCallOnTakeViewOncePerView() {
    presenter.takeView(view);
    presenter.takeView(view);
    verify(presenter).onTakeView(view);
  }

  @Test
  public void shouldNotCallOnDropIfViewIsNotAttached() {
    presenter.dropView(view);
    verify(presenter, never()).onDropView(view);
  }

  @Test
  public void shouldIgnoreOnDropIfViewIsNotTheSame() {
    presenter.takeView(view);

    final ViewCallbacks anotherView = mock(ViewCallbacks.class);
    presenter.dropView(anotherView);
    verify(presenter, never()).onDropView(view);
  }

  @Test
  public void shouldDropPreviousViewWhenNewViewIsTaken() {
    presenter.takeView(view);

    final ViewCallbacks newView = mock(ViewCallbacks.class);
    presenter.takeView(newView);
    verify(presenter).onDropView(view);
  }

  @Test
  public void shouldCallOnTakeViewAfterViewIsTaken() {
    presenter.dummy = false;
    presenter.takeView(view);
  }

  @Test
  public void shouldCallOnDropViewBeforeViewIsDropped() {
    presenter.dummy = false;
    presenter.takeView(view);
    presenter.dropView(view);
  }

  @Test
  public void takenViewShouldNotBeNull() {
    thrown.expect(IllegalArgumentException.class);
    presenter.takeView(null);
  }

  @Test
  public void droppedViewShouldNotBeNull() {
    thrown.expect(IllegalArgumentException.class);
    presenter.dropView(null);
  }

  @Test
  public void constructorViewShouldNotBeNull() {
    thrown.expect(IllegalArgumentException.class);
    new BasePresenter<>(null);
  }

  @Test
  public void constructorShouldSetView() {
    new BasePresenter<>(view);
  }
}