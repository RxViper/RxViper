package com.dzaitsev.rxviper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.google.common.truth.Truth.assertThat;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Nov-04, 01:08
 */
public final class PreconditionsTest {
  @Rule public ExpectedException thrown = ExpectedException.none();

  @Test
  public void argShouldNotBeNull() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Argument can not be null");
    Preconditions.requireNotNull(null);
  }

  @Test
  public void checkPrivateConstructor() throws Throwable {
    try {
      final Constructor<?>[] constructors = Preconditions.class.getDeclaredConstructors();
      assertThat(constructors).hasLength(1);
      final Constructor<?> constructor = constructors[0];
      assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
      constructor.setAccessible(true);
      constructor.newInstance();
    } catch (InvocationTargetException ite) {
      final Throwable cause = ite.getCause();
      thrown.expect(AssertionError.class);
      thrown.expectMessage("No instances please!");
      throw cause;
    }
  }

  @Test
  public void shouldReturnTheSameArg() {
    final Object actual = new Object();
    final Object expected = Preconditions.requireNotNull(actual);
    assertThat(actual).isSameAs(expected);
  }
}