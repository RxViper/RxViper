// Copyright 2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0.

package com.dzaitsev.rxviper.plugin

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RxViperPluginTest {
    @get:Rule
    val temporary = TemporaryFolder()

    @Test
    fun `extension values remain lazy until task inputs are read`() {
        val project = ProjectBuilder.builder().withProjectDir(temporary.newFolder()).build()
        project.plugins.apply(RxViperPlugin::class.java)
        val extension = project.extensions.getByType(RxViperExtension::class.java)
        val task = project.tasks.getByName("generateRxViper") as RxViperTask
        extension.packageName.set("example.later")
        extension.backend.set(Backend.RXJAVA3)
        extension.screens.create("home")
        assertEquals(4, task.sources.get().size)
        assertTrue(task.sources.get().keys.all { it.startsWith("example/later/home/") })
        assertTrue("rxjava3.Interactor" in task.sources.get().getValue("example/later/home/HomeInteractor.kt"))
    }

    @Test
    fun `task runs caches configuration becomes up to date and cleans obsolete generated files`() {
        val projectDir = temporary.newFolder()
        projectDir.resolve("settings.gradle").writeText("rootProject.name = 'generator-test'\n")
        val buildFile = projectDir.resolve("build.gradle")
        val fixture = """
            plugins { id 'com.dzaitsev.rxviper' }
            rxViper {
                packageName.set('example.fixture')
                backend.set(com.dzaitsev.rxviper.plugin.Backend.RXJAVA2)
                screens {
                    create('home') {
                        includeRouter.set(INCLUDE_ROUTER)
                        routesTo.set(['Details'])
                    }
                }
            }
        """.trimIndent()
        buildFile.writeText(fixture.replace("INCLUDE_ROUTER", "true"))
        fun run() = GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("generateRxViper", "--configuration-cache", "--stacktrace")
            .build()
        assertEquals(TaskOutcome.SUCCESS, run().task(":generateRxViper")?.outcome)
        val router = projectDir.resolve("build/generated/rxviper/kotlin/example/fixture/home/HomeRouter.kt")
        assertTrue(router.exists())
        val repeated = run()
        assertEquals(TaskOutcome.UP_TO_DATE, repeated.task(":generateRxViper")?.outcome)
        assertTrue("Reusing configuration cache" in repeated.output)
        buildFile.writeText(fixture.replace("INCLUDE_ROUTER", "false"))
        assertEquals(TaskOutcome.SUCCESS, run().task(":generateRxViper")?.outcome)
        assertFalse(router.exists())
    }
}
