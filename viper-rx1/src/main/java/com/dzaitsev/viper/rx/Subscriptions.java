/*
 * Copyright 2017 Dmytro Zaitsev
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

package com.dzaitsev.viper.rx;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2017-Nov-22, 02:11
 */

final class Subscriptions {
  private final CompositeSubscription   composite     = new CompositeSubscription();
  private final AtomicLong              id            = new AtomicLong(0);
  private final Map<Long, Subscription> subscriptions = new HashMap<>(4);

  void unsubscribe(long id) {
    synchronized(this) {
      final Subscription s = subscriptions.remove(id);
      if (s != null) {
        composite.remove(s);
      }
    }
  }

  long add(Subscription s) {
    synchronized(this) {
      composite.add(s);
      final long subscriptionId = id.incrementAndGet();
      subscriptions.put(subscriptionId, s);
      return subscriptionId;
    }
  }

  void clear() {
    synchronized(this) {
      composite.clear();
      subscriptions.clear();
      id.set(0);
    }
  }

  boolean isEmpty() {
    synchronized(this) {
      return subscriptions.isEmpty();
    }
  }
}
