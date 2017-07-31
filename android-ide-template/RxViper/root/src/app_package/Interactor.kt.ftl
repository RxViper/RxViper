package ${packageName}

import com.dzaitsev.rxviper.Interactor
<#if useInject>import javax.inject.Inject</#if>
import rx.Observable
import rx.Scheduler

class ${name}Interactor<#if useInject>@Inject constructor</#if>(subscribeScheduler: Scheduler, observeScheduler: Scheduler) : Interactor<Any, Any>(subscribeScheduler, observeScheduler) {

  override fun createObservable(requestModel: Any?): Observable<Any> {
    // TODO: Write your business logic here...
    return Observable.empty()
  }
}
