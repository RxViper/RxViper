// Copyright 2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0.

package com.dzaitsev.rxviper.plugin

import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KotlinGeneratorTest {
    @get:Rule
    val temporary = TemporaryFolder()

    private val screens = listOf(
        ScreenSpec("main", true, true, listOf("Details"), listOf(InteractorSpec("load", "kotlin.Unit", "kotlin.String"))),
        ScreenSpec("plain", false, false, emptyList(), emptyList()),
        ScreenSpec("fallback", false, true, emptyList(), emptyList()),
    )

    @Test
    fun `every backend generates Kotlin that compiles against actual public APIs`() {
        val classpath = listOf(
            "kotlin.Unit",
            "com.dzaitsev.rxviper.Presenter",
            "com.dzaitsev.rxviper.rxjava.Interactor",
            "com.dzaitsev.rxviper.rxjava2.Interactor",
            "com.dzaitsev.rxviper.rxjava3.Interactor",
            "com.dzaitsev.rxviper.coroutines.SuspendInteractor",
            "rx.Observable",
            "io.reactivex.Flowable",
            "io.reactivex.rxjava3.core.Flowable",
            "org.reactivestreams.Publisher",
            "kotlinx.coroutines.Job",
            "org.jetbrains.annotations.NotNull",
        ).map { Class.forName(it).protectionDomain.codeSource.location.toURI() }
            .distinct().joinToString(File.pathSeparator) { File(it).path }
        Backend.entries.forEach { backend ->
            val directory = temporary.newFolder(backend.name)
            val files = KotlinGenerator.generate("example.screens", backend, screens).map { (path, content) ->
                directory.resolve(path).apply {
                    parentFile.mkdirs()
                    writeText(content)
                }
            }
            val errors = ByteArrayOutputStream()
            val result = K2JVMCompiler().exec(
                PrintStream(errors),
                "-no-stdlib", "-no-reflect", "-jvm-target", "1.8",
                "-classpath", classpath,
                "-d", directory.resolve("classes").path,
                *files.map(File::getPath).toTypedArray(),
            )
            assertEquals(ExitCode.OK, result, "$backend generated code did not compile:\n$errors")
        }
    }

    @Test
    fun `generation is deterministic and keeps each backend independent`() {
        Backend.entries.forEach { backend ->
            val result = KotlinGenerator.generate("example", backend, screens)
            assertEquals(result, KotlinGenerator.generate("example", backend, screens))
            assertTrue(result.keys.all { it.endsWith(".kt") })
            val interactor = result.getValue("example/main/LoadInteractor.kt")
            val presenter = result.getValue("example/main/MainPresenter.kt")
            assertTrue("onDropView" in presenter)
            if (backend == Backend.COROUTINES) {
                assertTrue("override suspend fun run" in interactor)
                assertFalse("io.reactivex" in interactor)
                assertTrue("it.cancel()" in presenter)
                assertTrue("com.dzaitsev.rxviper.SuspendInteractor<kotlin.Unit, kotlin.String>" in presenter)
                assertTrue("import com.dzaitsev.rxviper.coroutines.execute" in presenter)
            } else {
                assertTrue(".${backend.name.lowercase()}.Interactor" in interactor)
                assertFalse("kotlinx.coroutines" in interactor)
                assertTrue("loadInteractor.clear()" in presenter)
                assertTrue("import com.dzaitsev.rxviper.${backend.name.lowercase()}.execute" in presenter)
            }
            assertFalse(result.containsKey("example/plain/PlainRouter.kt"))
            assertFalse(result.containsKey("example/plain/PlainInteractor.kt"))
        }
    }

    @Test
    fun `reject unsafe paths keywords type expressions and generated type collisions`() {
        listOf("../outside", "example..screen", "example.class", "").forEach { pkg ->
            assertFailsWith<IllegalArgumentException> { KotlinGenerator.generate(pkg, Backend.COROUTINES, screens) }
        }
        val invalid = listOf(
            screens.first().copy(name = "../main"),
            screens.first().copy(routesTo = listOf("Details", "details")),
            screens.first().copy(interactors = listOf(InteractorSpec("load", "kotlin.Unit", "List<String>"))),
            screens.first().copy(interactors = listOf(InteractorSpec("load", "kotlin.Unit", "kotlin.String"), InteractorSpec("Load", "kotlin.Unit", "kotlin.String"))),
        )
        invalid.forEach { screen ->
            assertFailsWith<IllegalArgumentException> { KotlinGenerator.generate("example", Backend.COROUTINES, listOf(screen)) }
        }
    }
}
