package com.dzaitsev.rxviper.plugin.internal.codegen

import com.dzaitsev.rxviper.Presenter
import com.dzaitsev.rxviper.ViperPresenter
import com.dzaitsev.rxviper.plugin.Screen
import com.dzaitsev.rxviper.plugin.UseCase
import com.dzaitsev.rxviper.plugin.aClass
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import rx.functions.Action1
import javax.lang.model.element.Modifier

internal class PresenterGenerator(screen: Screen) : Generator(screen) {
  override val typeName = "Presenter"

  override fun createSpec(): List<TypeSpec.Builder> {
    val useCases = if (screen.useCases.isEmpty()) listOf(UseCase(screen.name)) else screen.useCases.map { it }
    val constructorBuilder = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)

    val presenterBuilder = TypeSpec.classBuilder(typeSpecName)
        .superclass(superClass())
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)

    useCases.forEach { useCase ->
      val methodBuilder = MethodSpec.methodBuilder("do${useCase.name}")
          .addModifiers(Modifier.PUBLIC)
          .addParameter(useCase.requestModel, "requestModel")

      if (screen.hasInteractor) {
        val className = "${useCase.name}Interactor"
        val argName = className.decapitalize()

        constructorBuilder
            .addParameter(ClassName.get(screen.fullPackage, className), argName)
            .addStatement("this.$argName = $argName")

        presenterBuilder.addField(ClassName.get(screen.fullPackage, className), argName, Modifier.PRIVATE, Modifier.FINAL)
            .addMethod(methodBuilder.addStatement(methodBody(argName, useCase),
                aClass<Action1<*>>(),
                useCase.responseModel,
                useCase.responseModel,
                aClass<Throwable>(),
                aClass<Throwable>())
                .build())
      } else {
        presenterBuilder.addMethod(methodBuilder.addComment("TODO: Implement your business logic here...")
            .build())
      }
    }

    return listOf(presenterBuilder.addMethod(constructorBuilder.build()))
  }

  private fun methodBody(argName: String, useCase: UseCase): String {
    val value = useCase.responseModel.simpleName.first().toLowerCase()
    return when {
      screen.useLambdas -> "$argName.execute($value -> {\n" +
          "  // TODO: Implement onNext here...\n" +
          "}, t -> {\n" +
          "  // TODO: Implement onError here...\n" +
          "}, requestModel)"
      else -> "$argName.execute(new \$T<\$T>() {\n" +
          "  @Override\n" +
          "  public void call(\$T $value) {\n" +
          "    // TODO: Implement onNext here...\n" +
          "  }\n" +
          "}, new Action1<\$T>() {\n" +
          "  @Override\n" +
          "  public void call(\$T t) {\n" +
          "  // TODO: Implement onError here...\n" +
          "  }\n" +
          "}, requestModel)"
    }
  }

  private fun superClass(): TypeName {
    val viewCallbacks = ClassName.get(screen.fullPackage, "${screen.name}ViewCallbacks")

    return when {
      screen.hasRouter -> ParameterizedTypeName.get(
          ClassName.get(aClass<ViperPresenter<*, *>>()), viewCallbacks, ClassName.get(screen.fullPackage, "${screen.name}Router"))
      else -> ParameterizedTypeName.get(ClassName.get(aClass<Presenter<*>>()), viewCallbacks)
    }
  }
}