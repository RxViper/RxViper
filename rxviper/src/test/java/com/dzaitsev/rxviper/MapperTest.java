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

import static java.util.Arrays.asList;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-May-14, 13:32
 */
public final class MapperTest {

  private Mapper<Integer, String> spyMapper;

  @Before
  public void setUp() {
    spyMapper = spy(new Mapper<Integer, String>() {
      @Override
      public String map(final Integer entity) {
        return String.valueOf(entity);
      }
    });
  }

  @Test
  public void shouldCallOverloadedMethod() {
    spyMapper.map(asList(1, 2, 3));

    verify(spyMapper).map(1);
    verify(spyMapper).map(2);
    verify(spyMapper).map(3);
  }

  @Test
  public void shouldCallMap() {
    spyMapper.apply(42);

    verify(spyMapper).map(42);
  }
}