package viper;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
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

  public void unsubscribe() {
    if (mSubscription.isUnsubscribed()) {
      return;
    }

    mSubscription.unsubscribe();
  }

  protected abstract Observable<Result> createObservable(Param param);
}
