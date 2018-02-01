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
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.internal.util.ActionSubscriber;
import rx.subscriptions.CompositeSubscription;

import static com.dzaitsev.rxviper.RxViper.requireNotNull;
import static rx.internal.util.InternalObservableUtils.ERROR_NOT_IMPLEMENTED;

/**
 * Contains the business logic as specified by a use case.
 *
 * @param <RequestModel> the type of request message
 * @param <ResponseModel> the type of response message
 *
 * @author Dmytro Zaitsev
 * @since 0.1.0
 */
public abstract class Interactor<RequestModel, ResponseModel> implements Subscription {
  @Nonnull private final Scheduler             subscribeScheduler;
  @Nonnull private final Scheduler             observeScheduler;
  @Nonnull private final CompositeSubscription subscriptions;

  /**
   * @param subscribeScheduler the {@code Scheduler} that modifies source {@code Observable} returned from {@link #createObservable}
   *     to perform its emissions on.
   * @param observeScheduler the {@code Scheduler} that modifies source {@code Observable} returned from {@link #createObservable} to
   *     notify its {@code Observer}s on.
   *
   * @since 0.1.0
   */
  protected Interactor(@Nonnull Scheduler subscribeScheduler, @Nonnull Scheduler observeScheduler) {
    requireNotNull(subscribeScheduler);
    requireNotNull(observeScheduler);

    this.subscribeScheduler = subscribeScheduler;
    this.observeScheduler = observeScheduler;
    subscriptions = new CompositeSubscription();
  }

  /**
   * Subscribes to an {@code Observable} and provides a callback to handle the items it emits.
   *
   * @param onNext the {@code Action1<ResponseModel>} you have designed to accept emissions from the {@code Observable}
   *
   * @throws IllegalArgumentException if {@code onNext} is {@code null}
   * @see #execute(Action1, Object)
   * @since 0.4.0
   */
  public final void execute(@Nonnull Action1<? super ResponseModel> onNext) {
    execute(onNext, (RequestModel) null);
  }

  /**
   * Subscribes to an {@code Observable} and provides a callback to handle the items it emits.
   *
   * @param onNext the {@code Action1<ResponseModel>} you have designed to accept emissions from the {@code Observable}
   * @param requestModel the request message to a replier system which receives and processes the request
   *
   * @throws IllegalArgumentException if {@code onNext} is {@code null}
   * @see #execute(Action1)
   * @since 0.4.0
   */
  public final void execute(@Nonnull Action1<? super ResponseModel> onNext, @Nullable RequestModel requestModel) {
    requireNotNull(onNext);

    execute(new ActionSubscriber<>(onNext, ERROR_NOT_IMPLEMENTED, Actions.empty()), requestModel);
  }

  /**
   * Subscribes to an {@code Observable} and provides callbacks to handle the items it emits and any error notification it issues.
   *
   * @param onNext the {@code Action1<ResponseModel>} you have designed to accept emissions from the {@code Observable}
   * @param onError the {@code Action1<Throwable>} you have designed to accept any error notification from the {@code Observable}
   *
   * @throws IllegalArgumentException if {@code onNext} is {@code null}, or if {@code onError} is {@code null}
   * @see #execute(Action1, Action1, Object)
   * @since 0.4.0
   */
  public final void execute(@Nonnull Action1<? super ResponseModel> onNext, @Nonnull Action1<Throwable> onError) {
    execute(onNext, onError, (RequestModel) null);
  }

  /**
   * Subscribes to an {@code Observable} and provides callbacks to handle the items it emits and any error notification it issues.
   *
   * @param onNext the {@code Action1<ResponseModel>} you have designed to accept emissions from the {@code Observable}
   * @param onError the {@code Action1<Throwable>} you have designed to accept any error notification from the {@code Observable}
   * @param requestModel the request message to a replier system which receives and processes the request
   *
   * @throws IllegalArgumentException if {@code onNext} is {@code null}, or if {@code onError} is {@code null}
   * @see #execute(Action1, Action1)
   * @since 0.4.0
   */
  public final void execute(@Nonnull Action1<? super ResponseModel> onNext, @Nonnull Action1<Throwable> onError,
      @Nullable RequestModel requestModel) {
    requireNotNull(onNext);
    requireNotNull(onError);

    execute(new ActionSubscriber<>(onNext, onError, Actions.empty()), requestModel);
  }

