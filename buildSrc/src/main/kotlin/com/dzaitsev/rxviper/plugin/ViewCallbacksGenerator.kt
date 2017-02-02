package com.dzaitsev.rxviper.plugin

import com.dzaitsev.rxviper.ViewCallbacks
import com.squareup.javapoet.TypeSpec
import rx.Observable
import rx.Observable.just
import javax.lang.model.element.Modifier

internal class ViewCallbacksGenerator(feature: FeatureOptions) : Generator(feature) {
  override val typeName = "ViewCallbacks"

  override fun createSpec(): Observable<TypeSpec> = just(TypeSpec.interfaceBuilder(typeSpecName)
      .addSuperinterface(clazz<ViewCallbacks>())
      .addModifiers(Modifier.PUBLIC)
      .build())
}