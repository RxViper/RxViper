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
  private static final int    PARAM  = 1;
  private Interactor<Integer, String> spyInteractor;

  @Before public void setUp() {
    spyInteractor = spy(new Interactor<Integer, String>(Schedulers.immediate(), Schedulers.immediate()) {
      @Override protected Observable<String> createObservable(final Integer integer) {
        return just(String.valueOf(integer));
      }
    });
  }

  @Test public void shouldCreateObservableSubscriber() {
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

  @Test public void shouldCreateObservableSubscriberParam() {
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

  @Test public void shouldCreateObservableOnNext() {
    spyInteractor.execute(new Action1<String>() {
      @Override public void call(final String s) {
      }
    });
    verify(spyInteractor).createObservable(null);
  }

  @Test public void shouldCreateObservableOnNextParam() {
    spyInteractor.execute(new Action1<String>() {
      @Override public void call(final String s) {
      }
    }, PARAM);
    verify(spyInteractor).createObservable(PARAM);
  }

  @Test public void shouldCreateObservableOnNextOnError() {
    spyInteractor.execute(new Action1<String>() {
      @Override public void call(final String s) {
      }
    }, new Action1<Throwable>() {
      @Override public void call(final Throwable t) {
      }
    });
    verify(spyInteractor).createObservable(null);
  }

  @Test public void shouldCreateObservableOnNextOnErrorOnComplete() {
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

  @Test public void shouldCreateObservableOnNextOnErrorParam() {
    spyInteractor.execute(new Action1<String>() {
      @Override public void call(final String s) {
      }
    }, new Action1<Throwable>() {
      @Override public void call(final Throwable t) {
      }
    }, PARAM);
    verify(spyInteractor).createObservable(PARAM);
  }

  @Test public void shouldCreateObservableOnNextOnErrorOnCompleteParam() {
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
  public void onNextShouldNotBeNull() {
    //noinspection unchecked
    spyInteractor.execute((Action1) null);
  }

  @Test(expected = IllegalArgumentException.class) //
  public void onNextParamShouldNotBeNull() {
    //noinspection unchecked
    spyInteractor.execute((Action1) null, PARAM);
  }

  @Test(expected = IllegalArgumentException.class) //
  public void onErrorShouldNotBeNull() {
    //noinspection unchecked
    spyInteractor.execute(new Action1<String>() {
      @Override public void call(final String s) {
      }
    }, (Action1) null);
  }

  @Test(expected = IllegalArgumentException.class) //
  public void onErrorParamShouldNotBeNull() {
    spyInteractor.execute(new Action1<String>() {
      @Override public void call(final String s) {
      }
    }, null, PARAM);
  }

  @Test(expected = IllegalArgumentException.class) //
  public void onCompleteShouldNotBeNull() {
    spyInteractor.execute(new Action1<String>() {
      @Override public void call(final String s) {
      }
    }, new Action1<Throwable>() {
      @Override public void call(final Throwable throwable) {
      }
    }, (Action0) null);
  }

  @Test(expected = IllegalArgumentException.class) //
  public void onCompleteParamShouldNotBeNull() {
    spyInteractor.execute(new Action1<String>() {
      @Override public void call(final String s) {
      }
    }, new Action1<Throwable>() {
      @Override public void call(final Throwable throwable) {
      }
    }, null, PARAM);
  }

  @Test(expected = IllegalArgumentException.class) //
  public void subscribeOnShouldNotBeNull() {
    new Interactor<Object, Object>(null, Schedulers.immediate()) {
      @Override protected Observable<Object> createObservable(final Object o) {
        return null;
      }
    };
  }

  @Test(expected = IllegalArgumentException.class) //
  public void observeOnOnShouldNotBeNull() {
    new Interactor<Object, Object>(null, Schedulers.immediate()) {
      @Override protected Observable<Object> createObservable(final Object o) {
        return null;
      }
    };
  }
}