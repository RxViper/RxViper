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
  private ViewCallbacks dummyView;
  private TestPresenter spyPresenter;

  @Before
  public void setUp() {
    dummyView = mock(ViewCallbacks.class);
    spyPresenter = spy(new TestPresenter());
  }

  @Test
  public void constructorViewShouldNotBeNull() {
    thrown.expect(IllegalArgumentException.class);
    new TestPresenter(null);
  }

  @Test
  public void constructorShouldSetView() {
    new TestPresenter(dummyView);
  }

  @Test
  public void viewShouldNeverBeNull() {
    assertThat(spyPresenter.getView()).isNotNull();

    spyPresenter.takeView(dummyView);
    assertThat(spyPresenter.getView()).isNotNull();

    spyPresenter.dropView(dummyView);
    assertThat(spyPresenter.getView()).isNotNull();
  }

  @Test
  public void shouldNotHaveView() {
    assertThat(spyPresenter.hasView()).isFalse();
  }

  @Test
  public void shouldTakeView() {
    spyPresenter.takeView(dummyView);
    assertThat(spyPresenter.getView()).isEqualTo(dummyView);
    assertThat(spyPresenter.hasView()).isTrue();
  }

  @Test
  public void shouldDropView() {
    spyPresenter.takeView(dummyView);

    spyPresenter.dropView(dummyView);
    assertThat(spyPresenter.hasView()).isFalse();
  }

  @Test
  public void shouldCallOnTakeView() {
    spyPresenter.takeView(dummyView);
    verify(spyPresenter).onTakeView(dummyView);
  }

  @Test
  public void shouldCallOnTakeViewOncePerView() {
    spyPresenter.takeView(dummyView);
    spyPresenter.takeView(dummyView);
    verify(spyPresenter).onTakeView(dummyView);
  }

  @Test
  public void shouldNotCallOnDropIfViewIsNotAttached() {
    spyPresenter.dropView(dummyView);
    verify(spyPresenter, never()).onDropView(dummyView);
  }

  @Test
  public void shouldIgnoreOnDropIfViewIsNotTheSame() {
    spyPresenter.takeView(dummyView);

    final ViewCallbacks anotherView = mock(ViewCallbacks.class);
    spyPresenter.dropView(anotherView);
    verify(spyPresenter, never()).onDropView(dummyView);
  }

  @Test
  public void shouldDropPreviousViewWhenNewViewIsTaken() {
    spyPresenter.takeView(dummyView);

    final ViewCallbacks newView = mock(ViewCallbacks.class);
    spyPresenter.takeView(newView);
    verify(spyPresenter).onDropView(dummyView);
  }

  @Test
  public void shouldCallOnTakeViewAfterViewIsTaken() {
    spyPresenter.dummy = false;
    spyPresenter.takeView(dummyView);
  }

  @Test
  public void shouldCallOnDropViewBeforeViewIsDropped() {
    spyPresenter.dummy = false;
    spyPresenter.takeView(dummyView);
    spyPresenter.dropView(dummyView);
  }

  @Test
  public void takenViewShouldNotBeNull() {
    thrown.expect(IllegalArgumentException.class);
    spyPresenter.takeView(null);
  }

  @Test
  public void droppedViewShouldNotBeNull() {
    thrown.expect(IllegalArgumentException.class);
    spyPresenter.dropView(null);
  }

  private static class TestPresenter extends Presenter<ViewCallbacks> {
    boolean dummy = true;

    TestPresenter(ViewCallbacks dummyView) {
      super(dummyView);
      assertThatViewIsSet();
    }

    TestPresenter() {}

    @Override
    protected void onDropView(ViewCallbacks view) {
      super.onDropView(view);
      assertThatViewIsSet();
    }

    @Override
    protected void onTakeView(ViewCallbacks view) {
      super.onTakeView(view);
      assertThatViewIsSet();
    }

    private void assertThatViewIsSet() {
      if (!dummy) {
        assertThat(hasView()).isTrue();
      }
    }
  }
}