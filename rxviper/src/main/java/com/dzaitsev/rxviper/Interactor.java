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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.dzaitsev.rxviper.RxViper.requireNotNull;

/**
 * Contains the business logic as specified by a use case
 *
 * @param <RequestModel>
 *     the type of request message
 * @param <ResponseModel>
 *     the type of response message
 *
 * @author Dmytro Zaitsev
 * @since 0.1.0
 */
public abstract class Interactor<RequestModel, ResponseModel> implements Subscription, RxViperInteractor<RequestModel, ResponseModel> {
  @Nonnull private final Scheduler             subscribeScheduler;
  @Nonnull private final Scheduler             observeScheduler;
  @Nonnull private final CompositeSubscription subscriptions;

  /**
   * @param subscribeScheduler
   *     the Scheduler that modifies source Observable returned from {@link #createObservable} to perform its emissions on.
   * @param observeScheduler
   *     the Scheduler that modifies source Observable returned from {@link #createObservable} to notify its Observers on.
   *
   * @since 0.1.0
   */
  @SuppressWarnings("WeakerAccess")
  protected Interactor(@Nonnull Scheduler subscribeScheduler, @Nonnull Scheduler observeScheduler) {
    requireNotNull(subscribeScheduler);
    requireNotNull(observeScheduler);

    this.subscribeScheduler = subscribeScheduler;
    this.observeScheduler = observeScheduler;
    subscriptions = new CompositeSubscription();
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
   * @throws IllegalArgumentException
   *     if the {@link Subscriber} provided as the argument to {@code subscribe} is {@code null}
   * @throws rx.exceptions.OnErrorNotImplementedException
   *     if the {@link Subscriber}'s {@code onError} method is null
   * @throws RuntimeException
   *     if the {@link Subscriber}'s {@code onError} method itself threw a {@code Throwable}
   * @since 0.1.0
   */
  @SuppressWarnings("WeakerAccess")
  public final void execute(@Nonnull Subscriber<? super ResponseModel> subscriber, @Nullable RequestModel requestModel) {
    requireNotNull(subscriber);

    if (!(subscriber instanceof RxViperSubscriber)) {
      subscriber = RxViperSubscriber.of(subscriber);
    }

    subscriptions.add(createObservable(requestModel).subscribeOn(subscribeScheduler)
        .observeOn(observeScheduler)
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
  @SuppressWarnings("WeakerAccess")
  public final void execute(@Nonnull Subscriber<? super ResponseModel> subscriber) {
    execute(subscriber, null);
  }

  /**
   * Subscribes to an Observable and provides a callback to handle the items it emits.
   *
   * @param onNext
   *     the {@code OnNext<ResponseModel>} you have designed to accept emissions from the Observable
   *
   * @throws IllegalArgumentException
   *     if {@code onNext} is null
   * @throws rx.exceptions.OnErrorNotImplementedException
   *     if the Observable calls {@code onError}
   * @see #execute(OnNext, Object)
   * @since 0.4.0
   */
  @Override
  public final void execute(@Nonnull OnNext<? super ResponseModel> onNext) {
    execute(onNext, (RequestModel) null);
  }

  /**
   * Subscribes to an Observable and provides a callback to handle the items it emits.
   *
   * @param onNext
   *     the {@code OnNext<ResponseModel>} you have designed to accept emissions from the Observable
   * @param requestModel
   *     parameter which will be passed to {@link #createObservable(Object)}.
   *
   * @throws IllegalArgumentException
   *     if {@code onNext} is null
   * @throws rx.exceptions.OnErrorNotImplementedException
   *     if the Observable calls {@code onError}
   * @since 0.4.0
   */
  @Override
  public final void execute(@Nonnull OnNext<? super ResponseModel> onNext, @Nullable RequestModel requestModel) {
    requireNotNull(onNext);

    execute(RxViperSubscriber.of(onNext, OnError.NOT_IMPLEMENTED, OnComplete.EMPTY), requestModel);
  }

  /**
   * Subscribes to an Observable and provides callbacks to handle the items it emits and any error notification it issues.
   *
   * @param onNext
   *     the {@code OnNext<ResponseModel>} you have designed to accept emissions from the Observable
   * @param onError
   *     the {@code OnError} you have designed to accept any error notification from the Observable
   *
   * @throws IllegalArgumentException
   *     if {@code onNext} is null, or if {@code onError} is null
   * @see #execute(OnNext, Object)
   * @since 0.4.0
   */
  @Override
  public final void execute(@Nonnull OnNext<? super ResponseModel> onNext, @Nonnull OnError onError) {
    execute(onNext, onError, (RequestModel) null);
  }

  /**
   * Subscribes to an Observable and provides callbacks to handle the items it emits and any error notification it issues.
   *
   * @param onNext
   *     the {@code OnNext<ResponseModel>} you have designed to accept emissions from the Observable
   * @param onError
   *     the {@code OnError} you have designed to accept any error notification from the Observable
   * @param requestModel
   *     parameter which will be passed to {@link #createObservable(Object)}.
   *
   * @throws IllegalArgumentException
   *     if {@code onNext} is null, or if {@code onError} is null
   * @since 0.4.0
   */
  @Override
  public final void execute(@Nonnull OnNext<? super ResponseModel> onNext, @Nonnull OnError onError,
      @Nullable RequestModel requestModel) {
    requireNotNull(onNext);
    requireNotNull(onError);

    execute(RxViperSubscriber.of(onNext, onError, OnComplete.EMPTY), requestModel);
  }

  /**
   * Subscribes to an Observable and provides callbacks to handle the items it emits and any error or completion notification it issues.
   *
   * @param onNext
   *     the {@code OnNext<ResponseModel>} you have designed to accept emissions from the Observable
   * @param onError
   *     the {@code OnError} you have designed to accept any error notification from the Observable
   * @param onComplete
   *     the {@code OnComplete} you have designed to accept a completion notification from the Observable
   *
   * @throws IllegalArgumentException
   *     if {@code onNext} is null, or if {@code onError} is null, or if {@code onComplete} is null
   * @see #execute(OnNext, OnError, OnComplete, Object)
   * @since 0.4.0
   */
  @Override
  public final void execute(@Nonnull OnNext<? super ResponseModel> onNext, @Nonnull OnError onError, @Nonnull OnComplete onComplete) {
    execute(onNext, onError, onComplete, null);
  }

  /**
   * Subscribes to an Observable and provides callbacks to handle the items it emits and any error or completion notification it issues.
   *
   * @param onNext
   *     the {@code OnNext<ResponseModel>} you have designed to accept emissions from the Observable
   * @param onError
   *     the {@code OnError} you have designed to accept any error notification from the Observable
   * @param onComplete
   *     the {@code OnComplete} you have designed to accept a completion notification from the Observable
   * @param requestModel
   *     parameter which will be passed to {@link #createObservable(Object)}.
   *
   * @throws IllegalArgumentException
   *     if {@code onNext} is null, or if {@code onError} is null, or if {@code onComplete} is null
   * @since 0.4.0
   */
  @Override
  public final void execute(@Nonnull OnNext<? super ResponseModel> onNext, @Nonnull OnError onError, @Nonnull OnComplete onComplete,
      @Nullable RequestModel requestModel) {
    requireNotNull(onNext);
    requireNotNull(onError);
    requireNotNull(onComplete);

    execute(RxViperSubscriber.of(onNext, onError, onComplete), requestModel);
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

  /**
   * Indicates whether this {@code Interactor} is currently unsubscribed.
   *
   * @return {@code true} if this {@code Interactor} is currently unsubscribed, {@code false} otherwise
   *
   * @since 0.4.0
   */
  @Override
  public final boolean isUnsubscribed() {
    return !subscriptions.hasSubscriptions();
  }

  /**
   * Provides source Observable that will execute the specified parameter when {@code execute()} method is called.
   * <p>
   * It will use schedulers provided in {@link #Interactor(Scheduler, Scheduler)}.
   *
   * @param requestModel
   *     request message to a replier system which receives and processes the request
   *
   * @return source Observable
   *
   * @see #execute(Subscriber)
   * @see #execute(Subscriber, Object)
   * @see #execute(OnNext)
   * @see #execute(OnNext, Object)
   * @see #execute(OnNext, OnError)
   * @see #execute(OnNext, OnError, Object)
   * @see #execute(OnNext, OnError, OnComplete)
   * @see #execute(OnNext, OnError, OnComplete, Object)
   * @since 0.1.0
   */
  @SuppressWarnings("NullableProblems")
  @Nonnull
  protected abstract Observable<ResponseModel> createObservable(@Nullable RequestModel requestModel);
}
