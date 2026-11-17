plugins {
    kotlin("jvm")
    `java-gradle-plugin`
}

kotlin {
    compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

gradlePlugin {
    plugins {
        create("rxViper") {
            id = "com.dzaitsev.rxviper"
            implementationClass = "com.dzaitsev.rxviper.plugin.RxViperPlugin"
            displayName = "RxViper Kotlin scaffolding"
            description = "Generates Kotlin VIPER scaffolding for RxJava 1, 2, 3 or coroutines."
        }
    }
}

dependencies {
    testImplementation(gradleTestKit())
    testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:${libs.versions.kotlin.get()}")
    testImplementation(project(":rxviper-core"))
    testImplementation(project(":rxviper-rxjava"))
    testImplementation(project(":rxviper-rxjava2"))
    testImplementation(project(":rxviper-rxjava3"))
    testImplementation(project(":rxviper-coroutines"))
}
