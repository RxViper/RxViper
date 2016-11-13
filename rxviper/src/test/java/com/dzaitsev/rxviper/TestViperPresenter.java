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

package com.dzaitsev.rxviper;

import static com.google.common.truth.Truth.assertThat;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Nov-14, 02:08
 */
final class TestViperPresenter extends ViperPresenter<TestViewCallbacks, TestRouter> {
  boolean dummy = true;

  TestViperPresenter(TestViewCallbacks view, TestRouter router) {
    super(view, router);
    assertThatRouterIsSet(true);
    assertThatViewIsSet(true);
  }

  TestViperPresenter(TestRouter router) {
    super(router);
    assertThatRouterIsSet(true);
  }

  TestViperPresenter(TestViewCallbacks view) {
    super(view);
    assertThatViewIsSet(true);
  }

  TestViperPresenter() {}

  @Override
  protected void onDropRouter(TestRouter router) {
    super.onDropRouter(router);
    assertThatRouterIsSet(false);
  }

  @Override
  protected void onTakeRouter(TestRouter router) {
    super.onTakeRouter(router);
    assertThatRouterIsSet(false);
  }

  private void assertThatRouterIsSet(boolean forceCheck) {
    if (forceCheck || !dummy) {
      assertThat(hasRouter()).isTrue();
      assertThat(getRouter()).isNotNull();
    }
  }

  private void assertThatViewIsSet(boolean forceCheck) {
    if (forceCheck || !dummy) {
      assertThat(hasView()).isTrue();
      assertThat(getView()).isNotNull();
    }
  }
}
