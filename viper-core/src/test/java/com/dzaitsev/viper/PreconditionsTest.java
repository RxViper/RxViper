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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.google.common.truth.Truth.assertThat;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Nov-04, 01:08
 */
public final class PreconditionsTest {
  @Rule public ExpectedException thrown = ExpectedException.none();

  @Test
  public void requiredArgShouldBeTheSame() {
    final Object actual = new Object();
    final Object expected = Preconditions.requireNotNull(actual);
    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void requiredArgShouldNotBeNull() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Argument can not be null");
    Preconditions.requireNotNull(null);
  }

  @Test
  public void shouldCreateProxyRouter() {
    class TestRouterImpl implements TestRouter {}
    final TestRouter router = new TestRouterImpl();
    final TestRouter proxyRouter = NullObject.createProxy(router, ViperPresenter.class, TestViperPresenter.class, 1);
    assertThat(NullObject.isWrapped(proxyRouter.getClass())).isTrue();
    final NullObject handler = NullObject.unwrap(proxyRouter);
    assertThat(handler).isInstanceOf(NullObject.class);
    assertThat(handler.get()).isSameAs(router);
  }

  @Test
  public void shouldCreateProxyView() {
    final TestViewCallbacks view = new TestViewCallbacksImpl();
    final TestViewCallbacks proxyView = NullObject.createProxy(view, Presenter.class, TestPresenter.class, 0);
    assertThat(NullObject.isWrapped(proxyView.getClass())).isTrue();
    final NullObject handler = NullObject.unwrap(proxyView);
    assertThat(handler).isInstanceOf(NullObject.class);
    assertThat(handler.get()).isSameAs(view);
  }

  @Test
  public void shouldNotCreateInstances() throws Throwable {
    try {
      final Constructor<?>[] constructors = Preconditions.class.getDeclaredConstructors();
      assertThat(constructors).hasLength(1);
      final Constructor<?> constructor = constructors[0];
      assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
      constructor.setAccessible(true);
      constructor.newInstance();
    } catch (InvocationTargetException ite) {
      final Throwable cause = ite.getCause();
      thrown.expect(AssertionError.class);
      thrown.expectMessage("No instances please!");
      throw cause;
    }
  }

  @Test
  public void shouldReturnNullObject() {
    final TestPresenter presenter = new TestPresenter(new TestViewCallbacksImpl());
    final TestViewCallbacks proxyView = presenter.getView();
    final NullObject<TestViewCallbacks> nullObject = NullObject.unwrap(proxyView);
    assertThat(NullObject.unwrap(proxyView)).isSameAs(nullObject);
  }
}