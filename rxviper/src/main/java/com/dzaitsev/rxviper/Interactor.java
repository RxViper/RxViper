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
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.operators.flowable.FlowableInternalHelper;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

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
public abstract class Interactor<RequestModel, ResponseModel> implements Disposable {
  private final Scheduler           subscribeScheduler;
  private final Scheduler           observeScheduler;
  private final CompositeDisposable disposables;

  /**
   * @param subscribeScheduler
   *     the Scheduler that modifies source Flowable returned from {@link #createFlowable} to perform its emissions on.
   * @param observeScheduler
   *     the Scheduler that modifies source Flowable returned from {@link #createFlowable} to notify its Observers on.
   *
   * @since 0.1.0
   */
  protected Interactor(Scheduler subscribeScheduler, Scheduler observeScheduler) {
    RxViper.requireNotNull(subscribeScheduler);
    RxViper.requireNotNull(observeScheduler);

    this.subscribeScheduler = subscribeScheduler;
    this.observeScheduler = observeScheduler;
    disposables = new CompositeDisposable();
  }

  @Override
  public final void dispose() {
    // call clear() instead of dispose() to be able to manage new disposables
    disposables.clear();
  }

  @Override
  public final boolean isDisposed() {
    return disposables.size() == 0;
  }

  public final void execute(Consumer<? super ResponseModel> onNext) {
    execute(onNext, (RequestModel) null);
  }

  public final void execute(Consumer<? super ResponseModel> onNext, RequestModel requestModel) {
    execute(onNext, Functions.ERROR_CONSUMER, Functions.EMPTY_ACTION, FlowableInternalHelper.RequestMax.INSTANCE, requestModel);
  }

  public final void execute(Consumer<? super ResponseModel> onNext, Consumer<Throwable> onError) {
    execute(onNext, onError, (RequestModel) null);
  }

  public final void execute(Consumer<? super ResponseModel> onNext, Consumer<Throwable> onError, RequestModel requestModel) {
    execute(onNext, onError, Functions.EMPTY_ACTION, FlowableInternalHelper.RequestMax.INSTANCE, requestModel);
  }

  public final void execute(Consumer<? super ResponseModel> onNext, Consumer<Throwable> onError, Action onComplete) {
    execute(onNext, onError, onComplete, (RequestModel) null);
  }

  public final void execute(Consumer<? super ResponseModel> onNext, Consumer<Throwable> onError, Action onComplete,
      RequestModel requestModel) {
    execute(onNext, onError, onComplete, FlowableInternalHelper.RequestMax.INSTANCE, requestModel);
  }

  public final void execute(Consumer<? super ResponseModel> onNext, Consumer<Throwable> onError, Action onComplete,
      Consumer<? super Subscription> onSubscribe) {
    execute(onNext, onError, onComplete, onSubscribe, null);
  }

  public final void execute(Consumer<? super ResponseModel> onNext, Consumer<Throwable> onError, Action onComplete,
      Consumer<? super Subscription> onSubscribe, RequestModel requestModel) {
    disposables.add(flowable(requestModel).subscribe(onNext, onError, onComplete, onSubscribe));
  }

  public final void execute(Subscriber<? super ResponseModel> subscriber) {
    execute(subscriber, null);
  }

  public final void execute(Subscriber<? super ResponseModel> subscriber, RequestModel requestModel) {
    flowable(requestModel).subscribe(subscriber);
  }

  /**
   * Provides source Flowable that will execute the specified parameter when {@code execute()} method is called.
   * <p>
   * It will use schedulers provided in {@link #Interactor(Scheduler, Scheduler)}.
   *
   * @param requestModel
   *     request message to a replier system which receives and processes the request
   *
   * @return source Flowable
   *
   * @see #execute(Subscriber)
   * @see #execute(Subscriber, Object)
   * @see #execute(Consumer)
   * @see #execute(Consumer, Object)
   * @see #execute(Consumer, Consumer)
   * @see #execute(Consumer, Consumer, Object)
   * @see #execute(Consumer, Consumer, Action)
   * @see #execute(Consumer, Consumer, Action, Object)
   * @see #execute(Consumer, Consumer, Action, Consumer)
   * @see #execute(Consumer, Consumer, Action, Consumer, Object)
   * @since 0.1.0
   */
  protected abstract Flowable<ResponseModel> createFlowable(RequestModel requestModel);

  private Flowable<ResponseModel> flowable(RequestModel requestModel) {
    return createFlowable(requestModel).subscribeOn(subscribeScheduler)
        .observeOn(observeScheduler);
  }
}
