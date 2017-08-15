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

import com.dzaitsev.nullobject.NullObject;
import java.lang.reflect.Proxy;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.dzaitsev.viper.TestUtil.checkIllegalArgumentException;
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
  private TestViewCallbacks view;
  private TestPresenter     presenter;

  @Before
  public void setUp() {
    view = mock(TestViewCallbacks.class);
    presenter = spy(new TestPresenter());
  }

  @Test
  public void viewShouldNeverBeNull() {
    assertThat(presenter.getView()).isNotNull();

    presenter.takeView(view);
    assertThat(presenter.getView()).isNotNull();

    presenter.dropView(view);
    assertThat(presenter.getView()).isNotNull();
  }

  @Test
  public void shouldNotHaveView() {
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

    final TestViewCallbacks anotherView = mock(TestViewCallbacks.class);
    presenter.dropView(anotherView);
    verify(presenter, never()).onDropView(view);
  }

  @Test
  public void shouldDropPreviousViewWhenNewViewIsTaken() {
    presenter.takeView(view);

    final TestViewCallbacks newView = mock(TestViewCallbacks.class);
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
    checkIllegalArgumentException(() -> presenter.takeView(null));
  }

  @Test
  public void droppedViewShouldNotBeNull() {
    checkIllegalArgumentException(() -> presenter.dropView(null));
  }

  @Test
  public void constructorViewShouldNotBeNull() {
    checkIllegalArgumentException(() -> new TestPresenter(null));
  }

  @Test
  public void constructorShouldSetView() {
    new TestPresenter(view);
  }

  @Test
  public void shouldReturnProxyView() {
    presenter.takeView(view);

    final TestViewCallbacks proxyView = presenter.getView();
    assertThat(proxyView).isNotSameAs(view);
    assertThat(Proxy.isProxyClass(proxyView.getClass())).isTrue();
    assertThat(Proxy.getInvocationHandler(proxyView)).isInstanceOf(NullObject.class);
  }

  @Test
  public void proxyShouldWrapView() {
    final NullObject<TestViewCallbacks> nullObject = NullObject.unwrap(presenter.getView());
    assertThat(nullObject.get()).isNull();

    presenter.takeView(view);
    assertThat(nullObject.get()).isSameAs(view);

    presenter.dropView(view);
    assertThat(nullObject.get()).isNull();
  }

  @Ignore("Probably should be moved to null-object")
  @Test
  public void shouldCreateProxyView() {
    final TestViewCallbacks proxyView = NullObject.createProxy(view, Presenter.class, TestPresenter.class, 0);
    assertThat(NullObject.isWrapped(proxyView.getClass())).isTrue();
    final NullObject handler = NullObject.unwrap(proxyView);
    assertThat(handler).isInstanceOf(NullObject.class);
    assertThat(handler.get()).isSameAs(view);
  }

  @Ignore("Probably should be moved to null-object")
  @Test
  public void shouldReturnNullObject() {
    final TestPresenter presenter = new TestPresenter(view);
    final TestViewCallbacks proxyView = presenter.getView();
    final NullObject<TestViewCallbacks> nullObject = NullObject.unwrap(proxyView);
    assertThat(NullObject.unwrap(proxyView)).isSameAs(nullObject);
  }
}