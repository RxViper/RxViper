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

package rxviper.sample.presentation;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Jun-07, 11:51
 */
public final class CheeseViewModel {
  private final String  mName;
  private       boolean isChecked;

  public CheeseViewModel(String name) {
    mName = name;
  }

  public String getName() {
    return mName;
  }

  public boolean isChecked() {
    return isChecked;
  }

  public void setChecked(boolean checked) {
    isChecked = checked;
  }
}
