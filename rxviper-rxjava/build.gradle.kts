plugins {
    kotlin("jvm")
    `java-library`
}

dependencies {
    api(project(":rxviper-core"))
    api(libs.rxjava)
}
