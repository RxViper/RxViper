import nl.javadude.gradle.plugins.license.License

apply {
  plugin("com.github.ben-manes.versions")
}

buildscript {
  applyFrom(rootProject.file("dependencies.gradle"))
  fun gradlePlugins(name: String) = (extra.properties["gradlePlugins"] as Map<String, Any>)[name]!!

  repositories {
    gradleScriptKotlin()
    jcenter()
    maven { setUrl("https://plugins.gradle.org/m2/") }
  }

  dependencies {
    classpath(gradlePlugins("android"))
    classpath(gradlePlugins("bintray"))
    classpath(gradlePlugins("maven"))
    classpath(gradlePlugins("license"))
    classpath(gradlePlugins("apt"))
    classpath(gradlePlugins("retrolambda"))
    classpath(gradlePlugins("versions"))
    classpath(gradlePlugins("kotlin"))
//    classpath(gradlePlugins("rxViper"))
  }
}

allprojects {
  repositories {
    jcenter()
  }

  applyFrom(rootProject.file("dependencies.gradle"))
  applyFrom(rootProject.file("license.gradle"))
//  apply plugin: 'idea'
}

task<License>("licenseFormatEverything") {
  group = "license"
  //source = fileTree(projectDir).include(listOf("**/*.gradle", "**/*.properties", "**/*.xml", "**/*.kt"))
}
