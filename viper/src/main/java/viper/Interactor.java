package viper;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.OnErrorNotImplementedException;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmitriy Zaitsev
 * @since 2016-Feb-13, 22:40
 */
public abstract class Interactor<Param, Result> {
  private final Scheduler mSubscribeScheduler;
  private final Scheduler mObserveScheduler;
  private Subscription mSubscription = Subscriptions.empty();

  protected Interactor(Scheduler subscribeOn, Scheduler observeOn) {
    mSubscribeScheduler = subscribeOn;
    mObserveScheduler = observeOn;
  }

  public void execute(Subscriber<? super Result> subscriber, Param param) {
    mSubscription = createObservable(param).subscribeOn(mSubscribeScheduler)
        .observeOn(mObserveScheduler)
        .subscribe(subscriber);
  }

  public void execute(Subscriber<? super Result> subscriber) {
    execute(subscriber, null);
  }

  public void execute(final Action1<? super Result> onNext) {
    execute(onNext, (Param) null);
  }

  public void execute(final Action1<? super Result> onNext, Param param) {
    checkNotNull(onNext, "onNext");

    execute(new Subscriber<Result>() {
      @Override public void onCompleted() {
      }

      @Override public void onError(final Throwable e) {
        throw new OnErrorNotImplementedException(e);
      }

      @Override public void onNext(final Result result) {
        onNext.call(result);
      }
    });
  }

  public void execute(final Action1<? super Result> onNext, final Action1<Throwable> onError) {
    execute(onNext, onError, (Param) null);
  }

  public void execute(final Action1<? super Result> onNext, final Action1<Throwable> onError, Param param) {
    checkNotNull(onNext, "onNext");
    checkNotNull(onError, "onError");

    execute(new Subscriber<Result>() {
      @Override public void onCompleted() {
      }

      @Override public void onError(final Throwable e) {
        onError.call(e);
      }

      @Override public void onNext(final Result result) {
        onNext.call(result);
      }
    });
  }

  public void execute(final Action1<? super Result> onNext, final Action1<Throwable> onError,
      final Action0 onComplete) {
    execute(onNext, onError, onComplete, null);
  }

  public void execute(final Action1<? super Result> onNext, final Action1<Throwable> onError, final Action0 onComplete,
      Param param) {
    checkNotNull(onNext, "onNext");
    checkNotNull(onError, "onError");
    checkNotNull(onComplete, "onComplete");

    execute(new Subscriber<Result>() {
      @Override public void onCompleted() {
        onComplete.call();
      }

      @Override public void onError(final Throwable e) {
        onError.call(e);
      }

      @Override public void onNext(final Result result) {
        onNext.call(result);
      }
    });
  }

  public void unsubscribe() {
    if (mSubscription.isUnsubscribed()) {
      return;
    }

    mSubscription.unsubscribe();
  }

  protected abstract Observable<Result> createObservable(Param param);

  private static <T> void checkNotNull(T arg, String name) {
    if (arg == null) {
      throw new IllegalArgumentException(name + " can not be null");
    }
  }
}
