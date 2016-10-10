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

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Oct-10, 15:02
 */
public abstract class Interactor<RequestModel, ResponseModel> {
  private static final OnError     ON_ERROR     = new OnError() {
    @Override public void onError(Throwable t) {
    }
  };
  private static final OnCompleted ON_COMPLETED = new OnCompleted() {
    @Override public void onCompleted() {
    }
  };

  public final void execute(OnNext<? super ResponseModel> onNext) {
    execute(onNext, ON_ERROR, ON_COMPLETED, null);
  }

  public final void execute(OnNext<? super ResponseModel> onNext, RequestModel requestModel) {
    execute(onNext, ON_ERROR, ON_COMPLETED, requestModel);
  }

  public final void execute(OnNext<? super ResponseModel> onNext, OnError onError) {
    execute(onNext, onError, ON_COMPLETED, null);
  }

  public final void execute(OnNext<? super ResponseModel> onNext, OnError onError, RequestModel requestModel) {
    execute(onNext, onError, ON_COMPLETED, requestModel);
  }

  public final void execute(OnNext<? super ResponseModel> onNext, OnError onError, OnCompleted onCompleted) {
    execute(onNext, onError, onCompleted, null);
  }

  public final void execute(OnNext<? super ResponseModel> onNext, OnError onError, OnCompleted onCompleted, RequestModel requestModel) {
    Preconditions.requireNotNull(onNext);
    Preconditions.requireNotNull(onError);
    Preconditions.requireNotNull(onCompleted);

    doJob(onNext, onError, onCompleted, requestModel);
  }

  void doJob(OnNext<? super ResponseModel> onNext, OnError onError, OnCompleted onCompleted, RequestModel requestModel) {
    try {
      onNext.onNext(getData(requestModel));
      onCompleted.onCompleted();
    } catch (final Throwable throwable) {
      onError.onError(throwable);
    }
  }

  protected abstract ResponseModel getData(RequestModel requestModel);
}
