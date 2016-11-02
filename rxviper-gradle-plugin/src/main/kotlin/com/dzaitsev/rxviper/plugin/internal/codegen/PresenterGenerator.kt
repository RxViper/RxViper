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

import com.dzaitsev.rxviper.Presenter
import com.dzaitsev.rxviper.ViperPresenter
import com.dzaitsev.rxviper.plugin.aClass
import com.dzaitsev.rxviper.plugin.internal.dsl.Screen
import com.dzaitsev.rxviper.plugin.internal.dsl.UseCase
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import io.reactivex.functions.Consumer
import javax.lang.model.element.Modifier

internal class PresenterGenerator(screen: Screen) : Generator(screen) {
  override val typeName = "Presenter"

  override fun createSpec(): List<TypeSpec.Builder> {
    val constructorBuilder = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
    val onDropViewMethodBuilder = MethodSpec.methodBuilder("onDropView")
        .addModifiers(Modifier.PROTECTED)
        .addParameter(ClassName.get(screen.fullPackage, "${screenName}ViewCallbacks"), "view")
        .addAnnotation(aClass<Override>())
    val presenterBuilder = TypeSpec.classBuilder(typeSpecName)
        .superclass(superClass())
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
    val useCases = if (screen.useCases.isEmpty()) listOf(UseCase(screenName)) else screen.useCases.map { it }

    if (useCases.isEmpty()) {
      onDropViewMethodBuilder.addComment("TODO: Release your resources here...")
    }

    useCases.forEach { useCase ->
      val methodBuilder = MethodSpec.methodBuilder("do${useCase.name}")
          .addModifiers(Modifier.PUBLIC)
          .addParameter(useCase.requestClass, "requestModel")

      if (screen.includeInteractor) {
        val className = "${useCase.name}Interactor"
        val argName = className.decapitalize()

        constructorBuilder
            .addParameter(ClassName.get(screen.fullPackage, className), argName)
            .addStatement("this.\$1N = \$1N", argName)

        onDropViewMethodBuilder.addStatement("\$N.dispose()", argName)

        presenterBuilder.addField(ClassName.get(screen.fullPackage, className), argName, Modifier.PRIVATE, Modifier.FINAL)
        presenterBuilder.addMethod(when {
          screen.useLambdas -> methodBuilder.addStatement("\$N.execute(\$N -> {\n" +
              "  // TODO: Implement onNext here...\n" +
              "}, t -> {\n" +
              "  // TODO: Implement onError here...\n" +
              "}, \$N)", argName, useCase.responseClass.simpleName.first().toLowerCase().toString(), "requestModel").build()
          else -> methodBuilder.addStatement("\$N.execute(\$L, \$L, \$N)",
              argName,
              consumerAnonymous(useCase.responseClass, useCase.responseClass.simpleName.first().toLowerCase().toString(), "TODO: Implement onNext here..."),
              consumerAnonymous(aClass<Throwable>(), "t", "TODO: Implement onError here..."),
              "requestModel")
              .build()
        })
      } else {
        presenterBuilder.addMethod(methodBuilder.addComment("TODO: Implement your business logic here...")
            .build())
      }
    }

    return listOf(presenterBuilder.addMethod(constructorBuilder.build())
        .addMethod(onDropViewMethodBuilder.build()))
  }

  private fun consumerAnonymous(clazz: Class<*>, paramName: String, comment: String): TypeSpec {
    return TypeSpec.anonymousClassBuilder("")
        .addSuperinterface(ParameterizedTypeName.get(aClass<Consumer<*>>(), clazz))
        .addMethod(MethodSpec.methodBuilder("accept")
            .addAnnotation(aClass<Override>())
            .addModifiers(Modifier.PUBLIC)
            .addParameter(clazz, paramName)
            .addComment(comment)
            .build())
        .build()
  }

  private fun superClass(): TypeName {
    val viewCallbacks = ClassName.get(screen.fullPackage, "${screenName}ViewCallbacks")

    return when {
      screen.includeRouter -> ParameterizedTypeName.get(
          ClassName.get(aClass<ViperPresenter<*, *>>()), viewCallbacks, ClassName.get(screen.fullPackage, "${screenName}Router"))
      else -> ParameterizedTypeName.get(ClassName.get(aClass<Presenter<*>>()), viewCallbacks)
    }
  }
}