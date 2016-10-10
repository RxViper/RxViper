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

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
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
public class InteractorTest {
  private static final int PARAM = 1;
  private Interactor<Integer, String> spyInteractor;

  @BeforeMethod public final void setUp() {
    System.out.println("setUp");
    spyInteractor = spy(new Interactor<Integer, String>(Schedulers.immediate(), Schedulers.immediate()) {
      @Override protected Observable<String> createObservable(final Integer integer) {
        return just(String.valueOf(integer));
      }
    });
  }

  @AfterMethod public final void tearDown() {
    spyInteractor = null;
    System.out.println("tearDown");
  }

  @Test public final void shouldCreateObservableSubscriber() {
    System.out.println("InteractorTest.shouldCreateObservableSubscriber");
    spyInteractor.execute(new Subscriber<String>() {
      @Override public void onCompleted() {
      }

      @Override public void onError(final Throwable e) {
      }

      @Override public void onNext(final String s) {
      }
    });
    verify(spyInteractor).createObservable(null);
  }

  @Test public final void shouldCreateObservableSubscriberParam() {
    System.out.println("InteractorTest.shouldCreateObservableSubscriberParam");
    spyInteractor.execute(new Subscriber<String>() {
      @Override public void onCompleted() {
      }

      @Override public void onError(final Throwable e) {
      }

      @Override public void onNext(final String s) {
      }
    }, PARAM);
    verify(spyInteractor).createObservable(PARAM);
  }

  @Test public final void shouldCreateObservableOnNext() {
    System.out.println("InteractorTest.shouldCreateObservableOnNext");
    spyInteractor.execute(new Action1<String>() {
      @Override public void call(final String s) {
      }
    });
    verify(spyInteractor).createObservable(null);
  }

  @Test public final void shouldCreateObservableOnNextParam() {
    System.out.println("InteractorTest.shouldCreateObservableOnNextParam");
    spyInteractor.execute(new Action1<String>() {
      @Override public void call(final String s) {
      }
    }, PARAM);
    verify(spyInteractor).createObservable(PARAM);
  }

  @Test public final void shouldCreateObservableOnNextOnError() {
    System.out.println("InteractorTest.shouldCreateObservableOnNextOnError");
    spyInteractor.execute(new Action1<String>() {
      @Override public void call(final String s) {
      }
    }, new Action1<Throwable>() {
      @Override public void call(final Throwable t) {
      }
    });
    verify(spyInteractor).createObservable(null);
  }

  @Test public final void shouldCreateObservableOnNextOnErrorOnComplete() {
    System.out.println("InteractorTest.shouldCreateObservableOnNextOnErrorOnComplete");
    spyInteractor.execute(new Action1<String>() {
      @Override public void call(final String s) {
      }
    }, new Action1<Throwable>() {
      @Override public void call(final Throwable t) {
      }
    }, new Action0() {
      @Override public void call() {
      }
    });
    verify(spyInteractor).createObservable(null);
  }

  @Test public final void shouldCreateObservableOnNextOnErrorParam() {
    System.out.println("InteractorTest.shouldCreateObservableOnNextOnErrorParam");
    spyInteractor.execute(new Action1<String>() {
      @Override public void call(final String s) {
      }
    }, new Action1<Throwable>() {
      @Override public void call(final Throwable t) {
      }
    }, PARAM);
    verify(spyInteractor).createObservable(PARAM);
  }

  @Test public final void shouldCreateObservableOnNextOnErrorOnCompleteParam() {
    System.out.println("InteractorTest.shouldCreateObservableOnNextOnErrorOnCompleteParam");
    spyInteractor.execute(new Action1<String>() {
      @Override public void call(final String s) {
      }
    }, new Action1<Throwable>() {
      @Override public void call(final Throwable t) {
      }
    }, new Action0() {
      @Override public void call() {
      }
    }, PARAM);
    verify(spyInteractor).createObservable(PARAM);
  }

  @Test public final void shouldBeUnSubscribedOnStart() {
    System.out.println("InteractorTest.shouldBeUnSubscribedOnStart");
    assertThat(spyInteractor.isUnsubscribed()).isTrue();
  }

  @Test public final void shouldUnsubscribe() {
    System.out.println("InteractorTest.shouldUnsubscribe");
    spyInteractor.unsubscribe();
    assertThat(spyInteractor.isUnsubscribed()).isTrue();
    spyInteractor.unsubscribe();
    assertThat(spyInteractor.isUnsubscribed()).isTrue();
  }

  @Test(expectedExceptions = IllegalArgumentException.class) //
  public final void subscriberShouldNotBeNull() {
    System.out.println("InteractorTest.subscriberShouldNotBeNull");
    //noinspection unchecked
    spyInteractor.execute((Subscriber) null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class) //
  public final void subscriberParamShouldNotBeNull() {
    System.out.println("InteractorTest.subscriberParamShouldNotBeNull");
    //noinspection unchecked
    spyInteractor.execute((Subscriber) null, PARAM);
  }

  @Test(expectedExceptions = IllegalArgumentException.class) //
  public final void onNextShouldNotBeNull() {
    System.out.println("InteractorTest.onNextShouldNotBeNull");
    //noinspection unchecked
    spyInteractor.execute((Action1) null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class) //
  public final void onNextParamShouldNotBeNull() {
    System.out.println("InteractorTest.onNextParamShouldNotBeNull");
    //noinspection unchecked
    spyInteractor.execute((Action1) null, PARAM);
  }

  @Test(expectedExceptions = IllegalArgumentException.class) //
  public final void onErrorShouldNotBeNull() {
    System.out.println("InteractorTest.onErrorShouldNotBeNull");
    //noinspection unchecked
    spyInteractor.execute(new Action1<String>() {
      @Override public void call(final String s) {
      }
    }, (Action1) null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class) //
  public final void onErrorParamShouldNotBeNull() {
    System.out.println("InteractorTest.onErrorParamShouldNotBeNull");
    spyInteractor.execute(new Action1<String>() {
      @Override public void call(final String s) {
      }
    }, null, PARAM);
  }

  @Test(expectedExceptions = IllegalArgumentException.class) //
  public final void onCompleteShouldNotBeNull() {
    System.out.println("InteractorTest.onCompleteShouldNotBeNull");
    spyInteractor.execute(new Action1<String>() {
      @Override public void call(final String s) {
      }
    }, new Action1<Throwable>() {
      @Override public void call(final Throwable throwable) {
      }
    }, (Action0) null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class) //
  public final void onCompleteParamShouldNotBeNull() {
    System.out.println("InteractorTest.onCompleteParamShouldNotBeNull");
    spyInteractor.execute(new Action1<String>() {
      @Override public void call(final String s) {
      }
    }, new Action1<Throwable>() {
      @Override public void call(final Throwable throwable) {
      }
    }, null, PARAM);
  }

  @Test(expectedExceptions = IllegalArgumentException.class) //
  public final void subscribeOnShouldNotBeNull() {
    System.out.println("InteractorTest.subscribeOnShouldNotBeNull");
    new Interactor<Object, Object>(null, Schedulers.immediate()) {
      @Override protected Observable<Object> createObservable(final Object o) {
        return null;
      }
    };
  }

  @Test(expectedExceptions = IllegalArgumentException.class) //
  public final void observeOnOnShouldNotBeNull() {
    System.out.println("InteractorTest.observeOnOnShouldNotBeNull");
    new Interactor<Object, Object>(null, Schedulers.immediate()) {
      @Override protected Observable<Object> createObservable(final Object o) {
        return null;
      }
    };
  }
}