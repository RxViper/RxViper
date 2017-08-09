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

package com.dzaitsev.rxviper.sample.mainscreen.domain;

import com.dzaitsev.rxviper.sample.data.CheeseStorage;
import com.dzaitsev.viper.Interactor;
import java.util.Collection;
import rx.Observable;
import rx.Scheduler;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Jun-07, 10:32
 */
public final class GetCheesesInteractor extends Interactor<Integer, Collection<CheeseViewModel>> {
  private final CheeseStorage storage;
  private final CheeseMapper  mapper;

  public GetCheesesInteractor(Scheduler subscribeOn, Scheduler observeOn, CheeseStorage storage, CheeseMapper mapper) {
    super(subscribeOn, observeOn);
    this.storage = storage;
    this.mapper = mapper;
  }

  @Override
  protected Observable<Collection<CheeseViewModel>> createObservable(Integer amount) {
    return storage.getCheeses(amount)
        .map(mapper::map);
  }
}
