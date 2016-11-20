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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.junit.Before;
import org.junit.Test;

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
  private TestViewCallbacks target;

  @Before
  public void setUp() {
    target = new TestViewCallbacksImpl();
  }

  @Test
  public void shoulInvokeTargetMethods() throws Throwable {
    final TestViewCallbacks spy = spy(target);
    final Method doJob = TestViewCallbacks.class.getMethod("doJob");
    final NullObject<TestViewCallbacks> nullObject = new NullObject<>(spy);
    nullObject.invoke(null, doJob, new Object[0]);
    verify(spy).doJob();
  }

  @Test
  public void shouldClearTarget() {
    final NullObject<TestViewCallbacks> nullObject = new NullObject<>(target);
    nullObject.clear();
    assertThat(nullObject.get()).isNull();
  }

  @Test
  public void shouldHaveWeakTarget() {
    final NullObject<TestViewCallbacks> nullObject = new NullObject<>(target);
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
    final NullObject<TestViewCallbacks> nullObject1 = new NullObject<>(target);
    assertThat(nullObject1.get()).isSameAs(target);

    final NullObject<TestViewCallbacks> nullObject2 = new NullObject<>(null);
    assertThat(nullObject2.get()).isNull();
  }

  @Test
  public void shouldSetTarget() {
    final NullObject<TestViewCallbacks> nullObject = new NullObject<>(null);
    assertThat(nullObject.get()).isNull();

    nullObject.set(target);
    assertThat(nullObject.get()).isSameAs(target);

    final TestViewCallbacks target2 = new TestViewCallbacksImpl();
    nullObject.set(target2);
    assertThat(nullObject.get()).isNotSameAs(target);
    assertThat(nullObject.get()).isSameAs(target2);

    nullObject.set(null);
    assertThat(nullObject.get()).isNull();
  }
}