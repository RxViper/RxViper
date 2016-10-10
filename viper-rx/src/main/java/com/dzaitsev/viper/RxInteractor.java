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

package com.dzaitsev.viper;

import java.util.concurrent.Callable;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

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
public abstract class RxInteractor<RequestModel, ResponseModel> extends Interactor<RequestModel, ResponseModel> implements Subscription {
  private final Scheduler             mSubscribeOn;
  private final Scheduler             mObserveOn;
  private final CompositeSubscription mSubscription;

  /**
   * @param subscribeOn
   *     the Scheduler that modifies source Observable returned from {@link #createObservable} to perform its emissions
   *     on.
   * @param observeOn
   *     the Scheduler that modifies source Observable returned from {@link #createObservable} to notify its Observers
   *     on.
   *
   * @see #createObservable(Object)
   * @since 0.1.0
   */
  protected RxInteractor(Scheduler subscribeOn, Scheduler observeOn) {
    Preconditions.requireNotNull(subscribeOn);
    Preconditions.requireNotNull(observeOn);

    mSubscribeOn = subscribeOn;
    mObserveOn = observeOn;
    mSubscription = new CompositeSubscription();
  }

  /**
   * Subscribes to an Observable and provides a Subscriber that implements functions to handle the items the Observable
   * emits and any error or completion notification it issues.
   *
   * @param subscriber
   *     the Subscriber that will handle emissions and notifications from the Observable
   *
   * @see #execute(Subscriber, RequestModel)
   * @since 0.2.0
   */
  public final void execute(Subscriber<? super ResponseModel> subscriber) {
    execute(subscriber, null);
  }

  /**
   * Subscribes to an Observable and provides a Subscriber that implements functions to handle the items the Observable
   * emits and any error or completion notification it issues.
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
   * @see Observable#subscribe(Subscriber)
   * @since 0.1.0
   */
  public final void execute(Subscriber<? super ResponseModel> subscriber, final RequestModel requestModel) {
    Preconditions.requireNotNull(subscriber);

    mSubscription.add(createObservable(requestModel).subscribeOn(mSubscribeOn)
        .observeOn(mObserveOn)
        .subscribe(subscriber));
  }

  @Override final void doJob(final OnNext<? super ResponseModel> onNext, final OnError onError, final OnCompleted onCompleted,
      RequestModel requestModel) {
    execute(new Subscriber<ResponseModel>() {
      @Override public void onCompleted() {
        onCompleted.onCompleted();
      }

      @Override public void onError(Throwable e) {
        onError.onError(e);
      }

      @Override public void onNext(ResponseModel model) {
        onNext.onNext(model);
      }
    });
  }

  /**
   * Stops the receipt of notifications on the {@link Subscriber}s that were registered.
   * <p>
   * This allows unregistering executed {@link Subscriber}s before they have finished receiving all events (i.e. before
   * onCompleted is called).
   *
   * @since 0.1.0
   */
  @Override public final void unsubscribe() {
    // call clear() instead of unsubscribe() to be able to manage new subscriptions
    mSubscription.clear();
  }

  /**
   * Indicates whether this {@code RxInteractor} is currently unsubscribed.
   *
   * @return {@code true} if this {@code RxInteractor} is currently unsubscribed, {@code false} otherwise
   *
   * @since 0.4.0
   */
  @Override public final boolean isUnsubscribed() {
    return !mSubscription.hasSubscriptions();
  }

  @Override protected ResponseModel getData(RequestModel requestModel) {
    return null;
  }

  protected Observable<ResponseModel> createObservable(final RequestModel requestModel) {
    return Observable.fromCallable(new Callable<ResponseModel>() {
      @Override public ResponseModel call() {
        return getData(requestModel);
      }
    });
  }
}
