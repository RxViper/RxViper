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
 * @since 2016-May-13, 12:31
 */
class PresenterTest {
  private lateinit var view: ViewCallbacks
  private lateinit var presenter: TestPresenter

  @Before fun setUp() {
    view = mock(ViewCallbacks::class.java)
    presenter = spy(TestPresenter(dummy = true))
  }

  @Test fun shouldNotHaveView() {
    assertThat(presenter.protectedView).isNull()
    assertThat(presenter.hasView()).isFalse()
  }

  @Test fun shouldTakeView() {
    presenter.takeView(view)
    assertThat(presenter.protectedView).isEqualTo(view)
    assertThat(presenter.hasView()).isTrue()
  }

  @Test fun shouldDropView() {
    presenter.takeView(view)

    presenter.dropView(view)
    assertThat(presenter.protectedView).isNull()
    assertThat(presenter.hasView()).isFalse()
  }

  @Test fun shouldCallOnTakeView() {
    presenter.takeView(view)
    verify(presenter).onTakeView(view)
  }

  @Test fun shouldCallOnTakeViewOncePerView() {
    presenter.takeView(view)
    presenter.takeView(view)
    verify(presenter).onTakeView(view)
  }

  @Test fun shouldNotCallOnDropIfViewIsNotAttached() {
    presenter.dropView(view)
    verify(presenter, never()).onDropView(view)
  }

  @Test fun shouldIgnoreOnDropIfViewIsNotTheSame() {
    presenter.takeView(view)

    presenter.dropView(object : ViewCallbacks {})
    verify(presenter, never()).onDropView(view)
  }

  @Test fun shouldDropPreviousViewWhenNewViewIsTaken() {
    presenter.takeView(view)

    presenter.takeView(object : ViewCallbacks {})
    verify(presenter).onDropView(view)
  }

  @Test fun shouldCallOnTakeViewAfterViewIsTaken() {
    presenter = TestPresenter(dummy = false)

    presenter.takeView(view)
  }

  @Test fun shouldCallOnDropViewBeforeViewIsDropped() {
    presenter = TestPresenter(dummy = false)

    presenter.takeView(view)
    presenter.dropView(view)
  }
}