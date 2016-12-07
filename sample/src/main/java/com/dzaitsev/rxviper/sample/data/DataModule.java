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

package com.dzaitsev.rxviper.sample.data;

import android.content.res.Resources;
import com.dzaitsev.rxviper.sample.AppModule;
import dagger.Module;
import dagger.Provides;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Dec-17, 14:27
 */
@Module(includes = AppModule.class)
public final class DataModule {
  @Provides
  static CheeseStorage provideCheeseStorage(Resources resources) {
    return new CheeseStorage(resources);
  }
}
