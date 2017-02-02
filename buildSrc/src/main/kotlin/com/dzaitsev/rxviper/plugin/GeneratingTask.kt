package com.dzaitsev.rxviper.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class GeneratingTask : DefaultTask() {

  @TaskAction
  fun generate() = println("Generating...")
}