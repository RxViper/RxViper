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
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import rx.Subscriber
import rx.functions.Action0
import rx.functions.Action1
import rx.internal.util.ActionSubscriber

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-May-14, 13:56
 */
class InteractorTest {
  companion object {
    private val PARAM = 1
    private val ON_NEXT: Action1<String> = Action1 {}
    private val ON_ERROR: Action1<Throwable> = Action1 {}
    private val ON_COMPLETED: Action0 = Action0 {}
    private val SUBSCRIBER: Subscriber<String> = ActionSubscriber(ON_NEXT, ON_ERROR, ON_COMPLETED)
  }

  private lateinit var interactor: TestInteractor

  @Before fun setUp() {
    interactor = spy(TestInteractor())
  }

  @Test fun shouldCreateObservableSubscriber() {
    interactor.execute(SUBSCRIBER)
    verify(interactor).createObservable(null)
  }

  @Test fun shouldCreateObservableSubscriberParam() {
    interactor.execute(SUBSCRIBER, PARAM)
    verify(interactor).createObservable(PARAM)
  }

  @Test fun shouldCreateObservableOnNext() {
    interactor.execute(ON_NEXT)
    verify(interactor).createObservable(null)
  }

  @Test fun shouldCreateObservableOnNextParam() {
    interactor.execute(ON_NEXT, PARAM)
    verify(interactor).createObservable(PARAM)
  }

  @Test fun shouldCreateObservableOnNextOnError() {
    interactor.execute(ON_NEXT, ON_ERROR)
    verify(interactor).createObservable(null)
  }

  @Test fun shouldCreateObservableOnNextOnErrorOnCompleted() {
    interactor.execute(ON_NEXT, ON_ERROR, ON_COMPLETED)
    verify(interactor).createObservable(null)
  }

  @Test fun shouldCreateObservableOnNextOnErrorParam() {
    interactor.execute(ON_NEXT, ON_ERROR, PARAM)
    verify(interactor).createObservable(PARAM)
  }

  @Test fun shouldCreateObservableOnNextOnErrorOnCompletedParam() {
    interactor.execute(ON_NEXT, ON_ERROR, ON_COMPLETED, PARAM)
    verify(interactor).createObservable(PARAM)
  }

  @Test fun shouldBeUnSubscribedOnStart() {
    assertThat(interactor.isUnsubscribed).isTrue()
  }

  @Test fun shouldUnsubscribe() {
    interactor.unsubscribe()
    assertThat(interactor.isUnsubscribed).isTrue()
    interactor.unsubscribe()
    assertThat(interactor.isUnsubscribed).isTrue()
  }
}