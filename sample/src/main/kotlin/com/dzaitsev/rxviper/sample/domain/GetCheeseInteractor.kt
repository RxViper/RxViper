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

package com.dzaitsev.rxviper.sample.domain

import com.dzaitsev.rxviper.Interactor
import com.dzaitsev.rxviper.sample.dagger.Job
import com.dzaitsev.rxviper.sample.dagger.Main
import com.dzaitsev.rxviper.sample.data.CheeseStorage
import com.dzaitsev.rxviper.sample.presentation.CheeseViewModel
import rx.Observable
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Jun-07, 10:32
 */
@Singleton
class GetCheeseInteractor
@Inject constructor(
    @Job        subscribeOn  : Scheduler,
    @Main       observeOn    : Scheduler,
    private val cheeseStorage: CheeseStorage,
    private val cheeseMapper : CheeseMapper
) : Interactor<Int, Collection<CheeseViewModel>>(subscribeOn, observeOn) {

  override fun createObservable(requestModel: Int?): Observable<Collection<CheeseViewModel>> {
    return cheeseStorage.getCheeses(requestModel!!).map { cheeseMapper.map(it) }
  }


}
