package com.dzaitsev.rxviper;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2017-Jul-23, 00:08
 */

@SuppressWarnings("WeakerAccess")
public class RxViperException extends RuntimeException {
  public RxViperException(Throwable throwable) {
    super(throwable);
  }
}
