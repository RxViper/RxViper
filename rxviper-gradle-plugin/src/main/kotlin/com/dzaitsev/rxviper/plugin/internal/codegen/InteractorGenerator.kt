/*
 * Copyright 2016 Dmytro Zaitsev
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

import com.dzaitsev.rxviper.Interactor
import com.dzaitsev.rxviper.plugin.internal.dsl.Screen
import com.dzaitsev.rxviper.plugin.aClass
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import rx.Observable
import rx.Scheduler
import javax.lang.model.element.Modifier

internal class InteractorGenerator(screen: Screen) : Generator(screen) {
  override val typeName = "Interactor"

  override fun createSpec(): List<TypeSpec.Builder> = if (screen.useCases.isEmpty()) {
    listOf(create(typeSpecName, aClass(), aClass()))
  } else {
    screen.useCases.map { create("${it.name}Interactor", it.requestClass, it.responseClass) }
  }

  private fun create(name: String, requestClass: Class<Any>, responseClass: Class<Any>): TypeSpec.Builder {
    val subscribeScheduler = "subscribeScheduler"
    val observeScheduler = "observeScheduler"
    return TypeSpec.classBuilder(name)
        .superclass(ParameterizedTypeName.get(aClass<Interactor<*, *>>(), requestClass, responseClass))
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        .addMethod(MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(aClass<Scheduler>(), subscribeScheduler)
            .addParameter(aClass<Scheduler>(), observeScheduler)
            .addStatement("super(\$N, \$N)", subscribeScheduler, observeScheduler)
            .build())
        .addMethod(MethodSpec.methodBuilder("createObservable")
            .addModifiers(Modifier.PROTECTED)
            .addAnnotation(aClass<Override>())
            .addParameter(requestClass, "requestModel")
            .addComment("TODO: Write your business logic here...")
            .addStatement("return \$T.empty()", aClass<Observable<*>>())
            .returns(ParameterizedTypeName.get(aClass<Observable<*>>(), responseClass))
            .build())
  }
}