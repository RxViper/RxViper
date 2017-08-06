package com.dzaitsev.rxviper.sample;

import com.dzaitsev.rxviper.sample.mainscreen.MainActivity;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * @author Dmytro Zaitsev
 * @since 2017-Jul-27, 16:49
 */
@Module
abstract class BuildersModule {
  @ContributesAndroidInjector
  abstract MainActivity mainActivity();
}
