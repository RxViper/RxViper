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

package rxviper.sample.domain;

import javax.inject.Inject;
import javax.inject.Singleton;
import rxviper.sample.data.Cheese;
import rxviper.sample.presentation.CheeseViewModel;
import viper.Mapper;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Jun-07, 11:18
 */
@Singleton
public class CheeseMapper extends Mapper<Cheese, CheeseViewModel> {
  @Inject CheeseMapper() {
  }

  @Override public CheeseViewModel map(Cheese cheese) {
    final CheeseViewModel model = new CheeseViewModel(cheese.getName(), cheese.getId());
    model.setChecked(true);
    return model;
  }
}
