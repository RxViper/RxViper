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

package com.dzaitsev.nullobject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.Set;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.dzaitsev.nullobject.NullObject.getGenericParameterClass;
import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Nov-20, 11:15
 */
public final class NullObjectTest {
  @Rule public ExpectedException thrown = ExpectedException.none();
  private Target target;

  @Before
  public void setUp() {
    target = new TargetImpl();
  }

  @Test
  public void shoulInvokeTargetMethods() throws Throwable {
    final Target target = spy(this.target);
    final Method doJob = Target.class.getMethod("doJob");
    final NullObject<Target> nullObject = NullObject.wrap(target);
    nullObject.invoke(null, doJob, new Object[0]);
    verify(target).doJob();
  }

  @Test
  public void shouldClearTarget() {
    final NullObject<Target> nullObject = NullObject.wrap(target);
    nullObject.clear();
    assertThat(nullObject.get()).isNull();
  }

  @Test
  public void shouldHaveWeakTarget() {
    final NullObject<Target> nullObject = NullObject.wrap(target);
    target = null;
    System.gc();
    assertThat(nullObject.get()).isNull();
  }

  @Test
  public void shouldImplementInvocationHandler() {
    assertThat(NullObject.class).isAssignableTo(InvocationHandler.class);
  }

  @Test
  public void shouldReturnPassedTarget() {
    final NullObject<Target> nullObject1 = NullObject.wrap(target);
    assertThat(nullObject1.get()).isSameAs(target);

    final NullObject<Target> nullObject2 = NullObject.wrap(null);
    assertThat(nullObject2.get()).isNull();
  }

  @Test
  public void shouldSetTarget() {
    final NullObject<Target> nullObject = NullObject.wrap(null);
    assertThat(nullObject.get()).isNull();

    nullObject.set(target);
    assertThat(nullObject.get()).isSameAs(target);

    final Target target2 = new TargetImpl();
    nullObject.set(target2);
    assertThat(nullObject.get()).isNotSameAs(target);
    assertThat(nullObject.get()).isSameAs(target2);

    nullObject.set(null);
    assertThat(nullObject.get()).isNull();
  }

  @Test
  public void shouldNotGetTypeSelf() {
    thrown.expect(IllegalStateException.class);
    getGenericParameterClass(Object.class, Object.class, 0);
  }

  @Test
  public void testGenericParameterClass() {
    class ClassA<S, I> {} /*String, Integer*/
    class ClassB<I, S, C extends Collection> extends ClassA<S, I> {} /*Integer, String, Set*/
    class ClassC<S extends Comparable<String>, D, I> extends ClassB<I, S, Set<Long>> {} /*String, Double, Integer*/
    class ClassD<I, D extends Comparable<Double>> extends ClassC<String, D, I> implements InterfaceB<D, I> {} /*Integer, Double*/
    class ClassE extends ClassD<Integer, Double> {}
    class ClassF<B, L> extends ClassE {} /*Byte, Long*/
    class ClassG extends ClassF<Byte, Long> {}

    final Class<?> classG = ClassG.class;
    assertThat(getGenericParameterClass(classG, ClassA.class, 1)).isNotSameAs(String.class);
    assertThat(getGenericParameterClass(classG, ClassA.class, 0)).isSameAs(String.class);
    assertThat(getGenericParameterClass(classG, ClassA.class, 1)).isSameAs(Integer.class);

    assertThat(getGenericParameterClass(classG, ClassB.class, 0)).isSameAs(Integer.class);
    assertThat(getGenericParameterClass(classG, ClassB.class, 1)).isSameAs(String.class);
    assertThat(getGenericParameterClass(classG, ClassB.class, 2)).isSameAs(Set.class);

    assertThat(getGenericParameterClass(classG, ClassC.class, 0)).isSameAs(String.class);
    assertThat(getGenericParameterClass(classG, ClassC.class, 1)).isSameAs(Double.class);
    assertThat(getGenericParameterClass(classG, ClassC.class, 2)).isSameAs(Integer.class);

    assertThat(getGenericParameterClass(classG, ClassD.class, 0)).isSameAs(Integer.class);
    assertThat(getGenericParameterClass(classG, ClassD.class, 1)).isSameAs(Double.class);

    assertThat(getGenericParameterClass(classG, ClassF.class, 0)).isSameAs(Byte.class);
    assertThat(getGenericParameterClass(classG, ClassF.class, 1)).isSameAs(Long.class);
    assertThat(getGenericParameterClass(classG, InterfaceA.class, 0)).isSameAs(Integer.class);
    assertThat(getGenericParameterClass(classG, InterfaceB.class, 0)).isSameAs(Double.class);
    assertThat(getGenericParameterClass(classG, InterfaceB.class, 1)).isSameAs(Integer.class);

    thrown.expect(EmptyStackException.class);
    assertThat(getGenericParameterClass(classG, ClassE.class, 0)).isSameAs(Double.class);
  }

  interface InterfaceA<I> {} /*Integer*/

  interface InterfaceB<D, I> extends InterfaceA<I> {} /*Double, Integer*/
}