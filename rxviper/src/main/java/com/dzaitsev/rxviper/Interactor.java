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

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.reactivestreams.Subscriber;

import static com.dzaitsev.rxviper.RxViper.requireNotNull;
import static io.reactivex.internal.functions.Functions.EMPTY_ACTION;
import static io.reactivex.internal.functions.Functions.ERROR_CONSUMER;

/**
 * Contains the business logic as specified by a use case.
 *
 * @param <RequestModel> the type of request message
 * @param <ResponseModel> the type of response message
 *
 * @author Dmytro Zaitsev
 * @since 2.0.0
 */
public abstract class Interactor<RequestModel, ResponseModel> implements Disposable {
  @Nonnull private final Scheduler           subscribeScheduler;
  @Nonnull private final Scheduler           observeScheduler;
  @Nonnull private final CompositeDisposable disposables;

  /**
   * @param subscribeScheduler the {@code Scheduler} to perform subscription actions on
   * @param observeScheduler the {@code Scheduler} to notify {@code Subscriber}s on
   *
   * @since 2.0.0
   */
  protected Interactor(@Nonnull Scheduler subscribeScheduler, @Nonnull Scheduler observeScheduler) {
    requireNotNull(subscribeScheduler);
    requireNotNull(observeScheduler);

    this.subscribeScheduler = subscribeScheduler;
    this.observeScheduler = observeScheduler;
    disposables = new CompositeDisposable();
  }

  /**
   * Subscribes to a {@code Flowable} and provides callbacks to handle the items it emits.
   *
   * @param onNext the {@code Consumer<ResponseModel>} you have designed to accept emissions from the {@code Flowable}
   *
   * @throws IllegalArgumentException if {@code onNext} is {@code null}
   * @see #execute(Consumer, Object)
   * @since 2.0.0
   */
  public final void execute(@Nonnull Consumer<? super ResponseModel> onNext) {
    execute(onNext, (RequestModel) null);
  }

  /**
   * Subscribes to a {@code Flowable} and provides callbacks to handle the items it emits.
   *
   * @param onNext the {@code Consumer<ResponseModel>} you have designed to accept emissions from the {@code Flowable}
   * @param requestModel the request message to a replier system which receives and processes the request
   *
   * @throws IllegalArgumentException if {@code onNext} is {@code null}
   * @see #execute(Consumer)
   * @since 2.0.0
   */
  public final void execute(@Nonnull Consumer<? super ResponseModel> onNext, @Nullable RequestModel requestModel) {
    execute(onNext, ERROR_CONSUMER, EMPTY_ACTION, requestModel);
  }

  /**
   * Subscribes to a {@code Flowable} and provides callbacks to handle the items it emits and any error notification it issues.
   *
   * @param onNext the {@code Consumer<ResponseModel>} you have designed to accept emissions from the {@code Flowable}
   * @param onError the {@code Consumer<Throwable>} you have designed to accept any error notification from the {@code Flowable}
   *
   * @throws IllegalArgumentException if {@code onNext} is {@code null}, or if {@code onError} is {@code null}
   * @see #execute(Consumer, Consumer, Object)
   * @since 2.0.0
   */
  public final void execute(@Nonnull Consumer<? super ResponseModel> onNext, @Nonnull Consumer<Throwable> onError) {
    execute(onNext, onError, (RequestModel) null);
  }

  /**
   * Subscribes to a {@code Flowable} and provides callbacks to handle the items it emits and any error notification it issues.
   *
   * @param onNext the {@code Consumer<ResponseModel>} you have designed to accept emissions from the {@code Flowable}
   * @param onError the {@code Consumer<Throwable>} you have designed to accept any error notification from the {@code Flowable}
   * @param requestModel the request message to a replier system which receives and processes the request
   *
   * @throws IllegalArgumentException if {@code onNext} is {@code null}, or if {@code onError} is {@code null}
   * @see #execute(Consumer, Consumer)
   * @since 2.0.0
   */
  public final void execute(@Nonnull Consumer<? super ResponseModel> onNext, @Nonnull Consumer<Throwable> onError,
      @Nullable RequestModel requestModel) {
    execute(onNext, onError, EMPTY_ACTION, requestModel);
  }

  /**
   * Subscribes to a {@code Flowable} and provides callbacks to handle the items it emits and any error or completion notification it
   * issues.
   *
   * @param onNext the {@code Consumer<ResponseModel>} you have designed to accept emissions from the {@code Flowable}
   * @param onError the {@code Consumer<Throwable>} you have designed to accept any error notification from the {@code Flowable}
   * @param onComplete the {@code Action} you have designed to accept a completion notification from the {@code Flowable}
   *
   * @throws IllegalArgumentException if {@code onNext} is {@code null}, or if {@code onError} is {@code null}, or if {@code
   *     onComplete} is {@code null}
   * @see #execute(Consumer, Consumer, Action, Object)
   * @since 2.0.0
   */
  public final void execute(@Nonnull Consumer<? super ResponseModel> onNext, @Nonnull Consumer<Throwable> onError,
      @Nonnull Action onComplete) {
    execute(onNext, onError, onComplete, null);
  }

