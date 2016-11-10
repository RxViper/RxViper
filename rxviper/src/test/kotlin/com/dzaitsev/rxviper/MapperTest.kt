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

package com.dzaitsev.rxviper

import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import java.util.Arrays.asList

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-May-14, 13:32
 */
class MapperTest {
  private lateinit var mapper: Mapper<Int, String>

  @Before fun setUp() {
    mapper = spy(TestMapper())
  }

  @Test fun shouldCallOverloadedMethod() {
    mapper.map(asList(1, 2, 3))

    verify(mapper).map(1)
    verify(mapper).map(2)
    verify(mapper).map(3)
  }

  @Test fun shouldCallMap() {
    mapper.call(42)

    verify(mapper).map(42)
  }
}