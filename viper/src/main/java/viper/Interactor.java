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
  private final Scheduler mJobScheduler;
  private final Scheduler mPostExecutionScheduler;
  private Subscription mSubscription = Subscriptions.empty();

  protected Interactor(Scheduler jobScheduler, Scheduler postExecutionScheduler) {
    mJobScheduler = jobScheduler;
    mPostExecutionScheduler = postExecutionScheduler;
  }

  protected abstract Observable<Result> createObservable(Param param);

  public final void execute(Subscriber<? super Result> subscriber, Param param) {
    mSubscription = createObservable(param).subscribeOn(mJobScheduler)
        .observeOn(mPostExecutionScheduler)
        .subscribe(subscriber);
  }

  public void unsubscribe() {
    if (mSubscription.isUnsubscribed()) {
      return;
    }

    mSubscription.unsubscribe();
  }
}
