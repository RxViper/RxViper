package com.dzaitsev.rxviper.plugin.generator

import com.dzaitsev.rxviper.plugin.Screen
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import rx.Observable
import java.io.File

internal abstract class Generator(protected val screen: Screen) {
  protected abstract val typeName: String

  internal val typeSpecName: String get() = "${screen.name}$typeName"

  protected abstract fun createSpec(): Observable<TypeSpec>

  internal fun saveTo(directory: File) {
    createSpec().subscribe {
      JavaFile.builder(screen.fullPackage, it)
          .skipJavaLangImports(true)
          .build()
          .writeTo(directory)
      println("Generated ${it.name}")
    }
  }
}