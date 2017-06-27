import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

plugins {
  `java-library`
}

configure<JavaPluginConvention> {
  sourceCompatibility = JavaVersion.VERSION_1_7
  targetCompatibility = JavaVersion.VERSION_1_7
}

compileTestJava {
  sourceCompatibility = JavaVersion.VERSION_1_8.toString()
  targetCompatibility = JavaVersion.VERSION_1_8.toString()
}

dependencies {
  compile(Libraries.rxJava)
  testCompile(Libraries.junit)
  testCompile(Libraries.mockito)
  testCompile(Libraries.truth)
}

fun Project.compileTestJava(block: JavaCompile.() -> Unit) = (tasks["compileTestJava"] as JavaCompile).apply { block(this) }