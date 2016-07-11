@file:JvmName("InteractorExtensions")

package com.dzaitsev.rxviper

import rx.functions.Action0
import rx.functions.Action1
import rx.functions.Actions
import rx.internal.util.ActionSubscriber
import rx.internal.util.InternalObservableUtils

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 * @author Dmytro Zaitsev
 * @since 2017-Jan-17, 12:12
 */

inline fun <RequestModel, ResponseModel> Interactor<RequestModel, ResponseModel>.execute(
    crossinline onNext: (ResponseModel) -> Unit,
    requestModel: RequestModel? = null) {
  @Suppress("INACCESSIBLE_TYPE")
  execute(ActionSubscriber(
      Action1 { onNext.invoke(it) },
      InternalObservableUtils.ERROR_NOT_IMPLEMENTED,
      Actions.empty<Any, Any, Any, Any, Any, Any, Any, Any, Any>()
  ), requestModel)
}

inline fun <Request, Response> Interactor<Request, Response>.execute(
    crossinline onNext: (Response) -> Unit,
    crossinline onError: (Throwable) -> Unit,
    requestModel: Request? = null) {
  @Suppress("INACCESSIBLE_TYPE")
  execute(ActionSubscriber(
      Action1 { onNext.invoke(it) },
      Action1 { onError.invoke(it) },
      Actions.empty<Any, Any, Any, Any, Any, Any, Any, Any, Any>()
  ), requestModel)
}

inline fun <Request, Response> Interactor<Request, Response>.execute(
    crossinline onNext: (Response) -> Unit,
    crossinline onError: (Throwable) -> Unit,
    crossinline onCompleted: () -> Unit,
    requestModel: Request? = null) {
  execute(ActionSubscriber(
      Action1 { onNext.invoke(it) },
      Action1 { onError.invoke(it) },
      Action0 { onCompleted.invoke() }
  ), requestModel)
}