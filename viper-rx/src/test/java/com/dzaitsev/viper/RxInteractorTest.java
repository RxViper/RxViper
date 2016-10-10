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

import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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
public class RxInteractorTest {
  private static final int    PARAM  = 1;
  private RxInteractor<Integer, String> spyInteractor;

  @Before public void setUp() {
    spyInteractor = spy(new RxInteractor<Integer, String>(Schedulers.immediate(), Schedulers.immediate()) {
      @Override protected String getData(Integer integer) {
        return String.valueOf(integer);
      }
    });
  }

  @Test public void shouldGetDataSubscriber() {
    spyInteractor.execute(new Subscriber<String>() {
      @Override public void onCompleted() {
      }

      @Override public void onError(final Throwable e) {
      }

      @Override public void onNext(final String s) {
      }
    });
    verify(spyInteractor).getData(null);
  }

  @Test public void shouldGetDataSubscriberParam() {
    spyInteractor.execute(new Subscriber<String>() {
      @Override public void onCompleted() {
      }

      @Override public void onError(final Throwable e) {
      }

      @Override public void onNext(final String s) {
      }
    }, PARAM);
    verify(spyInteractor).getData(PARAM);
  }

  @Test public void shouldBeUnSubscribedOnStart() {
    assertThat(spyInteractor.isUnsubscribed()).isTrue();
  }

  @Test public void shouldUnsubscribe() {
    spyInteractor.unsubscribe();
    assertThat(spyInteractor.isUnsubscribed()).isTrue();
    spyInteractor.unsubscribe();
    assertThat(spyInteractor.isUnsubscribed()).isTrue();
  }

  @Test(expected = IllegalArgumentException.class) //
  public void subscriberShouldNotBeNull() {
    //noinspection unchecked
    spyInteractor.execute((Subscriber) null);
  }

  @Test(expected = IllegalArgumentException.class) //
  public void subscriberParamShouldNotBeNull() {
    //noinspection unchecked
    spyInteractor.execute((Subscriber) null, PARAM);
  }

  @Test(expected = IllegalArgumentException.class) //
  public void subscribeOnShouldNotBeNull() {
    new RxInteractor<Object, Object>(null, Schedulers.immediate()) {
      @Override protected Object getData(Object o) {
        return null;
      }
    };
  }

  @Test(expected = IllegalArgumentException.class) //
  public void observeOnOnShouldNotBeNull() {
    new RxInteractor<Object, Object>(null, Schedulers.immediate()) {
      @Override protected Object getData(Object o) {
        return null;
      }
    };
  }
}