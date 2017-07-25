package com.dzaitsev.rxviper.sample;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2017-Jul-25, 19:54
 */
public interface StartStop {
  void onStart();

  void onStop(boolean isChangingConfigurations);
}
