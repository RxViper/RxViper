package com.dzaitsev.rxviper;

import java.util.NoSuchElementException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * @author Dmytro Zaitsev
 * @since 2017-Jul-25, 16:47
 */
public class OptionalTest {
  @Rule final ExpectedException thrown = ExpectedException.none();

  @Test
  public void testEmpty() {
    assertSame(Optional.empty(), Optional.of(null));
  }

  @Test
  public void testIsPresentNonnull() {
    assertTrue(Optional.of("Hello")
        .isPresent());
  }

  @Test
  public void testIsPresentNullable() {
    assertFalse(Optional.of(null)
        .isPresent());
  }

  @Test
  public void testGetNonnull() {
    final String value = "Hello";
    assertSame(Optional.of(value)
        .get(), value);
  }

  @Test
  public void testGetNullable() {
    thrown.expect(NoSuchElementException.class);
    Optional.of(null)
        .get();
  }
}