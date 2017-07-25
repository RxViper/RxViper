package com.dzaitsev.rxviper;

import java.util.NoSuchElementException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Dmytro Zaitsev
 * @since 2017-Jul-25, 15:51
 */
@SuppressWarnings("WeakerAccess")
public class Optional<T> {
  private static final Optional<?> EMPTY = new Optional<>(null);
  @Nullable private final T value;

  public static <T> Optional<T> of(@Nullable T value) {
    return value == null ? empty() : new Optional<>(value);
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  public static <T> Optional<T> empty() {
    return (Optional<T>) EMPTY;
  }

  private Optional(@Nullable T value) {
    this.value = value;
  }

  public boolean isPresent() {
    return value != null;
  }

  @Nonnull
  public T get() {
    if (value == null) {
      throw new NoSuchElementException("No value present");
    }
    return value;
  }
}
