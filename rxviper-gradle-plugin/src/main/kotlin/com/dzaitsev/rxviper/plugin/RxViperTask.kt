package com.dzaitsev.rxviper.plugin

import com.android.build.gradle.AndroidConfig
import com.android.build.gradle.internal.api.DefaultAndroidSourceSet
import com.dzaitsev.rxviper.plugin.internal.codegen.InteractorGenerator
import com.dzaitsev.rxviper.plugin.internal.codegen.PresenterGenerator
import com.dzaitsev.rxviper.plugin.internal.codegen.RouterGenerator
import com.dzaitsev.rxviper.plugin.internal.codegen.ViewCallbacksGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME
import org.gradle.api.tasks.TaskAction

open class RxViperTask : DefaultTask() {
  private val androidConfig = project.extensions.getByName("android") as AndroidConfig

  protected var destinationDir = (androidConfig.sourceSets.findByName(MAIN_SOURCE_SET_NAME)
      ?: DefaultAndroidSourceSet(MAIN_SOURCE_SET_NAME, project, true)).java.srcDirs.first()
    @JvmName("destinationDir") set

  protected var screens = (project.extensions.getByName(RxViperExtension.NAME) as RxViperExtension).screens
    @JvmName("screens") set

  @TaskAction
  fun generateRxViper() {
    screens.all {
      val generators = mutableListOf(ViewCallbacksGenerator(it), PresenterGenerator(it))

      if (it.hasRouter) {
        generators.add(RouterGenerator(it))
      }
      if (it.hasInteractor) {
        generators.add(InteractorGenerator(it))
      }

      generators.forEach {
        it.saveTo(destinationDir)
      }
    }
  }

  companion object {
    @JvmStatic
    val NAME = "generateRxViper"

    @JvmStatic
    val GROUP = "RxViper"
  }
}