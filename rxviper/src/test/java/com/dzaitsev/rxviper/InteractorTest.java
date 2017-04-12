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

import io.reactivex.Flowable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.operators.flowable.FlowableInternalHelper;
import io.reactivex.internal.subscribers.LambdaSubscriber;
import io.reactivex.schedulers.Schedulers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.reactivestreams.Subscriber;

import static com.google.common.truth.Truth.assertThat;
import static io.reactivex.Flowable.just;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-May-14, 13:56
 */
public final class InteractorTest {
  private static final int                 PARAM       = 1;
  private static final Consumer<String>    ON_NEXT     = Functions.emptyConsumer();
  private static final Consumer<Throwable> ON_ERROR    = Functions.emptyConsumer();
  private static final Action              ON_COMPLETE = Functions.EMPTY_ACTION;
  private static final Subscriber<String>  SUBSCRIBER  =
      new LambdaSubscriber<>(ON_NEXT, ON_ERROR, ON_COMPLETE, FlowableInternalHelper.RequestMax.INSTANCE);

  @Rule public final ExpectedException thrown = ExpectedException.none();

  private Interactor<Integer, String> spyInteractor;

  @Before
  public void setUp() {
    spyInteractor = spy(new Interactor<Integer, String>(Schedulers.trampoline(), Schedulers.trampoline()) {
      @Override
      protected Flowable<String> createFlowable(final Integer integer) {
        return just(String.valueOf(integer));
      }
    });
  }

  @Test
  public void shouldCreateFlowableSubscriber() {
    spyInteractor.execute(SUBSCRIBER);
    verify(spyInteractor).createFlowable(null);
  }

  @Test
  public void shouldCreateFlowableSubscriberParam() {
    spyInteractor.execute(SUBSCRIBER, PARAM);
    verify(spyInteractor).createFlowable(PARAM);
  }

  @Test
  public void shouldCreateFlowableOnNext() {
    spyInteractor.execute(ON_NEXT);
    verify(spyInteractor).createFlowable(null);
  }

  @Test
  public void shouldCreateFlowableOnNextParam() {
    spyInteractor.execute(ON_NEXT, PARAM);
    verify(spyInteractor).createFlowable(PARAM);
  }

  @Test
  public void shouldCreateFlowableOnNextOnError() {
    spyInteractor.execute(ON_NEXT, ON_ERROR);
    verify(spyInteractor).createFlowable(null);
  }

  @Test
  public void shouldCreateFlowableOnNextOnErrorOnComplete() {
    spyInteractor.execute(ON_NEXT, ON_ERROR, ON_COMPLETE);
    verify(spyInteractor).createFlowable(null);
  }

  @Test
  public void shouldCreateFlowableOnNextOnErrorParam() {
    spyInteractor.execute(ON_NEXT, ON_ERROR, PARAM);
    verify(spyInteractor).createFlowable(PARAM);
  }

  @Test
  public void shouldCreateFlowableOnNextOnErrorOnCompleteParam() {
    spyInteractor.execute(ON_NEXT, ON_ERROR, ON_COMPLETE, PARAM);
    verify(spyInteractor).createFlowable(PARAM);
  }

  @Test
  public void shouldBeDisposedOnStart() {
    assertThat(spyInteractor.isDisposed()).isTrue();
  }

  @Test
  public void shouldDispose() {
    spyInteractor.dispose();
    assertThat(spyInteractor.isDisposed()).isTrue();
    spyInteractor.dispose();
    assertThat(spyInteractor.isDisposed()).isTrue();
  }

  @Test
  public void subscriberShouldNotBeNull() {
    thrown.expect(NullPointerException.class);
    //noinspection unchecked
    spyInteractor.execute((Subscriber) null);
  }

  @Test
  public void subscriberParamShouldNotBeNull() {
    thrown.expect(NullPointerException.class);
    //noinspection unchecked
    spyInteractor.execute((Subscriber) null, PARAM);
  }

  @Test
  public void onNextShouldNotBeNull() {
    thrown.expect(NullPointerException.class);
    //noinspection unchecked
    spyInteractor.execute((Consumer) null);
  }

  @Test
  public void onNextParamShouldNotBeNull() {
    thrown.expect(NullPointerException.class);
    //noinspection unchecked
    spyInteractor.execute((Consumer) null, PARAM);
  }

  @Test
  public void onErrorShouldNotBeNull() {
    thrown.expect(NullPointerException.class);
    //noinspection unchecked
    spyInteractor.execute(ON_NEXT, (Consumer) null);
  }

  @Test
  public void onErrorParamShouldNotBeNull() {
    thrown.expect(NullPointerException.class);
    spyInteractor.execute(ON_NEXT, null, PARAM);
  }

  @Test
  public void onCompleteShouldNotBeNull() {
    thrown.expect(NullPointerException.class);
    spyInteractor.execute(ON_NEXT, ON_ERROR, (Action) null);
  }

  @Test
  public void onCompleteParamShouldNotBeNull() {
    thrown.expect(NullPointerException.class);
    spyInteractor.execute(ON_NEXT, ON_ERROR, null, PARAM);
  }

  @Test
  public void subscribeOnShouldNotBeNull() {
    thrown.expect(IllegalArgumentException.class);
    new Interactor<Object, Object>(null, Schedulers.trampoline()) {
      @Override
      protected Flowable<Object> createFlowable(final Object o) {
        return null;
      }
    };
  }

  @Test
  public void observeOnOnShouldNotBeNull() {
    thrown.expect(IllegalArgumentException.class);
    new Interactor<Object, Object>(null, Schedulers.trampoline()) {
      @Override
      protected Flowable<Object> createFlowable(final Object o) {
        return null;
      }
    };
  }
}