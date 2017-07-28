package com.dzaitsev.rxviper.sample;

import android.app.Activity;
import com.dzaitsev.rxviper.sample.mainscreen.MainActivity;
import com.dzaitsev.rxviper.sample.mainscreen.MainScreenSubcomponent;
import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

/**
 * @author Dmytro Zaitsev
 * @since 2017-Jul-27, 16:49
 */
@Module
abstract class BuildersModule {
  @Binds
  @IntoMap
  @ActivityKey(MainActivity.class)
  abstract AndroidInjector.Factory<? extends Activity> bindMainActivityInjectorFactory(MainScreenSubcomponent.Builder builder);
}
