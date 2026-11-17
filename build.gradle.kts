import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    base
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.android.application) apply false
}

group = "com.dzaitsev.rxviper"
version = providers.gradleProperty("version").get()

subprojects {
    group = rootProject.group
    version = rootProject.version
    if (name != "sample") {
        apply(plugin = "org.jetbrains.kotlin.jvm")
        apply(plugin = "java-library")
        apply(plugin = "maven-publish")

        val pluginModule = name == "rxviper-gradle-plugin"
        val bytecodeVersion = if (pluginModule) JavaVersion.VERSION_17 else JavaVersion.VERSION_1_8
        extensions.configure<JavaPluginExtension> {
            sourceCompatibility = bytecodeVersion
            targetCompatibility = bytecodeVersion
            withSourcesJar()
            withJavadocJar()
        }
        extensions.configure<KotlinJvmProjectExtension> {
            compilerOptions {
                jvmTarget.set(if (pluginModule) JvmTarget.JVM_17 else JvmTarget.JVM_1_8)
                allWarningsAsErrors.set(true)
            }
        }
        dependencies {
            "testImplementation"(kotlin("test-junit", rootProject.libs.versions.kotlin.get()))
            "testImplementation"(rootProject.libs.junit)
        }
        tasks.withType<Test>().configureEach {
            useJUnit()
            testLogging { events("failed", "skipped") }
        }
        extensions.configure<PublishingExtension> {
            if (!pluginModule) {
                publications.create<MavenPublication>("mavenJava") {
                    from(components["java"])
                    pom {
                        name.set(project.name)
                        description.set("Modular Kotlin VIPER architecture: ${project.name}")
                        url.set("https://github.com/RxViper/RxViper")
                        licenses {
                            license {
                                name.set("The Apache License, Version 2.0")
                                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                            }
                        }
                        developers {
                            developer { id.set("dmitriyzaitsev"); name.set("Dmytro Zaitsev") }
                        }
                        scm {
                            url.set("https://github.com/RxViper/RxViper")
                            connection.set("scm:git:https://github.com/RxViper/RxViper.git")
                        }
                    }
                }
            }
            repositories {
                maven {
                    name = "build"
                    url = rootProject.layout.buildDirectory.dir("repository").get().asFile.toURI()
                }
            }
        }
        rootProject.tasks.named("check") { dependsOn(tasks.named("check")) }
        rootProject.tasks.named("assemble") { dependsOn(tasks.named("assemble")) }
        rootProject.tasks.named("clean") { dependsOn(tasks.named("clean")) }
    }
}
