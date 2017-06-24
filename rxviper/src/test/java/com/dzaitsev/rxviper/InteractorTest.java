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

import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.internal.util.ActionSubscriber;
import rx.schedulers.Schedulers;

import static com.dzaitsev.rxviper.TestUtil.checkIllegalArgumentException;
import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static rx.Observable.just;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-May-14, 13:56
 */
public final class InteractorTest {
  private static final int                PARAM        = 1;
  private static final Action1<String>    ON_NEXT      = Actions.empty();
  private static final Action1<Throwable> ON_ERROR     = Actions.empty();
  private static final Action0            ON_COMPLETED = Actions.empty();
  private static final Subscriber<String> SUBSCRIBER   = new ActionSubscriber<>(ON_NEXT, ON_ERROR, ON_COMPLETED);

  private Interactor<Integer, String> interactor;

  @Before
  public void setUp() {
    interactor = spy(new Interactor<Integer, String>(Schedulers.immediate(), Schedulers.immediate()) {
      @Override
      protected Observable<String> createObservable(final Integer integer) {
        return just(String.valueOf(integer));
      }
    });
  }

  @Test
  public void shouldCreateObservableSubscriber() {
    interactor.execute(SUBSCRIBER);
    verify(interactor).createObservable(null);
  }

  @Test
  public void shouldCreateObservableSubscriberParam() {
    interactor.execute(SUBSCRIBER, PARAM);
    verify(interactor).createObservable(PARAM);
  }

  @Test
  public void shouldCreateObservableOnNext() {
    interactor.execute(ON_NEXT);
    verify(interactor).createObservable(null);
  }

  @Test
  public void shouldCreateObservableOnNextParam() {
    interactor.execute(ON_NEXT, PARAM);
    verify(interactor).createObservable(PARAM);
  }

  @Test
  public void shouldCreateObservableOnNextOnError() {
    interactor.execute(ON_NEXT, ON_ERROR);
    verify(interactor).createObservable(null);
  }

  @Test
  public void shouldCreateObservableOnNextOnErrorOnCompleted() {
    interactor.execute(ON_NEXT, ON_ERROR, ON_COMPLETED);
    verify(interactor).createObservable(null);
  }

  @Test
  public void shouldCreateObservableOnNextOnErrorParam() {
    interactor.execute(ON_NEXT, ON_ERROR, PARAM);
    verify(interactor).createObservable(PARAM);
  }

  @Test
  public void shouldCreateObservableOnNextOnErrorOnCompletedParam() {
    interactor.execute(ON_NEXT, ON_ERROR, ON_COMPLETED, PARAM);
    verify(interactor).createObservable(PARAM);
  }

  @Test
  public void shouldBeUnSubscribedOnStart() {
    assertThat(interactor.isUnsubscribed()).isTrue();
  }

  @Test
  public void shouldUnsubscribe() {
    interactor.unsubscribe();
    assertThat(interactor.isUnsubscribed()).isTrue();
    interactor.unsubscribe();
    assertThat(interactor.isUnsubscribed()).isTrue();
  }

  @Test
  public void subscriberShouldNotBeNull() {
    checkIllegalArgumentException(() -> interactor.execute((Subscriber) null));
  }

  @Test
  public void subscriberParamShouldNotBeNull() {
    checkIllegalArgumentException(() -> interactor.execute((Subscriber) null, PARAM));
  }

  @Test
  public void onNextShouldNotBeNull() {
    checkIllegalArgumentException(() -> interactor.execute((Action1) null));
  }

  @Test
  public void onNextParamShouldNotBeNull() {
    checkIllegalArgumentException(() -> interactor.execute((Action1) null, PARAM));
  }

  @Test
  public void onErrorShouldNotBeNull() {
    checkIllegalArgumentException(() -> interactor.execute(ON_NEXT, (Action1) null));
  }

  @Test
  public void onErrorParamShouldNotBeNull() {
    checkIllegalArgumentException(() -> interactor.execute(ON_NEXT, null, PARAM));
  }

  @Test
  public void onCompletedShouldNotBeNull() {
    checkIllegalArgumentException(() -> interactor.execute(ON_NEXT, ON_ERROR, (Action0) null));
  }

  @Test
  public void onCompletedParamShouldNotBeNull() {
    checkIllegalArgumentException(() -> interactor.execute(ON_NEXT, ON_ERROR, null, PARAM));
  }

  @Test
  public void subscribeOnShouldNotBeNull() {
    checkIllegalArgumentException(() -> new Interactor<Object, Object>(null, Schedulers.immediate()) {
      @Override
      protected Observable<Object> createObservable(final Object o) {
        return null;
      }
    });
  }

  @Test
  public void observeOnOnShouldNotBeNull() {
    checkIllegalArgumentException(() -> new Interactor<Object, Object>(null, Schedulers.immediate()) {
      @Override
      protected Observable<Object> createObservable(final Object o) {
        return null;
      }
    });
  }
}