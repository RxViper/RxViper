package com.dzaitsev.rxviper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Dmytro Zaitsev
 * @since 2017-Jul-16, 18:46
 */
interface RxViperInteractor<RequestModel, ResponseModel> {
  void execute(@Nonnull OnNext<? super ResponseModel> onNext);

  void execute(@Nonnull OnNext<? super ResponseModel> onNext, @Nullable RequestModel requestModel);

  void execute(@Nonnull OnNext<? super ResponseModel> onNext, @Nonnull OnError onError);

  void execute(@Nonnull OnNext<? super ResponseModel> onNext, @Nonnull OnError onError, @Nullable RequestModel requestModel);

  void execute(@Nonnull OnNext<? super ResponseModel> onNext, @Nonnull OnError onError, @Nonnull OnComplete onComplete);

  void execute(@Nonnull OnNext<? super ResponseModel> onNext, @Nonnull OnError onError, @Nonnull OnComplete onComplete,
      @Nullable RequestModel requestModel);
}
