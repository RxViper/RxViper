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

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Implementation of <a href="https://en.wikipedia.org/wiki/Null_Object_pattern">Null Object pattern</a>.
 *
 * @author Dmytro Zaitsev
 * @since 0.11.0
 */
final class NullObject<T> implements InvocationHandler {
  private WeakReference<T> targetRef;

  NullObject(T target) {
    set(target);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    final T target = get();
    return target == null ? null : method.invoke(target, args);
  }

  void clear() {
    if (targetRef != null) {
      targetRef.clear();
      targetRef = null;
    }
  }

  T get() {
    return targetRef == null ? null : targetRef.get();
  }

  void set(T target) {
    targetRef = new WeakReference<>(target);
  }
}
