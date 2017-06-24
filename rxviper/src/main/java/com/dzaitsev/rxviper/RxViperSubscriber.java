package com.dzaitsev.rxviper;

import javax.annotation.Nonnull;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.internal.util.ActionSubscriber;
import rx.observers.SafeSubscriber;

final class RxViperSubscriber<I> extends SafeSubscriber<I> {
  static <I> RxViperSubscriber<I> of(@Nonnull final OnNext<? super I> onNext, @Nonnull final OnError onError,
      @Nonnull final OnComplete onComplete) {
    return new RxViperSubscriber<>(new ActionSubscriber<>(new Action1<I>() {
      @Override
      public void call(I item) {
        onNext.onNext(item);
      }
    }, new Action1<Throwable>() {
      @Override
      public void call(Throwable throwable) {
        onError.onError(throwable);
      }
    }, new Action0() {
      @Override
      public void call() {
        onComplete.onComplete();
      }
    }));
  }

  static <I> RxViperSubscriber<I> of(@Nonnull Subscriber<? super I> subscriber) {
    return new RxViperSubscriber<>(subscriber);
  }

  private boolean isDone;

  private RxViperSubscriber(@Nonnull Subscriber<? super I> subscriber) {
    super(subscriber);
  }

  @Override
  public void onCompleted() {
    super.onCompleted();
    isDone = true;
  }

  @Override
  public void onError(Throwable t) {
    super.onError(t);
    isDone = true;
  }

  boolean isDone() {
    return isDone;
  }
}
