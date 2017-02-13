package com.dzaitsev.rxviper.plugin.generator

import com.dzaitsev.rxviper.Router
import com.dzaitsev.rxviper.plugin.Screen
import com.dzaitsev.rxviper.plugin.clazz
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import rx.Observable
import rx.Observable.just
import javax.lang.model.element.Modifier

internal class RouterGenerator(screen: Screen) : Generator(screen) {
  override val typeName = "Router"

  override fun createSpec(): Observable<TypeSpec> {
    val routerBuilder = TypeSpec.interfaceBuilder(typeSpecName)
        .addSuperinterface(clazz<Router>())
        .addModifiers(Modifier.PUBLIC)
    screen.routesTo.forEach {
      routerBuilder.addMethod(MethodSpec.methodBuilder("navigateTo${it.capitalize()}")
          .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
          .build())
    }
    return just(routerBuilder.build())
  }
}