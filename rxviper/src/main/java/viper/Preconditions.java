package viper;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-May-14, 15:26
 */
final class Preconditions {
  static <T> void checkNotNull(T arg, String name) {
    if (arg == null) {
      throw new IllegalArgumentException(name + " can not be null");
    }
  }
}
