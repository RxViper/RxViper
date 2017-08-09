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

import java.util.Collection;
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

  private com.dzaitsev.viper.Mapper<Integer, String> mapper;

  @Before
  public void setUp() {
    mapper = spy(new com.dzaitsev.viper.Mapper<Integer, String>() {
      @Override
      public String map(final Integer entity) {
        return String.valueOf(entity);
      }
    });
  }

  @Test
  public void shouldCallOverloadedMethod() {
    mapper.map(asList(1, 2, 3));

    verify(mapper).map(1);
    verify(mapper).map(2);
    verify(mapper).map(3);
  }

  @Test
  public void shouldNotRespectNullCollections() {
    TestUtil.checkIllegalArgumentException(() -> mapper.map((Collection<Integer>) null));
  }
}