package com.dzaitsev.rxviper.plugin.internal.codegen

import com.dzaitsev.rxviper.ViewCallbacks
import com.dzaitsev.rxviper.plugin.internal.dsl.Screen
import com.dzaitsev.rxviper.plugin.aClass
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

internal class ViewCallbacksGenerator(screen: Screen) : Generator(screen) {
  override val typeName = "ViewCallbacks"

  override fun createSpec(): List<TypeSpec.Builder> = listOf(TypeSpec.interfaceBuilder(typeSpecName)
      .addSuperinterface(aClass<ViewCallbacks>())
      .addModifiers(Modifier.PUBLIC))
}