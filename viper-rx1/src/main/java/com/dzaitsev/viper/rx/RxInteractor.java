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

package com.dzaitsev.viper.rx;

import com.dzaitsev.viper.Interactor;
import com.dzaitsev.viper.callbacks.OnComplete;
import com.dzaitsev.viper.callbacks.OnFailure;
import com.dzaitsev.viper.callbacks.OnSuccess;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;

import static java.util.Objects.requireNonNull;

public abstract class RxInteractor<RequestModel, ResponseModel> extends Interactor<RequestModel, ResponseModel> implements Subscription {
  @Nonnull private final Scheduler     subscribeOn;
  @Nonnull private final Scheduler     observeOn;
  @Nonnull private final Subscriptions subscriptions;

  /**
   * @param subscribeOn
   *     the Scheduler that modifies source Observable returned from {@link #createObservable} to perform its emissions on.
   * @param observeOn
   *     the Scheduler that modifies source Observable returned from {@link #createObservable} to notify its Observers on.
   *
   * @since 0.1.0
   */
  protected RxInteractor(Scheduler subscribeOn, Scheduler observeOn) {
    requireNonNull(subscribeOn, "subscribeOn");
    requireNonNull(observeOn, "observeOn");

    this.subscribeOn = subscribeOn;
    this.observeOn = observeOn;
    subscriptions = new Subscriptions();
  }

  /**
   * Subscribes to an Observable and provides a Subscriber that implements functions to handle the items the Observable emits and any
   * error or completion notification it issues.
   *
   * @param subscriber
   *     the Subscriber that will handle emissions and notifications from the Observable
   * @param requestModel
   *     parameter which will be passed to {@link #createObservable(Object)}.
   *
   * @throws IllegalStateException
   *     if {@code subscribe} is unable to obtain an {@code OnSubscribe<>} function
   * @throws NullPointerException
   *     if the {@link Subscriber} provided as the argument to {@code subscribe} is {@code null}
   * @throws rx.exceptions.OnErrorNotImplementedException
   *     if the {@link Subscriber}'s {@code onError} method is null
   * @throws RuntimeException
   *     if the {@link Subscriber}'s {@code onError} method itself threw a {@code Throwable}
   * @since 0.1.0
   */
  public final long execute(Subscriber<? super ResponseModel> subscriber, @Nullable RequestModel requestModel) {
    requireNonNull(subscriber, "subscriber");

    if (!(subscriber instanceof RxViperSubscriber)) {
      subscriber = RxViperSubscriber.of(subscriber);
    }

    return subscriptions.add(createObservable(requestModel).subscribeOn(subscribeOn)
        .observeOn(observeOn)
        .subscribe(subscriber));
  }

  /**
   * Subscribes to an Observable and provides a Subscriber that implements functions to handle the items the Observable emits and any
   * error or completion notification it issues.
   *
   * @param subscriber
   *     the Subscriber that will handle emissions and notifications from the Observable
   *
   * @see #execute(Subscriber, Object)
   * @since 0.2.0
   */
  public final long execute(Subscriber<? super ResponseModel> subscriber) {
    return execute(subscriber, null);
  }

  /**
   * Subscribes to an Observable and provides callbacks to handle the items it emits and any error or completion notification it issues.
   *
   * @param onSuccess
   *     the {@code OnNext<ResponseModel>} you have designed to accept emissions from the Observable
   * @param onFailure
   *     the {@code OnError} you have designed to accept any error notification from the Observable
   * @param onComplete
   *     the {@code OnComplete} you have designed to accept a completion notification from the Observable
   *
   * @throws NullPointerException
   *     if {@code onNext} is null, or if {@code onError} is null, or if {@code onComplete} is null
   * @see #execute(OnSuccess, OnFailure, OnComplete, Object)
   * @since 0.4.0
   */
  public final long execute(OnSuccess<? super ResponseModel> onSuccess, OnFailure onFailure, OnComplete onComplete) {
    return execute(onSuccess, onFailure, onComplete, null);
  }

  /**
   * Subscribes to an Observable and provides callbacks to handle the items it emits and any error or completion notification it issues.
   *
   * @param onSuccess
   *     the {@code OnSuccess<ResponseModel>} you have designed to accept emissions from the Observable
   * @param onFailure
   *     the {@code OnFailure} you have designed to accept any error notification from the Observable
   * @param onComplete
   *     the {@code OnComplete} you have designed to accept a completion notification from the Observable
   * @param requestModel
   *     parameter which will be passed to {@link #createObservable(Object)}.
   *
   * @throws NullPointerException
   *     if {@code onSuccess} is null, or if {@code onFailure} is null, or if {@code onComplete} is null
   * @since 0.4.0
   */
  public final long execute(OnSuccess<? super ResponseModel> onSuccess, OnFailure onFailure, OnComplete onComplete,
      @Nullable RequestModel requestModel) {
    requireNonNull(onSuccess, "onSuccess");
    requireNonNull(onFailure, "onFailure");
    requireNonNull(onComplete, "onComplete");

    return execute(RxViperSubscriber.of(onSuccess, onFailure, onComplete), requestModel);
  }

  /**
   * Stops the receipt of notifications on the {@link Subscriber}s that were registered.
   * <p>
   * This allows unregistering executed {@link Subscriber}s before they have finished receiving all events (i.e. before onCompleted is
   * called).
   *
   * @since 0.1.0
   */
  @Override
  public final void unsubscribe() {
    // call clear() instead of unsubscribe() to be able to manage new subscriptions
    subscriptions.clear();
  }

  public final void unsubscribe(long id) {
    subscriptions.unsubscribe(id);
  }

  /**
   * Indicates whether this {@code RxInteractor} is currently unsubscribed.
   *
   * @return {@code true} if this {@code RxInteractor} is currently unsubscribed, {@code false} otherwise
   *
   * @since 0.4.0
   */
  @Override
  public final boolean isUnsubscribed() {
    return subscriptions.isEmpty();
  }

  @Override
  protected final long execInternal(OnSuccess<? super ResponseModel> onSuccess, OnFailure onFailure, @Nullable RequestModel model) {
    requireNonNull(onSuccess, "onSuccess");
    requireNonNull(onFailure, "onFailure");

    return execute(onSuccess, onFailure, OnComplete.EMPTY, model);
  }

  /**
   * Provides source Observable that will execute the specified parameter when {@code execute()} method is called.
   * <p>
   * It will use schedulers provided in {@link #RxInteractor(Scheduler, Scheduler)}.
   *
   * @param requestModel
   *     request message to a replier system which receives and processes the request
   *
   * @return source Observable
   *
   * @see #execute(Subscriber)
   * @see #execute(Subscriber, Object)
   * @see #execute(OnSuccess)
   * @see #execute(OnSuccess, Object)
   * @see #execute(OnSuccess, OnFailure)
   * @see #execute(OnSuccess, OnFailure, Object)
   * @see #execute(OnSuccess, OnFailure, OnComplete)
   * @see #execute(OnSuccess, OnFailure, OnComplete, Object)
   * @since 0.1.0
   */
  @Nonnull
  protected abstract Observable<ResponseModel> createObservable(@Nullable RequestModel requestModel);
}
