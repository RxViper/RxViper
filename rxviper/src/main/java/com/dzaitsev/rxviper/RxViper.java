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
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-May-14, 15:26
 */
final class RxViper {
  private RxViper() {
    throw new AssertionError("No instances please!");
  }

  static <T> T requireNotNull(T arg) {
    if (arg == null) {
      throw new IllegalArgumentException("Argument can not be null");
    }
    return arg;
  }

  @SuppressWarnings("rawtypes")
  static <V extends ViewCallbacks> V createView(V view, Class<? extends Presenter> presenterClass) {
    return createProxy(view, ViewCallbacks.class, Presenter.class, presenterClass);
  }

  @SuppressWarnings("rawtypes")
  static <R extends Router> R createRouter(R router, Class<? extends ViperPresenter> presenterClass) {
    return createProxy(router, Router.class, ViperPresenter.class, presenterClass);
  }

  @SuppressWarnings("unchecked")
  private static <T, G> T createProxy(T target, Class<? super T> targetClass, Class<G> baseClass, Class<? extends G> childClass) {
    try {
      final List<Class<?>> typeArguments = getTypeArguments(baseClass, childClass);
      final Class<?>[] interfaces = new Class[1];
      for (Class<?> typeArgument : typeArguments) {
        if (targetClass.isInterface() & targetClass.isAssignableFrom(typeArgument)) {
          interfaces[0] = typeArgument;
          break;
        }
      }
      return (T) Proxy.newProxyInstance(childClass.getClassLoader(), interfaces, new NullObject<>(target));
    } catch (NullPointerException | IllegalArgumentException ignored) {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  static <T> NullObject<T> getProxy(T target) {
    return (NullObject<T>) Proxy.getInvocationHandler(target);
  }

  /** Get the underlying class for a type, or null if the type is a variable type. */
  private static Class<?> getUnderlyingClass(Type type) {
    if (type instanceof Class) {
      return (Class) type;
    } else if (type instanceof ParameterizedType) {
      return getUnderlyingClass(((ParameterizedType) type).getRawType());
    } else if (type instanceof GenericArrayType) {
      final Type componentType = ((GenericArrayType) type).getGenericComponentType();
      final Class<?> componentClass = getUnderlyingClass(componentType);
      if (componentClass == null) {
        return null;
      } else {
        return Array.newInstance(componentClass, 0)
            .getClass();
      }
    } else {
      return null;
    }
  }

  /**
   * Get the actual type arguments a child class has used to extend a generic base class.
   *
   * @return a list of the raw classes for the actual type arguments.
   */
  private static <T> List<Class<?>> getTypeArguments(Class<T> baseClass, Class<? extends T> childClass) {
    Map<Type, Type> resolvedTypes = new HashMap<>();
    Type type = childClass;
    // start walking up the inheritance hierarchy until we hit baseClass
    while (!baseClass.equals(getUnderlyingClass(type))) {
      if (type instanceof Class) {
        // there is no useful information for us in raw types, so just keep going.
        type = ((Class) type).getGenericSuperclass();
      } else {
        final ParameterizedType parameterizedType = (ParameterizedType) type;
        final Class<?> rawType = (Class) parameterizedType.getRawType();
        final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        final TypeVariable<?>[] typeParameters = rawType.getTypeParameters();

        for (int i = 0; i < actualTypeArguments.length; i++) {
          resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
        }

        if (!baseClass.equals(rawType)) {
          type = rawType.getGenericSuperclass();
        }
      }
    }

    // finally, for each actual type argument provided to baseClass, determine (if possible) the raw class for that type argument.
    final Type[] actualTypeArguments =
        type instanceof Class ? ((Class) type).getTypeParameters() : ((ParameterizedType) type).getActualTypeArguments();
    final List<Class<?>> typeArgumentsAsClasses = new ArrayList<>();
    // resolve types by chasing down type variables.
    for (Type baseType : actualTypeArguments) {
      while (resolvedTypes.containsKey(baseType)) {
        baseType = resolvedTypes.get(baseType);
      }
      typeArgumentsAsClasses.add(getUnderlyingClass(baseType));
    }
    return typeArgumentsAsClasses;
  }

  static final class NullObject<T> implements InvocationHandler {
    private WeakReference<T> targetRef;

    NullObject(T target) {
      set(target);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
      final T value = get();
      return value == null ? null : method.invoke(value, args);
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

    void set(T value) {
      targetRef = new WeakReference<>(value);
    }
  }
}
