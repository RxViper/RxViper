plugins {
    kotlin("jvm")
    `java-library`
}

dependencies {
    api(project(":rxviper-core"))
    api(libs.coroutines.core)
    testImplementation(libs.coroutines.test)
}
