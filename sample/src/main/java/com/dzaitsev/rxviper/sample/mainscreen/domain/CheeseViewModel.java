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

package com.dzaitsev.rxviper.sample.mainscreen.domain;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Jun-07, 11:51
 */
public final class CheeseViewModel {
  private final String  name;
  private final long    id;
  private final String  type;
  private       boolean isChecked;

  CheeseViewModel(String name, long id, String type) {
    this.name = name;
    this.id = id;
    this.type = type;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public boolean isChecked() {
    return isChecked;
  }

  public void setChecked(boolean checked) {
    isChecked = checked;
  }

  public String getType() {
    return type;
  }
}
