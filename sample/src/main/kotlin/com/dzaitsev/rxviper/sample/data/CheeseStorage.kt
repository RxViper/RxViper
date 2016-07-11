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

package com.dzaitsev.rxviper.sample.data

import android.content.res.Resources
import com.dzaitsev.rxviper.sample.R
import rx.Observable
import rx.Observable.error
import rx.Observable.just
import rx.Observable.timer
import java.lang.Math.min
import java.security.SecureRandom
import java.util.ArrayList
import java.util.Random
import java.util.Collections.shuffle
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Jun-07, 10:08
 */
@Singleton
class CheeseStorage @Inject internal constructor(resources: Resources) {
  private val cheeses: MutableList<Cheese>
  private val random = SecureRandom()

  init {
    val hard = resources.getStringArray(R.array.hard_cheeses)
    val semiHard = resources.getStringArray(R.array.semi_hard_cheeses)
    val semiSoft = resources.getStringArray(R.array.semi_soft_cheeses)
    val soft = resources.getStringArray(R.array.soft_cheeses)

    cheeses = ArrayList<Cheese>(
        hard.size + semiHard.size + semiSoft.size + soft.size)
    var id = 0
    id = fill(cheeses, hard, id, CheeseType.HARD)
    id = fill(cheeses, semiHard, id, CheeseType.SEMI_HARD)
    id = fill(cheeses, semiSoft, id, CheeseType.SEMI_SOFT)
    fill(cheeses, soft, id, CheeseType.SOFT)
  }


  /** Emulates long operation */
  fun getCheeses(count: Int): Observable<List<Cheese>> {
    val timer = timer((random.nextInt(2) + 1).toLong(), SECONDS)
    return if (count < 0) {
      timer.concatWith(error<Long>(RuntimeException("Count cannot be less than zero. Given count = $count"))).map({ seconds -> null })
    } else {
      just(randomSublist(cheeses, count, random)).zipWith(timer, { cheeses, seconds -> cheeses })
    }
  }

  companion object {
    private fun fill(cheeses: MutableList<Cheese>, cheeseNames: Array<String>, id: Int, cheeseType: CheeseType): Int {
      var i = id
      cheeseNames.mapTo(cheeses) { Cheese(it, i++.toLong(), cheeseType) }
      return i
    }

    private fun <T> randomSublist(list: List<T>, count: Int, random: Random): List<T> {
      val shuffled = ArrayList(list)
      shuffle(shuffled, random)
      return shuffled.subList(0, min(count, shuffled.size))
    }
  }
}
