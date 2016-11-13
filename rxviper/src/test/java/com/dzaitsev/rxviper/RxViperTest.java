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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.Set;
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
public final class RxViperTest {
  @Rule public ExpectedException thrown = ExpectedException.none();

  @Test
  public void requiredArgShouldBeTheSame() {
    final Object actual = new Object();
    final Object expected = RxViper.requireNotNull(actual);
    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void requiredArgShouldNotBeNull() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Argument can not be null");
    RxViper.requireNotNull(null);
  }

  @Test
  public void shouldCreateProxyRouter() {
    class TestRouterImpl implements TestRouter {}
    final TestRouter router = new TestRouterImpl();
    final TestRouter proxyRouter = RxViper.createRouter(router, TestViperPresenter.class);
    assertThat(Proxy.isProxyClass(proxyRouter.getClass())).isTrue();
    final InvocationHandler handler = Proxy.getInvocationHandler(proxyRouter);
    assertThat(handler).isInstanceOf(NullObject.class);
    assertThat(((NullObject) handler).get()).isSameAs(router);
  }

  @Test
  public void shouldCreateProxyView() {
    final TestViewCallbacks view = new TestViewCallbacksImpl();
    final TestViewCallbacks proxyView = RxViper.createView(view, TestPresenter.class);
    assertThat(Proxy.isProxyClass(proxyView.getClass())).isTrue();
    final InvocationHandler handler = Proxy.getInvocationHandler(proxyView);
    assertThat(handler).isInstanceOf(NullObject.class);
    assertThat(((NullObject) handler).get()).isSameAs(view);
  }

  @Test
  public void shouldNotCreateInstances() throws Throwable {
    try {
      final Constructor<?>[] constructors = RxViper.class.getDeclaredConstructors();
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
  public void shouldNotGetTypeSelf() {
    thrown.expect(IllegalStateException.class);
    RxViper.getGenericParameterClass(Object.class, Object.class, 0);
  }

  @Test
  public void shouldReturnNullObject() {
    final TestPresenter presenter = new TestPresenter(new TestViewCallbacksImpl());
    final TestViewCallbacks proxyView = presenter.getView();
    final NullObject<TestViewCallbacks> nullObject = RxViper.getProxy(proxyView);
    assertThat(Proxy.getInvocationHandler(proxyView)).isSameAs(nullObject);
  }

  @Test
  public void testGenericParameterClass() {
    class Foo<S, I> {} /*String, Integer*/
    class Bar<I, S, C extends Collection> extends Foo<S, I> {} /*Integer, String, Set*/
    class Baz<S extends Comparable<String>, D, I> extends Bar<I, S, Set<Long>> {} /*String, Double, Integer*/
    class Qux<I, D extends Comparable<Double>> extends Baz<String, D, I> implements Plugh<D, I> {} /*Integer, Double*/
    class Corge extends Qux<Integer, Double> {}
    class Grault<B, L> extends Corge {} /*Byte, Long*/
    class Waldo extends Grault<Byte, Long> {}

    final Class<?> waldoClass = Waldo.class;
    assertThat(RxViper.getGenericParameterClass(waldoClass, Foo.class, 1)).isNotSameAs(String.class);
    assertThat(RxViper.getGenericParameterClass(waldoClass, Foo.class, 0)).isSameAs(String.class);
    assertThat(RxViper.getGenericParameterClass(waldoClass, Foo.class, 1)).isSameAs(Integer.class);

    assertThat(RxViper.getGenericParameterClass(waldoClass, Bar.class, 0)).isSameAs(Integer.class);
    assertThat(RxViper.getGenericParameterClass(waldoClass, Bar.class, 1)).isSameAs(String.class);
    assertThat(RxViper.getGenericParameterClass(waldoClass, Bar.class, 2)).isSameAs(Set.class);

    assertThat(RxViper.getGenericParameterClass(waldoClass, Baz.class, 0)).isSameAs(String.class);
    assertThat(RxViper.getGenericParameterClass(waldoClass, Baz.class, 1)).isSameAs(Double.class);
    assertThat(RxViper.getGenericParameterClass(waldoClass, Baz.class, 2)).isSameAs(Integer.class);

    assertThat(RxViper.getGenericParameterClass(waldoClass, Qux.class, 0)).isSameAs(Integer.class);
    assertThat(RxViper.getGenericParameterClass(waldoClass, Qux.class, 1)).isSameAs(Double.class);

    assertThat(RxViper.getGenericParameterClass(waldoClass, Grault.class, 0)).isSameAs(Byte.class);
    assertThat(RxViper.getGenericParameterClass(waldoClass, Grault.class, 1)).isSameAs(Long.class);
    assertThat(RxViper.getGenericParameterClass(waldoClass, Fred.class, 0)).isSameAs(Integer.class);
    assertThat(RxViper.getGenericParameterClass(waldoClass, Plugh.class, 0)).isSameAs(Double.class);
    assertThat(RxViper.getGenericParameterClass(waldoClass, Plugh.class, 1)).isSameAs(Integer.class);

    thrown.expect(EmptyStackException.class);
    assertThat(RxViper.getGenericParameterClass(waldoClass, Corge.class, 0)).isSameAs(Double.class);
  }

  interface Fred<I> {} /*Integer*/

  interface Plugh<D, I> extends Fred<I> {} /*Double, Integer*/
}