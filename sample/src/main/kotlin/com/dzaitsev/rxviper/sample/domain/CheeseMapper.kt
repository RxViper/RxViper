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

package com.dzaitsev.rxviper.sample.domain

import com.dzaitsev.rxviper.sample.data.Cheese
import com.dzaitsev.rxviper.sample.presentation.CheeseViewModel
import com.dzaitsev.rxviper.Mapper
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Jun-07, 11:18
 */
@Singleton
class CheeseMapper @Inject internal constructor() : Mapper<Cheese, CheeseViewModel>() {
  override fun map(cheese: Cheese): CheeseViewModel = CheeseViewModel(name = cheese.name, id = cheese.id, type = cheese.type.name).apply {
    with(this) { isChecked = true }
  }
}
