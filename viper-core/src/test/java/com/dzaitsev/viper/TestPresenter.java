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

package com.dzaitsev.viper;

import static com.google.common.truth.Truth.assertThat;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Nov-14, 02:08
 */
final class TestPresenter extends Presenter<TestViewCallbacks> {
  boolean dummy = true;

  TestPresenter(TestViewCallbacks view) {
    super(view);
    assertThatViewIsSet(true);
  }

  TestPresenter() {
    super();
  }

  @Override
  protected void onDropView(TestViewCallbacks view) {
    super.onDropView(view);
    assertThatViewIsSet(false);
  }

  @Override
  protected void onTakeView(TestViewCallbacks view) {
    super.onTakeView(view);
    assertThatViewIsSet(false);
  }

  private void assertThatViewIsSet(boolean forceCheck) {
    if (forceCheck || !dummy) {
      assertThat(hasView()).isTrue();
      assertThat(getView()).isNotNull();
    }
  }
}
