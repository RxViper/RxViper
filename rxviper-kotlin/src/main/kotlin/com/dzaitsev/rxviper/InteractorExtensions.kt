@file:JvmName("InteractorExtensions")

package com.dzaitsev.rxviper

import com.dzaitsev.rxviper.Interactor.ON_COMPLETED
import rx.functions.Action0
import rx.functions.Action1
import rx.internal.util.ActionSubscriber
import rx.internal.util.InternalObservableUtils.ERROR_NOT_IMPLEMENTED

/**
 * Subscribes to an Observable and provides a callback to handle the items it emits.
 *
 * @param onNext
 *     the `Action1<Result>` you have designed to accept emissions from the Observable
 * @param requestModel
 *     parameter which will be passed to [createObservable][Interactor.createObservable].
 *
 * @throws IllegalArgumentException
 *     if `onNext` is null
 * @throws rx.exceptions.OnErrorNotImplementedException
 *     if the Observable calls `onError`
 *
 * @see rx.Observable.subscribe
 * @since 0.4.0
 */
fun <RequestModel, ResponseModel> Interactor<RequestModel, ResponseModel>.execute(
    onNext: (ResponseModel) -> Unit,
    requestModel: RequestModel? = null
) = execute(ActionSubscriber(Action1(onNext), ERROR_NOT_IMPLEMENTED, ON_COMPLETED), requestModel)

/**
 * Subscribes to an Observable and provides callbacks to handle the items it emits and any error notification it issues.
 *
 * @param onNext
 *     the `Action1<ResponseModel>` you have designed to accept emissions from the Observable
 * @param onError
 *     the `Action1<Throwable>` you have designed to accept any error notification from the Observable
 * @param requestModel
 *     parameter which will be passed to [createObservable][Interactor.createObservable].
 *
 * @throws IllegalArgumentException
 *     if `onNext` is null, or if `onError` is null
 *
 * @see rx.Observable.subscribe
 * @since 0.4.0
 */
fun <RequestModel, ResponseModel> Interactor<RequestModel, ResponseModel>.execute(
    onNext: (ResponseModel) -> Unit,
    onError: (Throwable) -> Unit,
    requestModel: RequestModel? = null
) = execute(ActionSubscriber(Action1(onNext), Action1(onError), ON_COMPLETED), requestModel)

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
 *     parameter which will be passed to [createObservable][Interactor.createObservable].
 *
 * @throws IllegalArgumentException
 *     if `onNext` is null, or if `onError` is null, or if `onComplete` is null
 *
 * @see rx.Observable.subscribe
 * @since 0.4.0
 */
fun <RequestModel, ResponseModel> Interactor<RequestModel, ResponseModel>.execute(
    onNext: (ResponseModel) -> Unit,
    onError: (Throwable) -> Unit,
    onCompleted: () -> Unit,
    requestModel: RequestModel? = null
) = execute(ActionSubscriber(Action1(onNext), Action1(onError), Action0(onCompleted)), requestModel)