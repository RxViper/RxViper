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
    val screenName = screen.name.capitalize()
    val interactorClass = "${screenName}Interactor"
    val interactorName = interactorClass.decapitalize()
    return """${packageLine(screen)}

import com.dzaitsev.rxviper.ViperPresenter;
import javax.annotation.Generated;
import rx.functions.Action1;

${generatedAnnotation(generator)}
public final class ${generator.typeSpecName} extends ViperPresenter<${screenName}ViewCallbacks, ${screenName}Router> {
  private final $interactorClass $interactorName;

  public ${generator.typeSpecName}($interactorClass $interactorName) {
    this.$interactorName = $interactorName;
  }

  public void do$screenName(Object requestModel) {
    $interactorName.execute(requestModel, new Action1<Object>() {
      @Override
      public void call(Object o) {
        // TODO: Implement onNext here...
      }
    }, new Action1<Throwable>() {
      @Override
      public void call(Throwable t) {
        // TODO: Implement onError here...
      }
    });
  }

  @Override
  protected void onDropView(${screenName}ViewCallbacks view) {
    $interactorName.unsubscribe();
  }
}
"""
  }

  override fun createGenerator(screen: Screen) = PresenterGenerator(screen)
}