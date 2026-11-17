pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
    }
}
rootProject.name = "RxViper"
include(":rxviper-core", ":rxviper-rxjava", ":rxviper-rxjava2", ":rxviper-rxjava3", ":rxviper-coroutines", ":rxviper-gradle-plugin")
// Runtime libraries and generator build without an Android SDK.
if (providers.gradleProperty("androidSample").orNull == "true") include(":sample")
