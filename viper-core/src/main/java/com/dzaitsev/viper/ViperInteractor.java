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
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.dzaitsev.viper.internal.Preconditions.requireNotNull;

/**
 * @author Dmytro Zaitsev
 * @since 2017-Jul-16, 18:50
 */
abstract class ViperInteractor<RequestModel, ResponseModel> extends Interactor<RequestModel, ResponseModel> {
  @Nonnull private final Executor jobExecutor;
  @Nonnull private final Executor resultExecutor;

  protected ViperInteractor(@Nonnull Executor jobExecutor, @Nonnull Executor resultExecutor) {
    requireNotNull(jobExecutor);
    requireNotNull(resultExecutor);
    this.jobExecutor = jobExecutor;
    this.resultExecutor = resultExecutor;
  }

  @Override
  protected final void execInternal(@Nonnull final OnSuccess<? super ResponseModel> onSuccess, @Nonnull final OnFailure onFailure,
      @Nullable final RequestModel requestModel) {
    jobExecutor.execute(new Runnable() {
      @Override
      public void run() {
        final Result<ResponseModel> result = getResult();

        resultExecutor.execute(new Runnable() {
          @Override
          public void run() {
            if (result.throwable == null) {
              onSuccess.onSuccess(result.value);
            } else {
              onFailure.onFailure(result.throwable);
            }
          }
        });
      }

      private Result<ResponseModel> getResult() {
        try {
          return Result.success(getData(requestModel));
        } catch (Throwable throwable) {
          return Result.error(throwable);
        }
      }
    });
  }

  @SuppressWarnings("WeakerAccess")
  @Nonnull
  protected abstract ResponseModel getData(@Nullable RequestModel requestModel);
}
