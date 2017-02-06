package com.dzaitsev.rxviper.plugin.generator

import com.dzaitsev.rxviper.Presenter
import com.dzaitsev.rxviper.ViperPresenter
import com.dzaitsev.rxviper.plugin.FeatureOptions
import com.dzaitsev.rxviper.plugin.clazz
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import rx.Observable
import rx.Observable.just
import rx.functions.Action1
import javax.lang.model.element.Modifier

internal class PresenterGenerator(feature: FeatureOptions, val hasRouter: Boolean = true) : Generator(feature) {
  override val typeName = "Presenter"

  override fun createSpec(): Observable<TypeSpec> {
    val useCases = if (feature.useCases.isEmpty()) arrayOf(feature.name) else feature.useCases
    val constructorBuilder = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)

    val presenterBuilder = TypeSpec.classBuilder(typeSpecName)
        .superclass(superClass())
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)

    useCases.forEach { useCase ->
      val methodBuilder = MethodSpec.methodBuilder("do$useCase")
          .addModifiers(Modifier.PUBLIC)
          .addParameter(feature.requestModel, "requestModel")

      if (feature.justMvp) {
        presenterBuilder.addMethod(methodBuilder.addComment("TODO: Implement your business logic here...")
            .build())
      } else {
        val className = "${useCase}Interactor"
        val argName = className.decapitalize()

        constructorBuilder
            .addParameter(ClassName.get(feature.fullPackage, className), argName)
            .addCode("this.$argName = $argName;\n")

        presenterBuilder.addField(ClassName.get(feature.fullPackage, className), argName, Modifier.PRIVATE, Modifier.FINAL)
            .addMethod(methodBuilder.addStatement(methodBody(argName),
                clazz<Action1<*>>(),
                feature.responseModel,
                feature.responseModel,
                clazz<Throwable>(),
                clazz<Throwable>())
                .build())
      }
    }

    return just(presenterBuilder.addMethod(constructorBuilder.build()).build())
  }

  private fun methodBody(argName: String): String {
    val value = feature.responseModel.simpleName.first().toLowerCase()
    return when {
      feature.useLambdas -> "$argName.execute($value -> {\n" +
          "  // TODO: Implement onNext here...\n" +
          "}, t -> {\n" +
          "  // TODO: Implement onError here...\n" +
          "}, requestModel)"
      else -> "$argName.execute(new \$T<\$T>() {\n" +
          "@Override\n" +
          "public void call(\$T $value) {\n" +
          "  // TODO: Implement onNext here...\n" +
          "}\n }, new Action1<\$T>() {\n" +
          "@Override\n" +
          "public void call(\$T t) {\n" +
          "// TODO: Implement onError here...\n" +
          "}\n" +
          "}, requestModel)"
    }
  }

  private fun superClass(): TypeName {
    val viewCallbacks = ClassName.get(feature.fullPackage, "${feature.name}ViewCallbacks")

    return when {
      hasRouter -> ParameterizedTypeName.get(
          ClassName.get(clazz<ViperPresenter<*, *>>()), viewCallbacks, ClassName.get(feature.fullPackage, "${feature.name}Router"))
      else -> ParameterizedTypeName.get(ClassName.get(clazz<Presenter<*>>()), viewCallbacks)
    }
  }
}