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

package com.dzaitsev.rxviper.sample.data;

import android.content.res.Resources;
import com.dzaitsev.rxviper.sample.R;
import io.reactivex.Flowable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static io.reactivex.Flowable.error;
import static io.reactivex.Flowable.just;
import static io.reactivex.Flowable.timer;
import static java.lang.Math.min;
import static java.util.Collections.shuffle;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Jun-07, 10:08
 */
public final class CheeseStorage {
  private final List<Cheese> cheeses;
  private final SecureRandom random = new SecureRandom();

  CheeseStorage(Resources resources) {
    final String[] hard = resources.getStringArray(R.array.hard_cheeses);
    final String[] semiHard = resources.getStringArray(R.array.semi_hard_cheeses);
    final String[] semiSoft = resources.getStringArray(R.array.semi_soft_cheeses);
    final String[] soft = resources.getStringArray(R.array.soft_cheeses);
    cheeses = new ArrayList<>(hard.length + semiHard.length + semiSoft.length + soft.length);
    int id = 0;
    id = fill(cheeses, hard, id, CheeseType.HARD);
    id = fill(cheeses, semiHard, id, CheeseType.SEMI_HARD);
    id = fill(cheeses, semiSoft, id, CheeseType.SEMI_SOFT);
    fill(cheeses, soft, id, CheeseType.SOFT);
  }

  private static int fill(List<Cheese> cheeses, String[] cheeseNames, int id, CheeseType cheeseType) {
    int i = id;
    for (String name : cheeseNames) {
      cheeses.add(new Cheese(name, i++, cheeseType));
    }
    return i;
  }

  private static <T> List<T> randomSublist(List<T> list, int count, Random random) {
    final ArrayList<T> shuffled = new ArrayList<>(list);
    shuffle(shuffled, random);
    return shuffled.subList(0, min(count, shuffled.size()));
  }

  /**
   * Emulates long operation
   */
  public Flowable<List<Cheese>> getCheeses(int count) {
    final Flowable<Long> timer = timer(random.nextInt(2) + 1, SECONDS);
    return count < 0 ? timer.concatMap(seconds -> error(new RuntimeException("Count cannot be less than zero. Given count = " + count)))
        : just(randomSublist(cheeses, count, random)).zipWith(timer, (cheeses, seconds) -> cheeses);
  }
}
