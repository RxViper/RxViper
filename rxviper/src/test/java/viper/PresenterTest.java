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
 * @since 2016-May-13, 12:31
 */
public class PresenterTest {
  @Mock ViewCallbacks                    mView;
  @Mock Router                           mRouter;
  @Spy  Presenter<ViewCallbacks, Router> mPresenter;

  @Before public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test public void shouldNotHaveView() {
    assertThat(mPresenter.getView()).isNull();
    assertThat(mPresenter.hasView()).isFalse();
  }

  @Test public void shouldNotHaveRouter() {
    assertThat(mPresenter.getRouter()).isNull();
    assertThat(mPresenter.hasRouter()).isFalse();
  }

  @Test public void shouldTakeView() {
    mPresenter.takeView(mView);
    assertThat(mPresenter.getView()).isEqualTo(mView);
    assertThat(mPresenter.hasView()).isTrue();
  }

  @Test public void shouldTakeRouter() {
    mPresenter.takeRouter(mRouter);
    assertThat(mPresenter.getRouter()).isEqualTo(mRouter);
    assertThat(mPresenter.hasRouter()).isTrue();
  }

  @Test public void shouldDropView() {
    mPresenter.takeView(mView);

    mPresenter.dropView(mView);
    assertThat(mPresenter.getView()).isNull();
    assertThat(mPresenter.hasView()).isFalse();
  }

  @Test public void shouldDropRouter() {
    mPresenter.takeRouter(mRouter);

    mPresenter.dropRouter(mRouter);
    assertThat(mPresenter.getRouter()).isNull();
    assertThat(mPresenter.hasRouter()).isFalse();
  }

  @Test public void shouldCallOnTakeView() {
    mPresenter.takeView(mView);
    verify(mPresenter).onTakeView(mView);
  }

  @Test public void shouldCallOnTakeRouter() {
    mPresenter.takeRouter(mRouter);
    verify(mPresenter).onTakeRouter(mRouter);
  }

  @Test public void shouldCallOnTakeViewOncePerView() {
    mPresenter.takeView(mView);
    mPresenter.takeView(mView);
    verify(mPresenter).onTakeView(mView);
  }

  @Test public void shouldCallOnTakeRouterOncePerView() {
    mPresenter.takeRouter(mRouter);
    mPresenter.takeRouter(mRouter);
    verify(mPresenter).onTakeRouter(mRouter);
  }

  @Test public void shouldNotCallOnDropIfViewIsNotAttached() {
    mPresenter.dropView(mView);
    verify(mPresenter, never()).onDropView(mView);
  }

  @Test public void shouldNotCallOnDropIfRouterIsNotAttached() {
    mPresenter.dropRouter(mRouter);
    verify(mPresenter, never()).onDropRouter(mRouter);
  }

  @Test public void shouldIgnoreOnDropIfViewIsNotTheSame() {
    mPresenter.takeView(mView);

    final ViewCallbacks anotherView = mock(ViewCallbacks.class);
    mPresenter.dropView(anotherView);
    verify(mPresenter, never()).onDropView(mView);
  }

  @Test public void shouldIgnoreOnDropIfRouterIsNotTheSame() {
    mPresenter.takeRouter(mRouter);

    final Router anotherRouter = mock(Router.class);
    mPresenter.dropRouter(anotherRouter);
    verify(mPresenter, never()).onDropRouter(mRouter);
  }

  @Test public void shouldDropPreviousViewWhenNewViewIsTaken() {
    mPresenter.takeView(mView);

    final ViewCallbacks newView = mock(ViewCallbacks.class);
    mPresenter.takeView(newView);
    verify(mPresenter).onDropView(mView);
  }

  @Test public void shouldDropPreviousRouterWhenNewRouterIsTaken() {
    mPresenter.takeRouter(mRouter);

    final Router newRouter = mock(Router.class);
    mPresenter.takeRouter(newRouter);
    verify(mPresenter).onDropRouter(mRouter);
  }

  @Test public void shouldCallOnTakeViewAfterViewIsTaken() {
    mPresenter.takeView(mView);
    mPresenter.dropView(mView);

    final InOrder inOrder = Mockito.inOrder(mPresenter);
    (inOrder.verify(mPresenter)).assignView(mView);
    (inOrder.verify(mPresenter)).onTakeView(mView);
  }

  @Test public void shouldCallOnTakeRouterAfterRouterIsTaken() {
    mPresenter.takeRouter(mRouter);
    mPresenter.dropRouter(mRouter);

    final InOrder inOrder = Mockito.inOrder(mPresenter);
    (inOrder.verify(mPresenter)).assignRouter(mRouter);
    (inOrder.verify(mPresenter)).onTakeRouter(mRouter);
  }

  @Test public void shouldCallOnDropViewBeforeViewIsDropped() {
    mPresenter.takeView(mView);
    mPresenter.dropView(mView);

    final InOrder inOrder = Mockito.inOrder(mPresenter);
    (inOrder.verify(mPresenter)).onDropView(mView);
    (inOrder.verify(mPresenter)).releaseView();
  }

  @Test public void shouldCallOnDropRouterBeforeRouterIsDropped() {
    mPresenter.takeRouter(mRouter);
    mPresenter.dropRouter(mRouter);

    final InOrder inOrder = Mockito.inOrder(mPresenter);
    (inOrder.verify(mPresenter)).onDropRouter(mRouter);
    (inOrder.verify(mPresenter)).releaseRouter();
  }

  @Test(expected = IllegalArgumentException.class) //
  public void takenViewShouldNotBeNull() {
    mPresenter.takeView(null);
  }

  @Test(expected = IllegalArgumentException.class) //
  public void takenRouterShouldNotBeNull() {
    mPresenter.takeRouter(null);
  }

  @Test(expected = IllegalArgumentException.class) //
  public void droppedViewShouldNotBeNull() {
    mPresenter.dropView(null);
  }

  @Test(expected = IllegalArgumentException.class) //
  public void droppedRouterShouldNotBeNull() {
    mPresenter.dropRouter(null);
  }
}