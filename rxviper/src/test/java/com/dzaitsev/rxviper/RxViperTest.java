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
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Nov-04, 01:08
 */
final class RxViperTest {
  @Test
  void requiredArgShouldBeTheSame() {
    final Object actual = new Object();
    final Object expected = RxViper.requireNotNull(actual);
    assertThat(actual).isSameAs(expected);
  }

  @Test
  void requiredArgShouldNotBeNull() {
    final Throwable thrown = assertThrows(IllegalArgumentException.class, () -> RxViper.requireNotNull(null));
    assertThat(thrown.getMessage()).isEqualTo("Argument can not be null");
  }

  @Test
  void shouldCreateProxyRouter() {
    class TestRouterImpl implements TestRouter {}
    final TestRouter router = new TestRouterImpl();
    final TestRouter proxyRouter = RxViper.createRouter(router, TestViperPresenter.class);
    assertThat(Proxy.isProxyClass(proxyRouter.getClass())).isTrue();
    final InvocationHandler handler = Proxy.getInvocationHandler(proxyRouter);
    assertThat(handler).isInstanceOf(NullObject.class);
    assertThat(((NullObject) handler).get()).isSameAs(router);
  }

  @Test
  void shouldCreateProxyView() {
    final TestViewCallbacks view = new TestViewCallbacksImpl();
    final TestViewCallbacks proxyView = RxViper.createView(view, TestPresenter.class);
    assertThat(Proxy.isProxyClass(proxyView.getClass())).isTrue();
    final InvocationHandler handler = Proxy.getInvocationHandler(proxyView);
    assertThat(handler).isInstanceOf(NullObject.class);
    assertThat(((NullObject) handler).get()).isSameAs(view);
  }

  @Test
  void shouldNotCreateInstances() throws Throwable {
    try {
      final Constructor<?>[] constructors = RxViper.class.getDeclaredConstructors();
      assertThat(constructors).hasLength(1);
      final Constructor<?> constructor = constructors[0];
      assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
      constructor.setAccessible(true);
      constructor.newInstance();
    } catch (InvocationTargetException ite) {
      final Throwable cause = ite.getCause();
      assertThat(cause).isInstanceOf(AssertionError.class);
      assertThat(cause.getMessage()).isEqualTo("No instances please!");
    }
  }

  @Test
  void shouldNotGetTypeSelf() {
    assertThrows(IllegalStateException.class, () -> RxViper.getGenericParameterClass(Object.class, Object.class, 0));
  }

  @Test
  void shouldReturnNullObject() {
    final TestPresenter presenter = new TestPresenter(new TestViewCallbacksImpl());
    final TestViewCallbacks proxyView = presenter.getView();
    final NullObject<TestViewCallbacks> nullObject = RxViper.getProxy(proxyView);
    assertThat(Proxy.getInvocationHandler(proxyView)).isSameAs(nullObject);
  }

  @Test
  void testGenericParameterClass() {
    class ClassA<S, I> {} /*String, Integer*/
    class ClassB<I, S, C extends Collection> extends ClassA<S, I> {} /*Integer, String, Set*/
    class ClassC<S extends Comparable<String>, D, I> extends ClassB<I, S, Set<Long>> {} /*String, Double, Integer*/
    class ClassD<I, D extends Comparable<Double>> extends ClassC<String, D, I> implements InterfaceB<D, I> {} /*Integer, Double*/
    class ClassE extends ClassD<Integer, Double> {}
    class ClassF<B, L> extends ClassE {} /*Byte, Long*/
    class ClassG extends ClassF<Byte, Long> {}

    final Class<?> classG = ClassG.class;
    assertThat(RxViper.getGenericParameterClass(classG, ClassA.class, 1)).isNotSameAs(String.class);
    assertThat(RxViper.getGenericParameterClass(classG, ClassA.class, 0)).isSameAs(String.class);
    assertThat(RxViper.getGenericParameterClass(classG, ClassA.class, 1)).isSameAs(Integer.class);

    assertThat(RxViper.getGenericParameterClass(classG, ClassB.class, 0)).isSameAs(Integer.class);
    assertThat(RxViper.getGenericParameterClass(classG, ClassB.class, 1)).isSameAs(String.class);
    assertThat(RxViper.getGenericParameterClass(classG, ClassB.class, 2)).isSameAs(Set.class);

    assertThat(RxViper.getGenericParameterClass(classG, ClassC.class, 0)).isSameAs(String.class);
    assertThat(RxViper.getGenericParameterClass(classG, ClassC.class, 1)).isSameAs(Double.class);
    assertThat(RxViper.getGenericParameterClass(classG, ClassC.class, 2)).isSameAs(Integer.class);

    assertThat(RxViper.getGenericParameterClass(classG, ClassD.class, 0)).isSameAs(Integer.class);
    assertThat(RxViper.getGenericParameterClass(classG, ClassD.class, 1)).isSameAs(Double.class);

    assertThat(RxViper.getGenericParameterClass(classG, ClassF.class, 0)).isSameAs(Byte.class);
    assertThat(RxViper.getGenericParameterClass(classG, ClassF.class, 1)).isSameAs(Long.class);
    assertThat(RxViper.getGenericParameterClass(classG, InterfaceA.class, 0)).isSameAs(Integer.class);
    assertThat(RxViper.getGenericParameterClass(classG, InterfaceB.class, 0)).isSameAs(Double.class);
    assertThat(RxViper.getGenericParameterClass(classG, InterfaceB.class, 1)).isSameAs(Integer.class);

    assertThrows(EmptyStackException.class,
        () -> assertThat(RxViper.getGenericParameterClass(classG, ClassE.class, 0)).isSameAs(Double.class));
  }

  interface InterfaceA<I> {} /*Integer*/

  interface InterfaceB<D, I> extends InterfaceA<I> {} /*Double, Integer*/
}