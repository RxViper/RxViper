// Copyright 2026 Dmytro Zaitsev. Licensed under the Apache License, Version 2.0.

package com.dzaitsev.rxviper.plugin

import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

/** Kotlin and Groovy DSL for generated sources. All generated files belong to the task. */
open class RxViperExtension @Inject constructor(objects: ObjectFactory) {
    val packageName: Property<String> = objects.property(String::class.java).convention("generated")
    val backend: Property<Backend> = objects.property(Backend::class.java).convention(Backend.COROUTINES)
    val outputDirectory: DirectoryProperty = objects.directoryProperty()
    val screens: NamedDomainObjectContainer<Screen> = objects.domainObjectContainer(Screen::class.java) {
        Screen(it, objects)
    }

    fun screens(action: Action<NamedDomainObjectContainer<Screen>>) = action.execute(screens)
}

class Screen(private val screenName: String, objects: ObjectFactory) : Named {
    override fun getName(): String = screenName

    val includeRouter: Property<Boolean> = objects.property(Boolean::class.java).convention(true)
    val includeInteractor: Property<Boolean> = objects.property(Boolean::class.java).convention(true)
    val routesTo: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())
    val useCases: NamedDomainObjectContainer<UseCase> = objects.domainObjectContainer(UseCase::class.java) {
        UseCase(it, objects)
    }

    fun useCases(action: Action<NamedDomainObjectContainer<UseCase>>) = action.execute(useCases)
}

class UseCase(private val useCaseName: String, objects: ObjectFactory) : Named {
    override fun getName(): String = useCaseName

    /** Fully qualified Kotlin type name; use kotlin.Unit for an interactor without input. */
    val requestType: Property<String> = objects.property(String::class.java).convention("kotlin.Unit")
    val responseType: Property<String> = objects.property(String::class.java).convention("kotlin.Any")
}