  /**
   * Subscribes to a {@code Flowable} and provides callbacks to handle the items it emits and any error or completion notification it
   * issues.
   *
   * @param onNext the {@code Consumer<ResponseModel>} you have designed to accept emissions from the {@code Flowable}
   * @param onError the {@code Consumer<Throwable>} you have designed to accept any error notification from the {@code Flowable}
   * @param onComplete the {@code Action} you have designed to accept a completion notification from the {@code Flowable}
   * @param requestModel the request message to a replier system which receives and processes the request
   *
   * @throws IllegalArgumentException if {@code onNext} is {@code null}, or if {@code onError} is {@code null}, or if {@code
   *     onComplete} is {@code null}
   * @see #execute(Consumer, Consumer, Action)
   * @since 2.0.0
   */
  public final void execute(@Nonnull Consumer<? super ResponseModel> onNext, @Nonnull Consumer<Throwable> onError,
      @Nonnull Action onComplete, @Nullable RequestModel requestModel) {
    requireNotNull(onNext);
    requireNotNull(onError);
    requireNotNull(onComplete);
    disposables.add(flowable(requestModel).subscribe(onNext, onError, onComplete));
  }

  /**
   * Subscribes to a {@code Flowable} and provides a Subscriber that implements functions to handle the items the {@code Flowable}
   * emits and any error or completion notification it issues.
   *
   * @param subscriber the {@code Subscriber} that will consume signals from the {@code Flowable}
   *
   * @throws IllegalArgumentException if {@code subscriber} is {@code null}
   * @see #execute(Subscriber, Object)
   * @since 2.0.0
   */
  public final void execute(@Nonnull Subscriber<? super ResponseModel> subscriber) {
    execute(subscriber, null);
  }

  /**
   * Subscribes to a {@code Flowable} and provides a Subscriber that implements functions to handle the items the {@code Flowable}
   * emits and any error or completion notification it issues.
   *
   * @param subscriber the {@code Subscriber} that will consume signals from the {@code Flowable}
   * @param requestModel the request message to a replier system which receives and processes the request
   *
   * @throws IllegalArgumentException if {@code subscriber} is {@code null}
   * @since 2.0.0
   */
  public final void execute(@Nonnull Subscriber<? super ResponseModel> subscriber, @Nullable RequestModel requestModel) {
    requireNotNull(subscriber);

    final Flowable<ResponseModel> f = flowable(requestModel);
    disposables.add(subscriber instanceof Disposable ? (Disposable) f.subscribeWith(subscriber)
        : f.subscribeWith(new RxViperSubscriber<>(subscriber)));
  }

  /**
   * Stops the receipt of notifications on the {@code Subscriber}s that were registered.
   * <p>
   * This allows unregistering executed {@code Subscriber}s before they have finished receiving all events.
   *
   * @since 2.0.0
   */
  @Override
  public final void dispose() {
    // call clear() instead of dispose() to be able to manage new disposables
    disposables.clear();
  }

  /**
   * Indicates whether this {@code Interactor} is currently disposed.
   *
   * @return {@code true} if this {@code Interactor} is currently disposed, {@code false} otherwise
   *
   * @since 2.0.0
   */
  @Override
  public final boolean isDisposed() {
    return disposables.size() == 0;
  }

  /**
   * Provides source {@code Flowable} that will execute the specified parameter when {@code execute()} method is called.
   * <p>
   * It will use schedulers provided in {@link #Interactor(Scheduler, Scheduler) the constructor}.
   *
   * @param requestModel the request message to a replier system which receives and processes the request
   *
   * @return source {@code Flowable}
   *
   * @see #execute(Subscriber)
   * @see #execute(Subscriber, Object)
   * @see #execute(Consumer)
   * @see #execute(Consumer, Object)
   * @see #execute(Consumer, Consumer)
   * @see #execute(Consumer, Consumer, Object)
   * @see #execute(Consumer, Consumer, Action)
   * @see #execute(Consumer, Consumer, Action, Object)
   * @since 2.0.0
   */
  @Nonnull
  protected abstract Flowable<ResponseModel> createFlowable(@Nullable RequestModel requestModel);

  @Nonnull
  private Flowable<ResponseModel> flowable(@Nullable RequestModel requestModel) {
    return createFlowable(requestModel).subscribeOn(subscribeScheduler)
        .observeOn(observeScheduler);
  }
}
