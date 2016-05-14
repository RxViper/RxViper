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

package viper;

import java.util.ArrayList;
import java.util.Collection;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmitriy Zaitsev
 * @since 2016-Apr-04, 02:21
 */
public abstract class Mapper<From, To> {
  public abstract To map(From entity);

  public final Collection<To> map(Collection<From> entities) {
    final Collection<To> result = new ArrayList<>(entities.size());
    //noinspection Convert2streamapi
    for (From from : entities) {
      result.add(map(from));
    }
    return result;
  }
}
