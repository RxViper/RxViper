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

import rx.Observable
import rx.Scheduler
import rx.Subscriber
import rx.Subscription
import rx.functions.Action0
import rx.functions.Action1
import rx.functions.Actions
import rx.internal.util.ActionSubscriber
import rx.internal.util.InternalObservableUtils
import rx.subscriptions.CompositeSubscription

/**
 * Contains the business logic as specified by a use case
 *
 * @constructor
 * @param subscribeScheduler
 *     the Scheduler that modifies source Observable returned from [createObservable] to perform its emissions on.
 * @param observeScheduler
 *     the Scheduler that modifies source Observable returned from [createObservable] to notify its Observers on.
 *
 * @author Dmytro Zaitsev
 * @since 0.1.0
 */
abstract class Interactor<RequestModel, ResponseModel>
protected constructor(private val subscribeScheduler: Scheduler, private val observeScheduler: Scheduler) : Subscription {

  private val subscriptions = CompositeSubscription()

  /**
   * Subscribes to an Observable and provides a Subscriber that implements functions to handle the items the Observable emits and any
   * error or completion notification it issues.
   *
   * @param subscriber
   *     the Subscriber that will handle emissions and notifications from the Observable
   * @param requestModel
   *     parameter which will be passed to [createObservable].
   *
   * @throws IllegalStateException
   *     if `subscribe` is unable to obtain an `OnSubscribe<>` function
   * @throws IllegalArgumentException
   *     if the [Subscriber] provided as the argument to `subscribe` is `null`
   * @throws rx.exceptions.OnErrorNotImplementedException
   *     if the [Subscriber]'s `onError` method is null
   * @throws RuntimeException
   *     if the [Subscriber]'s `onError` method itself threw a `Throwable`
   *
   * @see Observable.subscribe
   * @since 0.1.0
   */
  @JvmOverloads
  fun execute(subscriber: Subscriber<in ResponseModel>, requestModel: RequestModel? = null) {
    requireNotNull(subscriber)
    subscriptions.add(createObservable(requestModel).subscribeOn(subscribeScheduler).observeOn(observeScheduler).subscribe(subscriber))
  }

  /**
   * Subscribes to an Observable and provides a callback to handle the items it emits.
   *
   * @param onNext
   *     the `Action1<ResponseModel>` you have designed to accept emissions from the Observable
   * @param requestModel
   *     parameter which will be passed to [createObservable].
   *
   * @throws IllegalArgumentException
   *     if `onNext` is null
   * @throws rx.exceptions.OnErrorNotImplementedException
   *     if the Observable calls `onError`
   *
   * @see Observable.subscribe
   * @since 0.4.0
   */
  @JvmOverloads
  fun execute(onNext: Action1<in ResponseModel>, requestModel: RequestModel? = null) {
    requireNotNull(onNext)
    @Suppress("INACCESSIBLE_TYPE")
    execute(ActionSubscriber(onNext, InternalObservableUtils.ERROR_NOT_IMPLEMENTED,
        Actions.empty<Any, Any, Any, Any, Any, Any, Any, Any, Any>()), requestModel)
  }

  /**
   * Subscribes to an Observable and provides callbacks to handle the items it emits and any error notification it issues.
   *
   * @param onNext
   *     the `Action1<ResponseModel>` you have designed to accept emissions from the Observable
   * @param onError
   *     the `Action1<Throwable>` you have designed to accept any error notification from the Observable
   * @param requestModel
   *     parameter which will be passed to [createObservable].
   *
   * @throws IllegalArgumentException
   *     if `onNext` is null, or if `onError` is null
   *
   * @see Observable.subscribe
   * @since 0.4.0
   */
  @JvmOverloads
  fun execute(onNext: Action1<in ResponseModel>, onError: Action1<Throwable>, requestModel: RequestModel? = null) {
    requireNotNull(onNext)
    requireNotNull(onError)
    @Suppress("INACCESSIBLE_TYPE")
    execute(ActionSubscriber(onNext, onError, Actions.empty<Any, Any, Any, Any, Any, Any, Any, Any, Any>()), requestModel)
  }

  /**
   * Subscribes to an Observable and provides callbacks to handle the items it emits and any error or completion notification it issues.
   *
   * @param onNext
   *     the `Action1<ResponseModel>` you have designed to accept emissions from the Observable
   * @param onError
   *     the `Action1<Throwable>` you have designed to accept any error notification from the Observable
   * @param onCompleted
   *     the `Action0` you have designed to accept a completion notification from the Observable
   * @param requestModel
   *     parameter which will be passed to [createObservable].
   *
   * @throws IllegalArgumentException
   *     if `onNext` is null, or if `onError` is null, or if `onComplete` is null
   *
   * @see Observable.subscribe
   * @since 0.4.0
   */
  @JvmOverloads
  fun execute(onNext: Action1<in ResponseModel>, onError: Action1<Throwable>, onCompleted: Action0, requestModel: RequestModel? = null) {
    requireNotNull(onNext)
    requireNotNull(onError)
    requireNotNull(onCompleted)
    execute(ActionSubscriber(onNext, onError, onCompleted), requestModel)
  }

  /**
   * Stops the receipt of notifications on the [Subscriber]s that were registered.
   *
   *
   * This allows unregistering executed [Subscriber]s before they have finished receiving all events (i.e. before onCompleted is called).
   * @since 0.1.0
   */
  final override fun unsubscribe() = subscriptions.clear()

  /**
   * Indicates whether this `Interactor` is currently unsubscribed.
   *
   * @return `true` if this `Interactor` is currently unsubscribed, `false` otherwise
   *
   * @since 0.4.0
   */
  final override fun isUnsubscribed(): Boolean = !subscriptions.hasSubscriptions()

  /**
   * Provides source Observable that will execute the specified parameter when `execute()` method is called.
   *
   * It will use schedulers provided in [Interactor].
   *
   * @param requestModel
   *     optional parameter
   *
   * @return source Observable
   *
   * @see execute
   * @since 0.1.0
   */
  protected abstract fun createObservable(requestModel: RequestModel?): Observable<ResponseModel>
}
