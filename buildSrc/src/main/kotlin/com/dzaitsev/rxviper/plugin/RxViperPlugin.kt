package com.dzaitsev.rxviper.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class RxViperPlugin : Plugin<Project> {

  override fun apply(project: Project) = with(project) {
    val isLibrary = has<LibraryPlugin>()
    check(isLibrary || has<AppPlugin>()) { "RxViper plugin can only be applied to android projects" }

    val rxViper = extensions.create("rxViper", clazz<RxViperDSL>(), container(clazz<FeatureOptions>()))

    afterEvaluate {
      with(task<GeneratingTask>("generateScreens")) {
        configure(this) { group = "RxViper" }

        options = rxViper.options
        directory = if (isLibrary) {
          getExtension<LibraryExtension>("android")
        } else {
          getExtension<AppExtension>("android")
        }
            .sourceSets.findByName("main")
            .java
            .srcDirs.first()
      }
    }
  }
}