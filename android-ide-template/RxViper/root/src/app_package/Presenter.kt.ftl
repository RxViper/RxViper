package ${packageName}

import com.dzaitsev.rxviper.<#if generateRouter>Viper</#if>Presenter
<#if useInject>

import javax.inject.Inject
</#if>
<#if generateRouter>
class ${name}Presenter<#if useInject>@Inject constructor</#if>(private val ${name?uncap_first}Interactor: ${name}Interactor) : ViperPresenter<${name}ViewCallbacks, ${name}Router>() {
<#else>
class ${name}Presenter<#if useInject>@Inject constructor</#if>(private val ${name?uncap_first}Interactor: ${name}Interactor) : Presenter<${name}ViewCallbacks>() {
</#if>

  fun do${name}(requestModel: Any) {
    ${name?uncap_first}Interactor.execute({ o ->
      // TODO: Implement onNext here...
    }, { t ->
      // TODO: Implement onError here...
    }, requestModel)
  }

  override fun onDropView(view: ${name}ViewCallbacks) {
    ${name?uncap_first}Interactor.unsubscribe()
  }

  <#if generateRouter>
  override fun onDropRouter(router: ${name}Router) {
  }
  </#if>
}
