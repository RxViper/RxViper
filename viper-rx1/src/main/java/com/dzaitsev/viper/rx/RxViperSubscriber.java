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

import com.dzaitsev.viper.callbacks.OnComplete;
import com.dzaitsev.viper.callbacks.OnFailure;
import com.dzaitsev.viper.callbacks.OnSuccess;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.internal.util.ActionSubscriber;
import rx.observers.SafeSubscriber;

final class RxViperSubscriber<I> extends SafeSubscriber<I> {
  private boolean isDone;

  private RxViperSubscriber(Subscriber<? super I> subscriber) {
    super(subscriber);
  }

  static <I> RxViperSubscriber<I> of(final OnSuccess<? super I> onSuccess, final OnFailure onFailure, final OnComplete onComplete) {
    return new RxViperSubscriber<>(new ActionSubscriber<>(new Action1<I>() {
      @Override
      public void call(I item) {
        onSuccess.onSuccess(item);
      }
    }, new Action1<Throwable>() {
      @Override
      public void call(Throwable throwable) {
        onFailure.onFailure(throwable);
      }
    }, new Action0() {
      @Override
      public void call() {
        onComplete.onComplete();
      }
    }));
  }

  static <I> RxViperSubscriber<I> of(Subscriber<? super I> subscriber) {
    return new RxViperSubscriber<>(subscriber);
  }

  @Override
  public void onCompleted() {
    super.onCompleted();
    isDone = true;
  }

  @Override
  public void onError(Throwable t) {
    super.onError(t);
    isDone = true;
  }

  boolean isDone() {
    return isDone;
  }
}
