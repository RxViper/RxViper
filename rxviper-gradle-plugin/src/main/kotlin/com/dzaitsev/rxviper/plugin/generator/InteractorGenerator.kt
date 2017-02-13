package com.dzaitsev.rxviper.plugin.generator

import com.dzaitsev.rxviper.Interactor
import com.dzaitsev.rxviper.plugin.Screen
import com.dzaitsev.rxviper.plugin.clazz
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import rx.Observable
import rx.Observable.from
import rx.Observable.just
import rx.Scheduler
import javax.lang.model.element.Modifier

internal class InteractorGenerator(screen: Screen) : Generator(screen) {
  override val typeName = "Interactor"

  override fun createSpec(): Observable<TypeSpec> = if (screen.useCases.isEmpty()) {
    just(create(typeSpecName, clazz(), clazz()))
  } else {
    from(screen.useCases.map { create("${it.name}Interactor", it.requestModel, it.responseModel) })
  }

  private fun create(name: String, requestModel: Class<Any>, responseModel: Class<Any>) = TypeSpec.classBuilder(name)
      .superclass(ParameterizedTypeName.get(clazz<Interactor<*, *>>(), requestModel, responseModel))
      .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
      .addMethod(MethodSpec.constructorBuilder()
          .addModifiers(Modifier.PUBLIC)
          .addParameter(clazz<Scheduler>(), "subscribeScheduler")
          .addParameter(clazz<Scheduler>(), "observeScheduler")
          .addStatement("super(subscribeScheduler, observeScheduler)")
          .build())
      .addMethod(MethodSpec.methodBuilder("createObservable")
          .addModifiers(Modifier.PROTECTED)
          .addAnnotation(clazz<Override>())
          .addParameter(requestModel, "requestModel")
          .addComment("TODO: Write your business logic here...")
          .addStatement("return \$T.empty()", clazz<Observable<*>>())
          .returns(ParameterizedTypeName.get(clazz<Observable<*>>(), responseModel))
          .build())
      .build()
}