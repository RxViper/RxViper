package viper;

import rx.Subscriber;
import rx.exceptions.OnErrorNotImplementedException;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmitriy Zaitsev
 * @since 2016-May-14, 15:26
 */
final class Utils {
  static <T> void checkNotNull(T arg, String name) {
    if (arg == null) {
      throw new IllegalArgumentException(name + " can not be null");
    }
  }

  static class OnNextSubscriber<Result> extends Subscriber<Result> {
    private final Action1<? super Result> mOnNext;

    OnNextSubscriber(Action1<? super Result> onNext) {
      mOnNext = onNext;
    }

    @Override public void onCompleted() {
    }

    @Override public void onError(Throwable e) {
      throw new OnErrorNotImplementedException(e);
    }

    @Override public void onNext(Result result) {
      mOnNext.call(result);
    }
  }

  static class OnNextOnErrorSubscriber<Result> extends Subscriber<Result> {
    private final Action1<? super Result> mOnNext;
    private final Action1<Throwable>      mOnError;

    OnNextOnErrorSubscriber(Action1<? super Result> onNext, Action1<Throwable> onError) {
      mOnNext = onNext;
      mOnError = onError;
    }

    @Override public void onCompleted() {
    }

    @Override public void onError(Throwable e) {
      mOnError.call(e);
    }

    @Override public void onNext(Result result) {
      mOnNext.call(result);
    }
  }

  static class OnNextOnErrorOnCompleteSubscriber<Result> extends Subscriber<Result> {
    private final Action1<? super Result> mOnNext;
    private final Action1<Throwable>      mOnError;
    private final Action0                 mOnComplete;

    OnNextOnErrorOnCompleteSubscriber(Action1<? super Result> onNext, Action1<Throwable> onError, Action0 onComplete) {
      mOnNext = onNext;
      mOnError = onError;
      mOnComplete = onComplete;
    }

    @Override public void onCompleted() {
      mOnComplete.call();
    }

    @Override public void onError(Throwable e) {
      mOnError.call(e);
    }

    @Override public void onNext(Result result) {
      mOnNext.call(result);
    }
  }
}
