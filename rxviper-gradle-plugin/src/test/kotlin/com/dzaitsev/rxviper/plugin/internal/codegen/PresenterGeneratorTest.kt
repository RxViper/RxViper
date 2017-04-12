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

import com.dzaitsev.rxviper.plugin.internal.dsl.Screen

class PresenterGeneratorTest : GeneratorTest() {

  override fun defaultSource(screen: Screen, generator: Generator): String {
    val interactorClass = "${screen.name}Interactor"
    val interactorName = interactorClass.decapitalize()
    return "${packageLine(screen)}\n" +
        "\n" +
        "import com.dzaitsev.rxviper.ViperPresenter;\n" +
        "import io.reactivex.functions.Consumer;\n" +
        "import javax.annotation.Generated;\n" +
        "\n" +
        "${generatedAnnotation(generator)}\n" +
        "public final class ${generator.typeSpecName} extends ViperPresenter<${screen.name}ViewCallbacks, ${screen.name}Router> {\n" +
        "  private final $interactorClass $interactorName;\n" +
        "\n" +
        "  public ${generator.typeSpecName}($interactorClass $interactorName) {\n" +
        "    this.$interactorName = $interactorName;\n" +
        "  }\n" +
        "\n" +
        "  public void do${screen.name}(Object requestModel) {\n" +
        "    $interactorName.execute(new Consumer<Object>() {\n" +
        "      @Override\n" +
        "      public void accept(Object o) {\n" +
        "        // TODO: Implement onNext here...\n" +
        "      }\n" +
        "    }, new Consumer<Throwable>() {\n" +
        "      @Override\n" +
        "      public void accept(Throwable t) {\n" +
        "        // TODO: Implement onError here...\n" +
        "      }\n" +
        "    }, requestModel);\n" +
        "  }\n" +
        "\n" +
        "  @Override\n" +
        "  protected void onDropView(${screen.name}ViewCallbacks view) {\n" +
        "    $interactorName.dispose();\n" +
        "  }\n" +
        "}\n"
  }

  override fun createGenerator(screen: Screen) = PresenterGenerator(screen)
}