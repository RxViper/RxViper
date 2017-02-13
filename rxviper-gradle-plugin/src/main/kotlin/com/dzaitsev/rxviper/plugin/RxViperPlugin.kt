package com.dzaitsev.rxviper.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.dzaitsev.rxviper.plugin.RxViperTask.Companion.NAME as RXVIPER_TASK
import com.dzaitsev.rxviper.plugin.RxViperExtension.Companion.NAME as RXVIPER_EXT

class RxViperPlugin : Plugin<Project> {
  override fun apply(project: Project) = with(project) {
    check(has<LibraryPlugin>() || has<AppPlugin>()) {
      "RxViper plugin can only be applied to Android projects.\n" +
          "Required plugins: 'com.android.application' or 'com.android.library'."
    }

    extensions.create(RXVIPER_EXT, clazz<RxViperExtension>(), container(clazz<Screen>(), ScreenFactory(this)))

    afterEvaluate {
      task<RxViperTask>(RXVIPER_TASK)
    }
  }
}