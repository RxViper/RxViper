package ${packageName};

import com.dzaitsev.rxviper.Interactor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
<#if useInject>import javax.inject.Inject;</#if>
import rx.Observable;
import rx.Scheduler;

public final class ${name}Interactor extends Interactor<Object, Object> {
  <#if useInject>@Inject</#if>
  public ${name}Interactor(@Nonnull Scheduler subscribeScheduler, @Nonnull Scheduler observeScheduler) {
    super(subscribeScheduler, observeScheduler);
  }

  @Override
  @Nonnull
  protected Observable<Object> createObservable(@Nullable Object requestModel) {
    // TODO: Write your business logic here...
    return Observable.empty();
  }
}
