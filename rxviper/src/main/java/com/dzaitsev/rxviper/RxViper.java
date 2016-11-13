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

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Stack;

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

  static <V extends ViewCallbacks> V createView(V view, Class<? extends Presenter> actualClass) {
    return createProxy(view, Presenter.class, actualClass, 0);
  }

  static <R extends Router> R createRouter(R router, Class<? extends ViperPresenter> actualClass) {
    return createProxy(router, ViperPresenter.class, actualClass, 1);
  }

  @SuppressWarnings("unchecked")
  static <T> NullObject<T> getProxy(T target) {
    return (NullObject<T>) Proxy.getInvocationHandler(target);
  }

  @SuppressWarnings("unchecked")
  static Class getGenericParameterClass(final Class actualClass, final Class genericClass, final int parameterIndex) {
    check(genericClass.isAssignableFrom(actualClass) && !genericClass.equals(actualClass),
        "Class " + genericClass.getName() + " is not a superclass of " + actualClass.getName() + ".");
    final boolean isInterface = genericClass.isInterface();
    final Stack<ParameterizedType> genericClasses = new Stack<ParameterizedType>();
    Class clazz = actualClass;

    while (true) {
      final Type genericInterface = isInterface ? getGenericInterface(clazz, genericClass) : null;
      final Type currentType = genericInterface != null ? genericInterface : clazz.getGenericSuperclass();

      final boolean isParameterizedType = currentType instanceof ParameterizedType;
      if (isParameterizedType) {
        genericClasses.push((ParameterizedType) currentType);
      } else {
        genericClasses.clear();
      }
      final Type rawType = isParameterizedType ? ((ParameterizedType) currentType).getRawType() : currentType;
      if (!rawType.equals(genericClass)) {
        clazz = (Class) rawType;
      } else {
        break;
      }
    }

    Type result = genericClasses.pop()
        .getActualTypeArguments()[parameterIndex];

    while (result instanceof TypeVariable && !genericClasses.empty()) {
      final int actualArgumentIndex = getParameterTypeDeclarationIndex((TypeVariable) result);
      final ParameterizedType type = genericClasses.pop();
      result = type.getActualTypeArguments()[actualArgumentIndex];
    }

    check(!(result instanceof TypeVariable), "Unable to resolve type variable "
        + result
        + ". Try to replace instances of parametrized class with its non-parameterized subtype.");

    if (result instanceof ParameterizedType) {
      result = ((ParameterizedType) result).getRawType();
    }

    check(result != null, "Unable to determine actual parameter type for " + actualClass.getName() + ".");
    check(result instanceof Class, "Actual parameter type for " + actualClass.getName() + " is not a Class.");
    return (Class) result;
  }

  @SuppressWarnings("unchecked")
  private static <T, G> T createProxy(T target, Class<G> baseClass, Class<? extends G> childClass, int index) {
    try {
      final Class typeArgument = getGenericParameterClass(childClass, baseClass, index);
      final Class[] interfaces;
      if (typeArgument.isInterface()) {
        interfaces = new Class[1];
        interfaces[0] = typeArgument;
      } else {
        interfaces = typeArgument.getInterfaces();
      }
      return (T) Proxy.newProxyInstance(childClass.getClassLoader(), interfaces, new NullObject<>(target));
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private static int getParameterTypeDeclarationIndex(final TypeVariable typeVariable) {
    final GenericDeclaration genericDeclaration = typeVariable.getGenericDeclaration();
    final TypeVariable[] typeVariables = genericDeclaration.getTypeParameters();
    int actualArgumentIndex = -1;
    for (int i = 0; i < typeVariables.length; i++) {
      if (typeVariables[i].equals(typeVariable)) {
        actualArgumentIndex = i;
        break;
      }
    }
    check(actualArgumentIndex != -1, "Argument " + typeVariable.toString() + " is not found in " + genericDeclaration.toString() + ".");
    return actualArgumentIndex;
  }

  @SuppressWarnings("unchecked")
  private static Type getGenericInterface(final Class sourceClass, final Class genericInterface) {
    final Type[] types = sourceClass.getGenericInterfaces();
    for (Type type : types) {
      if (type instanceof Class) {
        if (genericInterface.isAssignableFrom((Class) type)) {
          return type;
        }
      } else if (type instanceof ParameterizedType) {
        if (genericInterface.isAssignableFrom((Class) ((ParameterizedType) type).getRawType())) {
          return type;
        }
      }
    }
    return null;
  }

  private static void check(boolean condition, String message) {
    if (!condition) {
      throw new IllegalStateException(message);
    }
  }
}
