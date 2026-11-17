pluginManagement {
    // Settings plugins resolve before version catalogs are available.
    val trellisVersion = providers.gradleProperty("trellisVersion").get()
    plugins {
        id("com.dzaitsev.trellis") version trellisVersion
        id("com.dzaitsev.trellis.conventions") version trellisVersion
    }
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

plugins {
    id("com.dzaitsev.trellis")
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
    }
}
rootProject.name = "RxViper"

trellis {
    java("rxviper-core")
    java("rxviper-rxjava")
    java("rxviper-rxjava2")
    java("rxviper-rxjava3")
    java("rxviper-coroutines")
    java("rxviper-gradle-plugin")

    // Runtime libraries and generator build without an Android SDK.
    if (providers.gradleProperty("androidSample").orNull == "true") {
        androidApp("sample", "com.dzaitsev.rxviper.sample") { usesCompose = true }
    }
}
