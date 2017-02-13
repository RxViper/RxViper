package com.dzaitsev.rxviper.plugin.internal.codegen

import com.dzaitsev.rxviper.Router
import com.dzaitsev.rxviper.plugin.internal.dsl.Screen
import com.dzaitsev.rxviper.plugin.aClass
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

internal class RouterGenerator(screen: Screen) : Generator(screen) {
  override val typeName = "Router"

  override fun createSpec(): List<TypeSpec.Builder> {
    val routerBuilder = TypeSpec.interfaceBuilder(typeSpecName)
        .addSuperinterface(aClass<Router>())
        .addModifiers(Modifier.PUBLIC)
    screen.routesTo.forEach {
      routerBuilder.addMethod(MethodSpec.methodBuilder("navigateTo${it.capitalize()}")
          .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
          .build())
    }
    return listOf(routerBuilder)
  }
}