  /**
   * Subscribes to an {@code Observable} and provides callbacks to handle the items it emits and any error or completion notification it
   * issues.
   *
   * @param onNext the {@code Action1<ResponseModel>} you have designed to accept emissions from the {@code Observable}
   * @param onError the {@code Action1<Throwable>} you have designed to accept any error notification from the {@code Observable}
   * @param onCompleted the {@code Action0} you have designed to accept a completion notification from the {@code Observable}
   *
   * @throws IllegalArgumentException if {@code onNext} is {@code null}, or if {@code onError} is {@code null}, or if {@code
   *     onComplete} is {@code null}
   * @see #execute(Action1, Action1, Action0, Object)
   * @since 0.4.0
   */
  public final void execute(@Nonnull Action1<? super ResponseModel> onNext, @Nonnull Action1<Throwable> onError,
      @Nonnull Action0 onCompleted) {
    execute(onNext, onError, onCompleted, null);
  }

  /**
   * Subscribes to an {@code Observable} and provides callbacks to handle the items it emits and any error or completion notification it
   * issues.
   *
   * @param onNext the {@code Action1<ResponseModel>} you have designed to accept emissions from the {@code Observable}
   * @param onError the {@code Action1<Throwable>} you have designed to accept any error notification from the {@code Observable}
   * @param onCompleted the {@code Action0} you have designed to accept a completion notification from the {@code Observable}
   * @param requestModel the request message to a replier system which receives and processes the request
   *
   * @throws IllegalArgumentException if {@code onNext} is {@code null}, or if {@code onError} is {@code null}, or if {@code
   *     onComplete} is {@code null}
   * @see #execute(Action1, Action1, Action0)
   * @since 0.4.0
   */
  public final void execute(@Nonnull Action1<? super ResponseModel> onNext, @Nonnull Action1<Throwable> onError,
      @Nonnull Action0 onCompleted, @Nullable RequestModel requestModel) {
    requireNotNull(onNext);
    requireNotNull(onError);
    requireNotNull(onCompleted);

    execute(new ActionSubscriber<>(onNext, onError, onCompleted), requestModel);
  }

  /**
   * Subscribes to an {@code Observable} and provides a {@code Subscriber} that implements functions to handle the items the {@code
   * Observable} emits and any error or completion notification it issues.
   *
   * @param subscriber the {@code Subscriber} that will handle emissions and notifications from the {@code Observable}
   *
   * @see #execute(Subscriber, Object)
   * @since 0.2.0
   */
  public final void execute(@Nonnull Subscriber<? super ResponseModel> subscriber) {
    execute(subscriber, null);
  }

  /**
   * Subscribes to an {@code Observable} and provides a {@code Subscriber} that implements functions to handle the items the {@code
   * Observable} emits and any error or completion notification it issues.
   *
   * @param subscriber the {@code Subscriber} that will handle emissions and notifications from the {@code Observable}
   * @param requestModel the request message to a replier system which receives and processes the request
   *
   * @throws IllegalStateException if {@code subscribe} is unable to obtain an {@code OnSubscribe<>} function
   * @see #execute(Subscriber)
   * @since 0.1.0
   */
  public final void execute(@Nonnull Subscriber<? super ResponseModel> subscriber, @Nullable RequestModel requestModel) {
    requireNotNull(subscriber);

    subscriptions.add(createObservable(requestModel).subscribeOn(subscribeScheduler)
        .observeOn(observeScheduler)
        .subscribe(subscriber));
  }

  /**
   * Stops the receipt of notifications on the {@code Subscriber}s that were registered.
   * <p>
   * This allows unregistering executed {@code Subscriber}s before they have finished receiving all events.
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
   * Provides source {@code Observable} that will execute the specified parameter when {@code execute()} method is called.
   * <p>
   * It will use schedulers provided in {@link #Interactor(Scheduler, Scheduler) the constructor}.
   *
   * @param requestModel the request message to a replier system which receives and processes the request
   *
   * @return source {@code Observable}
   *
   * @see #execute(Subscriber)
   * @see #execute(Subscriber, Object)
   * @see #execute(Action1)
   * @see #execute(Action1, Object)
   * @see #execute(Action1, Action1)
   * @see #execute(Action1, Action1, Object)
   * @see #execute(Action1, Action1, Action0)
   * @see #execute(Action1, Action1, Action0, Object)
   * @since 0.1.0
   */
  @Nonnull
  protected abstract Observable<ResponseModel> createObservable(@Nullable RequestModel requestModel);
}
