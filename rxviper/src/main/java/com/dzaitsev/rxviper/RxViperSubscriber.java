/*
 * Copyright 2018 Dmytro Zaitsev
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

import io.reactivex.subscribers.DisposableSubscriber;
import org.reactivestreams.Subscriber;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2018-Jan-29, 11:38
 */
class RxViperSubscriber<T> extends DisposableSubscriber<T> {
  private final Subscriber<T> subscriber;

  RxViperSubscriber(Subscriber<T> subscriber) {
    this.subscriber = subscriber;
  }

  @Override
  public void onNext(T t) {
    subscriber.onNext(t);
  }

  @Override
  public void onError(Throwable t) {
    subscriber.onError(t);
  }

  @Override
  public void onComplete() {
    subscriber.onComplete();
  }
}
