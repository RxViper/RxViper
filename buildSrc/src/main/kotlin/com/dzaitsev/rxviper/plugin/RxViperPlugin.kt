package com.dzaitsev.rxviper.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class RxViperPlugin : Plugin<Project> {
  private val GROUP_NAME = "RxViper"
  private val ANDROID_EXTENSION = "android"

  override fun apply(project: Project) = with(project) {
    val isLibrary = has<LibraryPlugin>()
    check(isLibrary || has<AppPlugin>()) {
      "RxViper plugin can only be applied to Android projects.\n" +
          "Required plugins: 'com.android.application' or 'com.android.library'."
    }

    val rxViper = extensions.create(RxViperExtension.NAME, clazz<RxViperExtension>(), container(clazz<Screen>(), ScreenFactory(this)))

    afterEvaluate {
      with(task<GenerateRxViperTask>(GenerateRxViperTask.NAME)) {
        configure(this) { group = GROUP_NAME }
        screens = rxViper.screens
        directory = if (isLibrary) {
          getExtension<LibraryExtension>(ANDROID_EXTENSION)
        } else {
          getExtension<AppExtension>(ANDROID_EXTENSION)
        }
            .sourceSets.findByName("main")
            .java
            .srcDirs.first()
      }
    }
  }
}