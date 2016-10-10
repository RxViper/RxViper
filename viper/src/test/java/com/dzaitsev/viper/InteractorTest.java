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

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-May-14, 13:56
 */
public class InteractorTest {
  private static final int            PARAM        = 1;
  private static final OnNext<String> ON_NEXT      = new OnNext<String>() {
    @Override public void onNext(String s) {
    }
  };
  private static final OnError        ON_ERROR     = new OnError() {
    @Override public void onError(Throwable t) {
    }
  };
  private static final OnCompleted    ON_COMPLETED = new OnCompleted() {
    @Override public void onCompleted() {
    }
  };
  private Interactor<Integer, String> spyInteractor;

  @Before public void setUp() {
    spyInteractor = spy(new Interactor<Integer, String>() {
      @Override protected String getData(Integer integer) {
        return String.valueOf(integer);
      }
    });
  }

  @Test public void shouldGetData() {
    spyInteractor.execute(ON_NEXT);
    verify(spyInteractor).getData(null);
  }

  @Test public void shouldGetDataOnNextParam() {
    spyInteractor.execute(ON_NEXT, PARAM);
    verify(spyInteractor).getData(PARAM);
  }

  @Test public void shouldGetDataOnNextOnError() {
    spyInteractor.execute(ON_NEXT, ON_ERROR);
    verify(spyInteractor).getData(null);
  }

  @Test public void shouldGetDataOnNextOnErrorParam() {
    spyInteractor.execute(ON_NEXT, ON_ERROR, PARAM);
    verify(spyInteractor).getData(PARAM);
  }

  @Test public void shouldGetDataOnNextOnErrorOnComplete() {
    spyInteractor.execute(ON_NEXT, ON_ERROR);
    verify(spyInteractor).getData(null);
  }

  @Test public void shouldGetDataOnNextOnErrorOnCompleteParam() {
    spyInteractor.execute(ON_NEXT, ON_ERROR, ON_COMPLETED, PARAM);
    verify(spyInteractor).getData(PARAM);
  }

  @Test(expected = IllegalArgumentException.class) //
  public void onNextShouldNotBeNull() {
    spyInteractor.execute(null);
  }

  @Test(expected = IllegalArgumentException.class) //
  public void onNextParamShouldNotBeNull() {
    spyInteractor.execute(null, PARAM);
  }

  @Test(expected = IllegalArgumentException.class) //
  public void onErrorShouldNotBeNull() {
    spyInteractor.execute(ON_NEXT, (OnError) null);
  }

  @Test(expected = IllegalArgumentException.class) //
  public void onErrorParamShouldNotBeNull() {
    spyInteractor.execute(ON_NEXT, null, PARAM);
  }

  @Test(expected = IllegalArgumentException.class) //
  public void onCompleteShouldNotBeNull() {
    spyInteractor.execute(ON_NEXT, ON_ERROR, (OnCompleted) null);
  }

  @Test(expected = IllegalArgumentException.class) //
  public void onCompleteParamShouldNotBeNull() {
    spyInteractor.execute(ON_NEXT, ON_ERROR, null, PARAM);
  }
}