/*
 * Copyright 2017 Dmytro Zaitsev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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