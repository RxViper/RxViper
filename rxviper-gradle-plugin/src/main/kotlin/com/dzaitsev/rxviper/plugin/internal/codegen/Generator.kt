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

import com.dzaitsev.rxviper.plugin.internal.dsl.Screen
import com.dzaitsev.rxviper.plugin.aClass
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.annotation.Generated

internal abstract class Generator(protected val screen: Screen) {
  protected abstract val typeName: String

  internal val typeSpecName: String get() = "${screen.name}$typeName"

  protected abstract fun createSpec(): List<TypeSpec.Builder>

  internal fun saveTo(directory: File) {
    createSpec().forEach { builder ->
      if (screen.addMetaInfo) {
        builder.addAnnotation(AnnotationSpec.builder(aClass<Generated>())
            .addMember("value", "\$S", javaClass.name)
            .addMember("date", "\$S", dateFormat.format(Date()))
            .addMember("comments", "\$S", "Created by RxViper Gradle Plugin")
            .build())
      }
      val typeSpec = builder.build()

      JavaFile.builder(screen.fullPackage, typeSpec)
          .skipJavaLangImports(true)
          .build()
          .writeTo(directory)
      println("Generated ${typeSpec.name}")
    }
  }

  companion object {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssZ", Locale.UK)
  }
}