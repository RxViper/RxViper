package com.dzaitsev.rxviper.plugin

import com.dzaitsev.rxviper.plugin.generator.InteractorGenerator
import com.dzaitsev.rxviper.plugin.generator.PresenterGenerator
import com.dzaitsev.rxviper.plugin.generator.RouterGenerator
import com.dzaitsev.rxviper.plugin.generator.ViewCallbacksGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerateRxViperTask : DefaultTask() {
  internal lateinit var screens: NamedDomainObjectContainer<Screen>
  internal lateinit var directory: File

  @TaskAction
  fun generateRxViper() {
    screens.all {
      val generators = mutableListOf(ViewCallbacksGenerator(it), PresenterGenerator(it, it.hasRouter))
      if (it.hasRouter) {
        generators.add(RouterGenerator(it))
      }
      if (!it.justMvp) {
        generators.add(InteractorGenerator(it))
      }

      generators.forEach {
        it.saveTo(directory)
      }
    }
  }

  companion object {
    @JvmStatic
    internal val NAME = "generateRxViper"

    @JvmStatic
    internal val GROUP = "RxViper"
  }
}