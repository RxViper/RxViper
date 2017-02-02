package com.dzaitsev.rxviper.plugin

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import rx.Observable
import java.io.File

internal abstract class Generator(protected val feature: FeatureOptions) {
  protected abstract val typeName: String

  internal val typeSpecName: String get() = "${feature.name}$typeName"

  protected abstract fun createSpec(): Observable<TypeSpec>

  internal fun saveTo(directory: File) {
    createSpec().subscribe {
      JavaFile.builder(feature.fullPackage, it)
          .skipJavaLangImports(true)
          .build()
          .writeTo(directory)
    }
  }
}