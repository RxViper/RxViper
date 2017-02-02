package com.dzaitsev.rxviper.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class RxViperPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    if (!(project.has<LibraryPlugin>() || project.has<AppPlugin>())) {
      throw IllegalStateException("RxViper plugin can only be applied to android projects")
    }

    val rxViper = project.extensions.create("rxViper", clazz<RxViperDSL>(), project.container(clazz<FeatureOptions>()))
    val generateScreensTask = project.task<GeneratingTask>("generateScreens")

    generateScreensTask.doLast {
      val android = project.getExtension<AppExtension>("android")

      val mainSourceSets = android.sourceSets.toSet().filter { "main" == it.name }
      if (mainSourceSets.isNotEmpty()) {
        val mainSourceSet = mainSourceSets[0]
        val srcDirs = mainSourceSet.java.srcDirs

        if (!srcDirs.isEmpty()) {
          val srcMainJava = srcDirs.toList()[0]
          rxViper.featureOptionsContainer.all {
            createRxViper(it, srcMainJava)
          }
        }
      }
    }
  }

  private fun createRxViper(feature: FeatureOptions, directory: File) {
    val list = mutableListOf(ViewCallbacksGenerator(feature),
        PresenterGenerator(feature, feature.hasRouter))
    if (feature.hasRouter) {
      list.add(RouterGenerator(feature))
    }
    if (!feature.justMvp) {
      list.add(InteractorGenerator(feature))
    }

    list.forEach { generator ->
      //            val path = "src/main/java/${feature.fullPackage.replace('.', File.separatorChar)}"
//            val directory = File(path)
//            if (!directory.exists()) {
//                directory.mkdirs()
//            }
      generator.saveTo(directory)
    }
  }
}