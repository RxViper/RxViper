package ${packageName};

import com.dzaitsev.rxviper.<#if generateRouter>Viper</#if>Presenter;

<#if useInject>import javax.inject.Inject;</#if>
import javax.annotation.Nonnull;
<#if !enableLambdas>import rx.functions.Action1;</#if>

<#if generateRouter>
public final class ${name}Presenter extends ViperPresenter<${name}ViewCallbacks, ${name}Router> {
<#else>
public final class ${name}Presenter extends Presenter<${name}ViewCallbacks> {
</#if>
  private final ${name}Interactor ${name?uncap_first}Interactor;

  <#if useInject>@Inject</#if>
  public ${name}Presenter(${name}Interactor ${name?uncap_first}Interactor) {
    this.${name?uncap_first}Interactor = ${name?uncap_first}Interactor;
  }

  public void do${name}(Object requestModel) {
    <#if enableLambdas>
    ${name?uncap_first}Interactor.execute(o -> {
      // TODO: Implement onNext here...
    }, t -> {
      // TODO: Implement onError here...
    }, requestModel);
    <#else>
    ${name?uncap_first}Interactor.execute(new Action1<Object>() {
      @Override
      public void call(Object o) {
        // TODO: Implement onNext here...
      }
    }, new Action1<Throwable>() {
      @Override
      public void call(Throwable t) {
        // TODO: Implement onError here...
      }
    }, requestModel);
    </#if>
  }

  @Override
  protected void onDropView(@Nonnull ${name}ViewCallbacks view) {
    ${name?uncap_first}Interactor.unsubscribe();
  }

  <#if generateRouter>
  @Override
  protected void onDropRouter(@Nonnull ${name}Router router) {
  }
  </#if>
}
