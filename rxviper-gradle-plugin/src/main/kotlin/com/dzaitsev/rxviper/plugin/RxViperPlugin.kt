// Copyright 2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0.

package com.dzaitsev.rxviper.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class RxViperPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("rxViper", RxViperExtension::class.java)
        extension.outputDirectory.convention(project.layout.buildDirectory.dir("generated/rxviper/kotlin"))
        project.tasks.register("generateRxViper", RxViperTask::class.java) { task ->
            task.outputDirectory.set(extension.outputDirectory)
            task.sources.set(project.providers.provider {
                KotlinGenerator.generate(
                    extension.packageName.get(),
                    extension.backend.get(),
                    extension.screens.map { screen ->
                        ScreenSpec(
                            screen.getName(),
                            screen.includeRouter.get(),
                            screen.includeInteractor.get(),
                            screen.routesTo.get(),
                            screen.useCases.map { UseCaseSpec(it.getName(), it.requestType.get(), it.responseType.get()) },
                        )
                    },
                )
            })
        }
    }
}
