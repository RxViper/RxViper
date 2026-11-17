// Copyright 2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0.

package com.dzaitsev.rxviper.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/** Generates a reproducible source tree. The output directory must contain only generated files. */
@CacheableTask
abstract class RxViperTask : DefaultTask() {
    @get:Input
    abstract val sources: MapProperty<String, String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Inject
    abstract val fileSystemOperations: FileSystemOperations

    init {
        group = "RxViper"
        description = "Generates Kotlin VIPER scaffolding. Copy it into src/main/kotlin before editing."
    }

    @TaskAction
    fun generateRxViper() {
        val staging = temporaryDir.resolve("sources")
        staging.deleteRecursively()
        staging.mkdirs()
        sources.get().forEach { (relativePath, source) ->
            require(!java.io.File(relativePath).isAbsolute && '\\' !in relativePath &&
                relativePath.split('/').none { it == ".." }) {
                "Generated path must remain inside the output directory: $relativePath"
            }
            staging.resolve(relativePath).apply {
                parentFile.mkdirs()
                writeText(source)
            }
        }
        fileSystemOperations.sync {
            it.from(staging)
            it.into(outputDirectory)
        }
    }
}
