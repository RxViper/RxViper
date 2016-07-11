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

package com.dzaitsev.rxviper

import java.lang.ref.WeakReference
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * Implementation of [Null Object pattern](https://en.wikipedia.org/wiki/Null_Object_pattern).
 *
 * @author Dmytro Zaitsev
 * @since 0.11.0
 */
internal class NullObject<T>(target: T?) : InvocationHandler {
  private var targetRef: WeakReference<T?>? = null

  init {
    set(target)
  }

  @Throws(Throwable::class)
  override fun invoke(proxy: Any?, method: Method, args: Array<Any>): Any? {
    val target = get()
    return if (target == null) null else method.invoke(target, *args)
  }

  fun clear() {
    targetRef?.clear()
    targetRef = null
  }

  fun get(): T? = targetRef?.get()

  fun set(target: T?) {
    targetRef = WeakReference(target)
  }
}
