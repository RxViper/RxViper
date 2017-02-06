package com.dzaitsev.rxviper.plugin.generator

import com.dzaitsev.rxviper.Interactor
import com.dzaitsev.rxviper.plugin.FeatureOptions
import com.dzaitsev.rxviper.plugin.clazz
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import rx.Observable
import rx.Observable.from
import rx.Observable.just
import rx.Scheduler
import javax.lang.model.element.Modifier

internal class InteractorGenerator(feature: FeatureOptions) : Generator(feature) {
  override val typeName = "Interactor"

  override fun createSpec(): Observable<TypeSpec> {
    val useCases = feature.useCases
    if (useCases.isEmpty()) {
      return just(from(typeSpecName))
    } else {
      val interactors = java.util.ArrayList<TypeSpec>()
      useCases.forEach { interactors.add(from("${it}Interactor")) }
      return from(interactors)
    }
  }

  private fun from(name: String) = TypeSpec.classBuilder(name)
      .superclass(ParameterizedTypeName.get(clazz<Interactor<*, *>>(), feature.requestModel, feature.responseModel))
      .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
      .addMethod(MethodSpec.constructorBuilder()
          .addModifiers(Modifier.PUBLIC)
          .addParameter(clazz<Scheduler>(), "subscribeScheduler")
          .addParameter(clazz<Scheduler>(), "observeScheduler")
          .addCode("super(subscribeScheduler, observeScheduler);\n")
          .build())
      .addMethod(MethodSpec.methodBuilder("createObservable")
          .addModifiers(Modifier.PROTECTED)
          .addAnnotation(clazz<Override>())
          .addParameter(feature.requestModel, "requestModel")
          .addComment("TODO: Write your business logic here...")
          .addStatement("return \$T.empty()", clazz<Observable<*>>())
          .returns(ParameterizedTypeName.get(clazz<Observable<*>>(), feature.responseModel))
          .build())
      .build()
}