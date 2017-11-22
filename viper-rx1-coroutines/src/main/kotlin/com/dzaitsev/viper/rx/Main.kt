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

package com.dzaitsev.viper.rx

import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import rx.Subscriber

suspend fun <RequestModel, ResponseModel> RxInteractor<RequestModel, ResponseModel>.await(model: RequestModel? = null): ResponseModel
    = suspendCancellableCoroutine { cont ->
  cont.invokeOnCompletion {
    unsubscribe(execute(object : Subscriber<ResponseModel>() {
      override fun onStart() {
        request(1)
      }

      override fun onNext(t: ResponseModel) {
        cont.resume(t)
      }

      override fun onCompleted() {
        if (cont.isActive) cont.resumeWithException(IllegalStateException("Should have invoked onNext"))
      }

      override fun onError(e: Throwable) {
        cont.resumeWithException(e)
      }
    }, model))
  }
}
