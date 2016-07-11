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

package com.dzaitsev.rxviper

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Jun-30, 11:01
 */
class ViperPresenterTest {
  private lateinit var router: Router
  private lateinit var viperPresenter: TestViperPresenter

  @Before fun setUp() {
    router = mock(Router::class.java)
    viperPresenter = spy(TestViperPresenter(dummy = true))
  }

  @Test fun shouldNotHaveRouter() {
    assertThat(viperPresenter.protectedRouter).isNull()
    assertThat(viperPresenter.hasRouter()).isFalse()
  }

  @Test fun shouldTakeRouter() {
    viperPresenter.takeRouter(router)
    assertThat(viperPresenter.protectedRouter).isEqualTo(router)
    assertThat(viperPresenter.hasRouter()).isTrue()
  }

  @Test fun shouldDropRouter() {
    viperPresenter.takeRouter(router)

    viperPresenter.dropRouter(router)
    assertThat(viperPresenter.protectedRouter).isNull()
    assertThat(viperPresenter.hasRouter()).isFalse()
  }

  @Test fun shouldCallOnTakeRouter() {
    viperPresenter.takeRouter(router)
    verify(viperPresenter).onTakeRouter(router)
  }

  @Test fun shouldCallOnTakeRouterOncePerView() {
    viperPresenter.takeRouter(router)
    viperPresenter.takeRouter(router)
    verify(viperPresenter).onTakeRouter(router)
  }

  @Test fun shouldNotCallOnDropIfRouterIsNotAttached() {
    viperPresenter.dropRouter(router)
    verify(viperPresenter, never()).onDropRouter(router)
  }

  @Test fun shouldIgnoreOnDropIfRouterIsNotTheSame() {
    viperPresenter.takeRouter(router)

    viperPresenter.dropRouter(object : Router {})
    verify(viperPresenter, never()).onDropRouter(router)
  }

  @Test fun shouldDropPreviousRouterWhenNewRouterIsTaken() {
    viperPresenter.takeRouter(router)

    viperPresenter.takeRouter(object : Router {})
    verify(viperPresenter).onDropRouter(router)
  }

  @Test fun shouldCallOnTakeRouterAfterRouterIsTaken() {
    viperPresenter = TestViperPresenter(dummy = false)
    viperPresenter.takeRouter(router)
  }

  @Test fun shouldCallOnDropRouterBeforeRouterIsDropped() {
    viperPresenter = TestViperPresenter(dummy = false)

    viperPresenter.takeRouter(router)
    viperPresenter.dropRouter(router)
  }
}