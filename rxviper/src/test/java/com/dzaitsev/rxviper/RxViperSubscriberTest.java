/*
 * Copyright 2018 Dmytro Zaitsev
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

import io.reactivex.Flowable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;

import static io.reactivex.Flowable.just;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2018-Jan-29, 15:12
 */
public class RxViperSubscriberTest {
  private RxViperSubscriber<Integer> subscriber;
  private Subscriber<Integer>        delegate;

  @Before
  public void setUp() {
    delegate = mock(Subscriber.class, Mockito.RETURNS_DEFAULTS);
    subscriber = new RxViperSubscriber<>(delegate);
  }

  @Test
  public void testOnComplete() {
    just(0).subscribe(subscriber);
    verify(delegate).onComplete();
  }

  @Test
  public void testOnError() {
    final RxViperTestException ex = new RxViperTestException();
    Flowable.<Integer>error(ex).subscribe(subscriber);
    verify(delegate).onError(ex);
  }

  @Test
  public void testOnNext() {
    just(0).subscribe(subscriber);
    verify(delegate).onNext(0);
  }

  private static class RxViperTestException extends RuntimeException {}
}