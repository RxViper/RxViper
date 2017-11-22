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

package com.dzaitsev.viper;

import com.dzaitsev.viper.callbacks.OnFailure;
import com.dzaitsev.viper.callbacks.OnSuccess;
import javax.annotation.Nullable;

import static com.dzaitsev.viper.Intrinsics.requireNotNull;

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
public abstract class Interactor<RequestModel, ResponseModel> {
  protected static final OnFailure ON_FAILURE_NOT_IMPLEMENTED = new OnFailure() {
    @Override
    public void onFailure(Throwable throwable) {
      throw new UnsupportedOperationException(throwable);
    }
  };

  /**
   * Subscribes to an Observable and provides a callback to handle the items it emits.
   *
   * @param onSuccess
   *     the {@code OnSuccess<ResponseModel>} you have designed to accept emissions from the Observable
   *
   * @throws IllegalArgumentException
   *     if {@code onSuccess} is null
   * @throws UnsupportedOperationException
   *     if the Observable calls {@code onFailure}
   * @see #execute(OnSuccess, Object)
   * @since 0.4.0
   */
  public final long execute(OnSuccess<? super ResponseModel> onSuccess) {
    return execute(onSuccess, ON_FAILURE_NOT_IMPLEMENTED, null);
  }

  /**
   * Subscribes to an Observable and provides a callback to handle the items it emits.
   *
   * @param onSuccess
   *     the {@code OnSuccess<ResponseModel>} you have designed to accept emissions from the Observable
   *
   * @throws IllegalArgumentException
   *     if {@code onSuccess} is null
   * @throws UnsupportedOperationException
   *     if the Observable calls {@code onFailure}
   * @since 0.4.0
   */
  public final long execute(OnSuccess<? super ResponseModel> onSuccess, @Nullable RequestModel requestModel) {
    return execute(onSuccess, ON_FAILURE_NOT_IMPLEMENTED, requestModel);
  }

  /**
   * Subscribes to an Observable and provides callbacks to handle the items it emits and any error notification it issues.
   *
   * @param onSuccess
   *     the {@code OnSuccess<ResponseModel>} you have designed to accept emissions from the Observable
   * @param onFailure
   *     the {@code OnFailure} you have designed to accept any error notification from the Observable
   *
   * @throws IllegalArgumentException
   *     if {@code onSuccess} is null, or if {@code onFailure} is null
   * @see #execute(OnSuccess, Object)
   * @since 0.4.0
   */
  public final long execute(OnSuccess<? super ResponseModel> onSuccess, OnFailure onFailure) {
    return execute(onSuccess, onFailure, null);
  }

  /**
   * Subscribes to an Observable and provides callbacks to handle the items it emits and any error notification it issues.
   *
   * @param onSuccess
   *     the {@code OnSuccess<ResponseModel>} you have designed to accept emissions from the Observable
   * @param onFailure
   *     the {@code OnFailure} you have designed to accept any error notification from the Observable
   *
   * @throws IllegalArgumentException
   *     if {@code onSuccess} is null, or if {@code onFailure} is null
   * @since 0.4.0
   */
  public final long execute(OnSuccess<? super ResponseModel> onSuccess, OnFailure onFailure, @Nullable RequestModel requestModel) {
    requireNotNull(onSuccess, "onSuccess");
    requireNotNull(onFailure, "onFailure");

    return execInternal(onSuccess, onFailure, requestModel);
  }

  protected abstract long execInternal(OnSuccess<? super ResponseModel> onSuccess, OnFailure onFailure,
      @Nullable RequestModel requestModel);
}
