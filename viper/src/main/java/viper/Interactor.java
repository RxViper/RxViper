package viper;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;

import static viper.Utils.OnNextOnErrorOnCompleteSubscriber;
import static viper.Utils.OnNextOnErrorSubscriber;
import static viper.Utils.OnNextSubscriber;
import static viper.Utils.checkNotNull;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmitriy Zaitsev
 * @since 2016-Feb-13, 22:40
 */
public abstract class Interactor<Param, Result> implements Subscription {
  private final Scheduler mSubscribeScheduler;
  private final Scheduler mObserveScheduler;
  private Subscription mSubscription = Subscriptions.empty();

  protected Interactor(Scheduler subscribeOn, Scheduler observeOn) {
    checkNotNull(subscribeOn, "subscribeOn");
    checkNotNull(observeOn, "observeOn");

    mSubscribeScheduler = subscribeOn;
    mObserveScheduler = observeOn;
  }

  public final void execute(Subscriber<? super Result> subscriber, Param param) {
    checkNotNull(subscriber, "subscriber");

    mSubscription = createObservable(param).subscribeOn(mSubscribeScheduler)
        .observeOn(mObserveScheduler)
        .subscribe(subscriber);
  }

  public final void execute(Subscriber<? super Result> subscriber) {
    execute(subscriber, null);
  }

  public final void execute(Action1<? super Result> onNext) {
    execute(onNext, (Param) null);
  }

  public final void execute(Action1<? super Result> onNext, Param param) {
    checkNotNull(onNext, "onNext");

    execute(new OnNextSubscriber<>(onNext), param);
  }

  public final void execute(Action1<? super Result> onNext, Action1<Throwable> onError) {
    execute(onNext, onError, (Param) null);
  }

  public final void execute(Action1<? super Result> onNext, Action1<Throwable> onError, Param param) {
    checkNotNull(onNext, "onNext");
    checkNotNull(onError, "onError");

    execute(new OnNextOnErrorSubscriber<>(onNext, onError), param);
  }

  public final void execute(Action1<? super Result> onNext, Action1<Throwable> onError, Action0 onComplete) {
    execute(onNext, onError, onComplete, null);
  }

  public final void execute(Action1<? super Result> onNext, Action1<Throwable> onError, Action0 onComplete,
      Param param) {
    checkNotNull(onNext, "onNext");
    checkNotNull(onError, "onError");
    checkNotNull(onComplete, "onComplete");

    execute(new OnNextOnErrorOnCompleteSubscriber<>(onNext, onError, onComplete), param);
  }

  @Override public final void unsubscribe() {
    if (mSubscription.isUnsubscribed()) {
      return;
    }

    mSubscription.unsubscribe();
  }

  @Override public final boolean isUnsubscribed() {
    return mSubscription.isUnsubscribed();
  }

  protected abstract Observable<Result> createObservable(Param param);
}
