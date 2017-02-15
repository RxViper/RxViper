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

package com.dzaitsev.rxviper.plugin.internal.dsl

import com.dzaitsev.rxviper.plugin.aClass
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project

internal class ScreenFactory(private val project: Project) : NamedDomainObjectFactory<Screen> {
  override fun create(name: String) = Screen(name, project.container(aClass<UseCase>()))
}