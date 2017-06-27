import nl.javadude.gradle.plugins.license.License
import org.gradle.script.lang.kotlin.repositories

apply {
  plugin("com.github.ben-manes.versions")
}

buildscript {
  applyFrom(rootProject.file("dependencies.gradle"))

  repositories {
    jcenter()
    maven { setUrl("https://plugins.gradle.org/m2/") }
  }

  dependencies {
    classpath(GradlePlugins.ANDROID)
    classpath(GradlePlugins.BINTRAY)
    classpath(GradlePlugins.MAVEN)
    classpath(GradlePlugins.LICENSE)
    classpath(GradlePlugins.APT)
    classpath(GradlePlugins.RETROLAMBDA)
    classpath(GradlePlugins.VERSIONS)
    classpath(GradlePlugins.KOTLIN)
    classpath(GradlePlugins.RX_VIPER)
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
